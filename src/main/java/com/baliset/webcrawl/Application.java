package com.baliset.webcrawl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.context.*;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.swing.*;


@SpringBootApplication
@EnableScheduling
public class Application
{

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  private static void setupLookAndFeel()
  {
    try {
      // Set cross-platform Java L&F (also called "Metal")
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {
      logger.error("setupLookAndFeel  exception", e);
    }
  }


  public static void main(String[] args)
  {
    setupLookAndFeel();

    ConfigurableApplicationContext ctx =
        new
            SpringApplicationBuilder(Application.class)
            .headless(false)
            .web(false)
            .run(args);
  }


}
