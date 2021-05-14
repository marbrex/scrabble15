package scrabble.network;

import java.util.ArrayList;
import java.util.Arrays;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.model.Profile;
import scrabble.GameController;
import scrabble.GameLobbyController;
import scrabble.model.GameInformationController;
import scrabble.model.GameStatusType;

public class LobbyHostProtocol implements NetworkPlayer, NetworkScreen {
  /**
   * Dummy protocol of an host which handle the screen update in an lobby/game.
   * Is used as a LobbyServer and a LobbyClient protocol, because a host is located on the same machine as
   * the server, so a network connection is not needed
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


  /** GameController of an game screen after changing from lobby to game */
  private GameController gameScreen;


  /**
   * Constructor of the LobbyHostProtocol to set specific instances of a LobbyController and a GameInformationController
   * and loading and updating the player visibility in the lobby.
   * Will be called by the LobbyServer himself after a player decided to host a network game.
   * A LobbyHostProtocol is always part of an lobby.
   * 
   * @param gameLobby GameLobbyController of the lobby screen
   * @param gameInfo Controller class of an network game.
   * @author hendiehl
   */
  public LobbyHostProtocol(GameLobbyController gameLobby, GameInformationController gameInfo) {
    this.gameLobby = gameLobby;
    this.gameInfoController = gameInfo;
    this.loadPlayer();
    System.out.println("HOST PROTOCOL : Protocol created");
    this.updateLobbyinformation(new ArrayList<Player>(Arrays.asList(this.player)));
  }

  /**
   * Method to load the actual chosen player instance of an HumanPlayer chosen in the MainMenu or by app start.
   * Will be loaded from the Database.
   * @author hendiehl
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
   * Method to return the player instance connected with the LobbyHostProtocol
   * @return HumanPlayer instance of the profile loaded in the actual program state.
   * @author hendiehl
   */
  @Override
  public Player getPlayer() {
    return this.player;
  }

  /**
   * Method to update the player profiles on the screen in dependence to the actual list of all lobby members.
   * Will activate the visibility of player profiles in the lobby and their name, after reset the visibility,
   * because it will be also called if an player leave the lobby.
   * @param players ArrayList of a player instance in the Lobby. 
   * @author hendiehl
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
   * Method to kick a player from the lobby and deleting him from the GameInformationController.
   * Will automatically send new lobby information to other players in reason to update the lobby screen.
   * Because a kick is only allowed during the lobby waiting it is only allowed to kick players above position
   * 0, because this is always the host himself during the lobby waiting, because he is ervytime the first player
   * joining a lobby.
   * 
   * @param i player position wanted to be kicked from the lobby.
   * @author hendiehl
   */
  public void kickPlayer(int i) {
    System.out.println("HOST PROTOCOL : Kick client");
    if (i > 0) { // at the moment the host is 0 (every time), will be changed in future,
      this.gameInfoController.kickPlayer(i);
    }

  }

  /**
   * Method to adding points to the internal positions in the game.
   * The points will be given by other players, representing the sequence they chose.
   * @param i position of the host chosen by any player in the lobby 
   * @author hendiehl
   */
  public void addSequence(int i) {
    this.sequencePos += i;

  }

  /**
   * Method to return the number representing the sequence election points.
   * It is the sum of all positions gave by players.
   * @author hendiehl
   */
  public int getSequencePos() {
    return this.sequencePos;
  }

  /**
   * Method called when the maximum of players joined the lobby.
   * Will start the election procedure on the lobby screen in which every player can chose his personal sequence for the game.
   * @author hendiehl
   */
  public void sendFullMessage() {
    System.out.println("HOST PROTOCOL : START ELECTION");
    this.gameLobby.startElection();
  }

  /**
   * Method to send a chat message with the chat client to the Chat server.
   * Called by the lobby screen or the game screen in case the player wants to send a message.
   * @param message message of the chat client which will be send to the server
   * @author hendiehl
   */
  public void sendChatMessage(String message) {
    if (this.gameLobby != null) {
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
    if (this.gameLobby != null) {
      this.gameLobby.printChatMessage(message);
    }
  }

  /**
   * Method to create a Chat Client with an port given by the ServerProtocol.
   * 
   * @param port port of the ChatServer started by the main server.
   * @author hendiehl
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
   * @author hendiehl
   */
  @Override
  public int getPlayerAmount() {
    return this.gameInfoController.getPlayerAmount();
  }

  /**
   * Method to inform the clients that the game is about to start A sequence Message is expected,
   * except of the host protocol
   * @author hendiehl
   */
  @Override
  public void sendStartMessage() { // Perhaps the need of a time limited call back is needed
    this.gameInfoController.addSequence(this.gameLobby.getPositionList(), this);
  }

  /**
   * Method to inform the lobby member that the lobby will be changed to gameField
   * @author hendiehl
   */
  @Override
  public void sendGameMessage() {
    this.gameLobby.startGame();
  }

  /**
   * Method to start a Game before the lobby is full, in case a host doesn't want to wait. The game
   * will then be filled with AiPlayers.
   * @author hendiehl
   */
  public void startGame() {
    System.out.println("HOST PROTOCOL : Start game");
    this.gameInfoController.lobbyFull();
  }
  
  /**
   * Method to signal a player in the game that his game move started.
   * Will call the GameController to enable game controls.
   * @author hendiehl
   */
  @Override
  public void startMove() {
    if (this.gameScreen != null) { // be aware of not loading gameScreen
      // this.gameScreen.startMove();
    }

  }
  /**
   * Method to signal a player that his game move ended.
   * Will call the GameController to disable game controls.
   * @author hendiehl
   */
  @Override
  public void endMove() {
    if (this.gameScreen != null) {
      // this.gameScreen.endMove();
    }

  }
  /**
   * Method to set the GameController to the LobbyHostProtocol after the lobby will be leaved and the
   * GameScreen entered
   * @param gameScreen Corresponding controller to an GameFieldScreen
   * @author hendiehl
   */
  public void setGameScreen(GameController gameScreen) {
    this.gameScreen = gameScreen;
  }

  /**
   * Method to inform the Server that a player ended his move in Time
   * @author hendiehl
   */
  @Override
  public synchronized void sendEndMessage() {
    System.out.println("HOST PROTOCOL : End move by self");
    this.gameInfoController.endMoveForTime();// he didn't go in ???????????????????
  }
  /**
   * Method to shut down the complete server
   * @author hendiehl
   */
  public void shutdown() {
    System.out.println("HOST PROTOCOL : Prepare shutdown");
    this.gameInfoController.shutdown();
  }
  /**
   * Method to shutdown the chat protocol
   * @author hendiehl
   */
  @Override
  public void stopChatClient() {
    if(this.chat != null) {
      this.chat.disconnect();
      System.out.println("HOST PROTOCOL : Chat client stopped");
    }
  }
  /**
   * Method to check if a game started or not, mainly used by LobbyController 
   * @return boolean representation if the GameStatus Type is Lobby or Game
   * @author hendiehl
   */
  public boolean isNotInGame() {
	  return this.gameInfoController.getStatus() != GameStatusType.GAME;
  }
}
