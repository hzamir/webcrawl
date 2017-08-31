package com.baliset.webcrawl.model;

import com.fasterxml.jackson.dataformat.xml.annotation.*;

public class Stats
{
  @JacksonXmlProperty(isAttribute = true)
  public TerminationCode terminationCode;

  @JacksonXmlProperty(isAttribute = true)
  public String elapsedTime;

  public int total;
  public int unique;
  public int fetchedOk;
  public int fetchFailed;
  public int parsed;
}
