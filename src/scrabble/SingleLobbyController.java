package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class SingleLobbyController {

  @FXML
  public ChoiceBox<Integer> nbBots;

  @FXML
  public JFXButton backBtn;

  @FXML
  public JFXButton startBtn;

  /**
   * The function responsible for JavaFX Scene switching.
   *
   * @param resource FXML file.
   * @param style    CSS file.
   * @author ekasmamy
   * @author skeskinc
   */
  public void changeScene(String resource, String style) {
    try {
      Parent root = FXMLLoader.load(getClass().getResource(resource));
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource(style).toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initChoice() {

    nbBots.setValue(3);
    nbBots.getItems().add(1);
    nbBots.getItems().add(2);
    nbBots.getItems().add(3);

  }

  private void initButtons() {

    backBtn.setOnMouseClicked(mouseEvent -> {
      changeScene("/fxml/singlePlayerLobby.fxml", "/css/mainMenu.css");
    });

    startBtn.setOnMouseClicked(mouseEvent -> {
      Stage stage = ScrabbleApp.getStage();
      try {

        SingleGameController gameController = new SingleGameController(nbBots.getValue());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/interfaceSingle.fxml"));
        loader.setController(gameController);

        Parent root = loader.load();

        String parameters = "{\n"
            + " \"nbBots\": \"" + nbBots.getValue() + "\"\n"
            + "}";

        ScrabbleApp.getScene().getStylesheets().clear();
        ScrabbleApp.getScene().getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        ScrabbleApp.getScene().setRoot(root);

      } catch (IOException e) {

        e.printStackTrace();
      }

    });

  }

  @FXML
  private void initialize() {

    initChoice();

    initButtons();

  }

}
