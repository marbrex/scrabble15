package scrabble;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of the SettingsVideo.fxml
 *
 * @author Alexander Starchenkov
 */
public class SettingsVideoController implements Initializable {

    @FXML
    private JFXButton gameplayVid;

    @FXML
    private JFXButton soundVid;

    @FXML
    private JFXToggleButton windowMode;

    @FXML
    private ChoiceBox<String> resVid;

    @FXML
    private Button applyButton;

    @FXML
    private JFXButton backVid;

    public static boolean fullScreen;

    /** Switch  to gameplay settings screen*/
    @FXML
    public void switchToGameplay(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/SettingsGameplay.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setFullScreen(fullScreen);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Switch  to sound settings screen*/
    @FXML
    public void switchToSound(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/SettingsSound.fxml"));
            Pane root = loader.load();
            Scene scene = new Scene(root, 600, 400);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.setFullScreen(fullScreen);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Change window mode*/
    @FXML
    public void changeWindowMode(ActionEvent event) {
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (windowMode.isSelected()) {
            fullScreen = true;
            windowMode.setText("Full Screen");
        } else {
            fullScreen = false;
            windowMode.setText("Windowed");
        }
        window.setFullScreen(fullScreen);
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
            window.setFullScreen(fullScreen);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        windowMode.setSelected(fullScreen);
        if (fullScreen) {
            windowMode.setText("Full Screen");
        } else {
            windowMode.setText("Windowed");
        }
        String[] resolutions = {"1024×576", "1152×648", "1280×720", "1366×768",
                "1600×900", "1920×1080", "2560×1440", "3840×2160"};
        for (int i = 0;i < resolutions.length;i++){
            resVid.getItems().add(resolutions[i]);
        }
    }
}