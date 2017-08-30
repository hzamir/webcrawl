package com.baliset.webcrawl;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.util.*;

public class CrawlNode
{
  private String url;         // what is the url
  private boolean followed;   // was it followed
  private int status;
  private Reason reason;

  public String getUrl()              { return url; }
  public boolean isFollowed()         { return followed; }
  public int getStatus()              { return status; }
  public Reason getReason()           { return reason; }
  public Set<CrawlNode> getChildren() { return children; }

  private Set<CrawlNode> children;


  public CrawlNode(String url)
  {
    this.url = url;
  }

  public void addChild(CrawlNode node)
  {
    if(children == null)
      children = new HashSet<>();
    children.add(node);
  }


  public void setReason(Reason r)    { reason = r; }
  public void setStatus(int s)       { status = s; }
  public void setFollowed(boolean v) { followed = v; }


  

}
