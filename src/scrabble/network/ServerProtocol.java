package scrabble.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * scrabble.network.ServerProtocol to receive and send Client messages.
 * 
 * @author astarche
 * @author skeskinc
 */
public class ServerProtocol extends Thread {

  private Socket socket;
  private BufferedReader fromClient;
  private PrintWriter toClient;
  private Server myServer;
  private int clientNumber;
  private boolean running;

  /**
   * Constructor of the ServerProtocol.
   * 
   * @param server Current Chat-Server
   * @param socket Current Client-socket to work with
   * @param clientNumber Current Number of the client to identify
   * @author skeskinc
   */
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

  /**
   * Receiving and sending Chat-Messages.
   * 
   * @author astarche
   * @author skeskinc
   */
  public void run() {
    String message;
    while (running) {
      try {
        message = fromClient.readLine();
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
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Closing ServerProtocol-socket.
   * 
   * @author skeskinc
   */
  public void disconnect() {
    this.running = false;
    try {
      this.socket.close();
    } catch (SocketException e) {
      // Do nothing
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
