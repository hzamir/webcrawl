package com.baliset.webcrawl;

import de.felixroske.jfxsupport.*;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.*;

@FXMLController
public class CrawlController
{
  private CrawlConfig config;
  private EnumsConfig enumsConfig;

  @Autowired
  public CrawlController(CrawlConfig config, EnumsConfig enumsConfig)
  {
    this.config = config;
    this.enumsConfig = enumsConfig;
  }

  @FXML
  private TextField initialUrl;
  @FXML
  private TextField initialDomain;
  @FXML
  private ChoiceBox<String> outputFormat;      // xml, yaml, or json

  @FXML
  private CheckBox stayInDomain;     // stay in domain (don't go to another domain)
  @FXML
  private CheckBox allowSubdomains;  // if inside domain are subdomains ok?
  @FXML
  private Slider minutesLimit;     // max time
  @FXML
  private Slider depthLimit;       // max depth

  @FXML
  private ComboBox<String> userAgent;      // xml, yaml, or json


  @FXML
  private TextField minutesText;
  @FXML
  private TextField depthText;

  @FXML
  private Button run;

  @FXML
  private Label progressLabel;
  @FXML
  private ProgressBar progressBar;

  private Crawl worker;
  private Task mytask;

  private void populate()
  {
    initialUrl.textProperty().set(config.getInitialUrl());
    initialDomain.textProperty().set(config.getInitialDomain());

    stayInDomain.selectedProperty().set(config.isStayInDomain());
    allowSubdomains.selectedProperty().set(config.isAllowSubdomains());

    outputFormat.getItems().addAll(enumsConfig.getOutputFormats());

    outputFormat.setValue(config.getOutputFormat());
    int mins = config.getMinutesLimit();
    minutesLimit.valueProperty().set(mins);
    minutesText.textProperty().set("" + mins);

    depthLimit.valueProperty().set(config.getDepthLimit());
    depthText.textProperty().set("" + config.getDepthLimit());

    minutesLimit.setMin(1);
    minutesLimit.setMax(enumsConfig.getMaxMinutes());

    depthLimit.setMin(1);
    depthLimit.setMax(enumsConfig.getMaxDepth());

    userAgent.getItems().addAll(enumsConfig.getUseragents());
    userAgent.setValue(config.getUseragent());

    progressBar.setProgress(0.5);
  }


  private void p()
  {
    System.out.println(config.toString());
  }

  private void crawlStart()
  {
    run.textProperty().set("Stop");
    worker = new Crawl(config);
    Thread th = new Thread(worker::crawl, "CrawlThread");
    th.start();

    progressBar.progressProperty().unbind();
    progressBar.setProgress(0);
    progressLabel.setVisible(true);
    progressBar.setVisible(true);

    mytask =  ProgressUtil.createProgressTask(100, config.getMinutesLimit()*60_000);
    progressBar.progressProperty().bind(mytask.progressProperty());

    mytask.messageProperty().addListener((observable, oldValue, newValue) -> {
      if(!th.isAlive()) {
        System.out.println("donem cancelling");
        mytask.cancel();
        progressBar.progressProperty().unbind();
        progressBar.setProgress(1.0);
        crawlCompleted();
      }
    });

    new Thread(mytask).start();

  }

  private void crawlCompleted()
  {
    progressBar.progressProperty().unbind();
//    progressLabel.setVisible(false);
//    progressBar.setVisible(false);
    run.textProperty().set("Start");
  }

  private void crawlStop()
  {
    crawlCompleted();
    worker.stop();
    worker = null;
  }


  private void bindings()
  {
    initialUrl.textProperty().addListener((observable, ov, v) -> {
      config.setInitialUrl(v);
      initialDomain.textProperty().set(config.getInitialDomain());    // it is refreshed
    });

    stayInDomain.selectedProperty().addListener((observable, ov, v) -> {
      config.setStayInDomain(v);
      allowSubdomains.disableProperty().setValue(!v);
    });

    allowSubdomains.selectedProperty().addListener((observable, ov, v) -> config.setAllowSubdomains(v));

    outputFormat.getSelectionModel().selectedItemProperty().addListener((observable, ov, v) -> config.setOutputFormat(v));

    depthLimit.valueProperty().addListener((observable, ov, v) -> {
      config.setDepthLimit(v.intValue());
      depthText.textProperty().set(""+v.intValue());
    });

    minutesLimit.valueProperty().addListener((observable, ov, v) -> {
      int val = v.intValue();
      config.setMinutesLimit(val);
      minutesText.textProperty().set(""+val);
    });

    userAgent.getSelectionModel().selectedItemProperty().addListener((observable, ov, v) -> config.setUseragent(v));

    run.setOnAction((event) -> {
      if(worker == null) {
        crawlStart();
      } else {
        crawlStop();
      }
    });
  }

  public void initialize()
  {
    populate();   // show the initial default values of the controls
    bindings();   // bind the controls to the configuration settings
  }


}
