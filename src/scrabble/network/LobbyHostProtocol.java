package scrabble.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import com.google.common.collect.Multiset;
import javafx.util.Pair;
import scrabble.model.HumanPlayer;
import scrabble.model.Player;
import scrabble.model.Profile;
import scrabble.GameController;
import scrabble.GameLobbyController;
import scrabble.dbhandler.DBUpdate;
import scrabble.game.LetterBag.Tile;
import scrabble.game.LetterTile;
import scrabble.model.GameInformationController;
import scrabble.model.GameStatusType;

public class LobbyHostProtocol implements NetworkPlayer, NetworkScreen {
  /**
   * Dummy protocol of an host which handle the screen update in an lobby/game. Is used as a
   * LobbyServer and a LobbyClient protocol, because a host is located on the same machine as the
   * server, so a network connection is not needed.
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
  /** List of all members in the Lobby */
  private ArrayList<Player> players;
  /** own player id during a network game */
  private int ownID;
  /** player list only for show winner, because the normal one need different order */
  private ArrayList<Player> playersResult; // in order of the game win => only one time use.
  /** int array for the points gained during a network game */
  private int[] pointsResult;
  /** short save variable for the LobbyServer to set them to the new LobbyController */
  private LobbyServer shortSave;
  /** GameController of an game screen after changing from lobby to game */
  private GameController gameScreen;


  /**
   * Constructor of the LobbyHostProtocol to set specific instances of a LobbyController and a
   * GameInformationController and loading and updating the player visibility in the lobby. Will be
   * called by the LobbyServer himself after a player decided to host a network game. A
   * LobbyHostProtocol is always part of an lobby.
   * 
   * @param gameLobby GameLobbyController of the lobby screen.
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
   * Constructor for only testing purpose. Is needed for network internal actions during a non
   * network JUnit test.
   * 
   * @author hendiehl
   */
  public LobbyHostProtocol() {

  }

  /**
   * Method to load the actual chosen player instance of an HumanPlayer chosen in the MainMenu or by
   * app start. Will be loaded from the Database.
   * 
   * @author hendiehl
   */
  private void loadPlayer() {
    this.player = Profile.getPlayer();
    System.out.println("HOST PROTOCOL : Loading player");
    if (this.player != null) {
      System.err.println("Player is not null");
      System.err.println(player.getName());
    }
  }

  /**
   * Method to return the player instance connected with the LobbyHostProtocol.
   * 
   * @return HumanPlayer instance of the profile loaded in the actual program state.
   * @author hendiehl
   */
  @Override
  public Player getPlayer() {
    return this.player;
  }

  /**
   * Method to update the player profiles on the screen in dependence to the actual list of all
   * lobby members. Will activate the visibility of player profiles in the lobby and their name,
   * after reset the visibility, because it will be also called if an player leave the lobby.
   * 
   * @param players ArrayList of a player instance in the Lobby.
   * @author hendiehl
   */
  @Override
  public void updateLobbyinformation(ArrayList<Player> players) { // need of change because of
                                                                  // gameField
    this.players = players;
    this.gameLobby.resetProfileVisibility();
    for (int i = 0; i < players.size(); i++) {
      this.gameLobby.setProfileVisible(i, players.get(i).getName());
      this.gameLobby.setProfilePicture(i, "/img/" + players.get(i).getImage());
    }

  }

  /**
   * Method to kick a player from the lobby and deleting him from the GameInformationController.
   * Will automatically send new lobby information to other players in reason to update the lobby
   * screen. Because a kick is only allowed during the lobby waiting it is only allowed to kick
   * players above position 0, because this is always the host himself during the lobby waiting,
   * because he is ervytime the first player joining a lobby.
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
   * Method to adding points to the internal positions in the game. The points will be given by
   * other players, representing the sequence they chose.
   * 
   * @param i position of the host chosen by any player in the lobby
   * @author hendiehl
   */
  public void addSequence(int i) {
    this.sequencePos += i;

  }

  /**
   * Method to return the number representing the sequence election points. It is the sum of all
   * positions gave by players.
   * 
   * @author hendiehl
   */
  public int getSequencePos() {
    return this.sequencePos;
  }

  /**
   * Method called when the maximum of players joined the lobby. Will start the election procedure
   * on the lobby screen in which every player can chose his personal sequence for the game.
   * 
   * @author hendiehl
   */
  public void sendFullMessage() {
    System.out.println("HOST PROTOCOL : START ELECTION");
    this.gameLobby.startElection();
  }

