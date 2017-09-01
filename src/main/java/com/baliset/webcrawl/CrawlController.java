package com.baliset.webcrawl;

import de.felixroske.jfxsupport.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.*;

@FXMLController
public class CrawlController
{
  private CrawlConfig config;
  private EnumsConfig enumsConfig;

  @Autowired public CrawlController(CrawlConfig config, EnumsConfig enumsConfig)
  {
    this.config = config;
    this.enumsConfig = enumsConfig;
  }

  @FXML private TextField initialUrl;
  @FXML private TextField initialDomain;
  @FXML private ChoiceBox<String> outputFormat;      // xml, yaml, or json
  @FXML private TextField outputPath;


  @FXML private CheckBox stayInDomain;     // stay in domain (don't go to another domain)
  @FXML private CheckBox allowSubdomains;  // if inside domain are subdomains ok?
  @FXML private Slider     minutesLimit;     // max time
  @FXML private Slider     depthLimit;       // max depth

  @FXML private ComboBox<String> userAgent;      // xml, yaml, or json


  @FXML private TextField minutesText;
  @FXML private TextField depthText;

  @FXML private Button run;

  private Crawl worker;



  /*


 FileChooser fileChooser = new FileChooser();
 fileChooser.setTitle("Open Resource File");
 fileChooser.getExtensionFilters().addAll(
         new ExtensionFilter("Text Files", "*.txt"),
         new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
         new ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
         new ExtensionFilter("All Files", "*.*"));
 File selectedFile = fileChooser.showOpenDialog(mainStage);
 if (selectedFile != null) {
    mainStage.display(selectedFile);
 }
   */
  private void populate()
  {
    initialUrl.textProperty().set(config.getInitialUrl());
    initialDomain.textProperty().set(config.getInitialDomain());

    stayInDomain.selectedProperty().set(config.isStayInDomain());
    allowSubdomains.selectedProperty().set(config.isAllowSubdomains());

    outputFormat.getItems().addAll(enumsConfig.getOutputFormats());

    String absoluteOutputPath = System.getProperty("user.home")+config.getOutputPath();
    config.setOutputPath(absoluteOutputPath);
    outputPath.textProperty().set(absoluteOutputPath); // prior bug fixed in java8 (see bugs.java.com/view_bug.do?bug_id=6519127)

    outputFormat.setValue(config.getOutputFormat());
    int mins =    config.getMinutesLimit();
    minutesLimit.valueProperty().set(mins);
    minutesText.textProperty().set(""+mins);

    depthLimit.valueProperty().set(config.getDepthLimit());
    depthText.textProperty().set(""+config.getDepthLimit());

    minutesLimit.setMin(1);
    minutesLimit.setMax(enumsConfig.getMaxMinutes());

    depthLimit.setMin(1);
    depthLimit.setMax(enumsConfig.getMaxDepth());
    
    userAgent.getItems().addAll(enumsConfig.getUseragents());
    userAgent.setValue(config.getUseragent());
  }


  private void p()
  {
    System.out.println(config.toString());
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

    outputPath.textProperty().addListener((observable, ov, v) -> {
      config.setOutputPath(v);
    });

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
        run.textProperty().set("Stop");
        worker = new Crawl(config);
        new Thread(worker::crawl, "CrawlThread").start();
      } else {
        worker.stop();
        worker = null;
        run.textProperty().set("Start");
      }
    });
  }

  public void initialize()
  {
    populate();   // show the initial default values of the controls
    bindings();   // bind the controls to the configuration settings
  }


}
