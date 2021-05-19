package scrabble.network;

import java.util.ArrayList;
import scrabble.model.AiPlayer;
import scrabble.model.Player;

public class LobbyAiProtocol implements NetworkPlayer {
  /**
   * AiProtocol is used to fill a network game with ai players if the maximum of players isn't
   * reached by HumanPlayers. Had the main function to interact with the players during a network
   * game by making game turns.
   * 
   * @author hendiehl
   */
  private AiPlayer ai;

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
   * Method to calculate a move by an AiPlayer.
   * 
   * @author hendiehl
   */
  @Override
  public void startMove(int turn, int id) {

  }

  // --------------------------------------------------------------------------
  // LobbyAiProtocol is a filler for missing HumanPlayer so the network Messages are not needed
  @Override
  public void updateLobbyinformation(ArrayList<Player> playersArrayList) {
    // Not in use because of Non-Network communication
  }

  @Override
  public void addSequence(int i) {
    // Not in use because of Non-Network communication
  }

  @Override
  public int getSequencePos() {
    // Not in use because of Non-Network communication
    return 0;
  }

  @Override
  public void sendFullMessage() {
    // Not in use because of Non-Network communication
  }

  @Override
  public void sendStartMessage() {
    // Not in use because of Non-Network communication
  }

  @Override
  public void sendGameMessage(ArrayList<Player> players) {
    // Not in use because of Non-Network communication
  }

  @Override
  public void informOther(int turn, int id) {
    // Not in use because of Non-Network communication
  }

  @Override
  public void sendFieldMessage(String path) {
    // Not in use because of Non-Network communication
  }

  @Override
  public void sendDictionaryMessage(String dictionaryContent) {
    // Not in use because of Non-Network communication
  }
}
