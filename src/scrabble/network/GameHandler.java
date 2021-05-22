package scrabble.network;

import java.util.ArrayList;
import java.util.HashMap;
import scrabble.model.GameInformationController;

public class GameHandler extends Thread {
  /**
   * GameInfoController of the game and the lobby, holding the information and logic of a network
   * game.
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

  // should i control a specific notify call ?
  /**
   * Constructor for the GameHandler which setting the corresponding GameInformationController
   * 
   * @param game GameInformationController
   * @param players list of all NetworkPlayers of the game.
   * @author hendiehl
   */
  public GameHandler(GameInformationController game, ArrayList<NetworkPlayer> players) {
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
    System.err.println("GAME HANDLER : Outrun");
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
      if (player instanceof LobbyServerProtocol || player instanceof LobbyHostProtocol) {
        System.out.println("GAME HANDLER : Human move");
        this.actual = player;
        player.startMove(this.turn, this.actual.getPlayer().getId()); // here adding the ID.
        this.informOthers();
        this.waitMazimumTime(); // change to approach only by HumanPlayer
        // here work with player move info
        this.checkRunning(); // check if a game should be ended --> any condition
      } else {
        System.out.println("GAME HANDLER : AI move");
        this.actual = player;
        this.informOthers();
        this.waitAiTime(); // Only in purpose to show AiPlayer on the field --> 10sek
        LobbyAiProtocol ai = (LobbyAiProtocol) player; // only left possibility because of condition
        ai.aiMove(this.host.getGameScreen());
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
   * Method to get the Information of a player turn.
   * 
   * @author hendiehl
   */
  private void getMoveInfo() {
    // TODO Auto-generated method stub

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
    // implement
  }

  /**
   * Method to shut down the GameHandler.
   * 
   * @author hendiehl
   */
  public void shutdown() {
    this.gameIsOn = false;
    // Need of braking the inner loop ?
  }

  /**
   * Method to wake the thread after all players finishing loading their game field.
   */
  public synchronized void invokeTurnPhase() {
    this.notify();
  }
}
