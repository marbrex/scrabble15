package scrabble;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;


/** scrabble.CreateProfileController to manage profile creation  */
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
  public int imageindex;

  /** Changing to the Scene */
  public void changeScene(String resource, String style, Event event) {
    try {
      System.out.println(resource);
      Parent root = FXMLLoader.load(getClass().getResource(resource));
      Button btn = ((Button) event.getSource());
      Stage stage = (Stage) btn.getScene().getWindow();
      Scene scene =
          new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource(style).toExternalForm());
      stage.setScene(scene);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }

  /** Generating a player id */
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

  /** Setting avatars and handling events */
  @FXML
  private void initialize() {
    Database.connectToDB();
    ImageView im = new ImageView(new Image(
        "file:" + System.getProperty("user.dir") + "\\resources\\scrabble\\img\\male.png"));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(0)).getChildren().add(im);
    im = new ImageView(new Image(
        "file:" + System.getProperty("user.dir") + "\\resources\\scrabble\\img\\female.png"));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(1)).getChildren().add(im);
    im = new ImageView(new Image(
        "file:" + System.getProperty("user.dir") + "\\resources\\scrabble\\img\\anonyms.png"));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(2)).getChildren().add(im);
    im = new ImageView(new Image(
        "file:" + System.getProperty("user.dir") + "\\resources\\scrabble\\img\\animal.png"));
    im.setFitHeight(50);
    im.setFitWidth(50);
    ((StackPane) avatarsBlock.getChildren().get(3)).getChildren().add(im);

    //Action on Cancel Button
    cancelBtn.setOnMouseClicked(event -> {
      changeScene("fxml/ChooseProfileScene.fxml", "css/mainMenu.css", event);
      Database.disconnectDB();
    });

    //Action on Create Button
    createBtn.setOnMouseClicked(event -> {
      String name = nameField.getText();
      int id = generateId();
      if (!name.isEmpty() && !DBInformation.containsName(name) && name.length() <= 15) {
        Database.fillTables(id, name, this.imageindex);
        Database.disconnectDB();
        changeScene("fxml/ChooseProfileScene.fxml", "css/mainMenu.css", event);
      }
    });
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
