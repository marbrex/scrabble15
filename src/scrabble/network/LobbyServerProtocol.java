package scrabble.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import com.google.common.collect.Multiset;
import javafx.util.Pair;
import scrabble.model.HumanPlayer;
import scrabble.model.LetterBagType;
import scrabble.model.Player;
import scrabble.game.LetterBag.Tile;
import scrabble.model.GameInformationController;
import scrabble.model.MessageType;

public class LobbyServerProtocol extends Thread implements NetworkPlayer {
  /**
   * Class which handle the communication with a client. Is responsible for the overall
   * communication with a LobbyClientProtocol.
   * 
   * @author hendiehl
   */
  // network
  /** connected client */
  private Socket client;
  /* output to client */
  private ObjectOutputStream outStream;
  /* input from client */
  private ObjectInputStream in;
  /* Corresponding overall lobby server from the protocol */
  private LobbyServer corespondingServer;
  // Control
  /* Control boolean of the thread run */
  private Boolean isRunning;
  /* controller of the game information */
  private GameInformationController gameInfoController;
  /* player instance of the connected client is allays a human player */
  private HumanPlayer player;
  /** Variable to compare the position in the list to create a game sequence of human player */
  private int sequencePos;
  /** Sender thread to provide a queue ordered sending. */
  private SenderHoldBackQueue out;

