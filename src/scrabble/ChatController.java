package scrabble;

import java.net.URL;
import java.util.ResourceBundle;

import com.sun.glass.events.KeyEvent;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scrabble.network.Client;

public class ChatController implements Initializable {
	@FXML
	private TextArea chatArea;
	@FXML
	private TextField insertionField;
	@FXML
	private Button enterButton;
	
	private Client myClient;
	
	
	@FXML
	public void enterMessage(ActionEvent event)  {
			if(myClient != null) {
			myClient.sendMessageToServer(this.getText());
			}
			this.insertionField.setText("");
			
		
	}

	public void applyMessageToArea(String message) {
		System.out.println("Third message: " + message);
		this.chatArea.appendText("Client Nr. " + message + "\n");


	}

	public String getText() {
		return insertionField.getText();
		
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		myClient = new Client(this);
		myClient.connect();
		myClient.start();
		
		
	}

}
