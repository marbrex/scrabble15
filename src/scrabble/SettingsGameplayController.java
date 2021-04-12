package scrabble;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of the SettingsGameplay.fxml
 *
 * @author Alexander Starchenkov
 */
public class SettingsGameplayController implements Initializable {

    @FXML
    private JFXButton videoGam;

    @FXML
    private  JFXButton soundGam;

    @FXML
    private JFXButton backGame;

    @FXML
    private JFXCheckBox easyBox;

    @FXML
    private JFXCheckBox hardBox;

    private static boolean difficult;
    /** Switch  to sound settings screen*/
    @FXML
    public void switchToSound(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/SettingsSound.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setFullScreen(SettingsVideoController.fullScreen);
            window.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /** Switch  to video settings screen*/
    @FXML
    public void switchToVideo(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/SettingsVideo.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setFullScreen(SettingsVideoController.fullScreen);
            window.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /** Switch difficulty to "easy"*/
    @FXML
    public void switchToEasy(){
        hardBox.setSelected(true);
        easyBox.setSelected(false);
        difficult = true;
    }
    /** Switch difficulty to "hard"*/
    @FXML
    public void switchToHard(){
        hardBox.setSelected(false);
        easyBox.setSelected(true);
        difficult = false;
    }
    /** Switch to main page*/
    @FXML
    public void goToMainPage(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/MainPage.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setFullScreen(SettingsVideoController.fullScreen);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (difficult){
            hardBox.setSelected(true);
            easyBox.setSelected(false);
        }else{
            hardBox.setSelected(false);
            easyBox.setSelected(true);
        }
    }
}