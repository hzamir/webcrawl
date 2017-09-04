package com.baliset.util;

import java.io.*;

public class OsXCapabilities extends NoCapabilities
{

  @Override public String revealPathCapability()    {return "Reveal in Finder"; }   // returns OS specific name of capability if it exists
  @Override public String openInBrowserCapability() { return "Open in Browser";}


  @Override public void revealPath(String path)
  {
    try {
      new ProcessBuilder("open", "-R", path).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void openInBrowser(String path)
  {
    try {
      new ProcessBuilder("open", "-a", "Safari", path).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }


}
