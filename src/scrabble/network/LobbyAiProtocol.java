package scrabble.network;

import java.util.ArrayList;
import scrabble.model.AiPlayer;
import scrabble.model.Player;

public class LobbyAiProtocol implements NetworkPlayer {
  private AiPlayer ai;

  public LobbyAiProtocol() {
    this.ai = new AiPlayer();
  }

  @Override
  public Player getPlayer() {
    return this.ai;
  }

  @Override
  public void startMove() {

  }

  @Override
  public void endMove() {

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
}
