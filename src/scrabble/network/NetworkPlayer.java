package scrabble.network;

import java.util.ArrayList;

import scrabble.model.Player;

public interface NetworkPlayer {
  /**
   * Interface for a network player. Can be a lobby or game client or a game/lobby host. Provide
   * methods which all types of network players should have.
   * 
   * @author hendiehl
   */

  public Player getPlayer();

  public void updateLobbyinformation(ArrayList<Player> playersArrayList);

  public void addSequence(int i);

  public int getSequencePos();

  public void sendFullMessage();

  public void sendStartMessage();

  public void sendGameMessage(ArrayList<Player> players);

  public void startMove();

  public void informOther(int i);

  public void sendFieldMessage(String path);

}
