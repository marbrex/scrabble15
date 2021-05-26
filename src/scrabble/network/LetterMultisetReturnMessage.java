package scrabble.network;

import java.io.Serializable;
import com.google.common.collect.Multiset;
import scrabble.game.LetterBag.Tile;
import scrabble.model.LetterBagType;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class LetterMultisetReturnMessage extends Message implements Serializable {
  /**
   * Network message for returning LetterBag operations in a network game Send by Server to client.
   * Every Method will send with this Message and will be recognized through Enum; Used for :
   * getRemainingVowels = GRV getRemainingConsonants = GRC getRemainingBlanks = GRB grabRandomTiles
   * = GRTS getRemainingTiles = GRET getAmount() = GA getValueOf = GV grabRandomTile = GRT
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  private Multiset<Tile> tiles;
  private int answer;
  private Tile tile;
  private LetterBagType type2;

  /**
   * Constructor for the return message. Will be used by all LetterBag network operations.
   * 
   * @param type Type of the network message.
   * @param owner Owner of the message.
   * @param tiles Multiset of Tiles : can be null if not needed.
   * @param count int value of the amount and value operation : can be 0 if not needed.
   * @param tile tile of the grabRandom method : can be null if not needed.
   * @author hendiehl
   */
  public LetterMultisetReturnMessage(MessageType type, Player owner, Multiset<Tile> tiles,
      int answer, Tile tile, LetterBagType type2) {
    super(type, owner);
    this.tiles = tiles;
    this.answer = answer;
    this.tile = tile;
    this.type2 = type2;
  }

  /**
   * Getter of the tile multiset used for : getRemainingVowels getRemainingConsonants
   * getRemainingBlanks grabRandomTiles getRemainingTiles.
   * 
   * @return multiset of tiles.
   * @author hendiehl
   */
  public Multiset<Tile> getTiles() {
    return tiles;
  }

  /**
   * Getter of the int answer used for : getAmount() getValueOf.
   * 
   * @return int answer.
   * @author hendiehl
   */
  public int getAnswer() {
    return answer;
  }

  /**
   * Getter of the tile used for grabRandomTile method.
   * 
   * @return random tile of Letter Bag.
   * @author hendiehl
   */
  public Tile getTile() {
    return tile;
  }

  /**
   * Method to get the LetterBagMessageType to identify which method should be performed.
   * 
   * @return LetterBagMessagetype.
   * @author hendiehl
   */
  public LetterBagType getType2() {
    return type2;
  }
}
