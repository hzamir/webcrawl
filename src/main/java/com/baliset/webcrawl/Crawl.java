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

  public Crawl(CrawlConfig config) {
    this.config = config;
    links = new HashSet<String>();
  }

  public void crawl()
  {
      getPageLinks(config.getInitialUrl(), 0);
  }

  private boolean shouldFollowProtocol(String protocol)
  {
    return "http".equals(protocol);
  }

  private boolean shouldFollowDomain(String domain)
  {
       return !config.isStayInDomain() || config.getInitialDomain().equals(domain);
  }

  private boolean shouldFollowUrl(String urlstring, int depth)
  {

    try {

      if(depth > config.getDepthLimit())
      {
        System.err.println("Hit depth limit: " + depth);
        return false;
        
      }

      if(config.timeLeft() <= 0) {
        System.err.println("we are out of time: ");
        return false;
      }

      URL url = new URL(urlstring);
      String protocol = url.getProtocol();
      if(!shouldFollowProtocol(protocol))
      {
        System.err.println("Not following protocol: " + protocol);
        return false;
      }

      String host = url.getHost();
      if(!shouldFollowDomain(host))
      {
        System.err.println("Not following host: " + host);
        return false;
      }


    } catch (MalformedURLException e) {
      System.err.println("Malformed URL: ");
      return false;
    }

    return true;
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

        if(!shouldFollowUrl(url, ++depth))
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