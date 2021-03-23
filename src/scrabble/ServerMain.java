package scrabble;

import javafx.application.Application;
import javafx.stage.Stage;
import scrabble.network.*;

public class ServerMain extends Application {

	public void start(Stage primaryStage) {
		try {
			new Server();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
