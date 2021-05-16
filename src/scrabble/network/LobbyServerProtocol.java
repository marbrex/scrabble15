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
   * Class which handle the communication with a client
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
   * constructor of the LobbyProtocol which are used to get in contact with players who want to join
   * an Lobby or an game
   * 
   * @param s Socket given by the Lobby Server, which are connected to an client
   * @param gameLobby controller of the GameLobbyScreen
   * @param gameInfoController controller which holds game specific information
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
   * run method of the thread which waits for an client message and react to specific MessageTypes
   */
  public void run() {
    System.out.println("SERVER PROTOCOL : Protocol started");
    while (isRunning) {
      react();
    }
    System.err.println("SERVER PROTOCOL OUTRUN");
  }

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
   * Method to react to an incoming LetterBag message. Has the function to perform a specific action
   * on the global LetterBag instance
   * 
   * @param message LetterBagMessage
   * @author hendiehl
   */
  private void reactToBag(Message message) {
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
   * Method to send the response back to the client
   * 
   * @param msg LetterBagMultisetReturnMessage
   * @author hendiehl
   */
  private void sendLetterBagResponse(LetterMultisetReturnMessage msg) {
    try {
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to react to an End Message received when a player wants to end his turn
   * 
   * @param message
   */
  private synchronized void reactToEnd(Message message) {
    System.out.println("SERVER PROTOCOL : End-Message received");
    this.gameInfoController.endMoveForTime(); // did he go in or not ?
  }

  /**
   * Method to react to an incoming start Message of a client which transmit the chosen player
   * sequence
   * 
   * @param message Message of the client, type : StartMessage
   */
  private void reactToStart(Message message) {
    System.out.println("SERVER PROTOCOL : Start-Message received");
    StartMessage msg = (StartMessage) message;
    this.gameInfoController.addSequence(msg.getSequence(), this);
  }

  /**
   * method to react to a client which ends the connection
   */
  private void reactToShutdown() {
    System.out.println("SERVER PROTOCOL : Shutdown-Message received");
    this.deletePlayer();
    // need to be deleted from server list
  }

  /**
   * method to delete the specific protocol
   */
  public void deletePlayer() {
    this.corespondingServer.deleteSpecificProtocol(this);
    this.gameInfoController.deletePlayer(this);
    // this.gameInfoController.updateAllLobbys();
    this.shutdownProtocol(false);
  }

  /**
   * method which react to an JoinMessage of an User If the gameInformationController add the player
   * information about the lobby are ended. If the gameInformationController not add the player the
   * client will be rejected.
   * 
   * @param message message which will be casted to the specific MessageType to get information
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
   * method to send an rejection information to a client, a shutdown message is expected.
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
   * method to send information about the Lobby to the client and also inform all players in the
   * lobby about the joining, also himself
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
   * method which sends the first information message for an client. Executed by the
   * LobbyServerThread not this thread because of the call hierarchy. A Join message is expected.
   */
  public void sendInformationMessage() {
    // System.out.println("Send basic lobby information");
    InformationMessage iM = new InformationMessage(MessageType.INFORMATION, this.player, // here
                                                                                         // perhaps
                                                                                         // null
                                                                                         // pointer
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
   * method to close the connection to an client
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
   * method to shutdown the protocol
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
   * method to send a last shutdown message to the client, to give them the opportunity to handle.
   * No respond expected.
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

  // here Problem with the null Pointer, player have to be set after first contact
  /**
   * method to get the specific player class of a client.
   */
  public Player getPlayer() {
    return this.player;
  }

  /**
   * method to send the client information about the Lobby, like player amount
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
   * method to adding points to the internal positions in the game
   */
  public void addSequence(int i) {
    this.sequencePos += i;
  }

  /**
   * method to return the number representing the sequence election points
   */
  public int getSequencePos() {
    return this.sequencePos;
  }

  /**
   * starting the lobby maximum procedure
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
   * Method to inform the clients that the game is about to start A sequence Message is expected,
   * except of the host protocol
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
   * Method to inform the lobby member that the lobby will be changed to gameField
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
   * Method to inform a player that his move has started
   */
  @Override
  public void startMove() {
    try {
      Message msg = new Message(MessageType.MOVE, this.player);
      this.out.writeObject(msg);
      this.out.flush();
      System.out.println("SERVER PROTOCOL : Move-Message sended");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  /**
   * Method to show the player on the move if the actual player isn't on the move
   * 
   * @param player on the move others than the actual player himself
   * @author hendiehl
   */
  @Override
  public void informOther(Player player) {
    try {
      Message msg = new Message(MessageType.OTHER, player);
      this.out.writeObject(msg);
      this.out.flush();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