  /**
   * Method to send a chat message with the chat client to the Chat server. Called by the lobby
   * screen or the game screen in case the player wants to send a message.
   * 
   * @param message message of the chat client which will be send to the server
   * @author hendiehl
   */
  public void sendChatMessage(String message) {
    if (this.gameScreen != null) {
      this.chat.sendMessageToServer(message);
    } else if (this.gameLobby != null) {
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
    } else if (this.gameLobby != null) {
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
    this.chat = new Client(this, port, this.player.getName(), "localhost");
    this.chat.connect();
    this.chat.start();
    System.out.println("HOST PROTOCOL : Chat client started");
    // this.sendChatMessage("Hi i am in "); //sending first message for testing purpose
  }

  /**
   * Method to get the the amount of players in the lobby, used by the GameLobbyController to set up
   * the position election.
   * 
   * @author hendiehl
   */
  @Override
  public int getPlayerAmount() {
    return this.gameInfoController.getPlayerAmount();
  }

  /**
   * Method to inform the clients that the game is about to start A sequence Message is expected,
   * except of the host protocol.
   * 
   * @author hendiehl
   */
  @Override
  public void sendStartMessage() { // Perhaps the need of a time limited call back is needed
    this.gameInfoController.addSequence(this.gameLobby.getPositionList(), this);
  }

  /**
   * Method to inform the lobby member that the lobby will be changed to gameField.
   * 
   * @author hendiehl
   */
  @Override
  public void sendGameMessage(ArrayList<Player> players) {
    this.players = players;
    this.ownID = this.player.getId();
    this.gameLobby.startGame();
  }

  /**
   * Method to start a Game before the lobby is full, in case a host doesn't want to wait. The game
   * will then be filled with AiPlayers.
   * 
   * @author hendiehl
   */
  public void startGame() {
    System.out.println("HOST PROTOCOL : Start game");
    this.gameInfoController.lobbyFull();
  }

  /**
   * Method to signal a player in the game that his game move started. Will call the GameController
   * to enable game controls.
   * 
   * @author hendiehl
   */
  @Override
  public void startMove(int turn, int id) {
    if (this.gameScreen != null) { // be aware of not loading gameScreen
      System.out.println("HOST PROTOCOL : Move-Message received");
      this.gameScreen.api.startMove(turn, id); // start Move
    } else {
      System.out.println("HOST PROTOCOL : ERROR");
    }

  }

  /**
   * Method to set the GameController to the LobbyHostProtocol after the lobby will be leaved and
   * the GameScreen entered.
   * 
   * @param gameScreen Corresponding controller to an GameFieldScreen
   * @author hendiehl
   */
  @Override
  public void setGameScreen(GameController gameScreen) {
    System.out.println("HOST PROTOCOL : GAME-Controller set");
    this.gameScreen = gameScreen;
    this.gameLobby = null; // not needed anymore
  }

  /**
   * Getter for the game screen controller in reason to give them to an AiPlayer. AiPlayer work on
   * the field instance of the Host.
   * 
   * @return actual controller of the host game field.
   * @author hendiehl
   */
  public GameController getGameScreen() {
    return this.gameScreen;
  }

  /**
   * Method to inform the Server that a player ended his move in Time.
   * 
   * @param action String representation of the action a player performed in his last move.
   * @param points Points a player gain with his last move action.
   * @author hendiehl
   */
  @Override
  public synchronized void sendEndMessage(String action, int points) {
    System.out.println("HOST PROTOCOL : End move by self");
    this.gameInfoController.endMoveForTime(action, points);// he didn't go in ???????????????????
  }

  /**
   * Method to shut down the complete server.
   * 
   * @author hendiehl
   */
  public void shutdown() {
    System.out.println("HOST PROTOCOL : Prepare shutdown");
    this.gameInfoController.shutdown();
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
      System.out.println("HOST PROTOCOL : Chat client stopped");
    }
  }

