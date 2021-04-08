package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;

public class MainpageController implements Initializable {

  @FXML
  private ImageView backgroundPane;

  @FXML
  private Label switchsoundLabel;

  @FXML
  private Label idLabel;

  @FXML
  private JFXButton settingsButton;

  @FXML
  private JFXButton profileButton;

  @FXML
  private JFXButton exitButton;

  @FXML
  private JFXButton soundButton;

  @FXML
  private ProgressBar progressBar;

  @FXML
  private ImageView soundImage;

  @FXML
  private VBox buttonsBlock;

  @FXML
  private JFXButton playButton;

  @FXML
  private JFXButton settingsBtn;

  @FXML
  private BorderPane root;

  Collection<JFXButton> mainMenuButtons;

  /*
   * public void setSize(Stage stage) {
   * this.soundButton.fitHeightProperty().bind(stage.heightProperty());
   * this.soundButton.fitWidthProperty().bind(stage.widthProperty()); }
   */

  public void pressingButton(ActionEvent event) {
    switch (((Button) event.getSource()).getText()) {
      case "settingsButton":
        break;
      case "Play Scrabble":
        //		Main.setNetwork(false);
        try {
          this.changeScene("fxml/LoadingScreen.fxml", "css/mainMenu.css", event);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
      case "Play Network Game":
        //		Main.setNetwork(true);
        try {
          this.changeScene("fxml/LoadingScreen.fxml", "css/mainMenu.css", event);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
      case "profileButton":
        try {
          this.changeScene("ProfileScreen.fxml", "css/mainMenu.css", event);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        break;
      case "playTutorial":
        break;
      case "exitButton":
        break;
      default:
        break;
    }
  }

  public void increaseValue() {
    double progress = 0;
    for (int i = 0; i < 100; i++) {
      if (i % 25 == 0) {
        progress += 0.25;
        progressBar.setProgress(progress);
      }
    }
  }

  public void changeScene(String resource, String style, Event event) throws IOException {
    System.out.println(resource);
    Parent root = FXMLLoader.load(getClass().getResource(resource));
    Button btn = ((Button) event.getSource());
    Stage stage = (Stage) btn.getScene().getWindow();
    Scene scene = new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
    scene.getStylesheets().add(getClass().getResource(style).toExternalForm());
    stage.setScene(scene);
  }

  public Collection<JFXButton> getPlayButtons() {
    Collection<JFXButton> buttons = new ArrayList<JFXButton>();
    JFXButton singlePlayerBtn = new JFXButton("Singleplayer");
    singlePlayerBtn.setOnMouseClicked(event -> {
      try {
        changeScene("fxml/LoadingScreen.fxml", "css/mainMenu.css", event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    singlePlayerBtn.getStyleClass().add("button");
    buttons.add(singlePlayerBtn);

    JFXButton multiPlayerBtn = new JFXButton("Multiplayer");
    multiPlayerBtn.getStyleClass().add("button");
    multiPlayerBtn.setOnMouseClicked(event -> {
      try {
        changeScene("fxml/Menu.fxml", "css/style.css", event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    buttons.add(multiPlayerBtn);

    JFXButton tutorialBtn = new JFXButton("Tutorial");
    tutorialBtn.getStyleClass().add("button");
    buttons.add(tutorialBtn);

    JFXButton backBtn = new JFXButton("Back");
    backBtn.getStyleClass().add("button");
    backBtn.setOnMouseClicked((event -> {
      buttonsBlock.getChildren().clear();
      buttonsBlock.getChildren().setAll(mainMenuButtons);
    }));
    buttons.add(backBtn);

    return buttons;
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    Database.connectToDB();
    Database.createTables();

    ArrayList<String> playersData = new ArrayList<String>();
    playersData.add("1");
    playersData.add("John");
    playersData.add("3");
    playersData.add("1");
    playersData.add("3");

//    Database.fillPlayerTable(playersData);
    HumanPlayer player = DBInformation.getPlayerProfile();
    this.idLabel.setText("Welcome back, " + player.getName());
    Database.disconnectDB();
    //	setSize();

    // Save main menu buttons into a collection
    mainMenuButtons = new ArrayList<JFXButton>();
    for (int i = 0; i < buttonsBlock.getChildren().size(); i++) {
      mainMenuButtons.add((JFXButton) buttonsBlock.getChildren().get(i));
    }

    playButton.setOnMouseClicked((event -> {
      buttonsBlock.getChildren().clear();
      buttonsBlock.getChildren().setAll(getPlayButtons());
    }));

    settingsBtn.setOnMouseClicked(event -> {
      try {
        changeScene("fxml/Settings.fxml", "css/settings.css", event);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

}