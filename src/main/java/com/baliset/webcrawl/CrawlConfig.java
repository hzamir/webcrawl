package com.baliset.webcrawl;

import java.net.*;

// basic parameters governing the search
public class CrawlConfig
{
  private String  initialUrl;
  private boolean stayInDomain;     // stay in domain (don't go to another domain)
  private boolean allowSubdomains;  // if inside domain are subdomains ok?
  private int     minutesLimit;     // max time
  private int     depthLimit;       // max depth


  // ---derived information
  private String initialDomain;  // extracted from initialUrl
  private long deadline;         // when we must finish execution




  public boolean isStayInDomain()
  {
    return stayInDomain;
  }

  public void setStayInDomain(boolean stayInDomain)
  {
    this.stayInDomain = stayInDomain;
  }

  public boolean isAllowSubdomains()
  {
    return allowSubdomains;
  }

  public void setAllowSubdomains(boolean allowSubdomains)
  {
    this.allowSubdomains = allowSubdomains;
  }

  public int getMinutesLimit()
  {
    return minutesLimit;
  }

  public void setMinutesLimit(int minutesLimit)
  {
    this.minutesLimit = minutesLimit;

    // deadline to finish execution is
    this.deadline = System.currentTimeMillis() + (minutesLimit * 60_000);
  }

  public int getDepthLimit()
  {
    return depthLimit;
  }

  public void setDepthLimit(int depthLimit)
  {
    this.depthLimit = depthLimit;
  }

  public String getInitialUrl()
  {
    return initialUrl;
  }

  public void setInitialUrl(String initialUrl)
  {
    this.initialUrl = initialUrl;

    try {
      initialDomain =   new URL(initialUrl).getHost();
    } catch (MalformedURLException e) {
      throw new RuntimeException("IntialUrl does not look good", e);
    }
  }
  public String getInitialDomain()
  {
         return initialDomain;
  }

  public long timeLeft()
  {
    return deadline - System.currentTimeMillis();
  }

}

