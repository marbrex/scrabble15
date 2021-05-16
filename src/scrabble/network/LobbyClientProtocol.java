package scrabble.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import com.google.common.collect.Multiset;
import scrabble.model.HumanPlayer;
import scrabble.model.LetterBagType;
import scrabble.model.Player;
import scrabble.model.Profile;
import scrabble.GameController;
import scrabble.GameFinderController;
import scrabble.GameLobbyController;
import scrabble.game.LetterBag.Tile;
import scrabble.model.GameStatusType;
import scrabble.model.MessageType;

public class LobbyClientProtocol extends Thread implements NetworkScreen {
  /**
   * Class of a client which handle the communication with a server.
   * 
   * @author hendiehl
   */
  // network
  /** socket conection to the server */
  private Socket server;
  /** output to the server */
  private ObjectOutputStream out;
  /** input to the server */
  private ObjectInputStream in;
  /** standard connection port */
  private int port = 11111;
  /** network address for local network connections */
  private String adress = "localhost";
  /** own port number if wanted */
  private boolean ownPort;
  // Control
  /** Control boolean for run method */
  private boolean isRunning;
  /** list of all Players in the lobby */
  private ArrayList<Player> lobbyPlayers;
  /** boolean to avoid port connection on windows after slow connect try and fast screen leave */
  private boolean notConnected = true;
  // Gui
  /** controller of the gameFinder screen */
  private GameFinderController gameFinderController;
  /** controller of the GameLobby not loaded before lobby join */
  private GameLobbyController gameLobbyController;
  /** player instance of the client is allays human player */
  private HumanPlayer player;
  /** chat client for sending/receiving messages */
  private Client chat;

  // Only testing
  private GameController gameScreen;



  /**
   * Constructor for a GameFinderProtocol, which try to connect with an lobby server in the network
   * on 20 specific client known ports. If no server is found the Constructor throws an Exception to
   * contact the calling GameFinderController
   * 
   * @param controller Controller of the GameFinder screen
   * @author hendiehl
   */
  public LobbyClientProtocol(GameFinderController controller) {
    this.gameFinderController = controller;
    this.loadPlayer();
  }

  /**
   * Method to get player instance from DB
   * 
   * @author hendiehl
   */
  private void loadPlayer() { // not implemented yet
    this.player = Profile.getPlayer();
    // this.player.setName("Client");
    // dummy
  }

  /**
   * Method to set the connection on the standard ports
   * 
   * @throws ConnectException
   * @author hendiehl
   */
  private void setSocket() throws PortsOccupiedException { // change to ports occupied exception
    while (!this.isRunning && this.notConnected) {
      try {
        this.server = new Socket(adress, port);
        this.in = new ObjectInputStream(server.getInputStream());
        this.out = new ObjectOutputStream(server.getOutputStream());
        this.isRunning = true;
        System.out.println("CLIENT PROTOCOL : Connected with socket : " + this.port);
      } catch (ConnectException e) {
        System.out.println("CLIENT PROTOCOL : No connection at port " + this.port);
        if (this.port < 11131) { // In connection with the standart port (+20)
          this.port++;
        } else {
          throw new PortsOccupiedException("Standard ports are occupied");
        }
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        this.isRunning = false; // ?????????????
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        this.isRunning = false; // ?????????????????
        e.printStackTrace();
      }
    }
  }

  /**
   * Constructor for an specific Client known port for an lobby server
   * 
   * @param controller controller of the GameFinder screen
   * @param ownPort port given by the user
   * @throws ConnectException
   * @throws IOException
   * @author hendiehl
   */
  public LobbyClientProtocol(GameFinderController controller, int ownPort)
      throws ConnectException, IOException {
    this.gameFinderController = controller;
    this.server = new Socket(adress, ownPort);
    this.in = new ObjectInputStream(server.getInputStream());
    this.out = new ObjectOutputStream(server.getOutputStream());
    this.isRunning = true;
    this.port = ownPort;
    this.ownPort = true;
    this.loadPlayer();
  }

  /**
   * Run method of the thread class. The thread is waiting for messages from the server and react to
   * an specific MessageType
   * 
   * @author hendiehl
   */
  public void run() {
    System.out.println("CLIENT PROTOCOL : Protocol started");
    try {
      if (!ownPort) {
        this.setSocket();
        // this.gameFinderController.connectSucessful();
      }
    } catch (PortsOccupiedException e1) {
      this.gameFinderController.connectNotSucessful();
    }
    while (isRunning) {
      react();
    }
    System.err.println("CLIENT PROTOCOL OUTRUN");
  }

