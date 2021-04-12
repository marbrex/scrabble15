package scrabble;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
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
 * Controller of the SettingsSound.fxml
 *
 * @author Alexander Starchenkov
 */
public class SettingsSoundController implements Initializable {

    @FXML
    private JFXButton vidSo;

    @FXML
    private  JFXButton gameplaySo;

    @FXML
    private JFXButton backSo;

    @FXML
    private JFXToggleButton soundButton;

    private static boolean sound = true;
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
    /** Switch  to gameplay settings screen*/
    @FXML
    public void switchToGameplay(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/SettingsGameplay.fxml"));
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
    /** Turn off or turn on the sound*/
    @FXML
    public void switchSound(){
        if(soundButton.isSelected()){
            soundButton.setText("ON");
            sound = true;
        }else{
            soundButton.setText("OFF");
            sound = false;
        }
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
        soundButton.setSelected(sound);
        if(sound){
            soundButton.setText("ON");
        }else{
            soundButton.setText("OFF");
        }
    }
}