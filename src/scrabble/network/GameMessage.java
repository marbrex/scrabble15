package scrabble.network;

import java.util.ArrayList;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class GameMessage extends Message {
  /**
   * GameMessage class for sending the clients a list of players for the game.
   * The Id's are saved separately because in case of a send with players which id was set before
   * the id's are lost during message transmission. There is no logical explanation for data loose but
   * the id's were wrong transmitted by sending them within the player class.
   */
  
  private ArrayList<Player> players;
  private int[] ids;
  
  /**
   * Constructor for sending the player id's independent from the player class
   * @param type MessageType of the message
   * @param owner Owner of the message
   * @param players list of the players in election order
   * @param ids id list of the players in election order
   */
  public GameMessage(MessageType type, Player owner, ArrayList<Player> players, int[] ids) {
    super(type, owner);
    this.players = players;
    this.ids = ids;
  }

  /**
   * Getter of the players list.
   * @return list of players in sequence election order.
   * @author hendiehl
   */
  public ArrayList<Player> getPlayers() {
    return players;
  }

  /**
   * Getter for the id array.
   * @return id of players in sequence election order.
   * @author hendiehl
   */
  public int[] getIds() {
    return ids;
  }
  
}
