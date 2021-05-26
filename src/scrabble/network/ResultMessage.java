package scrabble.network;

import java.util.ArrayList;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class ResultMessage extends Message {
  /**
   * ResultMessage which will be used to inform players about a finished network game. Will also
   * lead to an screen change to a lobby screen.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  private ArrayList<Player> players; // Protocol representation and order
  private int[] points;
  private ArrayList<Player> ordered; // After game screen representation and order.

  /**
   * Constructor to set a list of players of an finished network game and the points of this players
   * in the same order.
   * 
   * @param type MessageType of the message.
   * @param owner Owner of the message.
   * @param players members of the finished game for protocol internal use.
   * @param points Points gained by players.
   * @param players for the after game screen.
   * @author hendiehl
   */
  public ResultMessage(MessageType type, Player owner, ArrayList<Player> players, int[] points,
      ArrayList<Player> ordered) {
    super(type, owner);
    this.players = players;
    this.points = points;
    this.ordered = ordered;
  }

  /**
   * Getter of the players which participated in a network game. Is used for the intern protocol
   * list;
   * 
   * @return players ArrayList of the players.
   * @author hendiehl
   */
  public ArrayList<Player> getPlayers() {
    return players;
  }

  /**
   * Getter of an points array gained by players during a game.
   * 
   * @return points Points gained in a game.
   * @author hendiehl
   */
  public int[] getPoints() {
    return points;
  }

  /**
   * Getter for the list of human players which is ordered by its points. Is used for the after game
   * screen.
   * 
   * @return humans HumanPlayers ordered by points.
   */
  public ArrayList<Player> getOrdered() {
    return ordered;
  }


}
