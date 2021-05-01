package scrabble.network;

import java.util.ArrayList;
import scrabble.model.GameInformationController;

public class GameHandler extends Thread {
  /**
   * GameInfoController of the game and the lobby, holding the information and logic of a network
   * game
   */
  private GameInformationController game;
  /** boolean to controll the execution of the thread */
  private boolean gameIsOn;
  /** working on a reference copy because of a deadlock */
  private ArrayList<NetworkPlayer> players;

  /**
   * Constructor for the GameHandler which setting the corresponding GameInformationController
   * 
   * @param game GameInformationController
   */
  public GameHandler(GameInformationController game, ArrayList<NetworkPlayer> players) { // GameMessages
                                                                                         // are send
                                                                                         // before,
                                                                                         // but will
                                                                                         // the
                                                                                         // Platform.runLater
                                                                                         // be
                                                                                         // faster
                                                                                         // -->
                                                                                         // implement
                                                                                         // callback
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
      System.out.println("GAME HANDLER : Player turn");
      player.startMove();
      this.waitMazimumTime(); // change to approach only by HumanPlayer
      player.endMove(); // doing it all the Time and only inform the Thread or ending itself and not
                        // send the Message ?
    }

  }

  /**
   * Method to waiting the maximum of 10min for a player turn. Interrupted by a NetworkPlayer to
   * inform the server that he finished his move
   */
  private synchronized void waitMazimumTime() { // here a illegal monitor state exception occurs
    System.out.println("GAME HANDLER : Wait move time");
    try {
      this.wait(60000); // wait the maximum time for a player move --> only testing replace with
                        // variable
    } catch (InterruptedException e) {
      // Doing something change to a other approach different message for endTurn and forceEndTurn
    }
  }

  /**
   * Method to interrupt a waiting gameHandler during a move
   */
  public synchronized void endMoveForTime() {
    System.out.println("GAME HANDLER : End move in time");
    this.notify();
  }
  
  public void shutdown() {
    this.gameIsOn = false;
    //Need of braking the inner loop ?
  }
}
