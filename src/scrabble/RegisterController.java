package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.*;

/**
 * 
 * The controller class for the Register.fxml screen
 * 
 * @author mraucher
 */
public class RegisterController implements Initializable {

  private static final String nameMissingWarning = "Please choose a name!\n";
  private static final String nameLengthWarning = "Name should be less than 16 characters!\n";
  private static final String imageNotSetWarning = "Please choose an image!\n";
  private static final String nameAlreadyExists =
      "The name already exists, please choose another name!\n";

  private String user;
  @FXML
  private JFXTextArea txtName;
  @FXML
  private JFXButton backBtn;
  @FXML
  private JFXButton registerBtn;
  @FXML
  private BorderPane root;

  private HumanPlayer plyr;


  /**
   * The event handler for the register button
   * 
   * @param event ActionEvent if the register button is clicked
   * @author mraucher
   */
  @FXML
  public void registerBtnOnAction(ActionEvent event) {

    String name = txtName.getText().trim();
    plyr.setName(name);

    // checking for warnings and construct the warningMessage
    String warningMessage = "";
    if (name.isEmpty()) {
      warningMessage += nameMissingWarning;
    }
    if (name.length() > 15) {
      warningMessage += nameLengthWarning;
    }
    if (user == null) {
      warningMessage += imageNotSetWarning;
    }
    if (DBInformation.containsName(name)) {
      warningMessage += nameAlreadyExists;
    }
    if (warningMessage != "") {
      AlertMessage.showWarningMessage(warningMessage);
    } else {

      // write new profile to DB
      Database.fillTables(generateId(), name, user == "male" ? 0
          : user == "female" ? 1 : user == "anonyms" ? 2 : user == "animal" ? 3 : 0);

      try {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/ChooseProfileScene.fxml"));
        /*
        Stage stage = (Stage) this.root.getScene().getWindow();
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
      }
    }
  }

  /**
   * Initializes a new human player
   * 
   * @author mraucher
   */
  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    plyr = new HumanPlayer();
  }

  /**
   * Sets the user to female when the corresponding button is pressed
   * 
   * @param event ActionEvent if the button is clicked
   * @author mraucher
   */
  @FXML
  public void femaleBtnPressed(ActionEvent event) {

    user = "female";
  }

  /**
   * Sets the user to male when the corresponding button is pressed
   * 
   * @param event ActionEvent if the button is clicked
   * @author mraucher
   */
  @FXML
  public void maleBtnPressed(ActionEvent event) {

    user = "male";
  }

  /**
   * Sets the user to anonymous when the corresponding button is pressed
   * 
   * @param event ActionEvent if the button is clicked
   * @author mraucher
   */
  @FXML
  public void anonymsBtnPressed(ActionEvent event) {

    user = "anonyms";
  }

  /**
   * Sets the user to animal when the corresponding button is pressed
   * 
   * @param event ActionEvent if the button is clicked
   * @author mraucher
   */
  @FXML
  public void animalBtnPressed(ActionEvent event) {

    user = "animal";
  }

  /**
   * Generating a player id
   * 
   * @return the generated Id
   * @author mraucher
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

}