  /**
   * Method to react to an incoming message.
   * 
   * @author hendiehl
   */
  private void react() {
    try {
      // System.out.println("Run pass");
      Object o = in.readObject();
      // System.out.println("Object received");
      Message message = (Message) o;
      // System.out.println("Message casted");
      switch (message.getType()) {
        case INFORMATION:
          this.reactToInformation(message);
          break;
        case SHUTDOWN:
          this.reactToShutdown();
          break;
        case ACEPTED:
          this.reactToAcepted(message);
          break;
        case LOBBY:
          this.reactToLobby(message);
          break;
        case KICK:
          this.reactToKick(message);
          break;
        case REJECTED:
          this.reactRejected(message);
          break;
        case FULL:
          this.reactToFullMessage(message);
          break;
        case START:
          this.reactToStartMessage(message);
          break;
        case GAME:
          this.reactToGameMessage(message);
          break;
        case MOVE:
          this.reactToMove(message);
          break;
        case BAG:
          this.reactToBag(message);
          break;
        case OTHER:
          this.reactToOther(message);
          break;
      }
    } catch (EOFException e) {
      this.shutdownProtocol(true);
    } catch (SocketException e) {
      // do something

    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      this.isRunning = false;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  /**
   * Message to inform a client that a other player is o0n the move
   * @param message
   */
  private void reactToOther(Message message) {
    
  }

  /**
   * Method to react to an LetterMultisetReturnMessage response in order of a finished LetterBag
   * server operation
   * 
   * @param message LetterBagMultisetReturnMessage
   * @author hendiehl
   */
  private void reactToBag(Message message) {
    LetterMultisetReturnMessage msg = (LetterMultisetReturnMessage) message;
    Multiset<Tile> tiles;
    switch (msg.getType2()) {
      case GA:
        int i = msg.getAnswer();
        // callback
        this.gameScreen.getAmountAnswer(i);
        break;
      case GRB:
        tiles = msg.getTiles();
        // callback
        this.gameScreen.getRemainingBlanksAnswer(tiles);
        break;
      case GRC:
        tiles = msg.getTiles();
        // callback
        this.gameScreen.getRemainingConsonantsAnswer(tiles);
        break;
      case GRET:
        tiles = msg.getTiles();
        // callback
        this.gameScreen.getRemainingTilesAnswer(tiles);
        break;
      case GRT:
        Tile tile = msg.getTile();
        // callback
        this.gameScreen.grabRandomTileAnswer(tile);
        break;
      case GRTS:
        tiles = msg.getTiles();
        // callback
        this.gameScreen.grabRandomTilesAnswer(tiles);
        break;
      case GRV:
        tiles = msg.getTiles();
        // callback
        this.gameScreen.getRemainingVowelsAnswer(tiles);
        break;
      case GV:
        int j = msg.getAnswer();
        // callback
        this.gameScreen.getValueOfAnswer(j);
        break;
    }
  }

  /**
   * Method to react to an incoming Move message which inform the player that he is on move
   * 
   * @param message Move message
   * @author hendiehl
   */
  private void reactToMove(Message message) {
    System.out.println("CLIENT PROTOCOL : Move-Message received");
    if (this.gameScreen != null) { // Perhaps the screen isn't loaded, because JavaFX loading time (no control about that)
      this.gameScreen.api.startMove(); //start Move
    }

  }

  /**
   * Message to react to an Game Message. After this Message the lobby will be left to get into a
   * GameScreen
   * 
   * @param message Message from the Server
   * @author hendiehl
   */
  private void reactToGameMessage(Message message) {
    System.out.println("CLIENT PROTOCOL : Game-Message received");
    LobbyInformationMessage msg = (LobbyInformationMessage) message;
    this.lobbyPlayers = msg.getPlayers();
    if (this.gameLobbyController != null) {
      this.gameLobbyController.startGame();
    }
  }

  /**
   * Method to react to the start message of the server which will be send when the server wants the
   * chosen player sequence
   * 
   * @param message Message from the Server
   * @author hendiehl
   */
  private void reactToStartMessage(Message message) {
    System.out.println("CLIENT PROTOCOL : Start-Message received");
    try {
      if (this.gameLobbyController != null) {
        StartMessage msg = new StartMessage(MessageType.START, this.player,
            this.gameLobbyController.getPositionList());
        this.out.writeObject(msg);
        this.out.flush();
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Method to react to the start of an full lobby procedure
   * 
   * @param message message from the server
   * @author hendiehl
   */
  private void reactToFullMessage(Message message) {
    System.out.println("CLIENT PROTOCOL : Full-Message received");
    if (this.gameLobbyController != null) {
      // System.err.println("Election procedure started");
      this.gameLobbyController.startElection();
    }

  }

  /**
   * Method to react to an rejection message
   * 
   * @param message message of the server
   * @author hendiehl
   */
  private void reactRejected(Message message) {
    System.out.println("CLIENT PROTOCOL : Reject-Message received");
    this.shutdownProtocol(true);

  }

  private void reactToKick(Message message) {
    System.out.println("CLIENT PROTOCOL : Kick-Message received");
    this.chat.sendKickToServer();
    this.shutdownProtocol(false);
    if (this.gameLobbyController != null) {
      this.gameLobbyController.openMenu();
    } else {
      this.gameFinderController.openMenu();
    }
  }

  /**
   * Method to react to a shutdown message, no response expected
   * 
   * @author hendiehl
   */
  private void reactToShutdown() {
    System.out.println("CLIENT PROTOCOL : Shutdown-Message received");
    this.shutdownProtocol(false);
    if (this.gameLobbyController != null) {
      this.gameLobbyController.openMenu();
    } else {
      this.gameFinderController.openMenu();
    }

  }

  /**
   * Method to react to lobby informations of the server. Will update the screen in consequence.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToLobby(Message message) {
    System.out.println("CLIENT PROTOCOL : Lobby-Message received");
    LobbyInformationMessage msg = (LobbyInformationMessage) message;
    // this.gameFinderController.goInLobby();
    this.lobbyPlayers = msg.getPlayers();
    // System.err.println("LOBBY UPDATE : " + this.lobbyPlayers.size());
    if (msg.getPlayers() == null) {
      System.err.println("null list from server !!!!!!!11");
    }
    // null pointer because of run later, need a call back after Lobby is created
    // here past position of null set of finderController
    if (this.gameLobbyController != null) {
      this.updateLobbyinformation();
    }
  }

  /**
   * Method which react to an acceptance of a server and transform the screen in an GameLobby
   * screen.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToAcepted(Message message) {
    System.out.println("CLIENT PROTOCOL : Accept-Message received");
    this.gameFinderController.goInLobby();
    // starting a chat client
    AceptedMessage msg = (AceptedMessage) message;
    this.startChatClient(msg.getPort());
  }

  /**
   * Method for reacting on information message from server and show them on the gui
   * 
   * @param message message from the server which will be castes to the specific type
   * @author hendiehl
   */
  private void reactToInformation(Message message) {
    System.out.println("CLIENT PROTOCOL : Information-Message received");
    InformationMessage iM = (InformationMessage) message;
    GameStatusType status = iM.getStatus(); // if in Game next port should be tried
    if (iM.getStatus() != GameStatusType.GAME) {
      int amount = iM.getLobbyPlayers();
      this.gameFinderController.setStatusLabel("In : " + status.name());
      this.gameFinderController.setStatusLabel2("Amount of Players : " + amount);
      this.gameFinderController.setPortInformation(this.port);
      this.gameFinderController.connectSucessful();
    } else {
      this.gameFinderController.connectNotSucessful();
      this.gameFinderController.setStatusLabel("In : " + status.name());
      this.gameFinderController.setStatusLabel2("Sorry the game started");
      this.gameFinderController.setPortInformation(this.port);
    }
  }

  /**
   * Method to close the connection of the protocol
   * 
   * @author hendiehl
   */
  private void closeConnection() { // change !!!!!!!!!!!!!!!!!
    try { // if a input or output stream is closed the other close himself and the close method
          // throw the exception
      if (this.server != null) {
        this.server.close();
      }
      // System.out.println("connection closed");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to end the running thread and close the connection
   * 
   * @author hendiehl
   */
  public void shutdownProtocol(boolean selfcall) {
    System.out.println("CLIENT PROTOCOL : Shutdown");
    this.notConnected = false;
    this.isRunning = false;
    if (selfcall) {
      this.sendShutdownMsg(); // last message
    }
    this.closeConnection();
    this.stopChatClient();
    // System.out.println("GameFinderProtocol shutdown");
  }

  /**
   * Method to change the controller of the screen when new window is loaded
   * 
   * @param glc
   * @author hendiehl
   */
  public void setLobbyController(GameLobbyController glc) {
    this.gameFinderController = null;
    this.gameLobbyController = glc;
    if (glc != null) {
      System.out.println("Controller not null");
    }
    this.updateLobbyinformation();
  }

  /**
   * Method to update the visibility of the player profiles of the GameLobby screen.
   * 
   * @author hendiehl
   */
  private void updateLobbyinformation() {
    this.gameLobbyController.resetProfileVisibility();
    if (this.lobbyPlayers == null) { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      System.err.println("PLayers null");
    }
    for (int i = 0; i < lobbyPlayers.size(); i++) {
      // System.err.println("Update gui :" + i);
      this.gameLobbyController.setProfileVisible(i, lobbyPlayers.get(i).getName());
      this.gameLobbyController.setProfilePicture(i, "img/" + lobbyPlayers.get(i).getImage());
    }

  }

  /**
   * Method which will inform the server that the client want to join the lobby.
   * 
   * @author hendiehl
   */
  public void sendJoinMessage() {
    Message msg = new Message(MessageType.JOIN, this.player);
    try {
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("CLIENT PROTOCOL : Join-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to send a last shutdown message to the client, to give them the opportunity to handle.
   * No respond expected.
   * 
   * @author hendiehl
   */
  public void sendShutdownMsg() {
    // System.out.println("Send shutdown message");
    Message msg = new Message(MessageType.SHUTDOWN, this.player);
    if (this.chat != null) {
      this.chat.sendLeaveMessageToServer();
    }
    try {
      if (this.server != null) {
        if (!this.server.isClosed()) {
          this.out.writeObject(msg);
          this.out.flush();
          System.out.println("CLIENT PROTOCOL : Shutdown-Message sended");
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to send a chat message with the chat client to the Chat server
   * 
   * @param message message of the chat client which will be send to the server
   * @author hendiehl
   */
  public void sendChatMessage(String message) {
    if (this.gameLobbyController != null) {
      this.chat.sendMessageToServer(message);
    }
  }

  /**
   * Method to handle an incoming chat message and print them on the GameLobbyScreen or the
   * GameScreen itself
   * 
   * @param message message from the chat server
   * @author hendiehl
   */
  public void printChatMessage(String message) {
    if (this.gameLobbyController != null) {
      this.gameLobbyController.printChatMessage(message);
    }
  }

  /**
   * Method to create a Chat Client with an port given by the ServerProtocol
   * 
   * @param port port of the Chat server started by the Main Server
   * @author hendiehl
   */
  public void startChatClient(int port) {
    this.chat = new Client(this, port, this.player.getName());
    this.chat.connect();
    this.chat.start();
    this.chat.sendJoinMessageToServer();
    System.out.println("CLIENT PROTOCOL : Chat client started");
    // this.sendChatMessage("Hi i am in "); //sending first message for testing purpose
  }

  /**
   * Method to get the the amount of players in the lobby, used by the GameLobbyController to set up
   * the position election
   * 
   * @author hendiehl
   */
  public int getPlayerAmount() {
    return this.lobbyPlayers.size();
  }

  /**
   * Method to set the game screen controller after the lobby is leaved for the actual game screen
   * 
   * @param gameScreen
   * @author hendiehl
   */
  public void setGameScreen(GameController gameScreen) {
    this.gameScreen = gameScreen;

  }

  /**
   * Method to inform the server that a player finished his move in time
   * 
   * @author hendiehl
   */
  @Override
  public void sendEndMessage() {
    try {
      Message msg = new Message(MessageType.END, this.player);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("CLIENT PROTOCOL : End-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Method to shutdown the chat protocol
   * 
   * @author hendiehl
   */
  @Override
  public void stopChatClient() {
    if (this.chat != null) {
      this.chat.disconnect();
      System.out.println("CLIENT PROTOCOL : Chat client stopped");
    }
  }

  /**
   * Method to get the PlayerList instance of an protocol, mainly to give it to the game field. Is
   * used to show players on the game field.
   * 
   * @return players List of players in the lobby.
   * @author hendiehl
   */
  @Override
  public ArrayList<Player> getPlayerList() {
    return this.lobbyPlayers;
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void grabRandomTile() {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRT);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void getValueOf(char letter) {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, letter, LetterBagType.GV);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingVowels() {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRV);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingConsonants() {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRC);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingBlanks() {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRB);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void grabRandomTiles(int count) {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, count, '0', LetterBagType.GRTS);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingTiles() {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRET);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to use LetterBag functionality in a network game
   * 
   * @author hendiehl
   */
  @Override
  public void getAmount() {
    try {
      LetterBagMessage msg =
          new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GA);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
