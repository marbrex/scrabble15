package scrabble;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {
	/**
	 * Controller of the network game menu screen
	 * @author Hendrik Diehl
	 */
	//gui
	@FXML private JFXButton joinGameButton;
	@FXML private JFXButton hostGameButton;
	
	
	/**
	 * method to react to the host game button and open up the GameLobby screen with specific constructor and
	 * setting up a method if the window is closed
	 */
	@FXML private void hostGameAction() {
		try {
			GameLobbyController lobbyController;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GameLobby.fxml"));
			loader.setControllerFactory(c -> {
				return new GameLobbyController(true);
			});
			Stage stage = (Stage) this.joinGameButton.getScene().getWindow();
			Scene scene = new Scene(loader.load());
			lobbyController = loader.<GameLobbyController>getController();
			stage.setScene(scene);
			stage.setTitle("Scrabble");
			stage.setHeight(600);
			stage.setWidth(800);
			stage.setResizable(false);
			stage.setOnHidden(e -> {lobbyController.shutdown();}); //what if gamescreen is shown
			stage.show();
			//Set up Host
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * method to react to the join game button and open the GameFinder screen and setting up an method if the window is closed.
	 */
	@FXML private void joinGameAction() {
		try {
			GameFinderController finderController;
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GameFinder.fxml"));
			Stage stage = (Stage) this.joinGameButton.getScene().getWindow();
			Scene scene = new Scene(loader.load());
			finderController = loader.<GameFinderController>getController();
			stage.setScene(scene);
			stage.setTitle("Scrabble");
			stage.setHeight(600);
			stage.setWidth(800);
			stage.setResizable(false);
			stage.setOnHidden(e -> {finderController.shutdown();});
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
