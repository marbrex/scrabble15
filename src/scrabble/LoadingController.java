package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
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
import javafx.util.Duration;
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

  /**
   * Constructor for a network game which has the function to briefly save game necessary
   * information, which will be needed for a network game.
   * 
   * @param protocol corresponding protocol of the player
   * @param isHost boolean condition for host allocation
   * @param mapContent content of a multiplier field file
   * @param players members of the game
   * @param dictionary content of a dictionary file
   * @author hendiehl
   */
  public LoadingController(NetworkScreen protocol, boolean isHost, String mapContent,
      ArrayList<Player> players, String dictionary) {
    this.protocol = protocol;
    this.isHost = isHost;
    this.mapContent = mapContent;
    this.players = players;
    this.dictionary = dictionary;
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

      private double progress = 0;

      /**
       * Handle Ping, which is random.
       * 
       * @author skeskinc
       */
      @Override
      public void handle(ActionEvent event) {
        int randomNumber = (int) (1 + Math.random() * 120);
        if (randomNumber >= 60 && randomNumber <= 99) {
          showPing.setTextFill(Color.web("#ff8d1b", 0.8));
          pingCircle.setFill(Color.web("#ff8d1b"));
        } else if (randomNumber > 99) {
          showPing.setTextFill(Color.web("#ff0000", 0.8));
          pingCircle.setFill(Color.web("#ff0000"));
        } else if (randomNumber < 60) {
          showPing.setTextFill(Color.web("#27b53c", 0.8));
          pingCircle.setFill(Color.web("#27b53c"));
        }
        showPing.setText("" + randomNumber + " ms");
        progress += 0.125;
        progressBar.setProgress(progress);
        if (progressBar.getProgress() > 1) {
          try {
            Parent root;
            if (!MainPageController.isNetwork()) {
              root = FXMLLoader.load(getClass().getResource("fxml/interface.fxml"));
            } else {
              FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/interface.fxml"));
              loader.setControllerFactory(c -> {
                return new GameController(protocol, isHost, mapContent, players, dictionary);
              });
              root = loader.load();
            }
            /*
             * Stage stage = (Stage) progressBar.getScene().getWindow(); Scene scene = new
             * Scene(root, pane.getScene().getWidth(), pane.getScene().getHeight());
             * scene.getStylesheets().add(getClass()
             * .getResource("css/style.css").toExternalForm()); stage.setScene(scene);
             */
            ScrabbleApp.getScene().getStylesheets().clear();
            ScrabbleApp.getScene().getStylesheets()
                .add(getClass().getResource("css/style.css").toExternalForm());
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
    this.increaseValue();
  }
}
