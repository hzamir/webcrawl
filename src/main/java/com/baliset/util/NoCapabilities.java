package com.baliset.util;

/**
 *  Perform tasks in an operating system specific way
 *  methods labeled XXXCapability return a string describing the capability also in OS specific terms
 *  (e.g. "Reveal in Finder" vs "Show in Explorer") they return null where not implemented for that OS
 */
public class NoCapabilities implements Capabilities
{
  public String revealPathCapability()   {return null; }   // returns OS specific name of capability if it exists
  public void   revealPath(String path)  {}                //

  public String openInBrowserCapability() { return null;}
  public void openInBrowser(String path) {}
}
