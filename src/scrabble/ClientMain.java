package scrabble;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import socket.Client;

public class ClientMain extends Application{
	
	@FXML private TextField userField;
	@FXML private Button submitUser;

	private Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setScene(initUser());
		
	}
	
	
	public Scene initUser() {
		Scene scene = null;
		try {
			Pane root = (Pane)FXMLLoader.load(getClass().getResource("Init.fxml"));
			scene = new Scene(root);		
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scene;
	}

	
	
	public static void main(String[] args) {
		launch(args);
	}
}
