package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scrabble.model.Player;
import scrabble.network.NetworkScreen;

/**
 * scrabble.LoadingController for the LoadingScreen.
 * 
 * @author skeskinc
 */
public class LoadingController implements Initializable {

  @FXML
  public Label hintLabel;

  @FXML
  public ProgressBar progressBar;

  @FXML
  public Circle pingCircle;

  @FXML
  public Label pingLabel;

  @FXML
  public Label modeLabel;

  @FXML
  public Label showPing;

  @FXML
  public BorderPane pane;

  private NetworkScreen protocol;
  
  private boolean isHost;

  private String mapContent;

  private String dictionary;

  private ArrayList<Player> players;

  private double progress = 0;

  private int randomNumber;
  
  private int ownID;

  /**
   * Constructor for a network game which has the function to briefly save game necessary
   * information, which will be needed for a network game.
   * 
   * @param protocol corresponding protocol of the player
   * @param isHost boolean condition for host allocation
   * @param mapContent content of a multiplier field file
   * @param players members of the game
   * @param dictionary content of a dictionary file
   * @param ownID Id of the own player during a network game.
   * @author hendiehl
   */
  public LoadingController(NetworkScreen protocol, boolean isHost, String mapContent,
      ArrayList<Player> players, String dictionary, int ownID) {
    this.protocol = protocol;
    this.isHost = isHost;
    this.mapContent = mapContent;
    this.players = players;
    this.dictionary = dictionary;
    this.ownID = ownID;
  }

  /**
   * Basic constructor for non network games.
   * 
   * @author hendiehl
   */
  public LoadingController() {
    // Constructor for non network game
  }
  
  /**
   * Changing hints of the Loading-Screen.
   * 
   * @author skeskinc
   */
  public void changeHint() {
    randomNumber = (int) (1 + Math.random() * 5);
    if (progress == 0 || progress == 0.5) {
      switch (randomNumber) {
        case 1:
          hintLabel.setText(
              "Hint: If seven tiles have been laid on the board in one turn after all of the words formed have been scored, 50 bonus points are added.");
          break;
        case 2:
          hintLabel.setText(
              "Hint: Words can only be played as a continuous string of letters by reading them from left to top or top to bottom.");
          break;
        case 3:
          hintLabel.setText(
              "Hint: Blank tiles are joker. By using them, the player can define what letter the blank tile stands for. Blank tiles scores zero points.");
          break;
        case 4:
          hintLabel.setText(
              "Hint: If a player uses more than 10 minutes of overtime, the game is ending.");
          break;
        case 5:
          hintLabel.setText(
              "Hint: The player has the options to pass a turn, play at least one tile on the game field or exchange one or more tiles for an equal number from the bag.");
          break;
        default:
          hintLabel.setText(
              "Hint: If seven tiles have been laid on the board in one turn after all of the words formed have been scored, 50 bonus points are added.");
          break;
      }
    }
  }

  /**
   * Increase value of timebar.
   * 
   * @author skeskinc
   */
  public void increaseValue() {

    if (!MainPageController.isNetwork()) {
      pingLabel.setVisible(false);
      pingCircle.setVisible(false);
      showPing.setVisible(false);
      modeLabel.setText("Mode: Offline game");
    } else {
      pingLabel.setVisible(false);
      pingCircle.setVisible(false);
      showPing.setVisible(false);
      modeLabel.setText("Mode: Network game");
    }

    KeyFrame keyframe = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

      /**
       * Handle Ping, which is random and ProgressBar value management.
       * 
       * @author skeskinc
       */
      @Override
      public void handle(ActionEvent event) {
        int randomPing = (int) (1 + Math.random() * 120);
        if (randomPing >= 60 && randomPing <= 99) {
          showPing.setTextFill(Color.web("#ff8d1b", 0.8));
          pingCircle.setFill(Color.web("#ff8d1b"));
        } else if (randomPing > 99) {
          showPing.setTextFill(Color.web("#ff0000", 0.8));
          pingCircle.setFill(Color.web("#ff0000"));
        } else if (randomPing < 60) {
          showPing.setTextFill(Color.web("#27b53c", 0.8));
          pingCircle.setFill(Color.web("#27b53c"));
        }
        showPing.setText("" + randomPing + " ms");
        progress += 0.125;
        progressBar.setProgress(progress);
        //Changing hint
        changeHint();

        if (progressBar.getProgress() > 1) {
          try {
            Parent root;
            if (!MainPageController.isNetwork()) {
              root = FXMLLoader.load(getClass().getResource("/fxml/interface.fxml"));
            } else {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/interface.fxml"));
              loader.setControllerFactory(c -> {
                return new GameController(protocol, isHost, mapContent, players, dictionary, ownID);
              });
              root = loader.load();
              GameController gameController = loader.<GameController>getController();
              ScrabbleApp.getStage().setOnHidden(e -> {
                gameController.shutdown();
              });
            }
            ScrabbleApp.getScene().getStylesheets().clear();
            ScrabbleApp.getScene().getStylesheets()
                .add(getClass().getResource("/css/style.css").toExternalForm());
            ScrabbleApp.getScene().setRoot(root);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }

    });
    // Setting Timeline
    Timeline handleProgressValue = new Timeline();
    // Adding KeyFrame to handle ProgressBar
    handleProgressValue.getKeyFrames().add(keyframe);
    // Doing this 9 times
    handleProgressValue.setCycleCount(9);
    // Starting the counter
    handleProgressValue.play();
  }

  /**
   * Increasing timebar-value here.
   * 
   * @author skeskinc
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    this.changeHint();
    // Increasing progress-bar
    this.increaseValue();
  }
}
