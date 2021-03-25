package scrabble.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import socket.*;

public class Server {

	private ServerSocket serversocket;
	private Socket clientsocket;
	private int port, maxPlayers;
	static ArrayList<ServerProtocol> allClients = new ArrayList<ServerProtocol>();
	private int counter = 0;

	public Server() {
		this.port = 2222;
		this.maxPlayers = 4;
		startServer(port, maxPlayers);
	}

	public void startServer(int port, int maxPlayers) {
		this.maxPlayers = maxPlayers;
		this.port = port;
		try {
			this.serversocket = new ServerSocket(port);
			System.out.println("Server is running!");
			while (true) {
				this.clientsocket = serversocket.accept();
				
				ServerProtocol sp = new ServerProtocol(this, clientsocket, ++counter);
				allClients.add(sp);
				sp.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ArrayIndexOutOfBoundsException ae) {
			System.out.println("No valid port found.");
		}

	}

	public void stopServer() {
		try {
			if (!this.serversocket.isClosed()) {
				this.serversocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
