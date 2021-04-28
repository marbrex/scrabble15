package scrabble.network;

import java.io.*;
import java.net.*;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import scrabble.ChatController;
import scrabble.ClientMain;
import javafx.application.Platform;

public class ServerProtocol extends Thread {

	private Socket socket;
	private BufferedReader fromClient;
	private PrintWriter toClient;
	private Server myServer;
	private int clientNumber;

	public ServerProtocol(Server server, Socket socket, int clientNumber) {
		try {
			this.myServer = server;
			this.socket = socket;
			this.fromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.toClient = new PrintWriter(this.socket.getOutputStream(), true);
			this.clientNumber = clientNumber;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void run() {
		try {
			String username = fromClient.readLine();
			String message = "";
			while (true) {
				message = fromClient.readLine();
				//System.out.println("Second Message: " + message);
//					System.out.println("Size of Clients: " + Server.allClients.size());
				for (ServerProtocol sp : Server.allClients) {
					toClient = new PrintWriter(sp.socket.getOutputStream());
					toClient.println(username + ": " + message);
					toClient.flush();
				}

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			this.toClient.close();
			this.fromClient.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
