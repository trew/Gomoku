package gomoku.logic;

/**
 * Represents a player in a Gomoku game
 *
 * @author Samuel Andersson
 */
public class Player {

	/**
	 * The player color
	 *
	 * @see {@link Board#REDPLAYER}, {@link Board#BLUEPLAYER}
	 */
	private int color;

	/** The player name */
	private String name;

	/** Empty constructor for Kryonet's pleasure */
	public Player() {
	}

	/**
	 * Create a new player
	 *
	 * @param name
	 *            The player name
	 * @param color
	 *            The player color. {@link Board#REDPLAYER} or
	 *            {@link Board#BLUEPLAYER}
	 * @throws IllegalArgumentException
	 *             Indicates that the provided player color was wrong
	 */
	public Player(String name, int color) throws IllegalArgumentException {
		this.name = name;
		if (color == Board.REDPLAYER || color == Board.BLUEPLAYER)
			this.color = color;
		else
			throw new IllegalArgumentException("Color cannot be this color: \""
					+ color + "\".");
	}

	/**
	 * Get the player color
	 *
	 * @return The player color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Get the player name
	 *
	 * @return The player name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the player name
	 *
	 * @param name
	 *            The new player name
	 * @return The old name
	 */
	public String setName(String name) {
		String old = this.name;
		this.name = name;
		return old;
	}

}