  /**
   * Constructor of the LobbyProtocol which are used to get in contact with players who want to join
   * an Lobby or an game.
   * 
   * @param s Socket given by the Lobby Server, which are connected to an client
   * @param gameLobby controller of the GameLobbyScreen
   * @param gameInfoController controller which holds game specific information
   * @author hendiehl
   */
  public LobbyServerProtocol(Socket s, LobbyServer server,
      GameInformationController gameInfoController) {
    try {
      this.corespondingServer = server;
      this.outStream = new ObjectOutputStream(s.getOutputStream());
      this.in = new ObjectInputStream(s.getInputStream());
      this.gameInfoController = gameInfoController;
      this.isRunning = true;
      this.player = new HumanPlayer();
      this.out = new SenderHoldBackQueue(outStream);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Run method of the thread which waits for an client message and react to specific MessageTypes.
   * 
   * @author hendiehl
   */
  public void run() {
    System.out.println("SERVER PROTOCOL : Protocol started");
    while (isRunning) {
      react();
    }
    System.err.println("SERVER PROTOCOL OUTRUN");
  }

  /**
   * Method to read an incoming Message from a corresponding client and take action in of the
   * MessageType.
   * 
   * @author hendiehl
   */
  private void react() {
    try { // after Server closed the loop is executed ?
      // System.out.println("Protocol run pass");
      Object o = in.readObject();
      // System.err.println("SERVER PROTOCOL : Message received");
      Message message = (Message) o;
      switch (message.getType()) {
        case JOIN:
          this.reactToJoin(message);
          break;
        case SHUTDOWN:
          this.reactToShutdown();
          break;
        case START:
          this.reactToStart(message);
          break;
        case END:
          this.reactToEnd(message);
          break;
        case BAG:
          this.reactToBag(message);
          break;
        case LOAD:
          this.reactToLoad(message);
          break;
        case EXCHANGE:
          this.reactToExchange(message);
          break;
        case INTAM:
          this.reactToAmountMessage(message);
          break;
      }
    } catch (EOFException e) {
      this.isRunning = false;
      this.closeConnection();
    } catch (SocketException e) {
      this.closeConnection();
    } catch (ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to react to an AmountMessage intend.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToAmountMessage(Message message) {
    ArrayList<Pair<Character, Integer>> amount = this.gameInfoController.getAmountOfEveryTile();
    AmountMessage msg = new AmountMessage(MessageType.INTAM, this.player, amount);
    this.out.writeObject(msg);
    this.out.flush();
  }

  /**
   * Method to react to an incoming ExchangeMessage.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToExchange(Message message) {
    ExchangeMessage msg = (ExchangeMessage) message;
    Multiset<Tile> tiles;
    LetterMultisetReturnMessage answer;
    tiles = this.gameInfoController.exchangeLetterTiles(msg.getTile());
    answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, tiles, 0, null,
        LetterBagType.EXC);
    // no need of size change, amount will not change.
    this.sendLetterBagResponse(answer);
  }

  /**
   * Method to react to an load message form a client, which is send in reason to inform the server
   * that his game field finished loading.
   * 
   * @param message
   * @author hendiehl
   */
  private void reactToLoad(Message message) {
    System.out.println("SERVER PROTOCOL : Load-Message received");
    this.gameInfoController.informLoading(this);
  }

  /**
   * Method to react to an incoming LetterBag message. Has the function to perform a specific action
   * on the global LetterBag instance.
   * 
   * @param message LetterBagMessage
   * @author hendiehl
   */
  private void reactToBag(Message message) {
    System.out.println("SERVER PROTOCOL : Bag-Message received");
    LetterBagMessage msg = (LetterBagMessage) message;
    LetterMultisetReturnMessage answer;
    Multiset<Tile> tiles;
    switch (msg.getType2()) {
      case GA:
        int i = this.gameInfoController.getAmount();
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, null, i, null,
            LetterBagType.GA);
        this.sendLetterBagResponse(answer);
        break;
      case GRB:
        tiles = this.gameInfoController.getRemainingBlanks();
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, tiles, 0, null,
            LetterBagType.GRB);
        this.sendLetterBagResponse(answer);
        break;
      case GRC:
        tiles = this.gameInfoController.getRemainingConsonants();
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, tiles, 0, null,
            LetterBagType.GRC);
        this.sendLetterBagResponse(answer);
        break;
      case GRET:
        tiles = this.gameInfoController.getRemainingTiles();
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, tiles, 0, null,
            LetterBagType.GRET);
        this.sendLetterBagResponse(answer);
        break;
      case GRT:
        Tile tile = this.gameInfoController.grabRandomTile();
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, null, 0, tile,
            LetterBagType.GRT);
        this.sendLetterBagResponse(answer);
        break;
      case GRTS:
        tiles = this.gameInfoController.grabRandomTiles(msg.getCount());
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, tiles, 0, null,
            LetterBagType.GRTS);
        this.sendLetterBagResponse(answer);
        break;
      case GRV:
        tiles = this.gameInfoController.getRemainingVowels();
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, tiles, 0, null,
            LetterBagType.GRV);
        this.sendLetterBagResponse(answer);
        break;
      case GV:
        int j = this.gameInfoController.getValueOf(msg.getLetter());
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, null, j, null,
            LetterBagType.GV);
        this.sendLetterBagResponse(answer);
        break;
      case GAO:
        int a = this.gameInfoController.getAmountOf(msg.getLetter());
        answer = new LetterMultisetReturnMessage(MessageType.BAG, this.player, null, a, null,
            LetterBagType.GAO);
        this.sendLetterBagResponse(answer);
        break;
    }
    this.gameInfoController.checkBagSize(); // after every message.
  }

  /**
   * Method to send the response back to the client.
   * 
   * @param msg LetterBagMultisetReturnMessage
   * @author hendiehl
   */
  private void sendLetterBagResponse(LetterMultisetReturnMessage msg) {
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : LMR-Message sended");
  }

  /**
   * Method to react to an End Message received when a player wants to end his turn.
   * 
   * @param message
   * @author hendiehl
   */
  private synchronized void reactToEnd(Message message) {
    System.out.println("SERVER PROTOCOL : End-Message received");
    EndMessage msg = (EndMessage) message;
    this.gameInfoController.endMoveForTime(msg.getAction(), msg.getPoints()); // did he go in or not
                                                                              // ?
  }

  /**
   * Method to react to an incoming start Message of a client which transmit the chosen player
   * sequence.
   * 
   * @param message Message of the client, type : StartMessage
   * @author hendiehl
   */
  private void reactToStart(Message message) {
    System.out.println("SERVER PROTOCOL : Start-Message received");
    StartMessage msg = (StartMessage) message;
    this.gameInfoController.addSequence(msg.getSequence(), this);
  }

  /**
   * Method to react to a client which ends the connection.
   * 
   * @author hendiehl
   */
  private void reactToShutdown() {
    System.out.println("SERVER PROTOCOL : Shutdown-Message received");
    this.deletePlayer();
    // need to be deleted from server list
  }

  /**
   * Method to delete the specific protocol by deleting it from the overall server and the
   * GameInfoController and shut it down because no action should be performed after that.
   * 
   * @author hendiehl
   */
  public void deletePlayer() {
    this.corespondingServer.deleteSpecificProtocol(this);
    this.gameInfoController.deletePlayer(this);
    // this.gameInfoController.updateAllLobbys();
    this.shutdownProtocol(false);
  }

  /**
   * Method which react to an JoinMessage of an User If the gameInformationController add the player
   * information about the lobby are ended. If the gameInformationController not add the player the
   * client will be rejected.
   * 
   * @param message message which will be casted to the specific MessageType to get information
   * @author hendiehl
   */
  private void reactToJoin(Message message) {
    System.out.println("SERVER PROTOCOL : Join-Message received");
    if (this.gameInfoController.addPlayer(this)) {
      this.player = (HumanPlayer) message.getOwner();
      this.sendLobbyInformation();
      this.gameInfoController.checkLobbySize();
    } else {
      this.sendRejectInfomation(); // should the protocol be shutdown ?
    }
  }

  /**
   * Method to send an rejection information to a client, a shutdown message is expected.
   * 
   * @author hendiehl
   */
  private void sendRejectInfomation() { // after this a protocol should be shutdown !!!!!!!!!!!!!!!!
    Message msg = new Message(MessageType.REJECTED, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Reject-Message sended");
  }

  /**
   * Method to send information about the Lobby to the client and also inform all players in the
   * lobby about the joining, also himself.
   * 
   * @author hendiehl
   */
  public void sendLobbyInformation() { // !!!!!!!!!!!!!!!!!!!!!!!!
    // System.out.println("Send lobby information after client add");
    Message msg =
        new AceptedMessage(MessageType.ACEPTED, this.player, this.corespondingServer.getChatPort());
    this.out.writeObject(msg); // only acceptence message
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Accept-Message sended");
    // other Message from here onwards
    // Update all Lobbys
    this.gameInfoController.updateAllLobbys(); // here the new list is send to all players
  }

  /**
   * Method which sends the first information message for an client.A Join message is expected.
   * 
   * @author hendiehl
   */
  public void sendInformationMessage() {
    // System.out.println("Send basic lobby information");
    InformationMessage iM = new InformationMessage(MessageType.INFORMATION, this.player,
        this.gameInfoController.getStatus(), this.gameInfoController.getPlayerAmount());
    this.out.writeObject(iM);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Information-Message sended");
  }

  /**
   * Method to close the connection to an client.
   * 
   * @author hendiehl
   */
  private void closeConnection() { // change !!!!!!!!!!!!!!!!!!!!!
    try {
      if (this.client != null) {
        this.out.shutdwon(this.client); // shutdown the queue
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to shutdown the protocol by breaking the thread loop and closing the connection.
   * 
   * @param selfcall boolean condition which decides if a protocol is shut down by itself or forced
   *        to do it
   * @author hendiehl
   */
  public void shutdownProtocol(boolean selfcall) { // change !!!!!!!!!!!!!!!!!!!
    System.out.println("SERVER PROTOCOL : Shutdown");
    this.isRunning = false;
    if (selfcall) {
      this.sendShutdownMsg();
    }
    this.closeConnection();
  }

  /**
   * Method to send a last shutdown message to the client, to give them the opportunity to handle.
   * No respond expected.
   * 
   * @author hendiehl
   */
  public void sendShutdownMsg() { // critical
    Message msg = new Message(MessageType.SHUTDOWN, this.player);
    if (this.client != null && !this.client.isClosed()) { // Doesn't go in here ??????ß
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Shutdown-Message sended");
    } else { // only trying purpose !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Here problem in
             // kick procedure
      this.out.writeObject(msg);
      this.out.flush(); // change
    }
  }

  /**
   * Method to get the specific player class of a client.
   * 
   * @author hendiehl
   */
  public Player getPlayer() {
    return this.player;
  }

  /**
   * Method to send the client information about the Lobby, like player amount.
   * 
   * @author hendiehl
   */
  @Override
  public void updateLobbyinformation(ArrayList<Player> players) {
    // System.out.println("Update the Lobby information");
    LobbyInformationMessage msg =
        new LobbyInformationMessage(MessageType.LOBBY, this.player, players);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Lobby-Update sended");
  }

  /**
   * Method to send a message to an client to inform him that he was kicked by an host from the
   * lobby. A shutdown message is expected.
   * 
   * @author hendiehl
   */
  public void sendKickMessage() {
    Message msg = new Message(MessageType.KICK, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Kick-Message sended");
  }

  /**
   * Method to adding points to the internal positions in the game.
   * 
   * @author hendiehl
   */
  public void addSequence(int i) {
    this.sequencePos += i;
  }

  /**
   * Method to return the number representing the sequence election points.
   * 
   * @author hendiehl
   */
  public int getSequencePos() {
    return this.sequencePos;
  }

  /**
   * Method to start the lobby election procedure by an client after a game is full or going to
   * start.
   * 
   * @author hendiehl
   */
  public void sendFullMessage() {
    Message msg = new Message(MessageType.FULL, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Full-Message sended");
  }

  /**
   * Method to inform the clients that the game is about to start. A sequence Message is expected,
   * except of the host protocol.
   * 
   * @author hendiehl
   */
  @Override
  public void sendStartMessage() {
    Message msg = new Message(MessageType.START, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Start-Message sended");
  }

  /**
   * Method to inform the lobby member that the lobby will be changed to gameField.
   * 
   * @author hendiehl
   */
  @Override
  public void sendGameMessage(ArrayList<Player> players) {
    System.err.println("List by server send");
    // Emergency solution because of data loose
    int[] ids = new int[4];
    for (int i = 0; i < players.size() && i < 4; i++) {
      ids[i] = players.get(i).getId();
      System.err.println(
          "Player : " + players.get(i).getName() + " connected with id " + players.get(i).getId());
    }
    int ownID = this.player.getId();
    // the server protocol works on the same machine as the GameInfoController so the id is here in
    // the instance.
    GameMessage msg = new GameMessage(MessageType.GAME, this.player, players, ids, ownID);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Game-Message sended");
  }

  /**
   * Method to inform a player that his move has started.
   * 
   * @author hendiehl
   */
  @Override
  public void startMove(int turn, int id) {
    MoveMessage msg = new MoveMessage(MessageType.MOVE, this.player, turn, id);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Move-Message sended");
  }

  /**
   * Method inform the players which player is actually on move if they are not self on the move and
   * which turn number it is.
   * 
   * @param player on the move others than the actual player himself
   * @author hendiehl
   */
  @Override
  public void informOther(int turn, int id) {
    OtherMessage msg = new OtherMessage(MessageType.OTHER, this.player, turn, id);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Other-Message sended");
  }

  /**
   * Method to send the chosen content of an multiplier file to the players.
   * 
   * @param path content of the file chosen by the host
   * @author hendiehl
   */
  @Override
  public void sendFieldMessage(String path) {
    FieldMessage msg = new FieldMessage(MessageType.FIELD, this.player, path);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Field-Message sended");
  }

  /**
   * Method to send the chosen content of an dictionary file to the players.
   * 
   * @param dictionaryContent content of the file chosen by the host
   * @author hendiehl
   */
  @Override
  public void sendDictionaryMessage(String dictionaryContent) {
    FieldMessage msg = new FieldMessage(MessageType.DICT, this.player, dictionaryContent);
    // because of same type of Message content the FieldMessage can also be used
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Dictionary sended");
  }

  /**
   * Method to inform the player about the action an other player performed in his move.
   * 
   * @param action action string of the other player
   * @param points points gained by this action
   * @param id id of the player performed the action
   * @author hendiehl
   */
  @Override
  public void sendActionMessage(String action, int points, int id) {
    ActionMessage msg = new ActionMessage(MessageType.ACTION, this.player, action, points, id);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Action-Message sended");
  }

  /**
   * Method to exchange the used GamInfoController in reason to return from an network game back
   * into the lobby. Is used to set back internal control structures.
   * 
   * @param gameInfoController2 New instance of an GameInfoController
   * @author hendiehl
   */
  @Override
  public void resetGameInfoCon(GameInformationController gameInfoController2) {
    this.gameInfoController = gameInfoController2;
    System.out.println("SERVER PROTOCOL : Game-Info exchange");
  }

  /**
   * Method to inform a client about his game win in reason to save his statistics.
   * 
   * @param points gained in a network game.
   * @author hendiehl
   */
  @Override
  public void sendDBMessage(boolean won) {
    DBMessage msg = new DBMessage(MessageType.DB, this.player, won);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER Protocol : DB-Message sended");
  }

  /**
   * Method to send the player the result of an network game. Will cause a screen change to the
   * lobby screen again.
   * 
   * @param players List of players classes of the network game, also Ai.
   * @param points Array of the gained points, in same order like the list.
   * @author hendiehl
   */
  @Override
  public void sendResultMessage(ArrayList<Player> players, int[] points,
      ArrayList<Player> ordered) {
    ResultMessage msg =
        new ResultMessage(MessageType.RETURN, this.player, players, points, ordered);
    this.out.writeObject(msg);
    this.out.flush();
    this.sequencePos = 0; // setting election back
    System.out.println("SERVER PROTOCOL : Result-Message sended");
  }

  /**
   * Method to set the actual amount of tiles left in the LetterBag to every player. A
   * AceptedMessage is used because it provides the right parameter.
   * 
   * @param size Tiles left in the LetterBag
   * @author hendiehl
   */
  @Override
  public void sendBagSize(int size) {
    AceptedMessage msg = new AceptedMessage(MessageType.SIZE, this.player, size);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : SIZE-Message sended");
  }

  /**
   * Method to inform the player in the game screen about a player who the game. Here is a
   * AceptedMessage used because it provides the right parameter.
   * 
   * @param id of the player who left the game.
   * @author hendiehl
   */
  @Override
  public void sendDeleteMessage(int id) {
    AceptedMessage msg = new AceptedMessage(MessageType.DELET, this.player, id);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Delete-Message sended");
  }

  /**
   * Method to inform a player in game that the game is about to end.
   * 
   * @author hendiehl
   */
  @Override
  public void sendPrepMessageChange() {
    Message msg = new Message(MessageType.PREP, this.player);
    this.out.writeObject(msg);
    this.out.flush();
    System.out.println("SERVER PROTOCOL : Prepare-Message sended");
  }
}
