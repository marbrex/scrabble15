package scrabble;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.DBUpdate;
import scrabble.model.HumanPlayer;
import scrabble.model.Profile;

/**
 * Controller of the Settings.fxml
 *
 * @author astarche
 */
public class SettingsController implements Initializable {

  @FXML
  private Label title;

  @FXML
  private JFXButton videoVid;

  @FXML
  private JFXButton gameplayVid;

  @FXML
  private JFXButton backVid;

  @FXML
  private VBox mainBlock;

  @FXML
  private HBox switchSection;

  private boolean fullScreen = DBInformation.isFullscreen(Profile.getPlayer());
  private boolean difficultyHard = DBInformation.isAiDifficultyHard(Profile.getPlayer());
  private int settingsId = DBInformation.getSettingsId(Profile.getPlayer());


  /** Switch to gameplay settings screen */
  @FXML
  public void switchToGameplay() {
    for (Node n : switchSection.getChildren()) {
      if (n instanceof JFXButton) {
        n.getStyleClass().removeAll("section-btn-left-current", "section-btn-mid-current",
            "section-btn-right-current");
      }
    }
    gameplayVid.getStyleClass().add("section-btn-right-current");
    // videoVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
    // soundVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
    // gameplayVid.setStyle(null);
    title.setText("Settings -> Gameplay");
    mainBlock.getChildren().clear();
    Label gameplayLabel = new Label("AI Difficulty");
    gameplayLabel.setFont(new Font("System", 43));
    HBox upperBlock = new HBox();
    upperBlock.setAlignment(Pos.CENTER);
    upperBlock.setPrefWidth(200);
    upperBlock.setPrefHeight(100);
    upperBlock.getChildren().add(gameplayLabel);
    mainBlock.getChildren().add(upperBlock);
    HBox lowerBlock = new HBox();
    lowerBlock.setAlignment(Pos.CENTER);
    lowerBlock.setPrefWidth(200);
    lowerBlock.setPrefHeight(100);
    JFXCheckBox hard = new JFXCheckBox();
    JFXCheckBox easy = new JFXCheckBox();
    hard.setText("Hard");
    hard.setFont(new Font("System", 26));
    easy.setText("Easy");
    easy.setFont(new Font("System", 26));
    hard.setPrefWidth(178);
    hard.setPrefHeight(18);
    easy.setPrefWidth(44);
    easy.setPrefHeight(18);
    if (difficultyHard) {
      hard.setSelected(true);
    } else {
      easy.setSelected(true);
    }
    hard.setOnAction(event -> {
      hard.setSelected(true);
      easy.setSelected(false);
      difficultyHard = true;
      DBUpdate.updateAiDifficulty(settingsId, "Hard");
    });
    easy.setOnAction(event -> {
      hard.setSelected(false);
      easy.setSelected(true);
      difficultyHard = false;
      DBUpdate.updateAiDifficulty(settingsId, "Easy");
    });
    lowerBlock.getChildren().add(hard);
    lowerBlock.getChildren().add(easy);
    mainBlock.getChildren().add(lowerBlock);
  }


  /** Switch to video settings screen */
  @FXML
  public void switchToVideo(ActionEvent event) {
    for (Node n : switchSection.getChildren()) {
      if (n instanceof JFXButton) {
        n.getStyleClass().removeAll("section-btn-left-current", "section-btn-mid-current",
            "section-btn-right-current");
      }
    }
    videoVid.getStyleClass().add("section-btn-left-current");
    mainBlock.getChildren().clear();
    title.setText("Settings -> Video");
    // videoVid.setStyle(null);
    // gameplayVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
    // soundVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
    initialize(null, null);
  }

  /** Switch to main page */
  @FXML
  public void goToMainPage(ActionEvent event) {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/fxml/MainPage.fxml"));
      Pane root = loader.load();
      /*
       * Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow(); Scene scene = new
       * Scene(root, (((Node) event.getSource()).getScene().getWidth()), (((Node)
       * event.getSource()).getScene().getHeight()));
       * scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
       * window.setScene(scene);
       */
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets()
          .add(getClass().getResource("/css/mainMenu.css").toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Initialize with video settings screen */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    videoVid.getStyleClass().add("section-btn-left-current");
    HBox upperBlockMain = new HBox();
    upperBlockMain.setAlignment(Pos.CENTER);
    upperBlockMain.setPrefWidth(200);
    upperBlockMain.setPrefHeight(100);
    HBox upperBlockOne = new HBox();
    upperBlockOne.setAlignment(Pos.CENTER);
    upperBlockOne.setPrefWidth(200);
    upperBlockOne.setPrefHeight(100);
    Label modeLabel = new Label("Window Mode:");
    modeLabel.setFont(new Font("System", 26));
    upperBlockOne.getChildren().add(modeLabel);
    HBox upperBlockTwo = new HBox();
    upperBlockTwo.setAlignment(Pos.CENTER);
    upperBlockTwo.setPrefWidth(200);
    upperBlockTwo.setPrefHeight(100);
    JFXToggleButton windowed = new JFXToggleButton();
    if (fullScreen) {
      windowed.setSelected(true);
      windowed.setText("Full Screen");
    } else {
      windowed.setSelected(false);
      windowed.setText("Windowed");
    }
    windowed.setFont(new Font("System", 20));
    windowed.setOnAction(event -> {
      Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
      if (windowed.isSelected()) {
        fullScreen = true;
        // window.setFullScreen(true);
        // window.setResizable(true);
        // ScrabbleApp.getStage().setFullScreen(true);
        windowed.setText("Full Screen");
      } else {
        fullScreen = false;
        // window.setFullScreen(false);
        // window.setResizable(true);
        // ScrabbleApp.getStage().setFullScreen(false);
        windowed.setText("Windowed");
      }
      ScrabbleApp.getStage().setFullScreen(fullScreen);
      DBUpdate.updateSceneMode(settingsId, fullScreen);
    });
    upperBlockTwo.getChildren().add(windowed);
    upperBlockMain.getChildren().add(upperBlockOne);
    upperBlockMain.getChildren().add(upperBlockTwo);
    HBox lowerBlockMain = new HBox();
    lowerBlockMain.setAlignment(Pos.CENTER);
    lowerBlockMain.setPrefWidth(200);
    lowerBlockMain.setPrefHeight(100);
    HBox lowerBlockOne = new HBox();
    lowerBlockOne.setAlignment(Pos.CENTER);
    lowerBlockOne.setPrefWidth(200);
    lowerBlockOne.setPrefHeight(100);
    Label colorLabel = new Label("Game Color:");
    colorLabel.setFont(new Font("System", 26));
    colorLabel.setVisible(false);
    lowerBlockOne.getChildren().add(colorLabel);
    HBox lowerBlockTwo = new HBox();
    lowerBlockTwo.setAlignment(Pos.CENTER);
    lowerBlockTwo.setPrefWidth(200);
    lowerBlockTwo.setPrefHeight(100);
    ChoiceBox<String> cb = new ChoiceBox();
    cb.getItems().add("some variants of color combinations");
    cb.setVisible(false);
    lowerBlockTwo.getChildren().add(cb);
    lowerBlockMain.getChildren().add(lowerBlockOne);
    lowerBlockMain.getChildren().add(lowerBlockTwo);
    mainBlock.getChildren().add(upperBlockMain);
    mainBlock.getChildren().add(lowerBlockMain);

  }

}


