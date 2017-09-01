package com.baliset.webcrawl;

import de.felixroske.jfxsupport.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class Main extends AbstractJavaFxApplicationSupport
{
  
  public static void main(String[] args) { launchApp(Main.class, CrawlView.class, new CrawlSplash(), args); }

}