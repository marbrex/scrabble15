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
import scrabble.model.Profile;

/**
 * <h1>The Main Controller linked with "MainPage.fxml" file.</h1>
 *
 * @author Eldar Kasmamytov
 * @author Sergen Keskincelik
 */
public class MainPageController implements Initializable {

  @FXML
  private Label idLabel;

  @FXML
  private VBox buttonsBlock;

  @FXML
  private JFXButton playBtn;

  @FXML
  private JFXButton settingsBtn;

  @FXML
  private JFXButton changeProfileBtn;
  
  @FXML JFXButton profileBtn;

  @FXML
  private JFXButton exitAppBtn;

  @FXML
  private BorderPane root;

  Collection<JFXButton> mainMenuButtons;

  private static boolean checkNetworkMode;

  private HumanPlayer player;

  /*
   * public void setSize(Stage stage) {
   * this.soundButton.fitHeightProperty().bind(stage.heightProperty());
   * this.soundButton.fitWidthProperty().bind(stage.widthProperty()); }
   */

  public static void setNetworkMode(boolean check) {
    checkNetworkMode = check;
  }

  public static boolean isNetwork() {
    return checkNetworkMode;
  }

  public void changeScene(String resource, String style, Event event) {
    try {
      System.out.println(resource);
      Parent root = FXMLLoader.load(getClass().getResource(resource));
     /*
      Button btn = ((Button) event.getSource());
      Stage stage = (Stage) btn.getScene().getWindow();
      Scene scene = new Scene(root, this.root.getScene().getWidth(),
          this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource(style).toExternalForm());
      */
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }

  public Collection<JFXButton> getPlayButtons() {
    Collection<JFXButton> buttons = new ArrayList<JFXButton>();
    JFXButton singlePlayerBtn = new JFXButton("Singleplayer");
    singlePlayerBtn.setOnMouseClicked(event -> {
      setNetworkMode(false);
      changeScene("fxml/LoadingScreen.fxml", "css/mainMenu.css", event);
    });
    singlePlayerBtn.getStyleClass().add("button");
    buttons.add(singlePlayerBtn);

    JFXButton multiPlayerBtn = new JFXButton("Multiplayer");
    multiPlayerBtn.getStyleClass().add("button");
    multiPlayerBtn.setOnMouseClicked(event -> {
      setNetworkMode(true);
      changeScene("fxml/Menu.fxml", "css/style.css", event);
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
//    Database.fillPlayerTable(playersData);
//    HumanPlayer player = DBInformation.loadProfile(0);
    player = Profile.getPlayer();
    this.idLabel.setText("Welcome back, " + Profile.getPlayer().getName());
    //	setSize();

    // Save main menu buttons into a collection
    mainMenuButtons = new ArrayList<JFXButton>();
    for (int i = 0; i < buttonsBlock.getChildren().size(); i++) {
      mainMenuButtons.add((JFXButton) buttonsBlock.getChildren().get(i));
    }

    playBtn.setOnMouseClicked((event -> {
      buttonsBlock.getChildren().clear();
      buttonsBlock.getChildren().setAll(getPlayButtons());
    }));
    
    profileBtn.setOnMouseClicked(event -> {
      changeScene("fxml/Statistics.fxml", "css/style.css", event);
    });
    
    settingsBtn.setOnMouseClicked(event -> {
      changeScene("fxml/Settings.fxml", "css/settings.css", event);
    });

    changeProfileBtn.setOnMouseClicked(event -> {
      Profile.setPlayer(null);
      changeScene("fxml/ChooseProfileScene.fxml", "css/changeProfile.css", event);
    });

    exitAppBtn.setOnMouseClicked(event -> {
      Database.disconnectDB();
      Stage stage = (Stage) exitAppBtn.getScene().getWindow();
      stage.close();
    });
  }

}