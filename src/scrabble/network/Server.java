package scrabble.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.ArrayList;
import scrabble.network.*;

public class Server extends Thread {

  private ServerSocket serversocket;
  private Socket clientsocket;
  private int port, maxPlayers;
  private boolean running;
  static ArrayList<ServerProtocol> allClients = new ArrayList<ServerProtocol>();
  private int counter = 0;

  /**
   * Constructor initializing the serverSocket
   */
  public Server() {
    this.maxPlayers = 4;
    try {
      this.serversocket = new ServerSocket(0);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to get the connected Port after a Server was created
   * 
   * @return port of the Chat server, 0 if serverSocket isn't created
   * @author hendiehl
   */
  protected int getServerPort() {
    if (this.serversocket != null) {
      return this.serversocket.getLocalPort();
    } else {
      return 0;
    }
  }

  public void startServer() {
    try {
      this.running = true;
      System.out.println("Server is running!");
      while (running) {
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
      this.running = false;
      if (!this.serversocket.isClosed()) {
        this.serversocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void run() {
    this.startServer();
  }

}
