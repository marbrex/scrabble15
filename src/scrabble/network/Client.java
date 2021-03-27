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
	private ArrayList<ChatController> allController = new ArrayList<ChatController>();
	private String username;

	public Client(ChatController cc, String username) {
		this.hostName = "localhost";
		this.port = 2222;
		this.chatcontroller = cc;
		this.allController.add(chatcontroller);
		this.username = username;
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
		System.out.println("[" + username + "] " + message);
		toServer.println(message);
		toServer.flush();
		/*
		 * try { String text = fromServer.readLine(); for (ChatController cc :
		 * this.allController) { cc.applyMessageToArea(clientNumber + " : " + text); }
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
	}

	/*
	 * public String sendMessageToServer(String message) {
	 * System.out.println("Client: " + message); toServer.println(message);
	 * toServer.flush(); try { String text = fromServer.readLine(); return message;
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); return null; } }
	 */

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
		try {
			toServer.println(this.username);
			while (true) {
				String msg = fromServer.readLine();
				System.out.println("Third Message: " + msg);
				this.chatcontroller.applyMessageToArea(msg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
