package scrabble;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateProfileController {

  @FXML
  public BorderPane root;

  @FXML
  public VBox mainBlock;

  @FXML
  public HBox buttonsBlock;

  @FXML
  public JFXButton cancelBtn;

  @FXML
  public JFXButton createBtn;

  public void changeScene(String resource, String style, Event event) {
    try {
      System.out.println(resource);
      Parent root = FXMLLoader.load(getClass().getResource(resource));
      Button btn = ((Button) event.getSource());
      Stage stage = (Stage) btn.getScene().getWindow();
      Scene scene = new Scene(root, this.root.getScene().getWidth(),
          this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource(style).toExternalForm());
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }

  @FXML
  private void initialize() {

    cancelBtn.setOnMouseClicked(event -> {
      changeScene("fxml/ChooseProfileScene.fxml", "css/mainMenu.css", event);
    });
  }
}
