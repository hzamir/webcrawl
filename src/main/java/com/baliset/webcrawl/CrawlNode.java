package com.baliset.webcrawl;

import java.util.*;

public class CrawlNode
{
  private String url;         // what is the url
  private boolean followed;   // was it followed
  private int status;
  private Reason reason;
  private Set<CrawlNode> children;

  private static ArrayList<String> indents ;

  static {

    indents = new ArrayList<>(500); // todo just arbitrary is good enough for now fix
    indents.add(""); // zero index preinitialized

  }

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



  private String indent(int level)
  {
    if(level >= indents.size()) { // this actually only be equal and never greater so == would equally work
      indents.add(new String(new char[level*2]).replace('\0', ' '));
    }
    return indents.get(level);
  }

  private void p(int  level,String s)
  {
    System.out.print("\n"+indent(level)+s);
  }

  public void print(int level)
  {

    p(level,"{ \"node\" : \""+url+"\"");
    System.out.print(", \"status\" : " + status);
    System.out.print(", \"reason\" : \"" + reason.name() + "\"");
    

    if(children != null) {
      System.out.print(", \"refs\" : [");
      int i = 0;

      for (CrawlNode n : children) {
        n.print(level + 1);
        if(++i < children.size())
          System.out.print(",");
      }
      System.out.print("]");
    }
    System.out.print("}");

  }

  public void setReason(Reason r)    { reason = r; }
  public void setStatus(int s)       { status = s; }
  public void setFollowed(boolean v) { followed = v; }
}
