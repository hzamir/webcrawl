package com.baliset.util;

import java.io.*;

public class WindowsCapabilities extends NoCapabilities
{

  @Override public String revealPathCapability()
  {
    return "Show in Explorer";
  }
  @Override public String openInBrowserCapability()
  {
    return "Open in Browser";
  }


  @Override public void revealPath(String path)
  {
    try {
      new ProcessBuilder("explorer", "/select,"+path).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void openInBrowser(String path)
  {
    try {
      new ProcessBuilder("explorer", path).start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
