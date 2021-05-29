package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;


/**
 * scrabble.CreateProfileController to manage profile creation.
 * 
 * @author skeskinc
 * @author ekasmamy
 */
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

  @FXML
  public TextField nameField;

  @FXML
  public FlowPane avatarsBlock;

  @FXML
  public Label errorLabel;

  @FXML
  public int imageindex;

  /**
   * Changing to the Scene.
   * 
   * @param resource URL-Resource to another scene
   * @param style given style-sheet for next scene
   * @param event Handling MouseEvent
   * @author ekasmamy
   */
  public void changeScene(String resource, String style, Event event) {
    try {
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
   * Generating a player id.
   * 
   * @return generated Identification number
   * @author skeskinc
   */
  private int generateId() {
    int id = 0;
    for (int i = 1; i < 5; i++) {
      if (!DBInformation.containsIdentification(i)) {
        id = i;
        break;
      }
    }
    return id;
  }

  /**
   * Setting avatars and handling events.
   * 
   * @author skeskinc
   */
  @FXML
  private void initialize() {
    // Loading all images which are available for profile creation
    ImageView im = new ImageView(new Image(getClass().getResourceAsStream("/img/male.png")));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(0)).getChildren().add(im);
    im = new ImageView(new Image(getClass().getResourceAsStream("/img/female.png")));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(1)).getChildren().add(im);
    im = new ImageView(new Image(getClass().getResourceAsStream("/img/anonyms.png")));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(2)).getChildren().add(im);
    im = new ImageView(new Image(getClass().getResourceAsStream("/img/animal.png")));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(3)).getChildren().add(im);
    this.imageindex = 5;

    // Action on Cancel Button
    cancelBtn.setOnMouseClicked(event -> {
      changeScene("/fxml/ChooseProfileScene.fxml", "/css/changeProfile.css", event);
    });

    // Action on Create Button
    createBtn.setOnMouseClicked(event -> {
      String name = nameField.getText();
      int id = generateId();
      if (!name.isEmpty() && !DBInformation.containsName(name) && name.length() <= 15
          && this.imageindex <= 3) {
        Database.fillTables(id, name, this.imageindex);
        changeScene("/fxml/ChooseProfileScene.fxml", "/css/changeProfile.css", event);
      } else if (DBInformation.containsName(name)) {
        nameField.setStyle("-fx-border-color: red;");
        errorLabel.setText("Name is already taken!");
      } else if (name.length() > 15) {
        nameField.setStyle("-fx-border-color: red;");
        errorLabel.setText("Name should contain less than 15 letters!");
      } else if (name.isEmpty()) {
        nameField.setStyle("-fx-border-color: red;");
        errorLabel.setText("Fill the Textfield!");
      } else if (imageindex >= 4) {
        errorLabel.setText("No image chosen!");
      }
    });
    // Changing border color on click
    for (int i = 0; i < avatarsBlock.getChildren().size(); i++) {
      int avatarId = i;
      avatarsBlock.getChildren().get(i).setOnMouseClicked(event -> {
        for (int j = 0; j < avatarsBlock.getChildren().size(); j++) {
          if (((StackPane) event.getSource()) == (StackPane) avatarsBlock.getChildren().get(j)) {
            avatarsBlock.getChildren().get(j).setStyle(" -fx-border-color: BLACK");
            this.imageindex = avatarId;
          } else {
            avatarsBlock.getChildren().get(j).setStyle(" -fx-border-color: GREY");
          }
        }
      });
    }
  }
}
