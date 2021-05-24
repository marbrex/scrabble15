package scrabble.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import scrabble.model.GameInformationController;

public class GameHandler extends Thread {
  /**
   * GameHandler class has the function to control the turn procedure and the information
   * transmitting of a network game.
   * 
   * @author hendiehl
   */
  private GameInformationController game;
  /** boolean to control the execution of the thread */
  private boolean gameIsOn;
  /** working on a reference copy because of a deadlock */
  private ArrayList<NetworkPlayer> players;
  /** counter for turns without action */
  private int actionlessMove;
  /** actual player which is on Move */
  private NetworkPlayer actual;
  /** Counter of the player turns during a game */
  private int turn = 0;
  /** Map for saving the points */
  private HashMap<NetworkPlayer, Integer> points;
  private LobbyHostProtocol host;
  private NetworkPlayer winner;
  private boolean loading;

  // should i control a specific notify call ?
  /**
   * Constructor for the GameHandler which setting the corresponding GameInformationController
   * 
   * @param game GameInformationController
   * @param players list of all NetworkPlayers of the game.
   * @author hendiehl
   */
  public GameHandler(GameInformationController game, ArrayList<NetworkPlayer> players) {
    this.loading = true;
    this.game = game;
    this.players = players;
    this.points = new HashMap<NetworkPlayer, Integer>();
    // initialize the map and the host
    {
      for (NetworkPlayer player : this.players) {
        if (player instanceof LobbyHostProtocol) {
          this.points.put(player, 0);
          this.host = (LobbyHostProtocol) player; // setting host;
        } else {
          this.points.put(player, 0);
        }
      }
    }
  }

  /**
   * Method of the Thread class which have the function to organize the game moves of players during
   * a game.
   * 
   * @author hendiehl
   */
  public void run() {
    System.err.println("GAME HANDLER : Started");
    this.waitMazimumTime(); // wait until all players finished loading
    while (gameIsOn) {
      System.out.println("GAME HANDLER : Make complete turn");
      this.makeATurn(); // Perhaps wrapping it in a synchronized method ?
    }
    // Here the GameHandler stop the turn procedure --> game ends
    this.calculateWinner(); // Calculate the winner with gained points
    this.sendResults(); // Sending game results for showing purpose
    this.sendDBPoints(); // sending the gained points to the player so they will be added to the DB.
    System.err.println("GAME HANDLER : Outrun");
  }

  /**
   * Method to send the results of an network game for showing purpose.
   * 
   * @author hendiehl
   */
  private void sendResults() {
    // Here a GameMessage can be used again because of same parameters.
    int[] ids = new int[4];
    int[] points = new int[4];
    // All additional informations will be send in different arrays in same order like the player
    // list.
    // Is done this way to prevent data loose.
    for (int i = 0; i < this.players.size(); i++) {
      ids[i] = this.players.get(i).getPlayer().getId(); // setting id sequence
      points[i] = this.points.get(this.players.get(i)); // setting points sequence
    }
    // Now sending the Message to all players --> perhaps changing the screen in this moment.
  }

  /**
   * Method to calculate the winner of an network game.
   * 
   * @author hendiehl
   */
  private void calculateWinner() {
    this.players.sort(new Comparator<NetworkPlayer>() {

      // Method to sort them in reversed sequence
      @Override
      public int compare(NetworkPlayer arg0, NetworkPlayer arg1) {
        if (points.get(arg0) > points.get(arg1)) {
          return -1;
        } else if (points.get(arg0) < points.get(arg1)) {
          return 1;
        } else {
          if (players.indexOf(arg0) > players.indexOf(arg1)) {
            return -1;
          } else if (players.indexOf(arg0) < players.indexOf(arg1)) {
            return 1;
          } else {
            return 0;
          }
        }
      }

    });
    // Control prints
    for (NetworkPlayer player : this.players) {
      System.out.println("GAMA INFO : " + player.getPlayer().getName() + " with "
          + this.points.get(player) + " points");
    }
    this.winner = this.players.get(0); // First player has most points
  }

  /**
   * Method to send each Player his gained points in reason to save them in the Database.
   * 
   * @author hendiehl
   */
  private void sendDBPoints() {
    for (NetworkPlayer player : this.players) {
      if (!(player instanceof LobbyAiProtocol)) { // only non AiPlayers
        player.sendDBMessage(this.points.get(player), player.equals(winner)); // inform them about
                                                                              // their result
      }
    }

  }

  /**
   * Method to start the game procedure.
   * 
   * @author hendiehl
   */
  public void startGame() {
    this.gameIsOn = true;
    this.start(); // starting the thread --> run method
  }

