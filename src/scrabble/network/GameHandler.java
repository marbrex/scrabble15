package scrabble.network;

import java.util.ArrayList;
import scrabble.model.GameInformationController;

public class GameHandler extends Thread {
  /**
   * GameInfoController of the game and the lobby, holding the information and logic of a network
   * game
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

  // should i control a specific notify call ?
  /**
   * Constructor for the GameHandler which setting the corresponding GameInformationController
   * 
   * @param game GameInformationController
   */
  public GameHandler(GameInformationController game, ArrayList<NetworkPlayer> players) {
    this.game = game;
    this.players = players;
  }

  /**
   * Method of the Thread class which have the function to organize the game moves of players during
   * a game
   */
  public void run() {
    System.err.println("GAME HANDLER : Started");
    while (gameIsOn) {
      System.out.println("GAME HANDLER : Make complete turn");
      this.makeATurn(); // Perhaps wrapping it in a synchronized method ?
    }
  }

  /**
   * Method to start the game procedure
   */
  public void startGame() {
    this.gameIsOn = true;
    this.start(); // starting the thread --> run method
  }

  /**
   * Method representing a full play turn through all players
   */
  private void makeATurn() {
    for (NetworkPlayer player : this.players) {
      if (!this.gameIsOn) {
        break;
      }
      this.checkRunning();
      System.out.println("GAME HANDLER : Player turn : " + player.getPlayer().getName());
      if (player instanceof LobbyServerProtocol || player instanceof LobbyHostProtocol) {
        this.actual = player;
        player.startMove();
        this.informOthers(player);
        this.waitMazimumTime(); // change to approach only by HumanPlayer
        // Here is the action performed after the player informed the server

      } else {
        // AI calculating
      }
    }

  }

  /**
   * Method to inform other players that a player is on the move
   * 
   * @param player Player on the move
   * @author hendiehl
   */
  private void informOthers(NetworkPlayer player) {
    for (NetworkPlayer others : this.players) {
      if (!others.equals(player)) { // inform every other player
        player.informOther(player.getPlayer());
      }
    }
  }

  /**
   * method to break the loop if the game should stop
   */
  private void checkRunning() {
    // check specific game ending tasks;
  }

  /**
   * Method to get the Information of a player turn
   */
  private void getMoveInfo() {
    // TODO Auto-generated method stub

  }

  /**
   * Method to waiting the maximum of 10min for a player turn. Interrupted by a NetworkPlayer to
   * inform the server that he finished his move
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
   * Method to interrupt a waiting gameHandler during a move
   */
  public synchronized void endMoveForTime() {
    System.out.println("GAME HANDLER : End move in time");
    // Perhaps here adding the move information because they should be ready
    this.notify();
  }

  public void shutdown() {
    this.gameIsOn = false;
    // Need of braking the inner loop ?
  }
}
