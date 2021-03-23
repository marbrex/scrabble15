package scrabble.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import scrabble.ChatController;
import javafx.application.Platform;

public class Client extends Thread {
	private String hostName;
	private int port;
	private Socket c = null;
	private BufferedReader fromServer = null;
	private PrintWriter toServer = null;
	private ChatController chatcontroller;

	public Client(ChatController cc) {
		this.hostName = "localhost";
		this.port = 2222;
		this.chatcontroller = cc;
	}

	public void connect() {
		try {
			c = new Socket(hostName, port);
			System.out.println("Client started!");
			this.fromServer = new BufferedReader(new InputStreamReader(c.getInputStream()));
			this.toServer = new PrintWriter(c.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
			this.disconnect();
		}
	}

	public void sendMessageToServer(String message) {
		System.out.println("Client: " + message);
		toServer.println(message);
		toServer.flush();
	}
	
	public void disconnect() {
		try {
			this.toServer.close();
			this.fromServer.close();
			this.c.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			try {
				String msg = fromServer.readLine();
		//		System.out.println("Third Message: " + msg);
				this.chatcontroller.applyMessageToArea(msg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
