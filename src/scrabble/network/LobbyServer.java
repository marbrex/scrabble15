package scrabble.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;

public class LobbyServer extends Thread {
  /**
   * Class of the lobby server which is responsible for a network game.
   * 
   * @author hendiehl
   */
  /** Standard port for an network game */
  private int port = 11111;
  /** Server socket for the server */
  private ServerSocket server;
  /** Control boolean for thread run */
  private boolean isRunning;
  /** Corresponding controller of an GameLobby */
  private GameLobbyController gameLobby;
  /** ChatServer for lobby and game communication */
  private Server chat;
  /** port on which the chatServer is running */
  private int chatPort;
  // Control
  /** controller of the game information */
  private GameInformationController gameInfoController;
  /** All connected clients, also ones only in GameFinder */
  private ArrayList<LobbyServerProtocol> clients;
  /** protocol of the game host */
  private LobbyHostProtocol host;


  // constructor with port assignment if occupied
  /**
   * Constructor which will inform the player if the server can run on a standard port.
   * 
   * @param gameLobby controller of the GameLobby screen
   * @throws PortsOccupiedException exception thrown if no standard port is available
   */
  public LobbyServer(GameLobbyController gameLobby) {
    // setting up controls
    initializeParts(gameLobby);
  }

  /**
   * method to initialize important parts of the server
   * 
   * @param gameLobby controller of the corresponding GameLobby
   */
  private void initializeParts(GameLobbyController gameLobby) {
    this.gameLobby = gameLobby;
    this.clients = new ArrayList<LobbyServerProtocol>();
    this.gameInfoController = new GameInformationController(this);
    this.host = new LobbyHostProtocol(gameLobby, gameInfoController);
    // Perhaps the host initializing in the run thread because of the DB connection (slow ?)
    this.gameLobby.setHostProtocol(this.host);
    this.gameLobby.setChatUser(this.host);
    this.gameInfoController.addPlayer(host);
    this.gameLobby.setProfileVisible(0, this.host.getPlayer().getName()); // change to protocol call
                                                                          // in future??
    //
  }

  /**
   * Method to set the server port
   * 
   * @throws PortsOccupiedException thrown when all ports occupied
   */
  private void setServer() throws PortsOccupiedException { // can be used in the run method not in
                                                           // the constructor, perhaps faster
                                                           // loading
    while (!isRunning) {
      try {
        this.server = new ServerSocket(port);
        this.isRunning = true; // breaking loop
        System.err.println("SERVER : Choosen port : " + this.port);
      } catch (SocketException e) {
        // Socket in use
        if (this.port < 11131) { // try standard ports
          this.port++; // increase port number
        } else {
          throw new PortsOccupiedException("Standart ports are occupied");
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  /**
   * Run method of the thread class in which the server will wait until a client try to connect. A
   * connecting client is add to a list and a LobbyServerProtocol will start.
   */
  public void run() {
    try {
      this.setServer();
      this.startChatServer();
    } catch (PortsOccupiedException e) {
      this.gameLobby.setTimeLabel(e.getMessage());
      // Here own port control should be allowed
      // this.gameLobby.activateConfigureControlls(); //old
    }
    while (isRunning) {
      react();
    }
    System.err.println("SERVER OUTRUN");
  }

  /**
   * method to accept a client
   */
  private void react() {
    try {
      Socket s = this.server.accept();
      System.out.println("SERVER : Client accepted");
      LobbyServerProtocol lobbyProtocol = new LobbyServerProtocol(s, this, gameInfoController);
      this.getInContact(lobbyProtocol);
    } catch (SocketException e) {
      // do something
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to close the connection of the server
   */
  private void closeConnection() { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    try {
      this.server.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method which will shutdown the server
   */
  public void shutdown() { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! critical
    this.isRunning = false;
    this.gameInfoController.shutdown();
    this.closeAllProtocols();
    this.closeConnection();
  }

  /**
   * Method which will call a the shutdown procedure for all connected clients.
   */
  private void closeAllProtocols() { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! critical
    for (LobbyServerProtocol lP : this.clients) {
      lP.sendShutdownMsg(); // here changing of the shutdown class
      lP.shutdownProtocol(true);
    }
  }

  /*
   * Method which will start the connection with a connecting client
   */
  private void getInContact(LobbyServerProtocol lsp) {
    this.clients.add(lsp);
    lsp.start();
    lsp.sendInformationMessage();
  }

  /**
   * method which will remove a specific server protocol from the server.
   * 
   * @param lsp LobbyServerProtocol which will be removed from the client list.
   */
  public void deleteSpecificProtocol(LobbyServerProtocol lsp) {
    if (this.clients.contains(lsp)) {
      this.clients.remove(lsp);
    }
  }

  /**
   * Method to start the chat server
   */
  private void startChatServer() {
    this.chat = new Server();
    this.chat.start();
    this.chatPort = this.chat.getServerPort(); // Perhaps the port isn't set yet ?
    System.out.println("SERVER : Chat server started on port : " + this.chatPort);
    this.host.startChatClient(this.chatPort); // Port isn't set, because thread start is slower than
                                              // main execution
  }

  /**
   * Method to get the port the chat server is running in reason to send it to a client joining the
   * lobby
   * 
   * @return
   */
  protected Integer getChatPort() {
    return this.chatPort;
  }
}
