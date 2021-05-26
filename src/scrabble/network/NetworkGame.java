package scrabble.network;

public interface NetworkGame {
  public void startMove(int turn, int id); // called when a player is on move and should enable the
                                           // game
  // screen for user input

  public void endMove(); // called when a player ends his move or when forced to end his move by the
                         // server. Should disable the GameField and collect all relevant info of
                         // the move.

  public void getOpponentsInfo(String action, int points, int id); // will be called by the protocol
                                                                   // and give the info of a
                                                                   // opponents
  // move to the player so it will update the game instance of the
  // other player.


  public void printChatMessage(String message); // used to print an incoming chat message into the
                                                // chat filed on the GameField

  public void sendChatMessage(String message); // used by a player to send a chat message to the
                                               // other players --> connected with an send Button

  public void informAboutTileAmount(int size); // inform about the amount tiles left in the
                                               // LetterBag
}
