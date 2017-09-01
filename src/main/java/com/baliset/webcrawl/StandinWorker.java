package com.baliset.webcrawl;

import java.time.*;

// this pretends to do something during the alloted time, can enhance for progress updates
public class StandinWorker
{
  private long startTime;
  private boolean stop;
  private CrawlConfig config;

  public StandinWorker(CrawlConfig config)
  {
    this.config = config;
  }

  private static String durationFormat(Duration duration)
  {
    long hours = duration.toHours();
    int minutes = (int) ((duration.getSeconds() % (60 * 60)) / 60);
    int seconds = (int) (duration.getSeconds() % 60);
    return String.format("%d:%d:%d", hours,minutes,seconds);
  }

  public void start()
  {
    startTime = System.currentTimeMillis();
    Instant startInstant = Instant.now();

    int i = 0;
    while (shouldContinue()) {
      try {
        Thread.sleep(1000);
        System.out.print("*");
        if(++i % 60 == 0)
          System.out.println("+");

      } catch (InterruptedException e) {
        stop = true;
        e.printStackTrace();
      }
    }

    String elapsed = durationFormat(Duration.between(startInstant, Instant.now()));
    System.out.println("\nRan for "+ elapsed);

  }

  private boolean shouldContinue()
  {
    if (stop)
      return false;

    long deadline = startTime + (config.getMinutesLimit() * 60_000);
    return (System.currentTimeMillis() < deadline);
  }


  public void stop()
  {
    stop = true;
  }
}
