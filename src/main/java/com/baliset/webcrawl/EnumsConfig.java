package com.baliset.webcrawl;

import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

import java.util.*;

@Configuration
@ConfigurationProperties(prefix = "webcrawl.enums")
public class EnumsConfig
{
  private List<String> useragents = new ArrayList<>();
  private List<String> outputFormats = new ArrayList<>();

  private int maxMinutes;
  private int maxDepth;


  public List<String> getUseragents() {
    return useragents;
  }
  public List<String> getOutputFormats() {
    return outputFormats;
  }


  public int getMaxMinutes()       { return maxMinutes;}
  public int getMaxDepth()         { return maxDepth;  }
  public void setMaxMinutes(int v) { maxMinutes = v;   }
  public void setMaxDepth(int v)   { maxDepth = v;     }
}

