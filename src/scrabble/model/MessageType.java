package scrabble.model;

public enum MessageType {
  /**
   * Enum of different types of network messages, used to identify the messages and react
   * specifically.
   * 
   * @author hendiehl
   */
  JOIN, // Client wants to join game.
  ACEPTED, // Player are added to a game.
  REJECTED, // Lobby is full.
  INFORMATION, // Lobby information.
  SHUTDOWN, // server shutdown.
  LOBBY, // information about the lobby.
  KICK, // kicking a player from the lobby.
  FULL, // lobby maximum is reached.
  START, // the game is about to start and the sequence should be transmitted.
  GAME, // the game starts.
  MOVE, // inform a player that he is on move.
  END, // inform player or Server that the move has been ended.
  BAG, // messages for LetterBag operations via network.
  OTHER, // message to perform a player about the move of somebody else.
  FIELD, // message to set the game field multiplier.
  DICT, // message to set a dictionary file content.
  LOAD, // message to inform about successful game Field Loading.
  ACTION, // message to inform about the move action of somebody else.
  RETURN, // message to return to the lobby from the gameScreen.
  DB, // message to inform a player about his game results.
  SIZE, // message to inform game members about the letter bag size.
}
