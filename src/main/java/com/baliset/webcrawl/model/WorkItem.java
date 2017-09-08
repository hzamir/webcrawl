package com.baliset.webcrawl.model;

public class WorkItem
{
  public  WorkItem(CrawlNode node, String attrKey, int depth)
  {
    this.node = node;
    this.attrKey = attrKey;
    this.depth = depth;
  }

  public CrawlNode  getNode()    { return node;    }
  public String     getAttrKey() { return attrKey; }
  public int        getDepth()   { return depth;   }

  private CrawlNode node;
  private String attrKey;
  private int depth;
}
