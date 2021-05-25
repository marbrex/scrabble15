package scrabble;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
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
   * Handle Delete-Action and scene change.
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
        root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));
      } else {
        root = FXMLLoader.load(getClass().getResource("/fxml/ChooseProfileScene.fxml"));
      }
      ScrabbleApp.getScene().getStylesheets().clear();
      ScrabbleApp.getScene().getStylesheets()
          .add(getClass().getResource("/css/changeProfile.css").toExternalForm());
      ScrabbleApp.getScene().setRoot(root);
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("Error: " + e.getMessage());
    }
  }
}

