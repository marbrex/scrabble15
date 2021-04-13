package scrabble;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.stage.StageStyle;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of the SettingsVideo.fxml
 *
 * @author Alexander Starchenkov
 */
public class SettingsController implements Initializable {

    @FXML
    private Label title;

    @FXML
    private JFXButton videoVid;

    @FXML
    private JFXButton gameplayVid;

    @FXML
    private JFXButton soundVid;

    @FXML
    private JFXButton backVid;

    @FXML
    private VBox mainBlock;

    private static boolean fullScreen = false;
    private static boolean difficult = false;
    private static boolean sound = true;

    /** Switch  to gameplay settings screen*/
    @FXML
    public void switchToGameplay() {
        videoVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
        soundVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
        gameplayVid.setStyle(null);
        title.setText("Settings -> Gameplay");
        mainBlock.getChildren().clear();
        Label gameplayLabel = new Label("AI Difficulty");
        gameplayLabel.setFont(new Font("System", 43));
        HBox upperBlock = new HBox();
        upperBlock.setAlignment(Pos.CENTER);
        upperBlock.setPrefWidth(200);
        upperBlock.setPrefHeight(100);
        upperBlock.getChildren().add(gameplayLabel);
        mainBlock.getChildren().add(upperBlock);
        HBox lowerBlock = new HBox();
        lowerBlock.setAlignment(Pos.CENTER);
        lowerBlock.setPrefWidth(200);
        lowerBlock.setPrefHeight(100);
        JFXCheckBox hard = new JFXCheckBox();
        JFXCheckBox easy = new JFXCheckBox();
        hard.setText("Hard");
        hard.setFont(new Font("System", 26));
        easy.setText("Easy");
        easy.setFont(new Font("System", 26));
        hard.setPrefWidth(178);
        hard.setPrefHeight(18);
        easy.setPrefWidth(44);
        easy.setPrefHeight(18);
        if (difficult) {
            hard.setSelected(true);
        }else {
            easy.setSelected(true);
        }
        hard.setOnAction(event -> {
            hard.setSelected(true);
            easy.setSelected(false);
            difficult = true;
        });
        easy.setOnAction(event -> {
            hard.setSelected(false);
            easy.setSelected(true);
            difficult = false;
        });
        lowerBlock.getChildren().add(hard);
        lowerBlock.getChildren().add(easy);
        mainBlock.getChildren().add(lowerBlock);
    }
    /** Switch  to sound settings screen*/
    @FXML
    public void switchToSound() {
        title.setText("Settings -> Sound");
        videoVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
        gameplayVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
        soundVid.setStyle(null);
        title.setText("Settings -> Sound");
        mainBlock.getChildren().clear();
        JFXToggleButton toggle = new JFXToggleButton();
        toggle.setSelected(sound);
        toggle.setFont(new Font("System", 28));
        toggle.setSelected(sound);
        if (sound){
            toggle.setText("ON");
        }else{
            toggle.setText("OFF");
        }
        toggle.setOnAction(event -> {
            if (sound){
                toggle.setSelected(false);
                toggle.setText("OFF");
                sound = false;
            }else{
                toggle.setSelected(true);
                toggle.setText("ON");
                sound = true;
            }
        });
        HBox upperBlock = new HBox();
        upperBlock.setAlignment(Pos.CENTER);
        upperBlock.setPrefHeight(100);
        upperBlock.setPrefWidth(200);
        upperBlock.getChildren().add(toggle);
        mainBlock.getChildren().add(upperBlock);
        HBox lowerBlockOne = new HBox();
        lowerBlockOne.setAlignment(Pos.CENTER);
        lowerBlockOne.setPrefWidth(200);
        lowerBlockOne.setPrefHeight(100);
        Label level = new Label("Sound Level:");
        level.setFont(new Font("System", 28));
        lowerBlockOne.getChildren().add(level);
        HBox lowerBlockTwo = new HBox();
        lowerBlockTwo.setAlignment(Pos.CENTER);
        lowerBlockTwo.setPrefWidth(200);
        lowerBlockTwo.setPrefHeight(100);
        Slider slider = new Slider();
        slider.showTickLabelsProperty().setValue(true);
        lowerBlockTwo.getChildren().add(slider);
        lowerBlockOne.getChildren().add(lowerBlockTwo);
        mainBlock.getChildren().add(lowerBlockOne);
    }
    /** Switch to video settings screen*/
    @FXML
    public void switchToVideo(ActionEvent event){
        mainBlock.getChildren().clear();
        title.setText("Settings -> Video");
        videoVid.setStyle(null);
        gameplayVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
        soundVid.setStyle("-fx-border-color: black;-fx-background-color: grey");
        initialize(null, null);
    }