  /**
   * Method to check if a game started or not, mainly used by LobbyController.
   * 
   * @return boolean representation if the GameStatus Type is Lobby or Game
   * @author hendiehl
   */
  public boolean isNotInGame() {
    return this.gameInfoController.getStatus() != GameStatusType.GAME;
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
    return this.players;
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void grabRandomTile() {
    Tile tile = this.gameInfoController.grabRandomTile();
    // callback
    this.gameScreen.grabRandomTileAnswer(tile);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getValueOf(char letter) {
    int i = this.gameInfoController.getValueOf(letter);
    // callback
    this.gameScreen.getValueOfAnswer(i);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingVowels() {
    Multiset<Tile> tiles = this.gameInfoController.getRemainingVowels();
    // callback
    this.gameScreen.getRemainingVowelsAnswer(tiles);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingConsonants() {
    Multiset<Tile> tiles = this.gameInfoController.getRemainingConsonants();
    // callback
    this.gameScreen.getRemainingConsonantsAnswer(tiles);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingBlanks() {
    Multiset<Tile> tiles = this.gameInfoController.getRemainingBlanks();
    // callback
    this.gameScreen.getRemainingBlanksAnswer(tiles);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void grabRandomTiles(int count) {
    Multiset<Tile> tiles = this.gameInfoController.grabRandomTiles(count);
    // callback
    this.gameScreen.grabRandomTilesAnswer(tiles);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getRemainingTiles() {
    Multiset<Tile> tiles = this.gameInfoController.getRemainingTiles();
    // callback
    this.gameScreen.getRemainingTilesAnswer(tiles);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to use LetterBag functionality in a network game.
   * 
   * @author hendiehl
   */
  @Override
  public void getAmount() {
    int i = this.gameInfoController.getAmount();
    // callback
    this.gameScreen.getAmountAnswer(i);
    this.gameInfoController.checkBagSize();
  }

  /**
   * Method to show the player on the move if the actual player isn't on the move.
   * 
   * @param player on the move others than the actual player himself
   * @author hendiehl
   */
  @Override
  public void informOther(int turn, int id) {
    System.out.println("HOST PROTOCOL : Other-Message received");
    if (this.gameScreen != null) {
      this.gameScreen.otherPlayerOnMove(turn, id);
    }
  }

  /**
   * Method to set the own multiplier file for the game field and save it on the server.
   * 
   * @param path content of the chosen multiplier file in the configure screen
   * @author hendiehl
   */
  public void setFieldMessage(String path) {
    System.out.println("HOST PROTOCOL : Set own multiplier");
    this.gameInfoController.setMultiplier(path);
  }

  /**
   * Method to set the content of an chosen multiplier file for future use in the game field.
   * 
   * @param path content of an multiplier file
   * @author hendiehl
   */
  @Override
  public void sendFieldMessage(String path) {
    System.out.println("Host PROTOCOL : Muliplier file content received");
    if (this.gameLobby != null) {
      this.gameLobby.setContentOfFile(path, false);
    }
  }

  /**
   * Method to set the own dictionary file content for the game field and save it on the server.
   * 
   * @param path content of the chosen multiplier file in the configure screen
   * @author hendiehl
   */
  public void setDictionaryMessage(String content) {
    System.out.println("HOST PROTOCOL : Set own dictionary");
    this.gameInfoController.setDictionary(content);
  }

  /**
   * Method to set the content of an chosen dictionary file for future use in the game field.
   * 
   * @param dictionaryContent content of an dictionary file
   * @author hendiehl
   */
  @Override
  public void sendDictionaryMessage(String dictionaryContent) {
    System.out.println("HOST PROTOCOL : Dictionary file content received");
    if (this.gameLobby != null) {
      this.gameLobby.setContentOfDictionary(dictionaryContent, false);
    }
  }

  /**
   * Method to inform the GameHandler that the loading of the game field screen finished. Will be
   * called form the GameController in his initialize method.
   * 
   * @author hendiehl
   */
  public void loadFinished() {
    System.out.println("HOST PROTOCOL : Load-Message sended");
    this.gameInfoController.informLoading(this);
  }

  /**
   * Method to inform the host about the action an other player performed in his move.
   * 
   * @param action action string of the other player
   * @param points points gained by this action
   * @param id id of the player performed the action
   * @author hendiehl
   */
  @Override
  public void sendActionMessage(String action, int points, int id) {
    if (this.gameScreen != null) {
      this.gameScreen.api.getOpponentsInfo(action, points, id);
      System.out.println("HOST PROTOCOL : Action-Message sended");
    }
  }

  /**
   * Method to exchange the used GamInfoController in reason to return from an network game back
   * into the lobby. Is used to set back internal control structures.
   * 
   * @param game New instance of an GameInfoController
   * @author hendiehl
   */
  @Override
  public void resetGameInfoCon(GameInformationController game) {
    this.shortSave = this.gameInfoController.getCorrespondingServer();
    this.gameInfoController = game;
    System.out.println("HOST PROTOCOL : Game-Info exchange");
  }

  /**
   * Method to save the result of an network game in the DB of the local player.
   * 
   * @param integer points gained in a network game
   * @param won boolean condition about the player win
   * @author hendiehl
   */
  @Override
  public void sendDBMessage(boolean won) {
    // Here save the data in the corresponding DB
    System.out.println("HOST PROTOCOL : DB-Message sended");
    if (won) {
      System.out.println("HOST PROTOCOL : Host win" + won);
      DBUpdate.updateGamesWon(this.player);
    } else {
      System.out.println("HOST : PROTOCOL : Host win : " + won);
      DBUpdate.updateGamesLost(this.player);
    }
  }

  /**
   * Method to leave the game screen for a new lobby screen and presenting the results of the
   * network game.
   * 
   * @param players Players of the finished network game.
   * @param points Points gained during the game.
   * @author hendiehl
   */
  @Override
  public void sendResultMessage(ArrayList<Player> players, int[] points, ArrayList<Player> humans) {
    this.playersResult = humans;
    this.pointsResult = points;
    this.players = players;
    if (this.gameScreen != null) {
      this.gameScreen.getToAfterGame(this, true, this.shortSave);
      this.shortSave = null;
    }
    this.sequencePos = 0; // setting election back;
  }

  /**
   * Getter method for the own game id during a network game.
   * 
   * @return id of the player during a network game.
   * @author hendiehl
   */
  @Override
  public int getOwnID() {
    return this.ownID;
  }

  /**
   * Method to load a winner screen after a player returned from a network game back to the lobby.
   * Will be called by the lobby after it finished loading to load the screen with data which has
   * been send to the protocol before.
   */
  @Override
  public void informLobbyReturn() {
    System.out.println("HOST PROTOCOL : Lobby load finish");
    if (this.gameLobby != null) {
      this.updateLobbyinformation(players);
      this.gameLobby.showWinScreen(this.playersResult, this.pointsResult);
      this.playersResult = null; // only once needed
      this.pointsResult = null;
    }
  }

  /**
   * Method to change the controller of the screen when new window is loaded.
   * 
   * @param glc
   * @author hendiehl
   */
  @Override
  public void setLobbyController(GameLobbyController glc) {
    System.out.println("HOST PROTOCOL : From game to lobby");
    this.gameLobby = glc;
    this.gameScreen = null;
  }

  /**
   * Method to inform the player about tiles left in the LetterBag.
   * 
   * @param size Tiles left in the message bag.
   * @author hendiehl
   */
  @Override
  public void sendBagSize(int size) {
    if (this.gameScreen != null) {
      this.gameScreen.api.informAboutTileAmount(size);
    }
  }

  /**
   * Method to inform the player in the game screen about a player who the game.
   * 
   * @param id of the player who left the game.
   * @author hendiehl
   */
  @Override
  public void sendDeleteMessage(int id) {
    System.out.println("HOST PROTOCOL : Delete-Message send");
    if (this.gameScreen != null) {
      this.gameScreen.api.informAboutLeave(id);
    }
  }

  /**
   * Method to inform a player in game that the game is about to end.
   * 
   * @author hendiehl
   */
  @Override
  public void sendPrepMessageChange() {
    System.out.println("HOST PROTOCOL : Prep-Message send");
    if (this.gameScreen != null) {
      this.gameScreen.api.informGameEnd();
    }
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

  /**
   * Method to inform the host about a full lobby, in reason to lead him to a game start.
   * 
   * @author hendiehl
   */
  public void informAboutLobby() {
    if (this.gameLobby != null) {
      this.gameLobby.setTimeLabel("Lobby is full, please start the game.");
    }
  }

  /**
   * Method to exchange the letter tiles of a host during a network game.
   * 
   * @param tiles which should be exchanged.
   * @author hendiehl
   */
  @Override
  public void exchangeLetterTiles(ArrayList<Tile> tilesToExchange) {
    Multiset<Tile> tiles = this.gameInfoController.exchangeLetterTiles(tilesToExchange);
    // callback
    this.gameScreen.exchangeLetterTilesAnswer(tiles);
  }

  /**
   * Method to get the remaining quantity of the specified letter tile.
   * 
   * @param letter which should be check
   * @author hendiehl
   */
  @Override
  public void getAmountOf(char letter) {
    int a = this.gameInfoController.getAmountOf(letter);
    // callback
    if (this.gameScreen != null) {
      this.gameScreen.getAmountOfAnswer(a);
    }

  }

  /**
   * Method to use the getAmountOfEveryTile method of the LetterBag.
   * 
   * @author hendiehl
   */
  @Override
  public void getAmountOfEveryTile() {
    ArrayList<Pair<Character, Integer>> amount = this.gameInfoController.getAmountOfEveryTile();
    if (this.gameScreen != null) {
      this.gameScreen.getAmountOfEveryTileAnswer(amount);
    }
  }
}
