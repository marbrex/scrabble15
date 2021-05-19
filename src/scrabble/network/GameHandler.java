package scrabble.network;

import java.util.ArrayList;
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
  /** Int representation of ten minutes */
  // private static final int tenMin = 600000; //not in use anymore
  /** actual player which is on Move */
  private NetworkPlayer actual;
  /** Counter of the player turns during a game */
  private int turn = 0;

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
  }

  /**
   * Method of the Thread class which have the function to organize the game moves of players during
   * a game.
   * 
   * @author hendiehl
   */
  public void run() {
    System.err.println("GAME HANDLER : Started");
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
        this.actual = player;
        player.startMove(this.turn, this.actual.getPlayer().getId()); // here adding the ID.
        this.informOthers();
        this.waitMazimumTime(); // change to approach only by HumanPlayer
        // here work with player move info
        this.checkRunning(); // check if a game should be ended --> any condition
      } else {
        this.actual = player;
      }
      this.turn++; // increase the turn counter
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
        player.informOther(this.turn, this.actual.getPlayer().getId()); // here sending the id +
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
   * Method to interrupt a waiting gameHandler during a move.
   * 
   * @author hendiehl
   */
  public synchronized void endMoveForTime() {
    System.out.println("GAME HANDLER : End move in time");
    // Perhaps here adding the move information because they should be ready
    this.notify();
  }

  /**
   * Method to shut down the GameHandler
   * 
   * @author hendiehl
   */
  public void shutdown() {
    this.gameIsOn = false;
    // Need of braking the inner loop ?
  }
}
