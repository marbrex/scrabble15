package scrabble.network;

import java.util.ArrayList;
import scrabble.model.GameInformationController;
import scrabble.model.Player;

public interface NetworkPlayer {
  /**
   * Interface for a network player. Can be the server protocol for a corresponding client protocol,
   * a host protocol or a ai protocol. Provide methods which all types of network players should
   * have.
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

  public void startMove(int turn, int id);

  public void informOther(int turn, int id);

  public void sendFieldMessage(String path);

  public void sendDictionaryMessage(String dictionaryContent);

  public void sendActionMessage(String action, int points, int id);

  public void resetGameInfoCon(GameInformationController game);

  public void sendDBMessage(boolean won);

  public void sendResultMessage(ArrayList<Player> players, int[] points, ArrayList<Player> ordered);

  public void sendBagSize(int size);

  public void sendDeleteMessage(int id);

  public void sendPrepMessageChange();

}
