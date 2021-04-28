package scrabble.model;

public enum MessageType {
  /**
   * Enum of different types of network messages, used to identify the messages and react
   * specifically.
   * 
   * @author hendiehl
   */
  JOIN, // Client wants to join game
  ACEPTED, // Player are added to a game
  REJECTED, // Lobby is full;
  INFORMATION, // Lobby information
  SHUTDOWN, // server shutdown
  LOBBY, // information about the lobby
  KICK, // kicking a player from the Lobby
  FULL, // lobby maximum is reached
  START, //the game is about to start and the sequence should be transmitted
  GAME, //the game starts 
}
