package com.baliset.util;

public interface Capabilities
{
  String revealPathCapability();
  void   revealPath(String path);

  String openInBrowserCapability();
  void openInBrowser(String path);
}
