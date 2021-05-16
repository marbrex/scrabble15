package scrabble.network;

import scrabble.model.GameInformationController;

public class StartGameHandler extends Thread {
  private GameInformationController game;

  public StartGameHandler(GameInformationController game) { // Perhaps the this starts before the
                                                            // lobby are update for everyone
    this.game = game;
  }

  /**
   * Run Method of the extended Thread class, to handle the start of an Game
   */
  public void run() {
    try {
      System.out.println("START HANDLER : Started");
      this.game.sendFullMessages(); // inform the lobby that no other members will join
      this.game.sendFieldMessage();
      StartGameHandler.sleep(60000); // sleeping the election time.
      System.out.println("START HANDLER : Time finished");
      this.game.sendStartMessage(); // inform the players the game will start --> sequence Messages
                                    // expected
      // after that the lobby protocol take the last step --> perhaps change to an other approach in
      // the future();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // invoke the lobby
  }
}
