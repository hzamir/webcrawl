package com.baliset.webcrawl;

import javax.swing.*;
import javax.swing.border.*;

public class GuiUtil
{

  public static JPanel createPanel(String name, int layout)
  {
    JPanel panel = new JPanel();
    if(layout >= 0)
      panel.setLayout(new BoxLayout(panel, layout));

    Border border = BorderFactory.createTitledBorder(name);
    panel.setBorder(border);
    return panel;
  }

}
