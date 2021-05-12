package scrabble.network;

import java.util.ArrayList;
import java.util.Arrays;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.model.Profile;
import scrabble.GameController;
import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;

public class LobbyHostProtocol implements NetworkPlayer, NetworkScreen {
  /**
   * Dummy protocol of an host which handle the screen update in an lobby/game
   * 
   * @author hendiehl
   */
  /** controller of the gameLobby */
  private GameLobbyController gameLobby;
  /** controller for game information */
  private GameInformationController gameInfoController;
  /** player instance of the protocol host are a human player */
  private HumanPlayer player;
  /** Variable to compare the position in the list to create a game sequence of human player */
  private int sequencePos;
  /** chat client for sending/receiving messages */
  private Client chat;


  // Only testing !!!!!!!!!!!!!!!!!!!!!!!!!!
  private GameController gameScreen;


  /**
   * Constructor
   * 
   * @param gameLobby controller of the screen
   */
  public LobbyHostProtocol(GameLobbyController gameLobby, GameInformationController gameInfo) {
    this.gameLobby = gameLobby;
    this.gameInfoController = gameInfo;
    this.loadPlayer();
    System.out.println("HOST PROTOCOL : Protocol created");
    this.updateLobbyinformation(new ArrayList<Player>(Arrays.asList(this.player)));
  }

  /**
   * method to get Player from Database
   */
  private void loadPlayer() {
    this.player = Profile.getPlayer();
    System.out.println("HOST PROTOCOL : Loading player");
    if(this.player != null) {
      System.err.println("Player is not null");
      System.err.println(player.getName());
    }
  }

  /**
   * method to get the player class instance.
   */
  @Override
  public Player getPlayer() {
    return this.player;
  }

  /**
   * method to update the player profiles on the screen.
   */
  @Override
  public void updateLobbyinformation(ArrayList<Player> players) {
    this.gameLobby.resetProfileVisibility();
    for (int i = 0; i < players.size(); i++) {
      this.gameLobby.setProfileVisible(i, players.get(i).getName());
      this.gameLobby.setProfilePicture(i, "img/" + players.get(i).getImage());
    }

  }

  /**
   * method to delete an player from the lobby
   * 
   * @param i player want to deleted
   */
  public void kickPlayer(int i) {
    System.out.println("HOST PROTOCOL : Kick client");
    if (i > 0) { // at the moment the host is 0 (every time), will be changed in future,
      this.gameInfoController.kickPlayer(i);
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
    System.out.println("HOST PROTOCOL : START ELECTION");
    this.gameLobby.startElection();
  }

  /**
   * Method to send a chat message with the chat client to the Chat server
   * 
   * @param message message of the chat client which will be send to the server
   */
  public void sendChatMessage(String message) {
    if (this.gameLobby != null) {
      this.chat.sendMessageToServer(message);
    }
  }

  /**
   * Method to handle an incoming chat message and print them on the GameLobbyScreen or the
   * GameScreen itself
   * 
   * @param message message from the chat server
   */
  public void printChatMessage(String message) {
    if (this.gameLobby != null) {
      this.gameLobby.printChatMessage(message);
    }
  }

  /**
   * Method to create a Chat Client with an port given by the ServerProtocol
   * 
   * @param port port of the Chat server started by the Main Server
   */
  public void startChatClient(int port) {
    this.chat = new Client(this, port, this.player.getName());
    this.chat.connect();
    this.chat.start();
    System.out.println("HOST PROTOCOL : Chat client started");
    // this.sendChatMessage("Hi i am in "); //sending first message for testing purpose
  }

  /**
   * Method to get the the amount of players in the lobby, used by the GameLobbyController to set up
   * the position election
   */
  @Override
  public int getPlayerAmount() {
    return this.gameInfoController.getPlayerAmount();
  }

  /**
   * Method to inform the clients that the game is about to start A sequence Message is expected,
   * except of the host protocol
   */
  @Override
  public void sendStartMessage() { // Perhaps the need of a time limited call back is needed
    this.gameInfoController.addSequence(this.gameLobby.getPositionList(), this);
  }

  /**
   * Method to inform the lobby member that the lobby will be changed to gameField
   */
  @Override
  public void sendGameMessage() {
    this.gameLobby.startGame();
  }

  /**
   * Method to start a Game before the lobby is full, in case a host doesn't want to wait. The game
   * will then be filled with AiPlayers.
   */
  public void startGame() {
    System.out.println("HOST PROTOCOL : Start game");
    this.gameInfoController.lobbyFull();
  }

  @Override
  public void startMove() {
    if (this.gameScreen != null) { // be aware of not loading gameScreen
      // this.gameScreen.startMove();
    }

  }

  @Override
  public void endMove() {
    if (this.gameScreen != null) {
      // this.gameScreen.endMove();
    }

  }

  public void setGameScreen(GameController gameScreen) {
    this.gameScreen = gameScreen;
  }

  /**
   * Method to inform the Server that a player ended his move in Time
   */
  @Override
  public synchronized void sendEndMessage() {
    System.out.println("HOST PROTOCOL : End move by self");
    this.gameInfoController.endMoveForTime();// he didn't go in ???????????????????
  }
  /**
   * Method to shut down the complete server
   */
  public void shutdown() {
    System.out.println("HOST PROTOCOL : Prepare shutdown");
    this.gameInfoController.shutdown();
  }
  /**
   * Method to shutdown the chat protocol
   */
  @Override
  public void stopChatClient() {
    if(this.chat != null) {
      this.chat.disconnect();
      System.out.println("HOST PROTOCOL : Chat client stopped");
    }
  }
}
