package scrabble.network;

public interface NetworkScreen {
  public void printChatMessage(String message);
  public void sendChatMessage(String message);
  public int getPlayerAmount();
}
