package com.baliset.webcrawl;

import javafx.concurrent.*;

public class ProgressUtil
{

  public static Task createProgressTask(long sleepMillis, long completeInMillis) {
    return new Task() {
      @Override
      protected Object call() throws Exception {
        long startTime = System.currentTimeMillis();
        
        while(true) {
          Thread.sleep(sleepMillis);
          long delta = System.currentTimeMillis() - startTime;
           if(delta >= completeInMillis)
             break;
          updateMessage(""+delta+"/"+completeInMillis);
          updateProgress(delta, completeInMillis);
        }
        return true;
      }
    };
  }



}
