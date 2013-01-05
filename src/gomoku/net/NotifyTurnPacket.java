package gomoku.net;

import gomoku.logic.Board;

/**
 * Contains information about the current turn
 *
 * @author Samuel Andersson
 */
public class NotifyTurnPacket {

	/** The player color of the current turnholder */
	private int color;

	/** Empty constructor for Kryonet */
	public NotifyTurnPacket() {
	}

	/**
	 * Create a new packet containing turn information
	 *
	 * @param color
	 *            The color of the player holding the turn
	 * @throws IllegalArgumentException
	 *             Indicates an invalid turncolor
	 */
	public NotifyTurnPacket(int color) throws IllegalArgumentException {
		setColor(color);
	}

	/**
	 * Get the turnholder color
	 *
	 * @return The turnholder color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Set the turnholder color
	 *
	 * @param color
	 *            The color of the turnholder
	 * @throws IllegalArgumentException
	 *             Indicates an invalid turncolor
	 */
	public void setColor(int color) throws IllegalArgumentException {
		if (color == Board.REDPLAYER || color == Board.BLUEPLAYER) {
			this.color = color;
		} else {
			throw new IllegalArgumentException("Turn color cannot be: \""
					+ color + "\"");
		}
	}
}
