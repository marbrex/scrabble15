package scrabble.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;


import scrabble.GameFinderController;
import scrabble.GameLobbyController;
import scrabble.model.GameStatusType;
import scrabble.model.MessageType;

public class LobbyClientProtocol extends Thread {
	/**
	 * Class of a client which handle the communication with a server.
	 * @author Hendrik Diehl
	 */
	//network
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int port = 11111;
	private String adress ="localhost";
	private int ownPort;
	//Controll
	private boolean isRunning;
	private ArrayList<String> lobbyPlayers;
	//Gui
	private GameFinderController gameFinderController;
	private GameLobbyController gameLobbyController;
	
	
	
	/**
	 * Constructor for a GameFinderProtocol, which try to connect with an lobby server in the network on 20 specific client known
	 * ports. If no server is found the Constructor throws an Exception to contact the calling GameFinderController
	 * @param controller Controller of the GameFinder screen
	 * @throws ConnectException thrown if connection on the known ports are not possible
	 */
	public LobbyClientProtocol(GameFinderController controller) throws ConnectException{
		this.gameFinderController = controller;
		this.setSocket();
	}
	private void setSocket() throws ConnectException {
		while(!this.isRunning) {
			try {
				this.client = new Socket(adress, port);
				this.in = new ObjectInputStream(client.getInputStream());
				this.out = new ObjectOutputStream(client.getOutputStream());
				this.isRunning = true;
				System.out.println("Verbunden mit Socket : " + this.port);
			} catch(ConnectException e) {
				System.out.println("Keine Verbindung auf Port " + this.port);
				if(this.port < 11131) { //In connection with the standart port (+20)
					this.port++;
				} else {
					throw e;
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				this.isRunning = false;
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.isRunning = false;
				e.printStackTrace();
			}
		}
	}
	/**
	 * Constructor for an specific Client known port for an lobby server
	 * @param controller controller of the GameFinder screen
	 * @param ownPort port given by the user
	 * @throws ConnectException
	 * @throws IOException
	 */
	public LobbyClientProtocol(GameFinderController controller, int ownPort) throws ConnectException, IOException {
		this.gameFinderController = controller;
		this.client = new Socket(adress, ownPort);
		this.in = new ObjectInputStream(client.getInputStream());
		this.out = new ObjectOutputStream(client.getOutputStream());
		this.isRunning = true;
	}
	/**
	 * run method of the thread class. The thread is waiting for messages from the server and react to an specific MessageType
	 */
	public void run() {
		while(isRunning) {
			try {
				//System.out.println("Run pass");
				Object o = in.readObject();
				//System.out.println("Object received");
				Message message = (Message) o;
				//System.out.println("Message casted");
				switch(message.getType()) {
				case INFORMATION :
					this.reactToInformation(message);
					break;
				case SHUTDOWN :
					this.reactToShutdown();
					break;
				case ACEPTED :
					this.reactToAcepted(message);
					break;
				case LOBBY :
					this.reactToLobby(message);
					break;
				}
			} catch (EOFException e) {
				this.shutdownProtocol();
			} catch (SocketException e) {
				//do something
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.isRunning = false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * method to react to a shutdown message, no response expected
	 */
	private void reactToShutdown() {
		System.err.println("Shutdown wanted from server");
		this.isRunning = false;
		this.closeConnection();
		if(this.gameLobbyController != null) {
			this.gameLobbyController.openMenu();
		} else {
			this.gameFinderController.goInMenu();
		}
		
	}
	/**
	 * method to react to lobby informations of the server. Will update the screen in consequence.
	 * @param message
	 */
	private void reactToLobby(Message message) {
		// TODO Auto-generated method stub
		//System.out.println("Lobby information received");
		LobbyInformationMessage msg = (LobbyInformationMessage) message;
		//this.gameFinderController.goInLobby();
		this.lobbyPlayers = msg.getPlayers();
		System.err.println("LOBBY UPDATE : " + this.lobbyPlayers.size());
		if(msg.getPlayers() == null) {
			System.err.println("null list from server !!!!!!!11");
		}
		//Hier kommt leere List an ????????????????????????????????
		//null pointer because of run later, need a call back after Lobby is created
		//here past position of null set of finderController
		if(this.gameLobbyController != null) {
			this.updateLobbyinformation();
		}
	}
	/**
	 * method which react to an acceptance of a server and transform the screen in an GameLobby screen.
	 * @param message
	 */
	private void reactToAcepted(Message message) {
		// TODO Auto-generated method stub
		//System.out.println("Join acceptence received");
		this.gameFinderController.goInLobby();
	}
	
	/**
	 * method for reacting on information message from server and show them on the gui
	 * @param message message from the server which will be castes to the specific type
	 */
	private void reactToInformation(Message message) {
		//System.out.println("Information Message received");
		InformationMessage iM = (InformationMessage) message;
		GameStatusType status = iM.getStatus();
		int amount = iM.getLobbyPlayers();
		this.gameFinderController.setStatusLabel("In : " + status.name());
		this.gameFinderController.setStatusLabel2("Amount of Players : " + amount);
		this.gameFinderController.setPortInformation(this.port);
	}
	
	/**
	 * method to close the connection of the protocol
	 */
	private void closeConnection() {
		try { //if a input or output stream is closed the other close himself and the close method throw the exception
			if(this.client != null) {
				this.client.close();
			}
			//System.out.println("connection closed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * method to end the running thread and close the connection
	 */
	public void shutdownProtocol() { //In future change to an approach with an boolean condition about call or selfcall
		this.isRunning = false;
		this.sendShutdownMsg(); //last message
		this.closeConnection();
		System.out.println("GameFinderProtocol shutdown");
	}
	/**
	 * method to change the controller of the screen when new window is loaded
	 * @param glc
	 */
	public void setLobbyController(GameLobbyController glc) {
		//glc is null by method, but why
		this.gameFinderController = null;
		this.gameLobbyController = glc;
		if(glc != null) {
			System.out.println("Controller not null");
		}
		//System.out.println("Lobby controller are set");
		this.updateLobbyinformation();
		//System.out.println("First Lobby informations are set");
	}
	/**
	 * method to update the visibility of the player profiles of the GameLobby screen.
	 */
	private void updateLobbyinformation() {
		//System.out.println("Client updated lobby informations");
		for(int i = 0; i < 4; i++) {
			this.gameLobbyController.setProfileNotVisible(i);
		}
		for(int i = 0; i < lobbyPlayers.size(); i++) {
			System.err.println("Update gui :" + i);
			this.gameLobbyController.setProfileVisible(i, lobbyPlayers.get(i));
		}
		
	}
	/**
	 * method which will inform the server that the client want to join the lobby.
	 */
	public void sendJoinMessage() {
		Message msg = new Message(MessageType.JOIN, "");
		try {
			this.out.writeObject(msg);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * method to send a last shutdown message to the client, to give them the opportunity to handle. No respond expected.
	 */
	public void sendShutdownMsg() {
		//System.out.println("Send shutdown message");
		Message msg = new Message(MessageType.SHUTDOWN, "");
		try {
			if(!this.client.isClosed()) {
				this.out.writeObject(msg);
				this.out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

