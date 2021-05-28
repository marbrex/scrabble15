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
import scrabble.dbhandler.DBUpdate;
import scrabble.game.LetterBag.Tile;
import scrabble.model.GameStatusType;
import scrabble.model.MessageType;

public class LobbyClientProtocol extends Thread implements NetworkScreen {
  /**
   * Class of a client which handle the communication with a server in order to inform the server
   * about the user input or action a user has performed in a network game.
   * 
   * @author hendiehl
   */
  // network
  /** socket conection to the server */
  private Socket server;
  /** output to the server */
  private ObjectOutputStream outStream;
  /** input to the server */
  private ObjectInputStream in;
  /** standard connection port */
  private int port = 11111;
  /** network address for local network connections */
  private String adress = "192.168.178.20";
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
  /** own player id during a game */
  private int ownID;
  /** controller of the game screen */
  private GameController gameScreen;
  /** List for one time use in after game screen, is ordered by points */
  private ArrayList<Player> playerResult;
  /** Points gained by players during a game, same order as ordered */
  private int[] pointsResult;
  /** Sender thread to provide a queue ordered sending. */
  private SenderHoldBackQueue out;



  /**
   * Constructor for a GameFinderProtocol, which try to connect with an lobby server in the network
   * on 20 specific client known ports. If no server is found the Constructor throws an Exception to
   * contact the calling GameFinderController.
   * 
   * @param controller Controller of the GameFinder screen
   * @author hendiehl
   */
  public LobbyClientProtocol(GameFinderController controller) {
    this.gameFinderController = controller;
    this.loadPlayer();
  }

  /**
   * Method to get player instance from DB.
   * 
   * @author hendiehl
   */
  private void loadPlayer() { // not implemented yet
    this.player = Profile.getPlayer();
    // this.player.setName("Client");
    // dummy
  }

