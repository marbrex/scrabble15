package scrabble.network;

import java.util.ArrayList;

import javafx.application.Platform;
import scrabble.game.LetterBag;
import scrabble.model.AiPlayer;
import scrabble.model.GameInformationController;
import scrabble.model.Player;

public class LobbyAiProtocol implements NetworkPlayer {
  /**
   * AiProtocol is used to fill a network game with ai players if the maximum of players isn't
   * reached by HumanPlayers. Has the main function to interact with the players during a network
   * game by making game turns.
   * 
   * @author hendiehl
   */
  private AiPlayer ai;
  private int pos;

  /**
   * Constructor which initialize the AiPlayer which is responsible for game action.
   * 
   * @author hendiehl
   */
  public LobbyAiProtocol() {
    this.ai = new AiPlayer();
  }

  /**
   * Getter method of the AiPlayer.
   * 
   * @author hendiehl
   */
  @Override
  public Player getPlayer() {
    return this.ai;
  }

  /**
   * Special method to calculate a move by an AiPlayer. A AiPLayer needs a game field instance to
   * work on the grid class. The normal move parameters turn and id are not needed here.
   * 
   * @param field game field of the host
   * @author hendiehl
   */
  public synchronized String aiMove() {
    System.out.println("AI PROTOCOL : Calculate move");
    // this.ai.makeTurn(); // making the turn
    this.ai.giveLettersToAiPlayer(LetterBag.getInstance());
    Platform.runLater(() -> {
      this.ai.makeTurn(); // making the turn
      this.ai.getController().grid.verifyWordsValidity();
    });
    try {
      this.wait(1000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return this.ai.createJsonString();
  }

  /**
   * Method to set the position of an AiProtocol. Is exactly once called by the
   * GameInformationsController with MAX.VALUE, to ensure that AiPlayers always set at the back of
   * the list.
   * 
   * @param i Integer.MAX_VALUE set by the GameInfoController.
   * @author hendiehl
   */
  @Override
  public void addSequence(int i) {
    this.pos += i;
  }

  /**
   * Method to return the position variable for comparing NetworkPlayers during sorting.
   * 
   * @return pos variable.
   * @author hendiehl
   */
  @Override
  public int getSequencePos() {
    return this.pos;
  }

  // --------------------------------------------------------------------------
  // LobbyAiProtocol is a filler for missing HumanPlayer so the network Messages are not needed.
  @Override
  public void updateLobbyinformation(ArrayList<Player> playersArrayList) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendFullMessage() {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendStartMessage() {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendGameMessage(ArrayList<Player> players) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void informOther(int turn, int id) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendFieldMessage(String path) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendDictionaryMessage(String dictionaryContent) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void startMove(int turn, int id) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendActionMessage(String action, int points, int id) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void resetGameInfoCon(GameInformationController game) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendDBMessage(boolean won) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendResultMessage(ArrayList<Player> players, int[] points,
      ArrayList<Player> ordered) {
    // Not in use because of Non-Network communication.
  }

  @Override
  public void sendBagSize(int size) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendDeleteMessage(int id) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendPrepMessageChange() {
    // TODO Auto-generated method stub

  }
}
