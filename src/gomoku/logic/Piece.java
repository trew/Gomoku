package gomoku.logic;

/**
 * A Gomoku piece on the board
 *
 * @author Samuel Andersson
 *
 */
public class Piece {

	/** The color of the piece */
	private int playerColor;

	/** The x location on the board */
	private int x;

	/** The y location on the board */
	private int y;

	public Piece() {}
	/**
	 * Create a new piece
	 *
	 * @param x
	 *            The x location on the board
	 * @param y
	 *            The y location on the board
	 * @param playerColor
	 *            The color of the player
	 */
	public Piece(int x, int y, int playerColor) {
		this.setPlayerColor(playerColor);
		this.setX(x);
		this.setY(y);
	}

	/**
	 * Get the player color of this piece
	 *
	 * @return The player color of this piece
	 */
	public int getPlayerColor() {
		return playerColor;
	}

	/**
	 * Set the player color of this piece
	 *
	 * @param playerColor
	 *            The new color
	 */
	public void setPlayerColor(int playerColor) {
		this.playerColor = playerColor;
	}

	/**
	 * Get the x location on the board
	 *
	 * @return The x location on the board
	 */
	public int getX() {
		return x;
	}

	/**
	 * Set the x location on the board
	 *
	 * @param x
	 *            The new x location
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Get the y location on the board
	 *
	 * @return The y location on the board
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the y location on the board
	 *
	 * @param y
	 *            The new y location
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Set a new location on the board
	 *
	 * @param x
	 *            The new x location
	 * @param y
	 *            The new y location
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
