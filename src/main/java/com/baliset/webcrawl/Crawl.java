package com.baliset.webcrawl;

import com.baliset.webcrawl.model.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.slf4j.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Crawl
{
  private static final Logger logger = LoggerFactory.getLogger(Crawl.class);

  private CrawlConfig config;
  private String outputPath;

  private Map<String, CrawlNode> links;
  private Queue<WorkItem>        queue;


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
    stats= new Stats();
    issues = new Issues();
    outputFormatter = new OutputFormatter();
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
    startTime = stats.start();

    getPageLinks(start, config.getInitialUrl(), 0);

    doWork();

    stats.stop();

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
      stats.stoppedByUser();
      return Reason.Stopped;
    }

    if (depth > config.getDepthLimit())
      return issues.addIssue(Reason.ExceededDepthLimit);

    long deadline  = startTime + (config.getMinutesLimit() * 60_000);

    if (System.currentTimeMillis() > deadline) {
      stats.timeExpired();
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


  private CrawlNode add(String url)
  {

    CrawlNode node = new CrawlNode(url);
    stats.update(url);

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

    stats.incrementTotal();
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
            stats.incrementFetched();

            node.setReason(Reason.Ok);
            Document document = connection.get();
            stats.incrementParsed();


            for (String spec : config.getLinkTypes())
              getLinkSpec(node, depth, document, spec);

          } else {
            stats.incrementFetchFailures();

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