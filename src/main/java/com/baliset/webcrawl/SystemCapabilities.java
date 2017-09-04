package com.baliset.webcrawl;

import com.baliset.util.*;
import org.springframework.stereotype.*;

@Component
public class SystemCapabilities implements Capabilities
{
  private  boolean _isWindows;
  private  boolean _isMac;
  private  String  _osName;

  private Capabilities capabilities;

  public SystemCapabilities()
  {
    _osName = System.getProperty("os.name", "unknown");
    _isWindows = _osName.startsWith("Windows");
    _isMac = _osName.startsWith("Mac") || _osName.startsWith("darwin");

    if(_isWindows)
      capabilities = new WindowsCapabilities();
    else if(_isMac)
      capabilities = new OsXCapabilities();
    else
      capabilities = new NoCapabilities(); // do nothing version
  }


  public  String getOs()      { return _osName;    }
  public  boolean isWindows() { return _isWindows; }
  public  boolean isMac()     { return _isMac;     }


  @Override public  String revealPathCapability()   { return capabilities.revealPathCapability();    }
  @Override public String openInBrowserCapability() { return capabilities.openInBrowserCapability(); }
  @Override public void revealPath(String path)     { capabilities.revealPath(path);                 }
  @Override public void openInBrowser(String path)  { capabilities.openInBrowser(path); }

  }

