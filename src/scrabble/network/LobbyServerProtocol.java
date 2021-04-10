package scrabble.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;
import scrabble.model.MessageType;

public class LobbyServerProtocol extends Thread implements NetworkPlayer {
	/**
	 * Class which handle the communication with a client
	 * @author Hendrik Diehl
	 */
	//network
	private Socket client;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private LobbyServer corespondingServer;
	//controll
	private Boolean isRunning;
	private GameInformationController gameInfoController;
	private String player;
	/**
	 * constructor of the LobbyProtocol which are used to get in contact with players who want to join an Lobby
	 * or an game
	 * @param s Socket given by the Lobby Server, which are connected to an client
	 * @param gameLobby controller of the GameLobbyScreen
	 * @param gameInfoController controller which holds game specific information
	 */
	public LobbyServerProtocol(Socket s, LobbyServer server, GameInformationController gameInfoController) {
		try {
			this.corespondingServer = server;
			this.out = new ObjectOutputStream(s.getOutputStream());
			this.in = new ObjectInputStream(s.getInputStream());
			this.gameInfoController = gameInfoController;
			this.isRunning = true;
			this.player = "test player"; // need to be changed
			//System.out.println("Lobby server protocol created");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * run method of the thread which waits for an client message and react to specific MessageTypes
	 */
	public void run() {
		while(isRunning) {
			try { //after Server closed the loop is executed ? 
				//System.out.println("Protocol run pass");
				Object o = in.readObject();
				Message message = (Message) o;
				switch(message.getType()) {
				case JOIN :
					this.reactToJoin(message);
					break;
				case SHUTDOWN :
					this.reactToShutdown();
					break;
				}
			} catch(EOFException e) {
				this.isRunning = false;
				this.closeConnection();
			} catch (SocketException e) {
				this.closeConnection();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * method to react to a client which ends the connection
	 */
	private void reactToShutdown() {
		//System.out.println("Client shutdown message received");
		deletePlayer();
		//need to be deleted from server list
	}
	/**
	 * method to delete the specific protocol
	 */
	public void deletePlayer() {
		this.corespondingServer.deleteSpecificProtocol(this);
		this.gameInfoController.deletePlayer(this);
		//this.gameInfoController.updateAllLobbys();
		this.shutdown();
	}
	
	/**
	 * method which react to an JoinMessage of an User
	 * If the gameInformationController add the player information about the lobby are ended.
	 * If the gameInformationController not add the player the client will be rejected.
	 * @param message message which will be casted to the specific MessageType to get information
	 */
	private void reactToJoin(Message message) {
		//System.out.println("Client Join Message received");
		if(this.gameInfoController.addPlayer(this)) {
			System.out.println("Player added");
			//Needed ?
			//int i = this.gameInfoController.getPlayerPosition(this);
			//this.gameLobby.setProfileVisible(i);
			this.sendLobbyInformation();
			//Implement if for error code 5
		}
		else {
			System.out.println("Add rejected");
			this.sendRejectInfomation();
		}
	}
	/**
	 * method to send an rejection information to a client, a shutdown message is expected.
	 */
	private void sendRejectInfomation() {
		// TODO Auto-generated method stub
		Message msg = new Message(MessageType.REJECTED, "");
		try {
			this.out.writeObject(msg);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * method to send information about the Lobby to the client and also inform all players in the lobby about the
	 * joining, also himself
	 */
	private void sendLobbyInformation() {
		//System.out.println("Send lobby information after client add");
		Message msg = new Message(MessageType.ACEPTED, "");
		try {
			this.out.writeObject(msg);
			this.out.flush();
			//Update all Lobbys 
			this.gameInfoController.updateAllLobbys();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * method which sends the first information message for an client. Executed by the LobbyServerThread not this thread
	 * because of the call hierarchy. A Join message is expected.
	 */
	public void sendInformationMessage() {
		//System.out.println("Send basic lobby information");
		InformationMessage iM = new InformationMessage(MessageType.INFORMATION, "1", this.gameInfoController.getStatus(), this.gameInfoController.getPlayerAmount());
		try {
			this.out.writeObject(iM);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * method to close the connection to an client
	 */
	private void closeConnection() {
		try {
			if(this.client != null) {
				this.client.close(); //close also in and output stream
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * method to shutdown the protocol
	 */
	public void shutdown() {
		//System.out.println("Shutdown Protocol");
		//this.sendShutdownMsg(); //Problem two calls ?
		this.isRunning = false;
		this.closeConnection();
	}
	/**
	 * method to send a last shutdown message to the client, to give them the opportunity to handle. No respond expected.
	 */
	public void sendShutdownMsg() {
		System.out.println("Send shutdown message");
		Message msg = new Message(MessageType.SHUTDOWN, "");
		try {
			if(this.client != null && !this.client.isClosed()) { //Doesn't go in here ??????ß
				this.out.writeObject(msg);
				this.out.flush();
				System.out.println("Shutdown sended");
			} else { //only trying purpose !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Here problem in kick procedure 
				this.out.writeObject(msg);
				this.out.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void transformProtocol() {
		// TODO Auto-generated method stub
		//implemet
	}
	//here Problem with the null Pointer, player have to be set after first contact
	/**
	 * method to get the specific player class of a client.
	 */
	public String getPlayer() {
		return this.player;
	}
	/**
	 * method to send the client information about the Lobby, like player amount
	 */
	@Override
	public void updateLobbyinformation(ArrayList<String> players) {
		//System.out.println("Update the Lobby information");
		LobbyInformationMessage msg = new LobbyInformationMessage(MessageType.LOBBY, "", players);
		try {
			this.out.writeObject(msg);
			this.out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
