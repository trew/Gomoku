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
     * @see {@link Board#BLACKPLAYER}, {@link Board#WHITEPLAYER}
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
     *            The player color. {@link Board#BLACKPLAYER} or
     *            {@link Board#WHITEPLAYER}
     * @throws IllegalArgumentException
     *             Indicates that the provided player color was wrong
     */
    public Player(String name, int color) throws IllegalArgumentException {
        this.name = name;
        if (color == Board.BLACKPLAYER || color == Board.WHITEPLAYER)
            this.color = color;
        else
            throw new IllegalArgumentException("Color cannot be this color: \""
                    + color + "\".");
    }

    /**
     * Returns the player color
     * 
     * @return the player color
     */
    public int getColor() {
        return color;
    }

    /**
     * Returns a string representing the color of the player
     * 
     * @return a string representing the color of the player
     */
    public String getColorName() {
        return color == Board.BLACKPLAYER ? "Black" : "White";
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
