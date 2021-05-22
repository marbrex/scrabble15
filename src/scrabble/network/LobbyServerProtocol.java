package scrabble.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import com.google.common.collect.Multiset;
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
  private ObjectOutputStream out;
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
      this.out = new ObjectOutputStream(s.getOutputStream());
      this.in = new ObjectInputStream(s.getInputStream());
      this.gameInfoController = gameInfoController;
      this.isRunning = true;
      this.player = new HumanPlayer(); // need to be changed !!!!!!!!!!!!!!!!!!!!!!future -> getting
                                       // from first Join message
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
    }
  }

  /**
   * Method to send the response back to the client.
   * 
   * @param msg LetterBagMultisetReturnMessage
   * @author hendiehl
   */
  private void sendLetterBagResponse(LetterMultisetReturnMessage msg) {
    try {
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : LMR-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
      // Implement if for error code 5
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
    try {
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Reject-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
    try {
      this.out.writeObject(msg); // only acceptence message
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Accept-Message sended");
      // other Message from here onwards
      // Update all Lobbys
      this.gameInfoController.updateAllLobbys(); // here the new list is send to all players
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
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
    try {
      this.out.writeObject(iM);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Information-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to close the connection to an client.
   * 
   * @author hendiehl
   */
  private void closeConnection() { // change !!!!!!!!!!!!!!!!!!!!!
    try {
      if (this.client != null) {
        this.client.close(); // close also in and output stream
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
    try {
      if (this.client != null && !this.client.isClosed()) { // Doesn't go in here ??????ß
        this.out.writeObject(msg);
        this.out.flush();
        System.out.println("SERVER PROTOCOL : Shutdown-Message sended");
      } else { // only trying purpose !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Here problem in
               // kick procedure
        this.out.writeObject(msg);
        this.out.flush(); // change
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
    try {
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Lobby-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to send a message to an client to inform him that he was kicked by an host from the
   * lobby. A shutdown message is expected.
   * 
   * @author hendiehl
   */
  public void sendKickMessage() {
    try {
      Message msg = new Message(MessageType.KICK, this.player);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Kick-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

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
    try {
      Message msg = new Message(MessageType.FULL, this.player);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Full-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to inform the clients that the game is about to start. A sequence Message is expected,
   * except of the host protocol.
   * 
   * @author hendiehl
   */
  @Override
  public void sendStartMessage() {
    try {
      Message msg = new Message(MessageType.START, this.player);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Start-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to inform the lobby member that the lobby will be changed to gameField.
   * 
   * @author hendiehl
   */
  @Override
  public void sendGameMessage(ArrayList<Player> players) {
    try {
      LobbyInformationMessage msg =
          new LobbyInformationMessage(MessageType.GAME, this.player, players); // just Testing
                                                                               // purpose
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Game-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to inform a player that his move has started.
   * 
   * @author hendiehl
   */
  @Override
  public void startMove(int turn, int id) {
    try {
      MoveMessage msg = new MoveMessage(MessageType.MOVE, this.player, turn, id);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Move-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

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
    try {
      OtherMessage msg = new OtherMessage(MessageType.OTHER, this.player, turn, id);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Other-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to send the chosen content of an multiplier file to the players.
   * 
   * @param path content of the file chosen by the host
   * @author hendiehl
   */
  @Override
  public void sendFieldMessage(String path) {
    try {
      FieldMessage msg = new FieldMessage(MessageType.FIELD, this.player, path);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Field-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to send the chosen content of an dictionary file to the players.
   * 
   * @param dictionaryContent content of the file chosen by the host
   * @author hendiehl
   */
  @Override
  public void sendDictionaryMessage(String dictionaryContent) {
    try {
      FieldMessage msg = new FieldMessage(MessageType.DICT, this.player, dictionaryContent);
      // because of same type of Message content the FieldMessage can also be used
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Dictionary sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }
}
