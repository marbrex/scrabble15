package scrabble.network;

import scrabble.model.GameInformationController;

public class GameHandler extends Thread {
  /** GameInfoController of the game and the lobby, holding the information and logic of a network game */
  private GameInformationController game;
  /** boolean to controll the execution of the thread */
  private boolean gameIsOn;
  /** Int representation of ten minutes*/
  private static final int tenMin = 600000;
  /**
   * Constructor for the GameHandler which setting the corresponding GameInformationController
   * @param game GameInformationController
   */
  public GameHandler(GameInformationController game) {
    this.game = game;
  }
  /**
   * Method of the Thread class which have the function to organize the game moves of players during a game
   */
  public void run() {
    while(gameIsOn) {
      
    }
  }
  /**
   * Method to start the game procedure
   */
  public void startGame() {
    this.gameIsOn = true;
    this.start(); //starting the thread --> run method
  }
  
  /**
   * Method to let the Thread sleep a maximum of ten minutes
   */
  private void sleepMaximumMoveTime() {
    try {
      this.sleep(tenMin); //sleep a maximum amount of time
      //here not in time handling take place
    } catch (InterruptedException e) {
      //Here the in time handling take time 
      e.printStackTrace();
    }
  }
}
