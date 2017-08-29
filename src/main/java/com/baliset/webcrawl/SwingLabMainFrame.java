package com.baliset.webcrawl;


import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;


@Component
public class SwingLabMainFrame extends JFrame
{
  private static final Logger logger = LoggerFactory.getLogger(SwingLabMainFrame.class);

  private final CrawlConfig config;
  private final String kDepthLimitPrefix = "Maximum Depth: ";
  private final String kMaxMinutesPrefix = "Maximum Minutes: ";



  private void setup()
  {

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    int kWidth = 400;
    int kHeight = 400;
    setSize(kWidth, kHeight);


    final JCheckBox checkboxStayInDomain = new JCheckBox("Stay In Domain", config.isStayInDomain());
    final JCheckBox checkboxFollowSubdomains = new JCheckBox("Follow Subdomains", config.isAllowSubdomains());

    final JSlider sliderDepth = new JSlider(SwingConstants.HORIZONTAL, 1, 100, config.getDepthLimit());  // todo externalize maximums
    final JSlider sliderMinutes = new JSlider(SwingConstants.HORIZONTAL, 1, 100, config.getMinutesLimit());

    final JLabel labelMinutes = new JLabel(kMaxMinutesPrefix + config.getMinutesLimit());
    final JLabel labelDepth = new JLabel(kDepthLimitPrefix + config.getDepthLimit());

    final JLabel labelUseragent = new JLabel(config.getUseragent());
    final JLabel labeStartUrl = new JLabel(config.getInitialUrl());
    final JLabel labelStartDomain =  new JLabel(config.getInitialDomain());


    sliderMinutes.setEnabled(true);
    sliderDepth.setEnabled(true);

    ChangeListener sliderDepthListener = event -> {
      JSlider slider = (JSlider) event.getSource();

      int v = slider.getValue();
      config.setDepthLimit(v);
      labelDepth.setText(kDepthLimitPrefix + v);
    };

    ChangeListener sliderMinutesListener = event -> {
      JSlider slider = (JSlider) event.getSource();

      int v = slider.getValue();
      config.setMinutesLimit(v);
      labelMinutes.setText(kMaxMinutesPrefix + v);
    };

    
    ActionListener stayInDomainListener = actionEvent -> {
      AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
      boolean v = abstractButton.getModel().isSelected();
      checkboxFollowSubdomains.setEnabled(v);
      config.setStayInDomain(v);
    };

    ActionListener depthActionListener = actionEvent -> {
      AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
      config.setAllowSubdomains(abstractButton.getModel().isSelected());
    };


    sliderDepth.addChangeListener(sliderDepthListener);
    sliderMinutes.addChangeListener(sliderMinutesListener);

    checkboxStayInDomain.addActionListener(stayInDomainListener);
    checkboxFollowSubdomains.addActionListener(depthActionListener);

    JPanel optionsPanel = new JPanel();
    optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

    Border optionsBorder = BorderFactory.createTitledBorder("Options");
    Border buttonsBorder = BorderFactory.createTitledBorder("Actions");

    optionsPanel.setBorder(optionsBorder);
    // optionsPanel.setPreferredSize(new Dimension(300,120)) ;

    optionsPanel.add(labeStartUrl);
    optionsPanel.add(labelStartDomain);
    optionsPanel.add(labelUseragent);
    optionsPanel.add(labelDepth);
    optionsPanel.add(sliderDepth);


    optionsPanel.add(checkboxStayInDomain);
    optionsPanel.add(checkboxFollowSubdomains);
    optionsPanel.add(labelMinutes);

    optionsPanel.add(sliderMinutes);     // number of sectors

    JPanel execPanel = new JPanel();
    execPanel.setBorder(buttonsBorder);
    execPanel.setPreferredSize(new Dimension(400, 100));
    JButton launch = createLaunchButton();
    execPanel.add(launch);

    Container contentPane = getContentPane();
    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
    contentPane.add(optionsPanel);
    contentPane.add(execPanel);

    pack();
    // setMinimumSize(new Dimension(200,200));
    setLocationByPlatform(true);
    setVisible(true);


  }

  @Autowired public SwingLabMainFrame(CrawlConfig crawlConfig)
  {
    //----- very basic Swing App initialization -----
    super("WebCrawl");

    this.config = crawlConfig;

    setup();

  }


  private JButton createLaunchButton()
  {
    SwingLabAction sa = SwingLabAction.Launch;

    AbstractAction action = new AbstractAction(sa.getLabel())
    {
      public void actionPerformed(ActionEvent e)
      {
        Crawl crawl = new Crawl(config);
        crawl.crawl();

      }

    };

    // assignAction(KeyEvent.VK_A, 0, sa, appendItemAction);

    return new JButton(action);
  }





}
