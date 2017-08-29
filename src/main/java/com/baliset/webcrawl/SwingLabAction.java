package com.baliset.webcrawl;


public enum SwingLabAction
{

  Launch("Crawl");


  private String label_;

  SwingLabAction()
  {
    label_ = this.name();
  }

  SwingLabAction(String l)
  {
    label_ = l;
  }

  public String getLabel()
  {
    return label_;
  }

}
