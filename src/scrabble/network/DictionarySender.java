package scrabble.network;

import scrabble.model.GameInformationController;

public class DictionarySender extends Thread {
  /**
   * Class which has the function to send a chosen dictionary to all clients with the corresponding
   * stream. Is set in a own specific thread because of the possible size of an dictionary String.
   * In this way the StartHandler isn't blocked for the start of the election wait procedure. It is
   * wanted that the Message is send during the one minute of election time.
   * 
   * @author hendiehl
   */
  GameInformationController game;

  /**
   * Constructor which sets the GameInformationController in purpose to get the a chosen dictionary
   * and start the send procedure for each client
   * 
   * @param game corresponding GameInformationController
   * @author hendiehl
   */
  public DictionarySender(GameInformationController game) {
    this.game = game;
  }

  /**
   * Run method of the thread class for independent call hierarchy of the StartGameHandler
   * 
   * @author hendiehl
   */
  public void run() {
    game.sendDictionaryMessage();
  }
}
