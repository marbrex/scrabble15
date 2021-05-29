package scrabble.network;

import java.util.ArrayList;
import java.util.Collection;
import scrabble.GameController;
import scrabble.GameLobbyController;
import scrabble.game.LetterBag.Tile;
import scrabble.game.LetterTile;
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

  public void sendEndMessage(String action, int points);

  public void startChatClient(int port);

  public void stopChatClient();

  public void loadFinished();

  public void setGameScreen(GameController gameScreen);

  public int getOwnID();

  public void informLobbyReturn();

  public void setLobbyController(GameLobbyController glc);

  public void sendWordMessage(String word);

  public void sendPassMessage();

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
  
  public void exchangeLetterTiles(ArrayList<Tile> tilesToExchange);
  // others will follow
  public void getAmountOf(char letter);
}
