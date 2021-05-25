package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scrabble.dbhandler.Database;
import scrabble.model.Profile;

/**
 * The Main Controller linked with "MainPage.fxml" file.
 *
 * @author ekasmamy
 * @author skeskinc
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

  /**
   * Setting boolean var to true, if network-mode is chosen.
   * 
   * @param check Setting var to true or false
   * @author skeskinc
   */
  public static void setNetworkMode(boolean check) {
    checkNetworkMode = check;
  }

  /**
   * Check, if chosen mode is a network-mode.
   * 
   * @return true, if multiplayer has been pressed, else false
   * @author skeskinc
   */
  public static boolean isNetwork() {
    return checkNetworkMode;
  }

  /**
   * Changing scene.
   * 
   * @param resource Getting resource of next scene
   * @param style Setting style-sheet of next scene
   * @param event Handling Event Actions
   * @author ekasmamy
   * @author skeskinc
   */
  public void changeScene(String resource, String style, Event event) {
    try {
      System.out.println(resource);
      Parent root = FXMLLoader.load(getClass().getResource(resource));
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }

  /**
   * Managing Button changes.
   * 
   * @return specific button-collections
   * @author ekasmamy
   */
  public Collection<JFXButton> getPlayButtons() {
    Collection<JFXButton> buttons = new ArrayList<JFXButton>();
    JFXButton singlePlayerBtn = new JFXButton("Singleplayer");
    singlePlayerBtn.setOnMouseClicked(event -> {
      setNetworkMode(false);
      changeScene("/fxml/LoadingScreen.fxml", "/css/mainMenu.css", event);
    });
    singlePlayerBtn.getStyleClass().add("button");
    buttons.add(singlePlayerBtn);

    JFXButton multiPlayerBtn = new JFXButton("Multiplayer");
    multiPlayerBtn.getStyleClass().add("button");
    multiPlayerBtn.setOnMouseClicked(event -> {
      setNetworkMode(true);
      changeScene("/fxml/Menu.fxml", "/css/style.css", event);
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

  /**
   * Handling Scene Change and application exit.
   * 
   * @author ekasmamy
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    this.idLabel.setText("Welcome, " + Profile.getPlayer().getName());

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
      changeScene("/fxml/Statistics.fxml", "/css/style.css", event);
    });
    
    settingsBtn.setOnMouseClicked(event -> {
      changeScene("/fxml/Settings.fxml", "/css/settings.css", event);
    });

    changeProfileBtn.setOnMouseClicked(event -> {
      Profile.setPlayer(null);
      changeScene("/fxml/ChooseProfileScene.fxml", "/css/changeProfile.css", event);
    });

    exitAppBtn.setOnMouseClicked(event -> {
      Database.disconnectDB();
      Stage stage = (Stage) exitAppBtn.getScene().getWindow();
      stage.close();
    });
  }

}