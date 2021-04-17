package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;

/**
 * scrabble.ChooseProfileController class to select or create a profile
 *
 * @author Sergen Keskincelik
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

  private List<Circle> circles;

  private static HumanPlayer player;

  private List<HumanPlayer> players;

  private int profilesize;

  @FXML
  private BorderPane root;

  /** Returns the player object of the chosen profile */
  public static HumanPlayer getPlayer() {
    return player;
  }

  /** Changing the profile of an Player */
  public void changeProfile(Event event) {
    for (int i = 0; i < circles.size(); i++) {
      if (((Circle) event.getSource()) == circles.get(i)) {
        circles.get(i).setStroke(Color.web("#000000"));
        if (!labels.get(i).getTextFill().equals(Color.web("FF0000", 0.8))) {
          player = players.get(i);
          System.out.println(player.getName());
          System.out.println(player.getImage());
        } else {
          player = null;
        }
      } else {
        circles.get(i).setStroke(Color.web("GREY"));
      }
    }
  }


  /** Changing the scene to the Mainmenu */
  public void changeScene(Event event) {
    Parent root;
    try {
      if (player != null) {
        root = FXMLLoader.load(getClass().getResource("fxml/MainPage.fxml"));
        Button btn = ((Button) event.getSource());
        Stage stage = (Stage) btn.getScene().getWindow();
        Scene scene =
            new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
        scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
        stage.setScene(scene);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Changing the scene to the Registration Scene */
  public void changeToRegistration(Event event) {
    Pane newRoot;
    if (profilesize < 4) {
      try {
        System.out.println(getClass().getResource("fxml/createProfile.fxml"));
        newRoot = FXMLLoader.load(getClass().getResource("fxml/createProfile.fxml"));
        Button btn = ((Button) event.getSource());
        Stage stage = (Stage) btn.getScene().getWindow();
        Scene scene =
            new Scene(newRoot, this.root.getScene().getWidth(), this.root.getScene().getHeight());
        scene.getStylesheets()
            .add(getClass().getResource("css/createProfile.css").toExternalForm());
        stage.setScene(scene);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }


  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    Database.connectToDB();
//  Database.dropAllTables();
    Database.createTables();
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
        circles.get(i).setFill(new ImagePattern((new Image(
            "file:" + System.getProperty("user.dir") + "\\resources\\scrabble\\img\\"
                + players.get(i).getImage()))));
      } else {
        labels.get(i).setText("Empty");
        labels.get(i).setTextFill(Color.web("#FF0000", 0.8));
      }
    }
    profilesize = DBInformation.getProfileSize();
    Database.disconnectDB();

  }

}
