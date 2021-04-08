package scrabble;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import scrabble.dbhandler.*;
import scrabble.model.*;

import com.jfoenix.controls.JFXButton;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoadingController implements Initializable {

	@FXML
	public Label hintLabel;

	@FXML
	public ProgressBar progressBar;

	@FXML
	public Circle pingCircle;

	@FXML
	public Label pingLabel;

	@FXML
	public Label modeLabel;

	@FXML
	public Label showPing;

	public void increaseValue() {
	  /*
		if (!Main.isNetwork()) {
			pingLabel.setVisible(false);
			pingCircle.setVisible(false);
			showPing.setVisible(false);
			modeLabel.setText("Mode: Offline game");
		} else {
			pingLabel.setVisible(true);
			pingCircle.setVisible(true);
			showPing.setVisible(true);
			modeLabel.setText("Mode: Network game");
		}
*/
		KeyFrame keyframe = new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {

			private double progress = 0;

			@Override
			public void handle(ActionEvent event) {
				int randomNumber = (int) (1 + Math.random() * 120);
				if (randomNumber >= 60 && randomNumber <= 99) {
					showPing.setTextFill(Color.web("#ff8d1b", 0.8));
					pingCircle.setFill(Color.web("#ff8d1b"));
				} else if (randomNumber > 99) {
					showPing.setTextFill(Color.web("#ff0000", 0.8));
					pingCircle.setFill(Color.web("#ff0000"));
				} else if (randomNumber < 60) {
					showPing.setTextFill(Color.web("#27b53c", 0.8));
					pingCircle.setFill(Color.web("#27b53c"));
				}
				showPing.setText("" + randomNumber + " ms");
				progress += 0.125;
				progressBar.setProgress(progress);
				if(progressBar.getProgress() > 1) {
					try {
						Parent root = FXMLLoader.load(getClass().getResource("Startscreen.fxml"));
						Stage stage = (Stage) progressBar.getScene().getWindow();
						stage.setScene(new Scene(root, 649, 539));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		});
		// Setting Timeline
		Timeline handleProgressValue = new Timeline();
		// Adding KeyFrame to handle ProgressBar
		handleProgressValue.getKeyFrames().add(keyframe);
		// Doing this 9 times
		handleProgressValue.setCycleCount(9);
		// Starting the counter
		handleProgressValue.play();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		this.increaseValue();
	}
}