  /**
   * Method representing a full play turn through all players.
   * 
   * @author hendiehl
   */
  private void makeATurn() {
    for (NetworkPlayer player : this.players) {
      this.checkRunning(); // check if a game should stop
      if (!this.gameIsOn) { // leaving the loop if a game stopped
        break;
      }
      System.out.println("GAME HANDLER : Player turn : " + player.getPlayer().getName());
      if (player instanceof LobbyAiProtocol) {
        System.out.println("GAME HANDLER : AI move");
        this.actual = player;
        this.informOthers();
        this.waitAiTime(); // Only in purpose to show AiPlayer on the field --> 10sek
        LobbyAiProtocol ai = (LobbyAiProtocol) player; // special move method of AiPlayer
        ai.aiMove(this.host.getGameScreen());
      } else { // In case : LobbyHost- or LobbyServerProtocol
        System.out.println("GAME HANDLER : Human move");
        this.actual = player;
        player.startMove(this.turn, this.actual.getPlayer().getId()); // here adding the ID.
        this.informOthers();
        this.waitMazimumTime(); // change to approach only by HumanPlayer
        // here work with player move info
        this.checkRunning(); // check if a game should be ended --> any condition
        // Perhaps here not needed
      }
      this.turn++; // increase the turn counter
    }

  }

  /**
   * Method to give the game fields time to show the move of an AiPlayer.
   * 
   * @author hendiehl
   */
  private synchronized void waitAiTime() {
    try {
      this.wait(10000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Method to inform other players that a player is on the move.
   * 
   * @author hendiehl
   */
  private void informOthers() {
    for (NetworkPlayer player : this.players) { // go through them
      if (!player.equals(this.actual)) {
        player.informOther(this.turn, this.actual.getPlayer().getId()); // here sending the id + //
                                                                        // turn
      }
    }
  }

  /**
   * Method to break the loop if the game should stop.
   * 
   * @author hendiehl
   */
  private void checkRunning() {
    if (this.actionlessMove == 6) { // six successive turns without action
      this.gameIsOn = false; // ending the game
    }
  }

  /**
   * Method to waiting for a player turn. Interrupted by a NetworkPlayer to inform the server that
   * he finished his move.
   * 
   * @author hendiehl
   */
  private synchronized void waitMazimumTime() { // here a illegal monitor state exception occurs
    System.out.println("GAME HANDLER : Wait move time");
    try {
      this.wait(); // wait until player inform server
    } catch (InterruptedException e) {
      // Doing something change to a other approach different message for endTurn and forceEndTurn
    }
  }

  /**
   * Method to interrupt a waiting gameHandler during a move. Will be called when a player ends his
   * move.
   * 
   * @param action String representation of the action a player performed in his last move
   * @param points Points a player gain with his last move action.
   * @author hendiehl
   */
  public synchronized void endMoveForTime(String action, int points) {
    // here adding a actual condition
    System.out.println("GAME HANDLER : End move in time");
    this.processAction(action, points); // work with the action
    this.notify(); // wake handler for next move
  }

  private void processAction(String action, int points) {
    if (action.matches("")) { // move without action
      System.out.println("GAME HANDLER : Actionless move");
      this.actionlessMove++; // increase counter
    } else {
      System.out.println("GAME HANDLER : Action move");
      this.actionlessMove = 0; // setting back counter
      int point = points + this.points.get(actual); // adding points
      this.points.replace(actual, point); // replacing points
      this.informActions(action, points);
    }
  }

  /**
   * Method to inform other players about the action a player performed during his last move.
   * Information will be send to other players to update their game field.
   * 
   * @param action String representation of last move action
   * @param points Points gained through the last move
   * @author hendiehl
   */
  private void informActions(String action, int points) {
    for (NetworkPlayer player : this.players) { // go through them
      if (!player.equals(this.actual)) { // not the actual
        if (!(player instanceof LobbyAiProtocol)) { // no AiPlayers they get information from host
                                                    // grid
          player.sendActionMessage(action, points, this.actual.getPlayer().getId());
        }
      }
    }
  }

  /**
   * Method to shut down the GameHandler.
   * 
   * @author hendiehl
   */
  public void shutdown() {
    this.gameIsOn = false;
    this.invokeTurnPhase(); // In case the thread is waiting
  }

  /**
   * Method to wake the thread after all players finishing loading their game field.
   */
  public synchronized void invokeTurnPhase() {
    if (this.loading) { // in case the method is called in the turn phase.
      this.notify();
      this.loading = false;
    }
  }

  /**
   * Method to return from the game field to the lobby after the game finished. Will prepare a new
   * list of members without the LobbyAiProtocols
   * 
   * @author hendiehl
   */
  private void prepareLobbyReturn() {
    ArrayList<NetworkPlayer> list = new ArrayList<NetworkPlayer>(); // new list
    list.add(this.host); // Adding host always first for the lobby
    for (NetworkPlayer player : this.players) { // Free list from AiPlayers
      if (player instanceof LobbyServerProtocol) {
        list.add(player); // Adding only LobbyServerProtocols
      }
    }
    this.game.prepareLobbyReturn(list); // changing the GameInfoController and the screen by all
                                        // members
  }

  /**
   * Method to inform the GameHandler that a player has left the game.
   * 
   * @param player player deleted from the list
   */
  public synchronized void playerDeleted(NetworkPlayer player) {
    this.points.remove(player);
    // Player should be deleted from list because of same
    if (player.equals(actual)) { // the actual player has left the game
      if (this.getState() == Thread.State.WAITING) { // Thread waits for end move
        this.invokeTurnPhase(); // just notify the thread so the method can be used
      }
    }
    // Now the players have to be informed.

  }
}
