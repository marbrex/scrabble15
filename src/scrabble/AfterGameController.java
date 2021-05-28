package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import scrabble.model.Player;


/**
 * scrabble.AfterGameController to show all scores after the game has ended.
 * 
 * @author skeskinc
 */
public class AfterGameController implements Initializable {

  @FXML
  private Circle playerOne;

  @FXML
  private Label playeroneLbl;

  @FXML
  private Label scoreOne;

  @FXML
  private Circle playerTwo;

  @FXML
  private Label playertwoLbl;

  @FXML
  private Label scoreTwo;

  @FXML
  private Circle playerThree;

  @FXML
  private Label playerthreeLbl;

  @FXML
  private Label scoreThree;

  @FXML
  private Circle playerFour;

  @FXML
  private Label playerfourLbl;

  @FXML
  private Label scoreFour;

  @FXML
  private JFXButton closeBtn;

  private List<Player> playerList = new ArrayList<Player>();

  private int[] points;

  private ArrayList<Circle> images = new ArrayList<Circle>();

  private ArrayList<Label> names = new ArrayList<Label>();

  private ArrayList<Label> score = new ArrayList<Label>();

  /**
   * Constructor of the Controller, setting Player-List and points.
   * 
   * @param playerList Current Player-List in a right order
   * @param points Current Score in a right order
   * @author skeskinc
   */
  public AfterGameController(List<Player> playerList, int[] points) {
    this.playerList = playerList;
    this.points = points;
  }

  /**
   * Default constructor of the Controller.
   * 
   * @author skeskinc
   */
  public AfterGameController() {
    // Do nothing here.
  }

  /**
   * Preparing the loop for the Announcement.
   * 
   * @author skeskinc
   */
  public void prepareLoop() {
    // Preparing Image-List
    images.add(playerOne);
    images.add(playerTwo);
    images.add(playerThree);
    images.add(playerFour);
    // Preparing Name-List
    names.add(playeroneLbl);
    names.add(playertwoLbl);
    names.add(playerthreeLbl);
    names.add(playerfourLbl);
    // Preparing Score-List
    score.add(scoreOne);
    score.add(scoreTwo);
    score.add(scoreThree);
    score.add(scoreFour);
  }

  /**
   * Setting the Announcement of the Players.
   * 
   * @author skeskinc
   */
  public void setAnnouncement() {
    for (int i = 0; i < playerList.size(); i++) {
      names.get(i).setText(playerList.get(i).getName());
      images.get(i).setFill(new ImagePattern(
          new Image(getClass().getResourceAsStream("/img/" + playerList.get(i).getImage()))));
      score.get(i).setText("Score: " + points[i]);
    }
  }

  /**
   * Changing the Scene on given Resource and Style-sheet.
   * 
   * 
   * @author skeskinc
   */
  public void changeScene(String resource, String style) {
    Parent root;
    try {
      root = FXMLLoader.load(getClass().getResource(resource));
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      // TODO Auto-generated
      e.printStackTrace();
    }
  }

  /**
   * Announcing the winner and the score of the Players.
   * 
   * @author skeskinc
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    // TODO Auto-generated method stub
    this.prepareLoop();
    this.setAnnouncement();
    closeBtn.setOnMouseClicked(e -> {
      Stage stage = (Stage) closeBtn.getScene().getWindow();
      stage.close();
    });
  }

}
