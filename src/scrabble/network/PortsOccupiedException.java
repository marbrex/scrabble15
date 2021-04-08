package scrabble.network;

public class PortsOccupiedException extends Exception{
	/**
	 * Exception used when all standart ports of the lobby are occupied or no server found.
	 * @author Hendrik Diehl
	 * @param msg
	 */
	public PortsOccupiedException(String msg) {
		super(msg);
	}
}
