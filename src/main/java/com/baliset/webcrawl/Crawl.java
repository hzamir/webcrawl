package com.baliset.webcrawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class Crawl
{

  private CrawlConfig config;

  private Map<String, CrawlNode> links;
  private CrawlNode start;     // place from which we trace all other nodes while crawling
  private Issues issues;

  public Crawl(CrawlConfig config) {
    this.config = config;
    links = new HashMap<>();
    issues = new Issues();
  }

  public void crawl()
  {
      getPageLinks(start, config.getInitialUrl(), 0);
      start.print(0);
      issues.printReasons();
  }

  private boolean shouldFollowProtocol(String protocol)
  {
    return "http".equals(protocol);
  }

  private boolean shouldFollowDomain(String domain)
  {
       return !config.isStayInDomain() || config.getInitialDomain().equals(domain);
  }

  private Reason shouldContinue(int depth)
  {

    if (depth > config.getDepthLimit())
      return issues.addIssue(Reason.ExceededDepthLimit);

    if (config.timeLeft() <= 0)
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

    //System.out.println(url);
    return node;
  }

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

        Document document = Jsoup.connect(url).get();
        Elements linksOnPage = document.select("a[href]");


        for (Element page : linksOnPage) {
          getPageLinks(node, page.attr("abs:href"), depth);
        }
      } catch (IOException e) {
        System.err.println("For '" + url + "': " + e.getMessage());
      }
    }
  }


}