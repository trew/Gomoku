package tictactoe.net;

/**
 * A small packet containing a specific request, but doesn't require much data,
 * so having a specific class for it would overcomplicate it.
 *
 * @author Samuel Andersson
 */
public class GenericRequestPacket {

	/**
	 * The request
	 *
	 * @see Request
	 */
	private int request;

	/** Empty constructor for Kryonet */
	public GenericRequestPacket() {
	}

	/**
	 * Create a new generic request
	 *
	 * @param req
	 *            The request ID
	 * @throws IllegalArgumentException
	 *             Indicates the request ID was faulty
	 */
	public GenericRequestPacket(int req) throws IllegalArgumentException {
		setRequest(req);
	}

	/**
	 * Get the request ID
	 * @return The request ID
	 * @see Request
	 */
	public int getRequest() {
		return request;
	}

	/**
	 * Set the request ID
	 *
	 * @param req
	 *            The request ID
	 * @throws IllegalArgumentException
	 *             Indicates the request ID was faulty
	 */
	public void setRequest(int req) throws IllegalArgumentException {
		if (!Request.validRequest(req)) {
			throw new IllegalArgumentException("Bad request: \"" + req + "\"");
		}
	}
}
