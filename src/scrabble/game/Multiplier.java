package scrabble.game;

/**
 * scrabble.game.Multiplier enum represents Multipliers (DW,DL,TW,TL,NO), which can be assigned to a
 * Slot.
 *
 * @author ekasmamy
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
   * Default Constructor.
   *
   * @param scope Scope ("WORD"/"LETTER"/"").
   * @param value Value (1/2/3).
   * @author ekasmamy
   */
  Multiplier(String scope, int value) {
    this.scope = scope;
    this.value = value;
  }

  /**
   * Get the scope.
   *
   * @return Scope (string).
   * @author ekasmamy
   */
  public String getScope() {
    return scope;
  }

  /**
   * Get the value.
   *
   * @return value.
   * @author ekasmamy
   */
  public int getValue() {
    return value;
  }

  /**
   * Get the code name of the multiplier.
   *
   * @return code name.
   * @author ekasmamy
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