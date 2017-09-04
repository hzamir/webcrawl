package com.baliset.webcrawl;

import com.baliset.util.*;
import de.felixroske.jfxsupport.*;
import javafx.concurrent.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;

import java.text.*;
import java.util.*;


@FXMLController
public class CrawlController
{
  private static final Logger logger = LoggerFactory.getLogger(CrawlController.class);

  private SimpleDateFormat simpleDateFormat;

  private CrawlConfig config;
  private EnumsConfig enumsConfig;
  private Capabilities capabilities;  // knows how to (and whether it can) do some tasks in OS specific way
  private String derivedFileName;

  @Autowired
  public CrawlController(CrawlConfig config, EnumsConfig enumsConfig, Capabilities capabilities)
  {
    this.simpleDateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");
    this.config = config;
    this.enumsConfig = enumsConfig;
    this.capabilities = capabilities;
  }


  @FXML private TextField initialUrl;
  @FXML private TextField initialDomain;
  @FXML private ChoiceBox<String> outputFormat;      // xml, yaml, or json
  @FXML private TextField outputPath;


  @FXML private CheckBox stayInDomain;     // stay in domain (don't go to another domain)
  @FXML private CheckBox allowSubdomains;  // if inside domain are subdomains ok?
  @FXML private Slider minutesLimit;     // max time
  @FXML private Slider depthLimit;       // max depth

  @FXML private ComboBox<String> userAgent;      // xml, yaml, or json


  @FXML private TextField minutesText;
  @FXML private TextField depthText;

  @FXML private Button run;

  @FXML private Label progressLabel;
  @FXML private ProgressBar progressBar;
  @FXML private ListView<String> recentlySaved;

  private Crawl worker;
  private Task progressTask;


  private String deriveFileName()
  {
    derivedFileName = String.format("%s/%s-%s.%s",
        config.getOutputDir(),
        simpleDateFormat.format(new Date()),
        config.getInitialDomain(),
        config.getOutputFormat()
    );
    return derivedFileName;
  }

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

    String absoluteOutputPath = System.getProperty("user.home")+config.getOutputDir();
    config.setOutputDir(absoluteOutputPath);
    outputPath.textProperty().set(absoluteOutputPath); // prior bug fixed in java8 (see bugs.java.com/view_bug.do?bug_id=6519127)

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
    logger.info(config.toString());
  }

  private void crawlStart()
  {
    run.textProperty().set("Stop");
    worker = new Crawl(config, deriveFileName());
    Thread th = new Thread(worker::crawl, "CrawlThread");
    th.start();

    progressBar.progressProperty().unbind();
    progressBar.setProgress(0);
    progressLabel.setVisible(true);
    progressBar.setVisible(true);

    progressTask =  ProgressUtil.createProgressTask(100, config.getMinutesLimit()*60_000);
    progressBar.progressProperty().bind(progressTask.progressProperty());

    progressTask.messageProperty().addListener((observable, oldValue, newValue) -> {
      if(!th.isAlive()) {
        progressTask.cancel();
        progressBar.progressProperty().unbind();
        progressBar.setProgress(1.0);
        crawlCompleted();
      }
    });

    new Thread(progressTask).start();

  }

  private void crawlCompleted()
  {
    progressBar.progressProperty().unbind();
     progressLabel.setVisible(false);
     progressBar.setVisible(false);
    run.textProperty().set("Start");

    if(worker != null) {
      worker.stop();
      worker = null;

      recentlySaved.getItems().removeAll(derivedFileName);      // only list item with same name once, since file is overwritten
      recentlySaved.getItems().add(0, derivedFileName);
    }
  }
  
  private void crawlStop()
  {
    crawlCompleted();
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

    outputPath.textProperty().addListener((observable, ov, v) -> config.setOutputDir(v));

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

    recentlySaved.getSelectionModel().selectedItemProperty().addListener((observable, ov, v) ->
      {
        if(capabilities.revealPathCapability() != null)
        {
          capabilities.revealPath(v);
        }
      }
    );
  }

  public void initialize()
  {
    populate();   // show the initial default values of the controls
    bindings();   // bind the controls to the configuration settings
  }


}
