package com.baliset.webcrawl;

import org.springframework.beans.factory.*;
import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

import java.net.*;
import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "webcrawl.defaults")
public class CrawlConfig implements InitializingBean
{
  private String  useragent;
  private String  initialUrl;
  private boolean stayInDomain;     // stay in domain (don't go to another domain)
  private boolean allowSubdomains;  // if inside domain are subdomains ok?
  private int     minutesLimit;     // max time
  private int     depthLimit;       // max depth
  private String outputFormat;      // xml, yaml, or json

  private List<String> linkTypes = new ArrayList<>();

  public List<String> getLinkTypes() {
    return linkTypes;
  }


  // ---derived information
  private String initialDomain;  // extracted from initialUrl




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

  public String toString()
  {
    return String.format("{url:%s, domain: %s, stay:%s, follow:%s, depth:%d, minutes:%d}",
        initialUrl, initialDomain, stayInDomain, allowSubdomains, depthLimit, minutesLimit
    );

  }

  public String getUseragent()
  {
    return useragent;
  }

  public void setUseragent(String useragent)
  {
    this.useragent = useragent;
  }

  @Override
  public void afterPropertiesSet() throws Exception
  {
    System.out.print("hi\n");
  }

  public void setOutputFormat(String s) { outputFormat = s;}
  public String getOutputFormat()
  {
    return outputFormat;
  }
}

