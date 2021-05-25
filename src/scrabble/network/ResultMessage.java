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
  private ArrayList<Player> players;
  private int[] points;

  /**
   * Constructor to set a list of players of an finished network game and the points of this players
   * in the same order.
   * 
   * @param type MessageType of the message.
   * @param owner Owner of the message.
   * @param players Members of the finished game.
   * @param points Points gained by players.
   * @author hendiehl
   */
  public ResultMessage(MessageType type, Player owner, ArrayList<Player> players, int[] points) {
    super(type, owner);
    this.players = players;
    this.points = points;
  }

  /**
   * Getter of the players which participated in a network game.
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


}
