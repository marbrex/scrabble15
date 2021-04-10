package scrabble.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;

public class LobbyServer extends Thread {
	/**
	 * Class of the lobby server which is responsible for a network game.
	 * @author Hendrik Diehl
	 */
	//network
	private int port = 11111;
	private ServerSocket server;
	private boolean isRunning;
	//gui
	private GameLobbyController gameLobby;
	//controll
	private GameInformationController gameInfoController;
	private ArrayList<LobbyServerProtocol> clients;
	private LobbyHostProtocol host;
	
	
	//constructor with port assignment if occupied
	/**
	 * Constructor which will inform the player if the server can run on a standard port.
	 * @param gameLobby controller of the GameLobby screen
	 * @throws PortsOccupiedException exception thrown if no standard port is available 
	 */
	public LobbyServer(GameLobbyController gameLobby) throws PortsOccupiedException{
		//Setting up server port
		this.setServer();
		//setting up controls
		this.gameLobby = gameLobby;
		this.clients = new ArrayList<LobbyServerProtocol>();
		this.gameInfoController = new GameInformationController(this);
		this.host = new LobbyHostProtocol(gameLobby, gameInfoController);
		this.gameLobby.setHostProtocol(this.host);
		this.gameInfoController.addPlayer(host);
		this.gameLobby.setProfileVisible(0, "Host"); //change to protocol call in future?? 
	}
	/**
	 * Method to set the server port
	 * @throws PortsOccupiedException thrown when all ports occupied
	 */
	private void setServer() throws PortsOccupiedException { //can be used in the run method not in the constructor, perhaps faster loading
		while(!isRunning) {
			try {
				this.server = new ServerSocket(port);
				this.isRunning = true; //Schleife brechen
				System.out.println("GameLobbyServer Constructor choosen port : " + this.port);
			} catch (SocketException e) {
				//Socket in use
				if(this.port < 11131) { //try standard ports
					this.port++; //increase port number
				} else {
					throw new PortsOccupiedException("Standart ports are occupied");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Run method of the thread class in which the server will wait until a client try to connect.
	 * A connecting client is add to a list and a LobbyServerProtocol will start.
	 */
	public void run() {
		//System.out.println("GameLobbyServerStart");
		while(isRunning) {
			try {
				System.out.println("GameLobbyServer run pass");
				System.out.println(this.clients.size());
				Socket s = this.server.accept();
				LobbyServerProtocol lobbyProtocol = new LobbyServerProtocol(s, this, gameInfoController);
				//System.out.println("LobbyProtocol constructor");
				this.getInContact(lobbyProtocol);
			} catch (SocketException e) {
				//do something
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("GameLobbyServerStopped");
	}
	/**
	 * Method to close the connection of the server
	 */
	private void closeConnection() {
		try {
			this.server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Method which will shutdown the server
	 */
	public void shutdown() {
		this.isRunning = false;
		this.closeAllProtocols();
		this.closeConnection();
		
	}
	/**
	 * Method which will call a the shutdown procedure for all connected clients.
	 */
	private void closeAllProtocols() { //need of deleting protocols
		for(LobbyServerProtocol lP : this.clients) {
			lP.sendShutdownMsg();
			lP.shutdown();
		}
	}
	/*
	 * Method which will start the connection with a connecting client
	 */
	private void getInContact(LobbyServerProtocol lsp) {
		this.clients.add(lsp);
		lsp.start();
		System.out.println("LobbyProtocol started");
		lsp.sendInformationMessage();
		System.out.println("Information sended");
	}
	/**
	 * method which will remove a specific server protocol from the server.
	 * @param lsp LobbyServerProtocol which will be removed from the client list.
	 */
	public void deleteSpecificProtocol(LobbyServerProtocol lsp) {
		if(this.clients.contains(lsp)) {
			this.clients.remove(lsp);
		}
	}
	
}
