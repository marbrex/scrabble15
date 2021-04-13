package scrabble.network;

public class PortsOccupiedException extends Exception {
  /**
   * Exception used when all standard ports of the lobby are occupied or no server found.
   * 
   * @author hendiehl
   */
  /**
   * constructor with an specific exception message
   * 
   * @param msg message string of the exception
   */
  public PortsOccupiedException(String msg) {
    super(msg);
  }
}
