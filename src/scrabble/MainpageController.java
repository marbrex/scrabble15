package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import scrabble.dbhandler.DBInformation;
import scrabble.dbhandler.Database;
import scrabble.model.HumanPlayer;

public class MainpageController implements Initializable {

	@FXML
	private ImageView backgroundPane;

	@FXML
	private Label switchsoundLabel;

	@FXML
	private Label idLabel;

	@FXML
	private JFXButton settingsButton;

	@FXML
	private JFXButton profileButton;

	@FXML
	private JFXButton exitButton;

	@FXML
	private JFXButton soundButton;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private ImageView soundImage;
	
	@FXML
	private Pane root;

	/*
	 * public void setSize(Stage stage) {
	 * this.soundButton.fitHeightProperty().bind(stage.heightProperty());
	 * this.soundButton.fitWidthProperty().bind(stage.widthProperty()); }
	 */

	public void pressingButton(ActionEvent event) {
		switch (((Button) event.getSource()).getText()) {
		case "settingsButton":
			break;
		case "Play Scrabble":
	//		Main.setNetwork(false);
			try {
				this.changeScene("LoadingScreen.fxml",event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "Play Network Game":
	//		Main.setNetwork(true);
			try {
				this.changeScene("LoadingScreen.fxml",event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "profileButton":
			try {
				this.changeScene("ProfileScreen.fxml",event);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case "playTutorial":
			break;
		case "exitButton":
			break;
		default:
			break;
		}
	}
	
	public void increaseValue() {
		double progress = 0;
		for(int i = 0; i < 100; i++) {
			if(i % 25 == 0) {
				progress += 0.25;
				progressBar.setProgress(progress);
			}
		}
	}
	
	public void changeScene(String resource, ActionEvent event) throws IOException {
		System.out.println(resource);
		Parent root = FXMLLoader.load(getClass().getResource(resource));
		Button btn = ((Button) event.getSource());
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.setScene(new Scene(root, 649, 539));
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Database.connectToDB();
		HumanPlayer player = DBInformation.getPlayerProfile();
		this.idLabel.setText("Welcome back, " + player.getName());
		Database.disconnectDB();
	//	setSize();
	}

}