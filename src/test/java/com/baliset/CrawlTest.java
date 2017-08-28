package com.baliset;


import com.baliset.webcrawl.*;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class CrawlTest
{

  @Test
  public void testCrawl() throws Exception {

    CrawlConfig config = new CrawlConfig();

    config.setAllowSubdomains(false);
    config.setStayInDomain(true);
    config.setDepthLimit(500);
    config.setMinutesLimit(5);
    config.setInitialUrl("http://www.wipro.com");

    Crawl crawler = new Crawl(config);
    crawler.crawl();


    assertTrue(true);
  }
}
