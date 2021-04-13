package scrabble.game;

/**
 * <h1>scrabble.game.Multiplier</h1>
 *
 * <p>This class represents Multipliers (DW,DL,TW,TL,NO), which can be assigned to a Slot</p>
 *
 * @author Eldar Kasmamytov
 */
public enum Multiplier {
  DL("LETTER", 2),
  DW("WORD", 2),
  TL("LETTER", 3),
  TW("WORD", 3),
  NO("", 1);

  private final String scope;
  private final int value;

  /**
   * Default Constructor
   *
   * @param scope Scope ("WORD"/"LETTER"/"")
   * @param value Value (1/2/3)
   */
  Multiplier(String scope, int value) {
    this.scope = scope;
    this.value = value;
  }

  /**
   * Get the scope
   *
   * @return Scope (string)
   */
  public String getScope() {
    return scope;
  }

  /**
   * Get the value
   *
   * @return value
   */
  public int getValue() {
    return value;
  }

  /**
   * Get the code name of the multiplier
   *
   * @return code name
   */
  public String getAsString() {
    String result;
    switch (this) {
      case DL:
        result = "DL";
        break;

      case DW:
        result = "DW";
        break;

      case TL:
        result = "TL";
        break;

      case TW:
        result = "TW";
        break;

      default:
        result = "NO";
        break;
    }
    return result;
  }
}