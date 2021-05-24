package scrabble.network;

import java.io.Serializable;
import scrabble.model.LetterBagType;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class LetterBagMessage extends Message implements Serializable {
  /**
   * Network message to perform a LetterBag method via the network. Send by a client to the server
   * 
   * @author hendiehl
   */
  private int count;
  private char letter;
  private LetterBagType type2;

  /**
   * Constructor for the LetterBagMessage
   * 
   * @param type Type of the NetworkMessage
   * @param owner owner of the message
   * @param count count integer for the grabRandomTile method : can be 0 if not needed
   * @param letter letter char for the getValueOf method can be 0 if not needed
   */
  public LetterBagMessage(MessageType type, Player owner, int count, char letter,
      LetterBagType type2) {
    super(type, owner);
    this.count = count;
    this.letter = letter;
    this.type2 = type2;
  }

  /**
   * Getter of the count integer used if it is used for the grabRandomTile method
   * 
   * @return count integer
   * @author hendiehl
   */
  public int getCount() {
    return count;
  }

  /**
   * getter for the letter char used if it is used for the getValueOf method
   * 
   * @return letter char
   * @author hendiehl
   */
  public char getLetter() {
    return letter;
  }

  /**
   * Method to get the LetterBagMessageType to identify which method should be performed
   * 
   * @return LetterBagMessagetype
   * @author hendiehl
   */
  public LetterBagType getType2() {
    return type2;
  }

}
