package scrabble.game;

public enum Multiplier {
  DL("LETTER", 2),
  DW("WORD", 2),
  TL("LETTER", 3),
  TW("WORD", 3),
  NO("", 1);

  private final String scope;
  private final int value;

  Multiplier(String scope, int value) {
    this.scope = scope;
    this.value = value;
  }

  public String getScope() {
    return scope;
  }

  public int getValue() {
    return value;
  }

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