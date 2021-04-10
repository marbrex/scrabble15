package scrabble;

import java.io.IOException;
import java.net.ConnectException;

import com.jfoenix.controls.JFXButton;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scrabble.network.LobbyClientProtocol;

public class GameFinderController implements LobbyController{
	/**
	 * Controller of the GameFinder.fxml screen
	 * @author Hendrik Diehl
	 */
	@FXML private JFXButton joinBtn;
	@FXML private TextField portField;
	@FXML private RadioButton useOwnPort;
	@FXML private Label statusLabel;
	@FXML private CheckBox portBox;
	@FXML private JFXButton backButton;
	@FXML private Label statusLabel2;
	private LobbyClientProtocol clientProtocol;
	
	
	
	
	/**
	 * method to activate the control fields for a specific port given by the user
	 * activates an TextField and show an Message on the screen
	 */
	private void activateOwnPortControlls() {
		this.portField.setDisable(false);
		this.portField.setVisible(true);
		this.statusLabel.setText("Please Type in port Number");
	}
	
	/**
	 * method to deactivate the control fields for a specific port given by the user
	 * deactivates an TextField and show an Message on the screen
	 */
	private void deactivateOwnPortControlls() {
		this.portField.setDisable(true);
		this.portField.setVisible(false);
		this.setStatusLabel("Press to search in network");
	}
	
	/**
	 * method to handle the ActionEvent of the joinBtn
	 */
	@FXML private void joinAction() {
		this.clientProtocol.sendJoinMessage();
	}
	
	/**
	 * method to show an specific string on the statusLabel
	 * @param message message to be shown
	 */
	public void setStatusLabel(String message) {
		Platform.runLater(()-> {this.statusLabel.setText(message);});
	}
	
	/**
	 * method to show an specific string on the statusLabel2
	 * @param message message to be shown
	 */
	public void setStatusLabel2(String message) {
		Platform.runLater(()-> {this.statusLabel2.setText(message);});
	}
	/**
	 * method to activate the joinBtn on the screen and show the accessibility on an port
	 */
	public void activateJoin() {
		this.joinBtn.setDisable(false);
		this.portBox.setSelected(true);
	}
	/**
	 * method to check if an String matches the characteristics of an port number
	 * @param port String representation of an user port
	 * @return boolean about the acceptance of the String parameter
	 */
	private boolean checkPortString(String port) {
		return port.matches("\\d{2,}");
	}
	
	/**
	 *  handles the actionEvent of the backButton and calls the openScreen method with the Menu.fxml file
	 */
	@FXML private void backButtonAction() {
		/*
			System.out.println("Back Button");
			this.shutdown();
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
			Stage stage = (Stage) this.joinBtn.getScene().getWindow();
			Scene scene;
			scene = new Scene(loader.load());
			stage.setScene(scene);
			stage.setTitle("Scrablle");
			stage.setHeight(700);
			stage.setWidth(900);
			stage.setResizable(false);
			stage.show();
			*/
			this.clientProtocol.shutdownProtocol(); //shutdown when in GameFinder screen
			goInMenu();
	}

	public void goInMenu(){
		Platform.runLater(()-> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Menu.fxml"));
				Parent root = loader.load();
				Stage stage = (Stage) this.backButton.getScene().getWindow();
				stage.setScene(new Scene(root, 900, 700));
				stage.setResizable(false);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	/**
	 * initialize method of javaFx in which the a joinProtokol automatically search for an game Lobby in an
	 * local Network. If an protocol is generated the joinBtn are activated. If not the option to input a user
	 * specific port is accessible. 
	 */
	@FXML private void initialize() {
		try {
			clientProtocol = new LobbyClientProtocol(this);
			System.out.println("GameFinderProtokol consructed");
			clientProtocol.start();
			System.out.println("GameFinderProtocol started");
			this.joinBtn.setDisable(false);
		} catch (IOException e) { //using of own exception needed
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("Exception");
			this.statusLabel.setText("No game at standart port");
			this.useOwnPort.setDisable(false);
		}
	}
	
	/**
	 * handles the useOwnPort ActionEvents and activates or deactivates the controls for an user input
	 */
	@FXML private void ownPortWanted() {
		//System.out.println("Selected");
		if(this.useOwnPort.isSelected()) {
			this.activateOwnPortControlls();
		}
		else {
			this.deactivateOwnPortControlls();
		}
	}
	/**
	 * handles the searchButton ActionEvent and try to create an instances of an GameFinderProtocol with an specific
	 * port given by the user
	 */
	@FXML private void searchAction() {
		if(this.checkPortString(this.portField.getText())) {
			int port = Integer.valueOf(this.portField.getText());
			try {
				clientProtocol = new LobbyClientProtocol(this, port);
				this.joinBtn.setDisable(false);
			} catch (ConnectException e) {
				System.out.println("No connection on own port");
				this.statusLabel.setText("No connection at this port");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("No connection on own port");
			}
		} else {
			this.setStatusLabel("Not a port");
		}
	}
	
	/**
	 * method to shutdown the client protocol
	 */
	@Override
	public void shutdown() {
		if(this.clientProtocol != null) {
			this.clientProtocol.sendShutdownMsg();
			this.clientProtocol.shutdownProtocol();
			System.out.println("Controller Shutdown");
		}
		
	}
	/**
	 * method to show port informations on the screen
	 * @param port specific port given by the user or one of the standard ports
	 */
	public void setPortInformation(int port) {
		Platform.runLater(()-> {
			this.portBox.setSelected(true);
			this.portBox.setText("Port : " + port);
		});
	}
	/**
	 * method to navigate from the GameFinder screen to the GameLobby screen as client
	 */
	public void goInLobby() {
			Platform.runLater(() -> {
				try {/*
					FXMLLoader loader = new FXMLLoader(getClass().getResource("GameLobby.fxml"));
					loader.setControllerFactory(c -> {
						return new GameLobbyController(false);
					});
					GameLobbyController lobbyController;
					Stage stage = (Stage) this.joinBtn.getScene().getWindow();
					Scene scene;
					scene = new Scene(loader.load());
					lobbyController = loader.<GameLobbyController>getController();
					lobbyController.setProtocol(this.clientProtocol);
					if(lobbyController == null) {
						System.out.println("LobbyController == null");
					}
					this.clientProtocol.setLobbyController(lobbyController);
					stage.setScene(scene);
					stage.setTitle("Scrablle");
					stage.setHeight(700);
					stage.setWidth(900);
					stage.setResizable(false);
					stage.show();
					*/
					FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/GameLobby.fxml"));
					loader.setControllerFactory(c -> {
						return new GameLobbyController(false);
					});
					Parent root = loader.load();
					GameLobbyController lobbyController = loader.<GameLobbyController>getController();
					lobbyController.setProtocol(this.clientProtocol);
					this.clientProtocol.setLobbyController(lobbyController);
					Stage stage = (Stage) this.joinBtn.getScene().getWindow();
					stage.setScene(new Scene(root, 900, 700));
					stage.setResizable(false);
					stage.setOnHidden(e -> {lobbyController.shutdown();});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
	}
	
}
