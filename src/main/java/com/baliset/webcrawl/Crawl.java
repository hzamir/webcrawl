package com.baliset.webcrawl;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.*;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Crawl
{
  private static final Logger logger = LoggerFactory.getLogger(Crawl.class);

  private CrawlConfig config;

  private Map<String, CrawlNode> links;
  private CrawlNode start;     // place from which we trace all other nodes while crawling
  private Issues issues;
  private long startTime;

  public Crawl(CrawlConfig config) {
    this.config = config;
    logger.info(config.toString());
    links = new HashMap<>();
    issues = new Issues();
  }

  public void crawl()
  {
      startTime = System.currentTimeMillis();
      getPageLinks(start, config.getInitialUrl(), 0);
      start.print(0);
      issues.printReasons();
  }

  private boolean shouldFollowProtocol(String protocol)
  {
    return protocol != null && protocol.startsWith("http");
  }

  private boolean shouldFollowDomain(String domain)
  {

      // not domain restricted, or strictly in the domain, or if supported in a subdomain of the domain
       return   !config.isStayInDomain() ||
                config.getInitialDomain().equals(domain) ||
                domain.endsWith(config.getInitialDomain());
  }

  private Reason shouldContinue(int depth)
  {

    if (depth > config.getDepthLimit())
      return issues.addIssue(Reason.ExceededDepthLimit);

    long deadline  = startTime + (config.getMinutesLimit() * 60_000);

    if (System.currentTimeMillis() > deadline)
      return issues.addIssue(Reason.ExceededExecutionTimeLimit);

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


    links.put(url, node);
    if(start == null)
      start = node;

    return node;
  }


  private void getLinkSpec(CrawlNode node,int depth, Document document, String spec)
  {
    Elements linksOnPage = document.select(spec);

    for (Element page : linksOnPage) {
      getPageLinks(node, page.attr("abs:href"), depth);
    }

  }

  static String[] linkrefs = {"a[href]", "link[href]",  "iframe[src]", "embed[src]", "input[src]", "script[src]", "img[src]", "object[data]", "q[cite]", "del[cite]" };

  private void getPageLinks(CrawlNode parent, String url, int depth)
  {

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
            node.setReason(Reason.Ok);
            Document document = connection.get();

            for (String spec : linkrefs)
              getLinkSpec(node, depth, document, spec);

          } else {
            node.setFollowed(false);
            node.setReason(Reason.OtherBadStatus);
          }

        } catch (Exception e) {
          node.setReason(Reason.UnsupportedFormat);
        }


      } catch (Exception e) {
        System.err.println("For '" + url + "': " + e.getMessage());
      }
    }
  }


}