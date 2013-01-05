package gomoku.net;

import gomoku.logic.Player;

/**
 * A packet requesting to place a piece on a certain location on the board
 *
 * @author Samuel Andersson
 */
public class PlacePiecePacket {

	/** The x location of the piece */
	public int x;

	/** The y location of the piece */
	public int y;

	/** The player color of the player placing the piece */
	public int playerColor;

	/** Empty constructor for Kryonet */
	public PlacePiecePacket() {
	}

	/**
	 * Create a new packet requesting piece placement
	 *
	 * @param x
	 *            The x location of the piece
	 * @param y
	 *            The y location of the piece
	 * @param player
	 *            The player color of the player placing the piece
	 * @throws IllegalArgumentException
	 *             Indicates that the player argument is null
	 */
	public PlacePiecePacket(int x, int y, Player player)
			throws IllegalArgumentException {
		this.x = x;
		this.y = y;
		if (player == null)
			throw new IllegalArgumentException("Player cannot be null");
		this.playerColor = player.getColor();
	}
}
