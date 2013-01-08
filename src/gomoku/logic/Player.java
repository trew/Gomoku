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
     *            The player color. {@link Board#BLACKPLAYER},
     *            {@link Board#WHITEPLAYER} or {@link Board#NOPLAYER}
     */
    public Player(String name, int color) {
        this.name = name;
        if (color == Board.BLACKPLAYER || color == Board.WHITEPLAYER
                || color == Board.NOPLAYER)
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
     * Sets the player color
     * 
     * @param color
     *            the player color
     * @throws IllegalArgumentException
     *             if the color is not {@link Board#BLACKPLAYER},
     *             {@link Board#WHITEPLAYER} or {@link Board#NOPLAYER}.
     */
    public void setColor(int color) throws IllegalArgumentException {
        if (color != Board.BLACKPLAYER && color != Board.WHITEPLAYER
                && color != Board.NOPLAYER) {
            throw new IllegalArgumentException("Player cannot be " + color);
        }
        this.color = color;
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
