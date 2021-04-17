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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import scrabble.*;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.*;

// @author mraucher

public class RegisterController implements Initializable {

  String user;
  @FXML
  private JFXTextArea txtPlayerId;
  @FXML
  private JFXTextArea txtPassword;
  @FXML
  private JFXTextArea txtName;
  @FXML
  private JFXButton backBtn;
  @FXML
  private JFXButton registerBtn;
  @FXML
  private Pane root;

  private HumanPlayer plyr;
  // Database db;

  @FXML
  public void registerBtnOnAction(ActionEvent event) {
    /*
     * try {
     * 
     * String playerId = txtPlayerId.getText().trim(); String name = txtName.getText().trim();
     * String pass = txtPassword.getText().trim();
     * 
     * 
     * plyr.setName(name); // plyr.setUser(user)
     * 
     * if (name.isEmpty() || playerId.isEmpty()) {
     * AlertMessage.showWarningMessage("All input fields are required"); return; }
     * 
     * // db.addPlayerProfile(new String[] {playerId, name, pass, user});
     * 
     * Database.connectToDB(); if
     * (!DBInformation.containsIdentification(Integer.parseInt(playerId))) {
     * Database.fillTables(Integer.parseInt(playerId), name, 1); Database.disconnectDB();
     * 
     * showHomePage(); //here problem
     * 
     * Stage stage1 = (Stage) txtName.getScene().getWindow(); stage1.close(); } } catch (Exception
     * e) { e.printStackTrace(); }
     */ // --> Problems with DB will be implemented later
    // Debug approach :
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/ChooseProfileScene.fxml"));
      Parent root = loader.load();
      Stage stage = (Stage) this.backBtn.getScene().getWindow();
      Scene scene =
          new Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
      scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
      stage.setScene(scene);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private int generateId() {
    int id = 0;
    Database.connectToDB();
    for (int i = 1; i < 5; i++) {
      if (!DBInformation.containsIdentification(i)) {
        id = i;
        break;
      }
    }
    Database.disconnectDB();
    /*
     * return (int) Math.floor(Math.random()*(9999-10+1)+10);
     */
    return id;
  }


  private void showHomePage() throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("fxml/ChooseProfileScene.fxml"));

    Stage stage = new Stage();
    stage.setScene(new Scene(loader.load(), this.root.getWidth(), this.root.getHeight()));

    stage.show();
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    txtPlayerId.setEditable(false);

    plyr = new HumanPlayer();

    // db = new Database();

    int id = generateId();

    // while(db.isExist(id))

    txtPlayerId.setText(id + "");

    // plyr.setId(id);

    user = "male";
  }



  @FXML
  public void femaleBtnPressed(ActionEvent event) {

    user = "female";
  }

  @FXML
  public void maleBtnPressed(ActionEvent event) {

    user = "male";
  }

  @FXML
  public void anonymsBtnPressed(ActionEvent event) {

    user = "anonyms";
  }

  @FXML
  public void animalBtnPressed(ActionEvent event) {

    user = "animal";
  }

  @FXML
  public void backBtnOnAction(ActionEvent event) {
    /*
     * // Parent root=FXMLLoader.load((getClass().getResource("fxml/signin_signup.fxml")));
     * BorderPane root = FXMLLoader.load(new URL("file:" + System.getProperty("user.dir") +
     * "\\resources\\" + "scrabble\\fxml\\ChooseProfileScene.fxml")); Button btn = ((Button)
     * event.getSource()); Stage stage = (Stage) btn.getScene().getWindow(); //
     * stage.initStyle(StageStyle.UNDECORATED); stage.setScene(new Scene(root, this.root.getWidth(),
     * this.root.getHeight()));
     * 
     * other approach -> test later
     * 
     * try { FXMLLoader loader = new
     * FXMLLoader(getClass().getResource("fxml/ChooseProfileScene.fxml")); Parent root =
     * loader.load(); Stage stage = (Stage) this.backBtn.getScene().getWindow(); Scene scene = new
     * Scene(root, this.root.getScene().getWidth(), this.root.getScene().getHeight());
     * scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
     * stage.setScene(scene); } catch (IOException e) { // TODO Auto-generated catch block
     * e.printStackTrace(); }
     */
    // --> will be implemented later

  }

}
