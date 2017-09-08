package com.baliset.webcrawl.model;

import com.fasterxml.jackson.dataformat.xml.annotation.*;

import java.net.*;
import java.time.*;
import java.util.*;

public class Stats
{
  // serialization properties
  @JacksonXmlProperty(isAttribute = true)
  public TerminationCode  getTerminationCode() { return terminationCode; }

  @JacksonXmlProperty(isAttribute = true)
  public String           getElapsedTime()     { return elapsedTime;     }
  public int              getTotal()           { return total;           }
  public int              getUnique()          { return unique;          }
  public int              getFetchedOk()       { return fetchedOk;       }
  public int              getFetchFailed()     { return fetchFailed;     }
  public int              getParsed()          { return parsed;          }
  public List<DomainHits> getDomainStats()     { return domainStats;     }

  // update methods
  public void stoppedByUser()          { terminationCode = TerminationCode.StoppedByUser; }
  public void timeExpired()            { terminationCode = TerminationCode.AllocatedTimeExpired;  }
  public void incrementTotal()         { ++total;       }
  public void incrementFetched()       { ++fetchedOk;   }
  public void incrementParsed()        { ++parsed;      }
  public void incrementFetchFailures() { ++fetchFailed; }

  public Stats()
  {
    terminationCode = TerminationCode.CrawlCompleted;
    mapstats = new HashMap<>();
  }


  public long start()
  {
    startInstant = Instant.now();
    return startInstant.toEpochMilli();
  }
  public void stop()
  {
    elapsedTime = durationFormat(Duration.between(startInstant, Instant.now()));

    List<DomainHits> hits = new ArrayList<>(mapstats.size());

    for(Map.Entry<String, Integer> entry: mapstats.entrySet())
    {
      hits.add(new DomainHits(entry.getKey(), entry.getValue()));
    }
    hits.sort((a,b)->b.hits-a.hits);
    domainStats = hits;
  }

  public void update(String s)
  {
      ++unique;
      try {
        URL    url = new URL(s);
        String h   = url.getHost();
        mapstats.merge(h, 1, (a, b) -> a + b);
      } catch (MalformedURLException ignored) {
      }
  }

  private String durationFormat(Duration duration)
  {
    long hours = duration.toHours();
    int minutes = (int) ((duration.getSeconds() % (60 * 60)) / 60);
    int seconds = (int) (duration.getSeconds() % 60);
    return String.format("%d:%d:%d", hours,minutes,seconds);
  }


  private TerminationCode terminationCode;

  private String elapsedTime;

  private int total;
  private int unique;
  private int fetchedOk;
  private int fetchFailed;
  private int parsed;

  private List<DomainHits> domainStats;

  private Instant startInstant;
  private Map<String, Integer> mapstats;
}
