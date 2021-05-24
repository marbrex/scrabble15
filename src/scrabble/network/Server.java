package scrabble.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;


/**
 * scrabble.network.Server to start Chat-Server and accept Chat-Clients.
 * 
 * @author astarche
 * @author skeskinc
 */
public class Server extends Thread {

  private ServerSocket serversocket;
  private Socket clientsocket;
  private int port;
  private int maxPlayers;
  private boolean running;
  static ArrayList<ServerProtocol> allClients = new ArrayList<ServerProtocol>();
  private int counter = 0;

  /**
   * Constructor initializing the serverSocket.
   * 
   * @author hendiehl
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
   * Method to get the connected Port after a Server was created.
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

  /**
   * Starting the Chat-Server.
   * 
   * @author astarche
   * @author skeskinc
   */
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
    } catch (SocketException e) {
      // this.closeProtocol();
      this.stopServer();
      // e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      this.running = false;
      e.printStackTrace();
    } catch (ArrayIndexOutOfBoundsException ae) {
      System.out.println("No valid port found.");
    }
  }

  /**
   * Closing all ChatServerProtocols.
   * 
   * @author skeskinc
   */
  public synchronized void closeProtocol() {
    for (ServerProtocol sp : allClients) {
      sp.disconnect();
    }
  }

  /**
   * Closing Socket of Chat-Server.
   * 
   * @author skeskinc
   */
  public void closeSocket() {
    try {
      this.serversocket.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Clearing protocol-list of Chat-Server.
   * 
   * @author skeskinc
   */
  public synchronized void clearProtocol() {
    allClients.clear();
  }

  /**
   * Stopping the Chat-Server.
   * 
   * @author skeskinc
   */
  public synchronized void stopServer() {

    this.running = false;
    this.closeSocket();
    this.closeProtocol();
    this.clearProtocol();
  }

  /**
   * Run Method for ChatServer-Thread.
   * 
   * @author hendiehl
   */
  public void run() {
    this.startServer();
  }
}
