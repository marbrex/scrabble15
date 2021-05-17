package scrabble.network;

import java.util.ArrayList;
import com.google.common.collect.Multiset;
import scrabble.game.LetterBag.Tile;
import scrabble.model.Player;

public interface NetworkScreen {

  /**
   * Interface which will be used to specify methods for a protocols which have access to interact
   * with a screen during a network game.
   * 
   * @author hendiehl
   */



  public void printChatMessage(String message);

  public void sendChatMessage(String message);

  public int getPlayerAmount();

  public void sendEndMessage();

  public void startChatClient(int port);

  public void stopChatClient();

  public ArrayList<Player> getPlayerList(); // need to send the new list after election
  // Methods of the LetterBag Class for Network use

  public void grabRandomTile();

  public void getValueOf(char letter);

  public void getRemainingVowels();

  public void getRemainingConsonants();

  public void getRemainingBlanks();

  public void grabRandomTiles(int count);

  public void getRemainingTiles();

  public void getAmount();
  // others will follow
}
