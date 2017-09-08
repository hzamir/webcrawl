package com.baliset.webcrawl;

import com.baliset.webcrawl.model.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;

public class Crawl
{
  private static final Logger logger = LoggerFactory.getLogger(Crawl.class);

  private CrawlConfig config;
  private String outputPath;

  private Map<String, CrawlNode> links;
  private Queue<WorkItem>        queue;
  private Map<String, Integer> domainStats;


  private CrawlNode start;     // place from which we trace all other nodes while crawling
  private Issues issues;
  private Stats stats;

  private OutputFormatter outputFormatter;

  private long startTime;
  private boolean stop;

  public Crawl(CrawlConfig config, String outputPath) {
    this.config = config;
    this.outputPath = outputPath;
    logger.info(config.toString());
    links = new HashMap<>();
    queue = new LinkedList<>();
    domainStats = new HashMap<>();
    stats= new Stats();
    stats.terminationCode = TerminationCode.CrawlCompleted;
    issues = new Issues();
    outputFormatter = new OutputFormatter();
  }

  private String durationFormat(Duration duration)
  {
    long hours = duration.toHours();
    int minutes = (int) ((duration.getSeconds() % (60 * 60)) / 60);
    int seconds = (int) (duration.getSeconds() % 60);
    return String.format("%d:%d:%d", hours,minutes,seconds);
  }



  private void printToFile(CrawlResults crawlResults)
  {
    outputFormatter.selectFormat(config.getOutputFormat());
    logger.info("Writing to file: " + outputPath);
    try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath))) {
      outputFormatter.print(crawlResults, writer);
    } catch (IOException e) {
      logger.error("Failed to write output", e);
    }    // the file will be automatically closed
  }

  public void crawl()
  {
    startTime = System.currentTimeMillis();
    Instant startInstant = Instant.now();


    getPageLinks(start, config.getInitialUrl(), 0);

    doWork();

    stats.elapsedTime = durationFormat(Duration.between(startInstant, Instant.now()));

    List<DomainHits> hits = new ArrayList<>(domainStats.size());

    for(Map.Entry<String, Integer> entry: domainStats.entrySet())
    {
      hits.add(new DomainHits(entry.getKey(), entry.getValue()));
    }
    hits.sort((a,b)->b.hits-a.hits);
    stats.domainStats = hits;

    CrawlResults crawlResults = new CrawlResults(config, stats, issues, start);
    printToFile(crawlResults);

  }

  private boolean shouldFollowProtocol(String protocol)
  {
    return protocol != null && protocol.startsWith("http");
  }

  private boolean shouldFollowDomain(String domain)
  {

    // not domain restricted, or strictly in the domain, or if supported in a subdomain of the domain
       return !config.isStayInDomain() ||
           config.getInitialDomain().equals(domain) ||
           domain.endsWith(config.getInitialDomain());
  }

  private Reason shouldContinue(int depth)
  {

    if(stop) {
      stats.terminationCode = TerminationCode.StoppedByUser;
      return Reason.Stopped;
    }

    if (depth > config.getDepthLimit())
      return issues.addIssue(Reason.ExceededDepthLimit);

    long deadline  = startTime + (config.getMinutesLimit() * 60_000);

    if (System.currentTimeMillis() > deadline) {
      stats.terminationCode = TerminationCode.AllocatedTimeExpired;
      return issues.addIssue(Reason.ExceededExecutionTimeLimit);
    }
    return Reason.Ok;
  }

  private Reason shouldFollowUrl(String urlstring)
  {
    try {
      URL url = new URL(urlstring);
      String protocol = url.getProtocol();
      if(!shouldFollowProtocol(protocol))
      {
        return Reason.UnsupportedProtocol;
      }

      String host = url.getHost();
      if(!shouldFollowDomain(host))
      {
        return Reason.OutsideDomain;
      }

    } catch (MalformedURLException e) {
      return Reason.MalformedUrl;
    }

    return Reason.Ok;
  }

  private void updateStats(String s)
  {
    try {
      URL url = new URL(s);
      String h = url.getHost();
      domainStats.merge(h, 1, (a, b) -> a.intValue() + b);

    } catch (MalformedURLException e) {
    }


  }
  private CrawlNode add(String url)
  {

    CrawlNode node = new CrawlNode(url);
    ++stats.unique;
    updateStats(url);

    links.put(url, node);
    if(start == null)
      start = node;

    return node;
  }


  private void doWork()
  {
      int count;

      while((count = queue.size()) > 0)
      {
          WorkItem peek = queue.peek();
          logger.info("level " + peek.getDepth() + " contains " + count + " items");
          for(int i = 0; i < count; ++i) {
            WorkItem item = queue.remove();
            getPageLinks(item.getNode(), item.getAttrKey(), item.getDepth());
          }
      }
  }


  private void getLinkSpec(CrawlNode node,int depth, Document document, String spec)
  {
    Elements linksOnPage = document.select(spec);
    String attributeKey = "abs:" +    spec.split("[\\[\\]]")[1];

    for (Element page : linksOnPage) {
      queue.add(new WorkItem(node,  page.attr(attributeKey), depth));  // bread first search queue
    }
  }


  private void getPageLinks(CrawlNode parent, String url, int depth)
  {

    ++stats.total;
    if (!links.containsKey(url)) {

      try {

        CrawlNode node = add(url);  // first track the node, whether we can download it or not

        if(parent != null)
          parent.addChild(node);    // keep a record of the children

        Reason reason =  shouldContinue(++depth);
        if(reason != Reason.Ok) {
          node.setFollowed(false);
          node.setReason(reason);
          return;
        }

        reason = shouldFollowUrl(url);
        if(reason != Reason.Ok) {
          node.setFollowed(false);
          node.setReason(reason);
          return;
        }


        try {
          Connection connection = Jsoup.connect(url);
          Connection.Response response = connection.userAgent(config.getUseragent()).ignoreHttpErrors(true).execute();
          int statusCode = response.statusCode();
          node.setStatus(statusCode);

          if (statusCode == 200) {
            ++stats.fetchedOk;
            node.setReason(Reason.Ok);
            Document document = connection.get();
            ++stats.parsed;

            for (String spec : config.getLinkTypes())
              getLinkSpec(node, depth, document, spec);

          } else {
            ++stats.fetchFailed;
            node.setFollowed(false);
            node.setReason(Reason.OtherBadStatus);
          }

        } catch (Exception e) {
          node.setReason(Reason.UnsupportedFormat);
        }

      } catch (Exception e) {
        logger.error("Unexpected Error", e);
      }
    }
  }


  public void stop()
  {
    stop = true;
  }
}