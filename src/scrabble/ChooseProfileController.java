package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import scrabble.dbhandler.DBInformation;
import scrabble.model.HumanPlayer;
import scrabble.model.Profile;

/**
 * scrabble.ChooseProfileController class to select or create a profile.
 *
 * @author skeskinc
 */

public class ChooseProfileController implements Initializable {

  @FXML
  private Circle profileOne;

  @FXML
  private Label profileoneLabel;

  @FXML
  private Circle profileTwo;

  @FXML
  private Label profiletwoLabel;

  @FXML
  private Circle profileThree;

  @FXML
  private Label profilethreeLabel;

  @FXML
  private Circle profileFour;

  @FXML
  private Label profilefourLabel;

  @FXML
  private JFXButton createButton;

  @FXML
  private JFXButton continueButton;

  @FXML
  private List<Label> labels;

  @FXML
  private JFXButton deleteButton;

  private List<Circle> circles;

  private List<HumanPlayer> players;

  private int profilesize;


  @FXML
  private BorderPane root;

  /**
   * Changing the profile of an Player.
   * 
   * @param event Handling ActionEvent
   * @author skeskinc
   */
  public void changeProfile(Event event) {
    for (int i = 0; i < circles.size(); i++) {
      if (((Circle) event.getSource()) == circles.get(i)) {
        circles.get(i).setStroke(Color.web("#000000"));
        if (!labels.get(i).getTextFill().equals(Color.web("FF0000", 0.8))) {
          // player = players.get(i);
          Profile.setPlayer(players.get(i));
        } else {
          // player = null;
          Profile.setPlayer(null);
        }
      } else {
        circles.get(i).setStroke(Color.web("GREY"));
      }
    }
  }

  /**
   * Changing to another Scene regarding the given Resource and Style.
   * 
   * @param resource Choosing Scene for the Application
   * @param style Choosing the Style-Sheet for the Scene
   * @param event Handling Action-Event
   * @author skeskinc
   */
  public void changeScene(String resource, String style, Event event) {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource(resource));
      // Button btn = ((Button) event.getSource());
      // Stage stage = (Stage) btn.getScene().getWindow();
      /*
       * Scene scene = new Scene(root, this.root.getScene().getWidth(),
       * this.root.getScene().getHeight());
       * scene.getStylesheets().add(getClass().getResource(style).toExternalForm());
       * stage.setScene(scene);
       */
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Loading images and handling Button-Events.
   * 
   * @author skeskinc
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    labels = new ArrayList<Label>();
    labels.add(profileoneLabel);
    labels.add(profiletwoLabel);
    labels.add(profilethreeLabel);
    labels.add(profilefourLabel);
    circles = new ArrayList<Circle>();
    circles.add(profileOne);
    circles.add(profileTwo);
    circles.add(profileThree);
    circles.add(profileFour);
    // player = DBInformation.loadProfile(0);
    // profileoneLabel.setText(player.getName());
    players = DBInformation.getPlayerProfiles();
    for (int i = 0; i < labels.size(); i++) {
      if (i <= players.size() - 1) {
        labels.get(i).setText(players.get(i).getName());
        circles.get(i).setFill(new ImagePattern(new Image(
            getClass().getResourceAsStream("/scrabble/img/" + players.get(i).getImage()))));
      } else {
        labels.get(i).setText("Empty");
        labels.get(i).setTextFill(Color.web("#FF0000", 0.8));
      }
    }
    profilesize = DBInformation.getProfileSize();
    Profile.setPlayer(null);

    // Action on Continue-Button
    continueButton.setOnMouseClicked(event -> {
      if (Profile.getPlayer() != null) {
        changeScene("fxml/MainPage.fxml", "css/mainMenu.css", event);
      }
    });

    // Action on Create-Button
    createButton.setOnMouseClicked(event -> {
      if (profilesize < 4) {
        changeScene("fxml/createProfile.fxml", "css/createProfile.css", event);
      }
    });

    // Action on Delete-Button
    deleteButton.setOnMouseClicked(event -> {
      if (Profile.getPlayer() != null) {
        changeScene("fxml/DeleteScene.fxml", "css/createProfile.css", event);
      }
    });
  }
}
