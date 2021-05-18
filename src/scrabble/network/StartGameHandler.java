package scrabble.network;

import scrabble.model.GameInformationController;

public class StartGameHandler extends Thread {
  /**
   * StartGameHandler class is used to start the procedure to go from a network lobby into a game
   * screen. Is mainly used to control the election procedure and sending message with a big
   * payload.
   * 
   * @author hendiehl
   */
  private GameInformationController game;

  /**
   * Constructor which sets the GameInformationControlller which is responsible for the overall
   * network control
   * 
   * @param game corresponding GAmeInformationsController of the lobby
   * @author hendiehl
   */
  public StartGameHandler(GameInformationController game) { // Perhaps the this starts before the
                                                            // lobby are update for everyone
    this.game = game;
  }

  /**
   * Run Method of the extended Thread class, to handle the start of an Game
   * 
   * @author hendiehl
   */
  public void run() {
    try {
      System.out.println("START HANDLER : Started");
      this.game.sendFullMessages(); // inform the lobby that no other members will join
      this.game.sendFieldMessage(); // inform the players about the chosen multiplier file
      //this.game.sendDictionaryMessage(); // inform the player about the chosen dictionary
      DictionarySender sender = new DictionarySender(game); //inform the others about the chosen dictionary
      sender.start(); //start the sending
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
