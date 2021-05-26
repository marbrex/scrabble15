package scrabble.network;

public class PortsOccupiedException extends Exception {
  /**
   * Exception used when all standard ports of the lobby are occupied or no server found.
   * 
   * @author hendiehl
   */

  private static final long serialVersionUID = 1L;

  /**
   * Constructor with an specific exception message.
   * 
   * @param msg message string of the exception.
   * @author hendiehl
   */
  public PortsOccupiedException(String msg) {
    super(msg);
  }
}
