package scrabble;

import java.net.URL;
import java.util.ResourceBundle;
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

//	private String text;

	private static ChatController cl;
	
	private Client myClient;
	
	protected static String username;
	
	
	
	
//	public void setUsername(String username) {
//		this.username = username;
//	}
	
	
	@FXML
	public void enterMessage(ActionEvent event)  {
			if(myClient != null) {
			myClient.sendMessageToServer(this.getText());
			}
			this.insertionField.setText("");
			
		
	}

	public void applyMessageToArea(String message) {
		System.out.println("Third message: " + message);
		this.chatArea.appendText(message + "\n");


	}

	public String getText() {
		return insertionField.getText();
		
	}

	
	public static ChatController getChatController() {
		return cl;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		myClient = new Client(this, username);
		myClient.connect();
		myClient.start();
		
		
	}

}