  /**
   * Method to set the connection on the standard ports.
   * 
   * @throws ConnectException
   * @author hendiehl
   */
  private void setSocket() throws PortsOccupiedException { // change to ports occupied exception
    while (!this.isRunning && this.notConnected) {
      try {
        this.server = new Socket(adress, port);
        this.in = new ObjectInputStream(server.getInputStream());
        this.outStream = new ObjectOutputStream(server.getOutputStream());
        this.isRunning = true;
        this.out = new SenderHoldBackQueue(outStream);
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
   * Constructor for an specific Client known port for an lobby server.
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
    this.outStream = new ObjectOutputStream(server.getOutputStream());
    this.isRunning = true;
    this.out = new SenderHoldBackQueue(outStream);
    this.port = ownPort;
    this.ownPort = true;
    this.loadPlayer();
  }

  /**
   * Run method of the thread class. The thread is waiting for messages from the server and react to
   * an specific MessageType.
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
        case FIELD:
          this.reactToField(message);
          break;
        case DICT:
          this.reactToDictionary(message);
          break;
        case ACTION:
          this.reactToAction(message);
          break;
        case DB:
          this.reactToDB(message);
          break;
        case RETURN:
          this.reactToReturn(message);
          break;
        case SIZE:
          this.reactToSize(message);
          break;
        case PREP:
          this.reactToPrep(message);
          break;
        case DELET:
          this.reactToDelet(message);
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
   * Method to inform a player in game that a other player has left the game. A AceptedMesseage is
   * used because its provide the same parameters.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToDelet(Message message) {
    AceptedMessage msg = (AceptedMessage) message;
    if (this.gameScreen != null) {
      this.gameScreen.api.informAboutLeave(msg.getPort());
    }
  }

  /**
   * Method to inform a player in game that the game is bout to end.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToPrep(Message message) {
    if (this.gameScreen != null) {
      this.gameScreen.api.informGameEnd();
    }
  }

  /**
   * Method to react to an Size-Message wrapped in a AceptedMessage. Used to show the players the
   * amount of tiles in the bag on the game screen.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToSize(Message message) {
    System.out.println("CLIENT PROTOCOL : Size-Message received");
    AceptedMessage msg = (AceptedMessage) message;
    if (this.gameScreen != null) {
      this.gameScreen.api.informAboutTileAmount(msg.getPort());
      // AceptedMessage is just used to not create a own message class.
    }
  }

  /**
   * Method to react to a ReturnMessage. Will cause a screen change.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToReturn(Message message) {
    System.out.println("CLIENT PROTCOL : Return-Message received");
    ResultMessage msg = (ResultMessage) message;
    this.lobbyPlayers = msg.getPlayers();
    this.playerResult = msg.getOrdered();
    this.pointsResult = msg.getPoints();
    if (this.gameScreen != null) {
      this.gameScreen.getToAfterGame(this, false); // changing screens.
    }
  }

  /**
   * Method to react to an DB-Message in reason to save the result of an network game in the DB of
   * the local player.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToDB(Message message) {
    System.out.println("CLIENT PROTOCOL : DB-Message received");
    DBMessage msg = (DBMessage) message;
    boolean won = msg.isWon();
    if (won) {
      System.out.println("CLIENT PROTOCOL : Client win" + won);
      DBUpdate.updateGamesWon(this.player);
    } else {
      System.out.println("CLIENT : PROTOCOL : Client win : " + won);
      DBUpdate.updateGamesLost(this.player);
    }
  }

  /**
   * Method to react to an ActionMessage which send in reason to inform a player about the action a
   * other player performed in his move.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToAction(Message message) {
    System.out.println("CLIENT PROTOCOL : Action-Message received");
    ActionMessage msg = (ActionMessage) message;
    if (this.gameScreen != null) {
      this.gameScreen.api.getOpponentsInfo(msg.getAction(), msg.getPoints(), msg.getId());
    }
  }

  /**
   * Method to react to an incoming Field Message in order to set the content of an dictionary file
   * the host chose for the dictionary.
   * 
   * @param message FieldMessage
   * @author hendiehl
   */
  private void reactToDictionary(Message message) {
    System.out.println("CLIENT PROTOCOL : Dictionary received");
    FieldMessage msg = (FieldMessage) message;
    if (this.gameLobbyController != null) {
      this.gameLobbyController.setContentOfDictionary(msg.getContent(), false);
    }
  }

  /**
   * Method to react to an incoming Field Message in order to set the content of an file the host
   * chose for the multiplier of the game Field.
   * 
   * @param message FieldMessage
   * @author hendiehl
   */
  private void reactToField(Message message) {
    System.out.println("CLIENT PROTOCOL : Field-Message received");
    FieldMessage msg = (FieldMessage) message;
    if (this.gameLobbyController != null) {
      this.gameLobbyController.setContentOfFile(msg.getContent(), false);
    }
  }

  /**
   * Message to inform a client that a other player is on the move. Will be received by all players
   * who are not actual on move.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToOther(Message message) {
    System.out.println("CLIENT PROTOCOL : Other-Message received");
    if (this.gameScreen != null) {
      OtherMessage msg = (OtherMessage) message;
      int i = msg.getTurn();
      int j = msg.getId();
      this.gameScreen.otherPlayerOnMove(i, j);
    }
  }

  /**
   * Method to react to an LetterMultisetReturnMessage response in order of a finished LetterBag
   * server operation.
   * 
   * @param message LetterBagMultisetReturnMessage
   * @author hendiehl
   */
  private void reactToBag(Message message) {
    System.out.println("CLIENT PROTOCOL : Bag-Message received");
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
   * Method to react to an incoming Move message which inform the player that he is on move.
   * 
   * @param message Move message
   * @author hendiehl
   */
  private void reactToMove(Message message) {
    System.out.println("CLIENT PROTOCOL : Move-Message received");
    if (this.gameScreen != null) {
      MoveMessage msg = (MoveMessage) message;
      int i = msg.getTurn();
      int j = msg.getId();
      this.gameScreen.api.startMove(i, j); // start Move
    }

  }

  /**
   * Message to react to an Game Message. After this Message the lobby will be left to get into a
   * GameScreen.
   * 
   * @param message Message from the Server
   * @author hendiehl
   */
  private void reactToGameMessage(Message message) {
    System.out.println("CLIENT PROTOCOL : Game-Message received");
    GameMessage msg = (GameMessage) message;
    // Emergency solution because of data loose
    ArrayList<Player> copie = msg.getPlayers();
    int[] ids = msg.getIds();
    this.ownID = msg.getOwnID();
    for (int i = 0; i < copie.size() && i < 4; i++) {
      copie.get(i).setId(ids[i]);
      System.err.println("Player : " + copie.get(i).getName() + " with id : " + ids[i]);
    }
    this.lobbyPlayers = copie;
    if (this.gameLobbyController != null) {
      this.gameLobbyController.startGame();
    }
  }

  /**
   * Method to react to the start message of the server which will be send when the server wants the
   * chosen player sequence.
   * 
   * @param message Message from the Server
   * @author hendiehl
   */
  private void reactToStartMessage(Message message) {
    System.out.println("CLIENT PROTOCOL : Start-Message received");
    if (this.gameLobbyController != null) {
      StartMessage msg = new StartMessage(MessageType.START, this.player,
          this.gameLobbyController.getPositionList());
      this.out.writeObject(msg);
      this.out.flush();
    }
  }

  /**
   * Method to react to the start of an full lobby procedure.
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
   * Method to react to an rejection message.
   * 
   * @param message message of the server
   * @author hendiehl
   */
  private void reactRejected(Message message) {
    System.out.println("CLIENT PROTOCOL : Reject-Message received");
    this.shutdownProtocol(true);

  }

  /**
   * Method to react to an kick message which will be send by the server if an host decided to kick
   * a client from the Lobby. Will shut down the protocol and switch the screen to the menu screen.
   * 
   * @param message
   * @author hendiehl
   */
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
   * Method to react to a shutdown message, no response expected.
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
   * Method for reacting on information message from server and show them on the gui.
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
   * Method to close the connection of the protocol.
   * 
   * @author hendiehl
   */
  private void closeConnection() { // change !!!!!!!!!!!!!!!!!
    try { // if a input or output stream is closed the other close himself and the close method
          // throw the exception
      if (this.server != null) {
        this.out.shutdwon(); // shutdown the queue
        this.server.close();
      }
      // System.out.println("connection closed");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to end the running thread and close the connection.
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
   * Method to change the controller of the screen when new window is loaded.
   * 
   * @param glc
   * @author hendiehl
   */
  @Override
  public void setLobbyController(GameLobbyController glc) {
    if (this.gameFinderController != null) { // from finder screen in lobby screen
      System.out.println("CLIENT PROTOCOL : From finder to lobby");
      this.gameFinderController = null;
      this.gameLobbyController = glc;
      if (this.lobbyPlayers != null) {
        this.updateLobbyinformation();
      }
    } else if (this.gameScreen != null) { // from game screen to lobby screen
      System.out.println("CLIENT PROTOCOL : From game to lobby");
      this.gameScreen = null;
      this.gameLobbyController = glc;
      this.updateLobbyinformation();
    }
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
      this.gameLobbyController.setProfilePicture(i, "/img/" + lobbyPlayers.get(i).getImage());
    }

  }

  /**
   * Method which will inform the server that the client want to join the lobby.
   * 
   * @author hendiehl
   */
  public void sendJoinMessage() {
    Message msg = new Message(MessageType.JOIN, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("CLIENT PROTOCOL : Join-Message sended");
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
    if (this.server != null) {
      if (!this.server.isClosed()) {
        this.out.writeObject(msg);
        this.out.flush();
        System.out.println("CLIENT PROTOCOL : Shutdown-Message sended");
      }
    }
  }

  /**
   * Method to send a chat message with the chat client to the Chat server.
   * 
   * @param message message of the chat client which will be send to the server
   * @author hendiehl
   */
  public void sendChatMessage(String message) {
    if (this.gameScreen != null) {
      this.chat.sendMessageToServer(message);
    } else if (this.gameLobbyController != null) {
      this.chat.sendMessageToServer(message);
    }
  }

  /**
   * Method to handle an incoming chat message and print them on the GameLobbyScreen or the
   * GameScreen itself.
   * 
   * @param message message from the chat server
   * @author hendiehl
   */
  public void printChatMessage(String message) {
    if (this.gameScreen != null) {
      this.gameScreen.api.printChatMessage(message);
    } else if (this.gameLobbyController != null) {
      this.gameLobbyController.printChatMessage(message);
    }
  }

  /**
   * Method to create a Chat Client with an port given by the ServerProtocol.
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
   * the position election.
   * 
   * @author hendiehl
   */
  public int getPlayerAmount() {
    return this.lobbyPlayers.size();
  }

  /**
   * Method to set the game screen controller after the lobby is leaved for the actual game screen.
   * 
   * @param gameScreen
   * @author hendiehl
   */
  @Override
  public void setGameScreen(GameController gameScreen) {
    System.out.println("CLIENT PROTOCOL : GAME-Controller set");
    this.gameScreen = gameScreen;
    this.gameLobbyController = null; // not needed anymore after GameScreen is set.
  }

  /**
   * Method to inform the server that a player finished his move in time.
   * 
   * @param action String representation of the action a player performed in his last move.
   * @param points Points a player gain with his last move action.
   * @author hendiehl
   */
  @Override
  public void sendEndMessage(String action, int points) {
    EndMessage msg = new EndMessage(MessageType.END, this.player, action, points);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("CLIENT PROTOCOL : End-Message sended");
  }

  /**
   * Method to shutdown the chat protocol.
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
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void grabRandomTile() {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRT);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getValueOf(char letter) {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, letter, LetterBagType.GV);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingVowels() {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRV);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingConsonants() {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRC);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingBlanks() {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRB);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void grabRandomTiles(int count) {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, count, '0', LetterBagType.GRTS);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingTiles() {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GRET);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getAmount() {
    LetterBagMessage msg =
        new LetterBagMessage(MessageType.BAG, this.player, 0, '0', LetterBagType.GA);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to inform the GameHandler that the loading of the game field screen finished. Will be
   * called form the GameController in his initialize method.
   * 
   * @author hendiehl
   */
  @Override
  public void loadFinished() {
    Message msg = new Message(MessageType.LOAD, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("CLIENT PROTOCOL : Load-Message sended");
  }

  /**
   * Method to get the own game id for a network game.
   * 
   * @return id Own game id.
   * @author hendiehl
   */
  @Override
  public int getOwnID() {
    return this.ownID;
  }

  /**
   * Method to inform the protocol about the finished screen loading of the lobby. Is used to open
   * the AfterGameScreen in the lobby.
   * 
   * @author hendiehl
   */
  @Override
  public void informLobbyReturn() {
    System.out.println("CLIENT PROTOCOL : Lobby load finish");
    if (this.gameLobbyController != null) {
      this.updateLobbyinformation();
      this.gameLobbyController.showWinScreen(playerResult, pointsResult);
    }
    this.playerResult = null;
    this.pointsResult = null;
  }

  /**
   * Method to send a word message in the chat, to inform other players about placed words.
   *
   * @param word A word placed on the game field.
   * @author hendiehl
   */
  @Override
  public void sendWordMessage(String word) {
    this.chat.sendWordMessageToServer(word);
  }

  /**
   * Method to send a pass message in the chat to inform players about a player pass.
   * 
   * @author hendiehl
   */
  @Override
  public void sendPassMessage() {
    this.chat.sendPassToServer();
  }
}