    /** Switch to main page*/
    @FXML
    public void goToMainPage(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("fxml/MainPage.fxml"));
            Pane root = loader.load();
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, (((Node) event.getSource()).getScene().getWidth()),
                    (((Node) event.getSource()).getScene().getHeight()));
            scene.getStylesheets().add(getClass().getResource("css/mainMenu.css").toExternalForm());
            window.setScene(scene);
            window.setFullScreen(fullScreen);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /** Initialize with video settings screen*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HBox upperBlockMain = new HBox();
        upperBlockMain.setAlignment(Pos.CENTER);
        upperBlockMain.setPrefWidth(200);
        upperBlockMain.setPrefHeight(100);
        HBox upperBlockOne = new HBox();
        upperBlockOne.setAlignment(Pos.CENTER);
        upperBlockOne.setPrefWidth(200);
        upperBlockOne.setPrefHeight(100);
        Label modeLabel = new Label("Window Mode:");
        modeLabel.setFont(new Font("System", 26));
        upperBlockOne.getChildren().add(modeLabel);
        HBox upperBlockTwo = new HBox();
        upperBlockTwo.setAlignment(Pos.CENTER);
        upperBlockTwo.setPrefWidth(200);
        upperBlockTwo.setPrefHeight(100);
        JFXToggleButton windowed = new JFXToggleButton();
        if (fullScreen){
            windowed.setSelected(true);
            windowed.setText("Full Screen");
        }else{
            windowed.setSelected(false);
            windowed.setText("Windowed");
        }
        windowed.setFont(new Font("System", 20));
        windowed.setOnAction(event -> {
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (windowed.isSelected()) {
                fullScreen = true;
                window.setFullScreen(true);
                window.setResizable(true);
                windowed.setText("Full Screen");
            } else {
                fullScreen = false;
                window.setFullScreen(false);
                window.setResizable(true);
                windowed.setText("Windowed");
            }
        });
        upperBlockTwo.getChildren().add(windowed);
        upperBlockMain.getChildren().add(upperBlockOne);
        upperBlockMain.getChildren().add(upperBlockTwo);
        HBox lowerBlockMain = new HBox();
        lowerBlockMain.setAlignment(Pos.CENTER);
        lowerBlockMain.setPrefWidth(200);
        lowerBlockMain.setPrefHeight(100);
        HBox lowerBlockOne = new HBox();
        lowerBlockOne.setAlignment(Pos.CENTER);
        lowerBlockOne.setPrefWidth(200);
        lowerBlockOne.setPrefHeight(100);
        Label colorLabel = new Label("Game Color:");
        colorLabel.setFont(new Font("System", 26));
        lowerBlockOne.getChildren().add(colorLabel);
        HBox lowerBlockTwo = new HBox();
        lowerBlockTwo.setAlignment(Pos.CENTER);
        lowerBlockTwo.setPrefWidth(200);
        lowerBlockTwo.setPrefHeight(100);
        ChoiceBox<String> cb = new ChoiceBox();
        cb.getItems().add("some variants of color combinations");
        lowerBlockTwo.getChildren().add(cb);
        lowerBlockMain.getChildren().add(lowerBlockOne);
        lowerBlockMain.getChildren().add(lowerBlockTwo);
        mainBlock.getChildren().add(upperBlockMain);
        mainBlock.getChildren().add(lowerBlockMain);
    }
}