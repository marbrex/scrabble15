package scrabble.network;

import scrabble.model.MessageType;
import scrabble.model.Player;

public class FieldMessage extends Message {
  /**
   * Message class to send the file content which will be used to set up the game field when the
   * game starts
   * 
   * @author hendiehl
   */
  private String content;

  /**
   * Constructor to set the string content of an multiplier file chosen by the host
   * 
   * @param type MessageType
   * @param owner owner of the message
   * @param content string content of the file
   * @author hendiehl
   */
  public FieldMessage(MessageType type, Player owner, String content) {
    super(type, owner);
    this.content = content;
  }

  /**
   * Method to get the content value of the Message
   * 
   * @return content of multiplier file chosen by host
   * @author hendiehl
   */
  public String getContent() {
    return content;
  }

}
