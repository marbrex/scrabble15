package scrabble.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import com.google.common.collect.Multiset;
import scrabble.game.LetterBag.Tile;
import scrabble.game.LetterBag;
import scrabble.network.GameHandler;
import scrabble.network.LobbyAiProtocol;
import scrabble.network.LobbyHostProtocol;
import scrabble.network.LobbyServer;
import scrabble.network.LobbyServerProtocol;
import scrabble.network.NetworkPlayer;
import scrabble.network.StartGameHandler;

public class GameInformationController {
  /**
   * Class which save and control informations about a network game. Is accessible from multiple
   * server threads.
   * 
   * @author hendiehl
   */
  private LobbyServer mainServer; // server himself
  private ArrayList<NetworkPlayer> players; // List of all players, maximum of 4
  private GameStatusType status; // status => Lobby or Game
  private int gamePort; // port to the GameServer
  private HashMap<NetworkPlayer, Boolean> check;
  private GameHandler gameHandler;// responsible for move organization
  private LetterBag bag;
  private String multiplierContent = ""; // own chosen multiplier field
  private String dictionaryContent = ""; // own chosen dictionary file

  /**
   * Constructor which initialize the class and set up important help classes.
   * 
   * @param mainServer server started by the host
   * @author hendiehl
   */
  public GameInformationController(LobbyServer mainServer) {
    this.mainServer = mainServer;
    this.players = new ArrayList<NetworkPlayer>();
    this.status = GameStatusType.LOBBY;
    this.check = new HashMap<NetworkPlayer, Boolean>();
  }

  /**
   * Method which returns the amount of players in a game/lobby. Will be executed by several threads
   * 
   * @return amount of players in game
   * @author hendiehl
   */
  public synchronized int getPlayerAmount() {
    return this.players.size();
  }

  /**
   * Method to get the boolean condition about the actual chosen multiplier field file. Will return
   * true for standard or false for own file
   * 
   * @return boolean condition about standard multiplier or not
   * @author hendiehl
   */
  public boolean isStandardMultiplier() {
    return this.multiplierContent.matches("");
  }

  /**
   * Method to get the boolean condition about the actual chosen dictionary field file. Will return
   * true for standard or false for own file
   * 
   * @return boolean condition about standard dictionary or not
   * @author hendiehl
   */
  public boolean isStandardDictionary() {
    return this.dictionaryContent.matches("");
  }

  /**
   * Method which returns the type of the game status. Will be executed by several threads
   * 
   * @return status of the game
   * @author hendiehl
   */
  public synchronized GameStatusType getStatus() {
    return this.status;
  }

  /**
   * Method to add players to a game. Only adds a player if the maximum of 4 players are not
   * reached. Will be executed by several threads
   * 
   * @param player player which want to be added
   * @return information about the success of add procedure
   * @author hendiehl
   */
  public synchronized boolean addPlayer(NetworkPlayer player) { // problem of
    if (this.players.size() < 4 && this.status == GameStatusType.LOBBY) {
      this.players.add(player);
      this.check.put(player, false);
      return true; // player added
    } else {
      // this.lobbyFull(); // Perhaps change the call location because of update procedure
      // wrong location do it above
      return false; // player not added because game/lobby is full
    }
  }

