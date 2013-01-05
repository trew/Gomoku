package gomoku.net;

/**
 * Contains possible requests mapped to integers
 *
 * @see GenericRequestPacket
 * @author Samuel Andersson
 */
public final class Request {

	/** Request of initial data needed to start the game on the client */
	public static int InitialData = 1;

	/** Request the board to be updated */
	public static int BoardUpdate = 2;

	/** Clear and reset the board */
	public static int ClearBoard = 3;

	/** Get the player color and turn */
	public static int GetColorAndTurn = 4;

	/** Get whose turn it is */
	public static int GetTurn = 5;

	/**
	 * Check a value if it's a valid request
	 *
	 * @param request
	 *            The request to be checked
	 * @return True if it is a valid request
	 */
	public static boolean validRequest(int request) {
		return request > 0 && request < 6;
	}
}
