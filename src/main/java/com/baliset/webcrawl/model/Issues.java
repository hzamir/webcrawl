package com.baliset.webcrawl.model;

import java.util.*;

public class Issues
{
  private Map<Reason, Integer> notFollowedReasons;

  public Issues()
  {
    notFollowedReasons = new HashMap<>();
  }

  public Map<Reason, Integer> getNotFollowedReasons()
  {
    return notFollowedReasons;
  }

  public Reason addIssue(Reason r)
  {
    Integer count = notFollowedReasons.get(r);

    if (count == null) {
      count = 1;
    }
    notFollowedReasons.put(r, count + 1);
    return r;
  }


  public void printReasons()
  {
    if (notFollowedReasons.size() == 0)
      return;
    System.err.println("Issues Encountered:");
    notFollowedReasons.forEach((k, v) -> System.err.printf("%s: %d\n", k.name(), v));
  }

}
