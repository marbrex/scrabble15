package scrabble.network;

import java.io.Serializable;
import java.util.Collection;
import scrabble.game.LetterTile;
import scrabble.model.MessageType;
import scrabble.model.Player;

public class ExchangeMessage extends Message implements Serializable {
  /**
   * EchnageMessage class is used to exchange tiles in a network game.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;
  private Collection<LetterTile> tile;

  /**
   * Constructor which sets the tiles of a player which wants to exchange his tiles.
   * 
   * @param type
   * @param owner
   * @param tilesToExchange
   */
  public ExchangeMessage(MessageType type, Player owner, Collection<LetterTile> tilesToExchange) {
    super(type, owner);
    this.tile = tilesToExchange;
  }

  /**
   * Getter of the messages tiles which should be exchanged by server.
   * 
   * @return LetterTiles of player
   * @author hendiehl
   */
  public Collection<LetterTile> getTile() {
    return tile;
  }


}
