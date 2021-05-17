package scrabble.network;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;

public class LobbyServer extends Thread {
  /**
   * Class of the lobby server which is responsible for a network game. Is the overall server class
   * which provides the communication with clients by accepting them and starting protocols.
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
  /** Host chosen port for a Network Game */
  private int ownPort = 0;


  // constructor with port assignment if occupied
  /**
   * Constructor which will inform the player if the server can run on a standard port.
   * 
   * @param gameLobby controller of the GameLobby screen
   * @author hendiehl
   */
  public LobbyServer(GameLobbyController gameLobby) {
    // setting up controls
    this.initializeParts(gameLobby);
  }

  /*
   * Constructor for a network server with an own port
   * 
   * @param gameLobby controller of the GameLobby screen
   * 
   * @author hendiehl
   */
  public LobbyServer(GameLobbyController gameLobby, int ownPort)
      throws ConnectException, IOException {
    this.setOwnServer(ownPort);
    this.initializeParts(gameLobby);
  }

  /**
   * Method to set a server with an specific port chosen by Host
   * 
   * @param port2 port given by the host
   * @author hendiehl
   */
  private void setOwnServer(int port2) throws ConnectException, IOException {
    System.out.println("SERVER : Own port set on " + port2);
    this.server = new ServerSocket(port2);
    this.ownPort = port2;
    this.isRunning = true;
    System.err.println("SERVER : Choosen own port :" + this.ownPort);
  }

  /**
   * Method to initialize important parts of the server
   * 
   * @param gameLobby controller of the corresponding GameLobby
   * @author hendiehl
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
    // this.gameLobby.setProfileVisible(0, this.host.getPlayer().getName()); // change to protocol
    // call
    // in future??
  }

  /**
   * Method to get the running Port
   * 
   * @return port teh server runs on or 0 if server isn't set
   * @author hendiehl
   */
  public int getRunningPort() {
    if (this.server != null) { // Pretend calling Method before the server started
      return this.server.getLocalPort();
    } else {
      return 0;
    }
  }

  /**
   * Method to check if a server is launched normally with the standard port or a specific port
   * 
   * @return boolean condition about a standard port use or a own port / netwrok error
   * @author hendiehl
   */
  public boolean portIsAutoSet() {
    return this.ownPort == 0; // if the ownPort isn't 0 a own Port is chosen
  }

  /**
   * Method to set the server port by testing a amount of predefined ports
   * 
   * @throws PortsOccupiedException thrown when all ports occupied
   * @author hendiehl
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
          throw new PortsOccupiedException("Standart ports are occupied, please configure");
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
   * 
   * @author hendiehl
   */
  public void run() {
    try {
      if (this.ownPort == 0) { // AutoPort is active if no own port is set !!!!!!!
        System.out.println("SERVER : Auto set");
        this.setServer(); // In this case the server is set in the constructor
      }
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
   * Method to accept a client and starting a ServerProtocol for them
   * 
   * @author hendiehl
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
   * 
   * @author hendiehl
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
   * 
   * @author hendiehl
   */
  public void shutdown() { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! critical
    System.out.println("SERVER : Shutdown");
    this.isRunning = false;
    // this.gameInfoController.shutdown();
    this.closeAllProtocols();
    this.closeConnection();
    this.host.stopChatClient();
    this.chat.stopServer();
  }

  /**
   * Method which will call a the shutdown procedure for all connected clients.
   * 
   * @author hendiehl
   */
  private void closeAllProtocols() { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! critical
    for (LobbyServerProtocol lP : this.clients) {
      lP.sendShutdownMsg(); // here changing of the shutdown class
      lP.shutdownProtocol(true);
    }
  }

  /*
   * Method which will start the communication with a connecting client
   * 
   * @author hendiehl
   */
  private void getInContact(LobbyServerProtocol lsp) {
    this.clients.add(lsp);
    lsp.start();
    lsp.sendInformationMessage();
  }

  /**
   * Method which will remove a specific server protocol from the server.
   * 
   * @param lsp LobbyServerProtocol which will be removed from the client list.
   * @author hendiehl
   */
  public void deleteSpecificProtocol(LobbyServerProtocol lsp) {
    if (this.clients.contains(lsp)) {
      this.clients.remove(lsp);
    }
  }

  /**
   * Method to start the chat server
   * 
   * @author hendiehl
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
   * @return port of the chat server
   * @author hendiehl
   */
  protected Integer getChatPort() {
    return this.chatPort;
  }
}
