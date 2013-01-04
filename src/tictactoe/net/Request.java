package tictactoe.net;

/**
 * Contains possible requests mapped to integers
 *
 * @see GenericRequestPacket
 * @author Samuel Andersson
 */
public final class Request {

	/** Request the board to be updated */
	public static int BoardUpdate = 1;

	/** Clear and reset the board */
	public static int ClearBoard = 2;

	/** Get the player color and turn */
	public static int GetColorAndTurn = 3;

	/** Get whose turn it is */
	public static int GetTurn = 4;

	/**
	 * Check a value if it's a valid request
	 *
	 * @param request
	 *            The request to be checked
	 * @return True if it is a valid request
	 */
	public static boolean validRequest(int request) {
		return request > 0 && request < 5;
	}
}
