package scrabble;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class InitController {
	
	@FXML private TextField userField;
	@FXML private Button submitUser;
	
	@FXML
	public void submitUser(ActionEvent e) {
		ChatController.username = this.userField.getText();
		Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		window.setScene(mainWindow());
		window.show();
	}
	
	
	public Scene mainWindow() {
		Scene scene = null;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("fxml/ChatScene.fxml"));
			Pane root = loader.load();
			scene = new Scene(root,600,450);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return scene;
	}
}