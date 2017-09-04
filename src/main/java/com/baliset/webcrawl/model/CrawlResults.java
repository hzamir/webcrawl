package com.baliset.webcrawl.model;

import com.baliset.webcrawl.*;

public class CrawlResults
{
  private CrawlConfig config;  // how it was configured to run   todo: determine jackson serialization issue
  public Stats stats;         // pertinent statistics
  public Issues issues;       // specific issues
  public CrawlNode node;      // node graph starts here

  public CrawlResults(CrawlConfig c,Stats s, Issues i, CrawlNode n)
  {
    config=c; stats = s; issues=i; node =n;
  }
  
}
