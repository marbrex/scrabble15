package scrabble;

import java.io.IOException;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scrabble.network.LobbyServer;
import scrabble.network.PortsOccupiedException;

public class GameLobbyController implements LobbyController{
	/**
	 * GameLobbyController is the controller of the GameLobby.fxml screen
	 * @author Hendrik Diehl
	 */
	//gui
	@FXML private Label timeLabel;
	@FXML private JFXButton backButton;
	@FXML private VBox profile0;
	@FXML private VBox profile1;
	@FXML private VBox profile2;
	@FXML private VBox profile3;
	//network
	private LobbyServer server;
	private boolean isHost;
	
	
	/**
	 * constructor with information about the type of lobby screen => host or client 
	 * @param isHost true if open by a host
	 */
	public GameLobbyController(boolean isHost) {
		this.isHost = isHost;
	}
	/**
	 * handles the actionEvent of the backButton and calls the openScreen method with the Menu.fxml file
	 */
	@FXML private void backAction() { //need implementation of server shutdown, because server Thread is running after 
		this.shutdown();
		this.openScreen("Menu.fxml"); //windows is closed
	}
	
	/**
	 * initialize method from JavaFX, which starts the server procedure if the lobby is opened from a host
	 */
	@FXML private void initialize() {
		if(this.isHost) {
			this.isHost();
		}
	}
	/**
	 * method for loading a screen from an fxml file
	 * @param s path to the fxml file which should be loaded
	 */
	private void openScreen(String s) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(s));
			Stage stage = (Stage) this.backButton.getScene().getWindow();
			Scene scene;
			scene = new Scene(loader.load());
			stage.setScene(scene);
			stage.setTitle("Scrabble");
			stage.setHeight(600);
			stage.setWidth(800);
			stage.setResizable(false);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * method which shutdown the server
	 */
	@Override
	public void shutdown() { //Can also be used if no host
		if(server != null) {
			server.shutdown();
		}
		
	}
	/**
	 * method which set up the server
	 */
	private void isHost() {
			try {
				this.server = new LobbyServer(this);
				this.server.start();
				this.activatePortControlls();
			} catch (PortsOccupiedException e) {
				// TODO Auto-generated catch block
				this.timeLabel.setText(e.getMessage());
				this.activatePortControlls();
			}
			
	}
	/**
	 * method to show host specific options on the screen
	 */
	private void activatePortControlls() {
		//implement
	}
	/**
	 * method which sets specific player profiles visible
	 * @param number number of the profile which should be shown
	 */
	public void setProfileVisible(int number) {
		Platform.runLater(()-> {
			switch(number) {
			case 0: 
				this.profile0.setVisible(true);
				this.profile0.setDisable(false);
				break;
			case 1: 
				this.profile1.setVisible(true);
				this.profile1.setDisable(false);
				break;
			case 2: 
				this.profile0.setVisible(true);
				this.profile0.setDisable(false);
				break;
			case 3: 
				this.profile1.setVisible(true);
				this.profile1.setDisable(false);
				break;
			}
		});
	}
	/**
	 * method which sets specific player profiles invisible
	 * @param number number of the profile which should be shown
	 */
	public void setProfileNotVisible(int number) {
		Platform.runLater(()-> {
			switch(number) {
			case 0: 
				this.profile0.setVisible(false);
				this.profile0.setDisable(true);
				break;
			case 1: 
				this.profile1.setVisible(false);
				this.profile1.setDisable(true);
				break;
			case 2: 
				this.profile0.setVisible(false);
				this.profile0.setDisable(true);
				break;
			case 3: 
				this.profile1.setVisible(false);
				this.profile1.setDisable(true);
				break;
			}
		});
	}
	/**
	 * method to set up the timer label (time or other messages)
	 * @param msg message which should be shown at the top of the screen
	 */
	public void setTimeLabel(String msg) {
		Platform.runLater(() -> {
			this.timeLabel.setText(msg);
		});
	}
}