  /**
   * Method to check maximum player amount in the lobby and activate the game procedure
   * 
   * @author hendiehl
   */
  public void checkLobbySize() {
    if (this.players.size() == 4) {
      System.err.println("Maximum Player Amount Joined");
      this.lobbyFull(); // will be called before the last lobby updates
                        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
  }

  /**
   * Method to start lobby procedure for a full lobby by starting the handler for a network game and
   * chnaging the lobby status
   * 
   * @author hendiehl
   */
  public void lobbyFull() { // do it in the StartHandler thread
    this.status = GameStatusType.GAME; // Is it also end to the clients
                                       // ????????????????????????????????????????????ß
    StartGameHandler starter = new StartGameHandler(this);
    starter.start(); // start the starting procedure.
  }

  /**
   * Method to inform the clients that the lobby maximum is reached.
   * 
   * @author hendiehl
   */
  public synchronized void sendFullMessages() {
    for (NetworkPlayer player : this.players) {
      player.sendFullMessage();
    }

  }

  /**
   * Method to inform the lobby that the game will start
   * 
   * @author hendiehl
   */
  public synchronized void sendStartMessage() {
    for (NetworkPlayer player : this.players) {
      player.sendStartMessage();
    }
  }

  /**
   * Method to set the port of a specific server
   * 
   * @param port port given and generated by the LobbyServer which are used by the game server
   * @author hendiehl
   */
  public synchronized void setGamePort(int port) {
    this.gamePort = port;
  }

  /**
   * Method to get the position of an player in reason to show them on the Lobby Screen.
   * 
   * @param player Player which position should be find
   * @return return the index of the player (0-3) or 5 if not in the list
   * @author hendiehl
   */
  public synchronized int getPlayerPosition(NetworkPlayer player) {
    if (this.players.contains(player)) {
      return this.players.indexOf(player);
    } else {
      return 5; // later changed with exception
    }
  }

  /**
   * Method to copie all player profiles of the lobby in reason to send them through the network.
   * 
   * @return players List of all Player profiles
   * @author hendiehl
   */
  private ArrayList<Player> getPlayersInformation() {
    ArrayList<Player> players = new ArrayList<Player>();
    for (NetworkPlayer player : this.players) {
      if (player.getPlayer() == null) { // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        System.err.println("Player instance of Protocol is null in InfoCon");
      } else {
        System.err.println(player.getPlayer().getName()); // Player instance of Joiner is null
      }
      players.add(player.getPlayer());
    }
    return players;
  }

  /**
   * Method to update the lobby informations of all players, by sending them a list of all players
   * in the lobby.
   * 
   * @author hendiehl
   */
  public synchronized void updateAllLobbys() {
    ArrayList<Player> playersArrayList = this.getPlayersInformation();
    for (NetworkPlayer player : this.players) {
      // System.err.println("Update all ");
      player.updateLobbyinformation(playersArrayList);
    }
    // System.err.println("LOBBY UPDATE : " + playersArrayList.size());
  }

  /**
   * Method to delete a player from the game/lobby.
   * 
   * @author hendiehl
   */
  public synchronized void deletePlayer(NetworkPlayer player) { // Possibility of a Deadlock,
                                                                // because of the election
    if (this.players.contains(player)) {
      this.players.remove(player);
      this.check.remove(player);
      this.updateAllLobbys();
      this.checkGameStart(); // just in case that a player left the lobby directly after all other
                             // players send their sequence
    }
  }

  /**
   * Method called by an host to kick a specific player in the lobby and deleting them from the
   * list.
   * 
   * @param i number of the player in the lobby
   * @author hendiehl
   */
  public void kickPlayer(int i) {
    if (i < this.players.size()) {
      System.err.println("Kick Player : " + i);
      LobbyServerProtocol player = (LobbyServerProtocol) this.players.get(i); // will be changed in
                                                                              // future first
                                                                              // approach.
      player.sendKickMessage();
      player.deletePlayer(); // will it call deletePlayer ???????????????????????????????????
    }
  }

  /**
   * Method to sort the list in a specific sequence in dependence of all sequence positions given by
   * the lobby members. In the game the sequence will used because one game turn is one pass through
   * the list.
   * 
   * @author hendiehl
   */
  private void setPlayerSequence() {
    this.players.sort(new Comparator<NetworkPlayer>() {

      @Override
      public int compare(NetworkPlayer arg0, NetworkPlayer arg1) {
        if (arg0.getSequencePos() > arg1.getSequencePos()) {
          return 1;
        } else if (arg0.getSequencePos() < arg1.getSequencePos()) {
          return -1;
        } else {
          if (players.indexOf(arg0) > players.indexOf(arg1)) {
            return 1;
          } else if (players.indexOf(arg0) < players.indexOf(arg1)) {
            return -1;
          }
          return 0;
        }
      }

    });
  }

  /**
   * Method used by Protocols to add the sequence chosen by a member of the Lobby to the protocols
   * 
   * @param pos position array of the user election
   * @author hendiehl
   */
  public synchronized void addSequence(int[] pos, NetworkPlayer caller) {
    System.out.println("GAME INFO : Player sequence received");
    this.check.replace(caller, true);
    for (int i = 0; i < this.players.size(); i++) {
      this.players.get(i).addSequence(pos[i]); // adding a elected position to a protocol
    }
    this.checkGameStart();
  }

  /**
   * Method to check if all protocols have send the election sequence;
   * 
   * @author hendiehl
   */
  private void checkGameStart() {
    boolean checker = true;
    for (NetworkPlayer player : this.players) {
      checker &= this.check.get(player);
    }
    System.out.println("GAME INFO : Check game start");
    if (checker) {
      System.out.println("GAME INFO : Start Game");
      this.startGame();
    }
  }

  /**
   * Method to start the game itself by giving the players the opportunity of chose a player
   * sequence
   * 
   * @author hendiehl
   */
  private void startGame() {
    this.setPlayerSequence();
    this.fillGame();
    this.setGameIDs();
    System.out.println("GAME INFO : Invoke Game");
    this.sendGameMessage(this.getPlayersInformation());// here adding new List
    try {
      this.wait(1000); // For testing perhaps change to a finish approach
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.bag = LetterBag.getInstance();
    // this.fillGame(); // Filling slots of missing Players with AiPlayers
    this.gameHandler = new GameHandler(this, this.players);
    this.gameHandler.startGame();
  }

  /**
   * Method to set a ID for a game. The id has the purpose to compare the players during a network
   * game and identify them on the different instances of player lists on the client machines.
   * 
   * @author hendiehl
   */
  private void setGameIDs() {
    int ids = 1;
    for (NetworkPlayer player : this.players) { // iterating through the NetworkPlayers to set their
                                                // player id
      player.getPlayer().setId(ids); // here set the id's
      ids++; // increase id's
    }
  }

  /**
   * Method to inform the players that the Game starts
   * 
   * @author hendiehl
   * @param players Player list for the started game
   */
  private void sendGameMessage(ArrayList<Player> players) {
    for (NetworkPlayer player : this.players) {
      player.sendGameMessage(players);
    }
  }

  /**
   * Method to end a player move before the maximum time goes by
   * 
   * @author hendiehl
   */
  public void endMoveForTime() { // here freeze
    System.out.println("GAME INFO : Interupt play move");
    this.gameHandler.endMoveForTime(); // Perhaps controlling the interaction so that only the
                                       // player onMove can notify ?
  }

  /**
   * Method to shutdown a Lobby or game
   * 
   * @author hendiehl
   */
  public void shutdown() {
    this.mainServer.shutdown();
    if (this.gameHandler != null) {
      this.gameHandler.shutdown();
    }
  }

  /**
   * Method to fill the list for a network game with AIPLayers if the lobby isn't full
   * 
   * @author hendiehl
   */
  private void fillGame() {
    if (this.players.size() < 4) {
      for (int i = this.players.size(); i < 4; i++) {
        LobbyAiProtocol ai = new LobbyAiProtocol();
        this.players.add(ai);
      }
    }
  }

  // --------------------------------------------
  // Because there should be only one instance of the Letter Bag for all
  // players in a network game they need to get access to a global instance over the network

  /**
   * Method to get access to the grabRandomTile method in an Network game
   * 
   * @return random tile from Letter Bag
   * @author hendiehl
   */
  public Tile grabRandomTile() {
    return this.bag.grabRandomTile();
  }

  /**
   * Method to get access to the getValueOf method in an network game
   * 
   * @param letter
   * @return value of the letter
   * @author hendiehl
   */
  public int getValueOf(char letter) {
    return this.bag.getValueOf(letter);
  }

  /**
   * Method to get access to the getRemainingVowels method in an network game
   * 
   * @return vowels of the letter bag
   * @author hendiehl
   */
  public Multiset<Tile> getRemainingVowels() {
    return this.bag.getRemainingVowels();
  }

  /**
   * Method to get access to the getRemainingConsonants method in an network game
   * 
   * @return consonants of the letter bag
   * @author hendiehl
   */
  public Multiset<Tile> getRemainingConsonants() {
    return this.getRemainingConsonants();
  }

  /**
   * Method to get access to the getRemainingBlanks method in an network game
   * 
   * @return blanks of the letter bag
   * @author hendiehl
   */
  public Multiset<Tile> getRemainingBlanks() {
    return this.bag.getRemainingBlanks();
  }

  /**
   * Method to get access to the grabRandomTiles(int count) method in an network game
   * 
   * @param count amount of tiles
   * @return tiles of the letter bag
   * @author hendiehl
   */
  public Multiset<Tile> grabRandomTiles(int count) {
    return this.bag.grabRandomTiles(count);
  }

  /**
   * Method to get access to the getRemainingTiles method in an network game
   * 
   * @return remaining tiles of the letter bag
   * @author hendiehl
   */
  public Multiset<Tile> getRemainingTiles() {
    return this.bag.getRemainingTiles();
  }

  /**
   * Method to get access to the getAmount method in an network game
   * 
   * @return amount of the letter bag
   * @author hendiehl
   */
  public int getAmount() {
    return this.bag.getAmount();
  }

  /**
   * Method to send the content of an multiplier file to all player, so it can be set.
   * 
   * @author hendiehl
   */
  public synchronized void sendFieldMessage() {
    System.out.println("GAME INFO : Send multiplier content to players");
    for (NetworkPlayer player : this.players) {
      player.sendFieldMessage(this.multiplierContent);
    }
  }

  /**
   * Method to set the content of an multiplier file chosen by host. Will be send to all Player
   * after the game started
   * 
   * @param path content of the chosen file
   * @author hendiehl
   */
  public synchronized void setMultiplier(String path) {
    System.out.println("GAME INFO : Set multiplier content");
    this.multiplierContent = path;
  }

  /**
   * Method to set the content of an dictionary file chosen by host. Will send it to all Player
   * after the game started
   * 
   * @param content of an file chosen by host
   * @author hendiehl
   */
  public void setDictionary(String content) {
    System.out.println("GAME INFO : Set dictionary content");
    this.dictionaryContent = content;
  }

  /**
   * Method to send the content of an multiplier file to all player, so it can be set.
   * 
   * @author hendiehl
   */
  public void sendDictionaryMessage() {
    System.out.println("GAME INFO : Send dictionary content to players");
    for (NetworkPlayer player : this.players) {
      player.sendDictionaryMessage(this.dictionaryContent);
    }
  }
}
