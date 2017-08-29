package com.baliset.webcrawl;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.stereotype.*;

@Component
public class Runner implements CommandLineRunner
{

  @Autowired
  private SwingLabMainFrame swingLabMainFrame;
  private static final Logger logger = LoggerFactory.getLogger(Runner.class);


  @Override
  public void run(String... args) throws Exception {

    for(String s:args)
    {
      logger.info("arg:" + s);
    }

    java.awt.EventQueue.invokeLater(() -> swingLabMainFrame.setVisible(true));   // must make visible via EventQueue
  }

}