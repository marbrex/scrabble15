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

import scrabble.model.HumanPlayer;
import scrabble.model.Player;
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
	/** socket conection to the server */
	private Socket server;
	/** output to the server */
	private ObjectOutputStream out;
	/** input to the server */
	private ObjectInputStream in;
	/** standard connection port */
	private int port = 11111;
	/** network address for local network connections */
	private String adress ="localhost";
	/** own port number if wanted */
	private int ownPort;
	//Control
	/** Control boolean for run method */
	private boolean isRunning;
	/** list of all Players in the lobby */
	private ArrayList<Player> lobbyPlayers;
	//Gui
	/** controller of the gameFinder screen */
	private GameFinderController gameFinderController;
	/** controller of the GameLobby not loaded before lobby join */
	private GameLobbyController gameLobbyController;
	/** player instance of the client is allays human player */
	private HumanPlayer player;
	
	
	
	/**
	 * Constructor for a GameFinderProtocol, which try to connect with an lobby server in the network on 20 specific client known
	 * ports. If no server is found the Constructor throws an Exception to contact the calling GameFinderController
	 * @param controller Controller of the GameFinder screen
	 * @throws ConnectException thrown if connection on the known ports are not possible
	 */
	public LobbyClientProtocol(GameFinderController controller){
		this.gameFinderController = controller;
		this.loadPlayer();
	}
	/**
	 * method to get player instance from DB
	 */
	private void loadPlayer() { //not implemented yet
		this.player = new HumanPlayer();
		this.player.setName("Client");
		//dummy
	}
	/**
	 * method to set the connection on the standard ports
	 * @throws ConnectException
	 */
	private void setSocket() throws PortsOccupiedException{ //change to ports occupied exception 
		while(!this.isRunning) {
			try {
				this.server = new Socket(adress, port);
				this.in = new ObjectInputStream(server.getInputStream());
				this.out = new ObjectOutputStream(server.getOutputStream());
				this.isRunning = true;
				System.out.println("Connected with socket : " + this.port);
			} catch(ConnectException e) {
				System.out.println("No connection at port " + this.port);
				if(this.port < 11131) { //In connection with the standart port (+20)
					this.port++;
				} else {
					throw new PortsOccupiedException("Standard ports are occupied");
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				this.isRunning = false; //?????????????
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.isRunning = false; //?????????????????
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
		this.server = new Socket(adress, ownPort);
		this.in = new ObjectInputStream(server.getInputStream());
		this.out = new ObjectOutputStream(server.getOutputStream());
		this.isRunning = true;
	}
	/**
	 * run method of the thread class. The thread is waiting for messages from the server and react to an specific MessageType
	 */
	public void run() {
		try {
			this.setSocket();
			this.gameFinderController.connectSucessful();
		} catch (PortsOccupiedException e1) {
			this.gameFinderController.connectNotSucessful();
		}
		while(isRunning) {
			react();
		}
		System.err.println("CLIENT PROTOCOL OUTRUN");
	}
	/**
	 * method to react to an incoming message.
	 */
	private void react() {
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
			case KICK :
				this.reactToKick(message);
				break;
			case REJECTED :
				this.reactRejected(message);
				break;
			case FULL : 
				this.reactToFullMessage(message);
				break;
			}
		} catch (EOFException e) {
			this.shutdownProtocol(true);
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
	/**
	 * method to react to the start of an full lobby procedure
	 * @param message message from the server
	 */
	private void reactToFullMessage(Message message) {
		if(this.gameLobbyController != null) {
			this.gameLobbyController.startTimer();
			//start procedure -> not implemented yet
		}
		
	}
	/**
	 * method to react to an rejection message
	 * @param message message of the server
	 */
	private void reactRejected(Message message) {
		this.shutdownProtocol(true);
		
	}
	private void reactToKick(Message message) {
		System.out.println("Kick message received");
		this.shutdownProtocol(false);
		if(this.gameLobbyController != null) {
			this.gameLobbyController.openMenu();
		} else {
			this.gameFinderController.openMenu();
		}
	}
	/**
	 * method to react to a shutdown message, no response expected
	 */
	private void reactToShutdown() {
		this.shutdownProtocol(false);
		if(this.gameLobbyController != null) {
			this.gameLobbyController.openMenu();
		} else {
			this.gameFinderController.openMenu();
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
	private void closeConnection() { //change !!!!!!!!!!!!!!!!!
		try { //if a input or output stream is closed the other close himself and the close method throw the exception
			if(this.server != null) {
				this.server.close();
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
	public void shutdownProtocol(boolean selfcall) { //In future change to an approach with an boolean condition about call or selfcall
		this.isRunning = false;
		if(selfcall) {
			this.sendShutdownMsg(); //last message
		}
		this.closeConnection();
		System.out.println("GameFinderProtocol shutdown");
	}
	/**
	 * method to change the controller of the screen when new window is loaded
	 * @param glc
	 */
	public void setLobbyController(GameLobbyController glc) {
		this.gameFinderController = null;
		this.gameLobbyController = glc;
		if(glc != null) {
			System.out.println("Controller not null");
		}
		this.updateLobbyinformation();
	}
	/**
	 * method to update the visibility of the player profiles of the GameLobby screen.
	 */
	private void updateLobbyinformation() {
		this.gameLobbyController.resetProfileVisibility();
		for(int i = 0; i < lobbyPlayers.size(); i++) {
			System.err.println("Update gui :" + i);
			this.gameLobbyController.setProfileVisible(i, lobbyPlayers.get(i).getName());
		}
		
	}
	/**
	 * method which will inform the server that the client want to join the lobby.
	 */
	public void sendJoinMessage() {
		Message msg = new Message(MessageType.JOIN, this.player);
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
		Message msg = new Message(MessageType.SHUTDOWN, this.player);
		try {
			if(this.server != null) {
				if(!this.server.isClosed()) {
					this.out.writeObject(msg);
					this.out.flush();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
