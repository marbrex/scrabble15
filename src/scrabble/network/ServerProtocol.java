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
  private boolean running;

  public ServerProtocol(Server server, Socket socket, int clientNumber) {
    try {
      this.myServer = server;
      this.socket = socket;
      this.fromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.toClient = new PrintWriter(this.socket.getOutputStream(), true);
      this.clientNumber = clientNumber;
      this.running = true;

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public void run() {
    // String username = fromClient.readLine();
    String message;
    while (running) {
      try {
        message = fromClient.readLine();
        // System.out.println("Second Message: " + message);
        // System.out.println("Size of Clients: " + Server.allClients.size());
        if (message != null) {
          for (ServerProtocol sp : Server.allClients) {
            toClient = new PrintWriter(sp.socket.getOutputStream());
            toClient.println("" + message);
            toClient.flush();
          }
        }
      } catch (NullPointerException e) {
        this.disconnect();
      } catch (SocketException e) {
        this.disconnect();
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void closeSocket() {
    try {
      this.socket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void disconnect() {
    this.running = false;
    try {
      this.socket.close();
      // System.out.println("ServerProtocol: disconnect");
    } catch (SocketException e) {
      // TODO Auto-generated catch block

      // e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
