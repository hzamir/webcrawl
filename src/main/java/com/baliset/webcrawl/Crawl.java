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
  private Set<String> links;
  private Issues issues;

  public Crawl(CrawlConfig config) {
    this.config = config;
    links = new HashSet<>();
    issues = new Issues();
  }

  public void crawl()
  {
      getPageLinks(config.getInitialUrl(), 0);
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

  private void add(String url)
  {
    if (links.add(url)) {
      System.out.println(url);
    }
  }

  private void getPageLinks(String url, int depth)
  {

    if (!links.contains(url)) {

      try {

        add(url);

        if(shouldContinue(++depth) != Reason.Ok)

        if(shouldFollowUrl(url) != Reason.Ok)
          return;

        Document document = Jsoup.connect(url).get();
        Elements linksOnPage = document.select("a[href]");


        //5. For each extracted URL... go back to Step 4.
        for (Element page : linksOnPage) {
          getPageLinks(page.attr("abs:href"), depth);
        }
      } catch (IOException e) {
        System.err.println("For '" + url + "': " + e.getMessage());
      }
    }
  }


}