package com.baliset.webcrawl;

import java.util.*;

public class Issues
{
    private Map<Reason,Integer> issues;

    public Issues()
    {
      issues = new HashMap<>();
    }

    public Reason addIssue(Reason r)
    {
      Integer count = issues.get(r);

      if(count == null)
      {
        count = 1;
      }
      issues.put(r, count + 1);
      return r;
    }


    public void printReasons()
    {
        if(issues.size()==0)
          return;
        System.err.println("Issues Encountered:");
        issues.forEach((k,v)->System.err.printf("%s: %d\n", k.name(), v) );
    }

}
