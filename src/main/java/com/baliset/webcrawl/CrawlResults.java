package com.baliset.webcrawl;

public class CrawlResults
{
    private CrawlConfig config;  // how it was configured to run
    public Stats stats;         // pertinent statistics
    public Issues issues;       // specific issues
    public CrawlNode startNode; // node graph

  public CrawlResults(CrawlConfig c,Stats s, Issues i, CrawlNode n)
  {
    config=c; stats = s; issues=i; startNode=n;
  }





}
