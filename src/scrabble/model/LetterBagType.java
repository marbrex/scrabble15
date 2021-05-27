package scrabble.model;

import java.io.Serializable;

public enum LetterBagType implements Serializable {
  /**
   * Enum to identify LetterBag methods via network server call.
   * 
   * @author hendiehl
   */
  GRV, // getRemainingVowels
  GRC, // getRemainingConsonants
  GRB, // getRemainingBlanks
  GRTS, // grabRandomTiles
  GRET, // getRemainingTiles
  GA, // getAmount
  GV, // getValueOf
  GRT, // grabRandomTile
}
