package scrabble.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


/**
 * scrabble.network.Client class for Client connections to the Chat.
 * 
 * @author astarche
 * @author skeskinc
 */
public class Client extends Thread {
  private String hostName;
  private int port;
  private Socket socket = null;
  private BufferedReader fromServer = null;
  private PrintWriter toServer = null;
  private String username;
  private boolean running;
  private NetworkScreen client;

  /**
   * Constructor for test-application which is not implemented in actual game.
   * 
   * @param cc ChatController of test-application
   * @param username Name of the connected user
   * @author astarche
   * @author skeskinc
   */
  public Client(String username) {
    this.hostName = "localhost";
    this.port = 2222;
    this.username = username;
  }

  /**
   * Constructor to print chat Message to an GameLobby or an GameField Version for an Lobby Client.
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
  /**
   * Connecting to the Chat-Server.
   * 
   * @author astarche
   * @author skeskinc
   */
  public void connect() {
    try {
      socket = new Socket(hostName, port);
      System.out.println("Client started!");
      this.fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      this.toServer = new PrintWriter(socket.getOutputStream(), true);
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
   * Sends a message to everyone, that a player has joined the lobby.
   * 
   * @author skeskinc
   */
  public void sendJoinMessageToServer() {
    toServer.println(username + " has joined the Lobby.");
    toServer.flush();
  }

  /**
   * Sending messages to the Server.
   * 
   * @param message Sending given message to the server
   * @author astarche
   * @author skeskinc
   */
  public void sendMessageToServer(String message) {
    System.out.println("[" + username + "] " + message);
    toServer.println(username + ": " + message);
    toServer.flush();
  }

  /**
   * Sending a message to everyone, that a player has found specific word.
   * 
   * @param word Specific word that has been found in the game
   * @author skeskinc
   */
  public void sendWordMessageToServer(String word) {
    toServer.println(username + " has put a word: " + word + ".");
    toServer.flush();
  }

  /**
   * Sending a message to everyone, that a player is passing his turn.
   * 
   * @author skeskinc
   */
  public void sendPassToServer() {
    toServer.println(username + " passed his turn.");
    toServer.flush();
  }

  /**
   * Sending a message to everyone, that a player has left the game.
   * 
   * @author skeskinc
   */
  public void sendLeaveGameToServer() {
    toServer.println(username + " has left the game.");
    toServer.flush();
  }

  /**
   * Disconnecting from the Chat-Server.
   * 
   * @author astarche
   * @author skeskinc
   */
  public void disconnect() {
    this.running = false;
    try {
      this.socket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Replacing specific strings with Emoticons.
   * 
   * @param msg for current message
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

  /**
   * Handling Chat-Messages receiving from Server.
   * 
   * @author astarche
   * @author skeskinc
   */
  public void run() {
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
      } catch (IOException e) {
        // TODO Auto-generated catch block
        this.running = false;
        e.printStackTrace();
      }
    }
  }
}
