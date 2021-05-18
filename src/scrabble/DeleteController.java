package scrabble;

import java.io.IOException;
import com.jfoenix.controls.JFXButton;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.Profile;

/**
 * scrabble.DeleteController to handle Profile deletion.
 * 
 * @author skeskinc
 */
public class DeleteController {

  @FXML
  private JFXButton deleteYesBtn;

  @FXML
  private JFXButton deleteNoBtn;

  @FXML
  private BorderPane root;

  /**
   * Deleting a profile (or not) and returning to the choose profile scene.
   * 
   * @param event Handling Delete-Action
   * @author skeskinc
   */
  @FXML
  public void handleDelete(Event event) {
    if (event.getSource() == deleteYesBtn) {
      int id = DBInformation.getSettingsId(Profile.getPlayer());
      Database.removePlayer(Profile.getPlayer());
      Database.removeSettings(id);
    }
    try {
      Parent root;
      if (DBInformation.getProfileSize() == 0) {
        root = FXMLLoader.load(getClass().getResource("fxml/Register.fxml"));
      } else {
        root = FXMLLoader.load(getClass().getResource("fxml/ChooseProfileScene.fxml"));
      }
      /*
      Button btn = ((Button) event.getSource());
      Stage stage = (Stage) btn.getScene().getWindow();
      Scene scene =
          new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource("css/changeProfile.css").toExternalForm());
      stage.setScene(scene);
      */
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets().add(getClass().getResource("css/changeProfile.css").toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }
}

