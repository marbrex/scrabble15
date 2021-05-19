package scrabble.network;

public interface NetworkGame {
  public void startMove(int turn, int id); // called when a player is on move and should enable the game
                                   // screen for user input

  public void endMove(); // called when a player ends his move or when forced to end his move by the
                         // server. Should disable the GameField and collect all relevant info of
                         // the move.

  public void sendMoveInformation(); // should send the info of a move to the server, to send them
                                     // to all other players. Is used if a move is forced to end or
                                     // a player end them self --> every time same procedure

  public void getOpponentsInfo(); // will be called by the protocol and give the info of a opponents
                                  // move to the player so it will update the game instance of the
                                  // other player.

  public void endMoveActions(); // will be used by the player to inform the server that he wants to
                                // end his turn --> an endMove() message from the server is a direct
                                // consequence.
  // the endMoveActions method which will be called by an Button should by only
  // clickable when the timer of the player is not below 30sek remaining to
  // counter a deadlock possibility

  public void printChatMessage(String message); // used to print an incoming chat message into the
                                                // chat filed on the GameField

  public void sendChatMessage(String message); // used by a player to send a chat message to the
                                               // other players --> connected with an send Button
}
