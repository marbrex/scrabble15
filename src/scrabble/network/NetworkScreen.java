package scrabble.network;

public interface NetworkScreen {
  public void printChatMessage(String message);
  public void sendChatMessage(String message);
  public int getPlayerAmount();
  public void sendEndMessage();
  public void startChatClient(int port);
  public void stopChatClient();
}
