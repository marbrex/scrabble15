package scrabble.network;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.util.Pair;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class AmountMessage extends Message implements Serializable {
  /**
   * AmountMessage is used to provide a transmission of the LetterBag getAmountOfEveryTile method
   * return.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  private ArrayList<Pair<Character, Integer>> amounts;

  /**
   * Constructor which sets the list of remaining letter amounts.
   * 
   * @param type MessageType of the message.
   * @param owner Owner of the message.
   * @param amounts list of letter amounts
   */
  public AmountMessage(MessageType type, Player owner, ArrayList<Pair<Character, Integer>> amounts) {
    super(type, owner);
    this.amounts = amounts;
  }

  /**
   * Getter for the amount list.
   * 
   * @return list of letter amounts.
   * @author hendiehl
   */
  public ArrayList<Pair<Character, Integer>> getAmounts() {
    return amounts;
  }

}
