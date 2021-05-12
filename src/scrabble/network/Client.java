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
  private boolean running;
  /** protocol instance for an corresponding chat */
  private NetworkScreen client;

  public Client(ChatController cc, String username) {
    this.hostName = "localhost";
    this.port = 2222;
    this.chatcontroller = cc;
    this.allController.add(chatcontroller);
    this.username = username;
  }

  /**
   * Constructor to print chat Message to an GameLobby or an GameField Version for an Lobby Client
   * 
   * @param client protocol of the corresponding user
   * @param port port on which the Chat server listen -> given by the network protocol
   * @param username name of the Human Player instance
   * @author hendiehl
   */
  public Client(NetworkScreen client, int port, String username) {
    this.port = port;
    this.hostName = "localhost";
    this.client = client;
    this.username = username;
  }

  // new Message have only call the print chatMessage of the LobbyClientProtocol
  public void connect() {
    try {
      c = new Socket(hostName, port);
      System.out.println("Client started!");
      this.fromServer = new BufferedReader(new InputStreamReader(c.getInputStream()));
      this.toServer = new PrintWriter(c.getOutputStream(), true);
      running = true;
    } catch (IOException e) {
      e.printStackTrace();
      this.disconnect();
    }
  }

  /**
   * Sends a message to everyone, that a player got kicked from the lobby.
   * 
   * @author skeskinc
   */
  public void sendKickToServer() {
    toServer.println(username + " has been kicked from the Lobby.");
    toServer.flush();
  }

  /**
   * Sends a message to everyone, that a player has left the lobby.
   * 
   * @author skeskinc
   */
  public void sendLeaveMessageToServer() {
    toServer.println(username + " has left the Lobby.");
    toServer.flush();
  }

  /**
   * Sends a message to everyone, that a player has joined the lobby
   * 
   * @author skeskinc
   */
  public void sendJoinMessageToServer() {
    toServer.println(username + " has joined the Lobby.");
    toServer.flush();
  }


  public void sendMessageToServer(String message) {
    System.out.println("[" + username + "] " + message);
    toServer.println(username + ": " + message);
    toServer.flush();
    /*
     * try { String text = fromServer.readLine(); for (ChatController cc : this.allController) {
     * cc.applyMessageToArea(clientNumber + " : " + text); }
     * 
     * } catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
     */
  }

  /*
   * public String sendMessageToServer(String message) { System.out.println("Client: " + message);
   * toServer.println(message); toServer.flush(); try { String text = fromServer.readLine(); return
   * message;
   * 
   * } catch (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); return null;
   * } }
   */

  public void disconnect() {
    this.running = false;
    try {
      this.c.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param msg for the current message
   * @return the message replaced by emoticons.
   * @author skeskinc
   */
  public String useEmoticons(String msg) {
    msg = msg.replaceAll(":D", "ðŸ˜�");
    msg = msg.replaceAll(":'D", "ðŸ˜‚");
    msg = msg.replaceAll("D:", "ðŸ˜©");
    msg = msg.replaceAll(":clap", "ðŸ™�");
    return msg;
  }

  public void run() {
    // toServer.println(this.username);
    String msg;
    while (running) {
      try {
        msg = fromServer.readLine();
        if (msg != null) {
          this.client.printChatMessage(msg); // printing
        }
      } catch (NullPointerException e) {
        this.disconnect();
      } catch (SocketException e) {
        this.disconnect();
        // e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        this.running = false;
        e.printStackTrace();
      }

    }
  }
}
