package com.baliset.webcrawl;

import com.fasterxml.jackson.dataformat.xml.annotation.*;

import java.util.*;

public class CrawlNode
{
  private String url;         // what is the url

  @JacksonXmlProperty(isAttribute = true)
  private boolean followed;   // was it followed
  @JacksonXmlProperty(isAttribute = true)
  private int status;
  @JacksonXmlProperty(isAttribute = true)
  private Reason reason;

  public String getUrl()              { return url; }
  public boolean isFollowed()         { return followed; }
  public int getStatus()              { return status; }
  public Reason getReason()           { return reason; }
  public Set<CrawlNode> getChildren() { return children; }

  @JacksonXmlProperty(localName = "node")
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
