package gomoku.logic;

/**
 * Represents a player in a Gomoku game
 *
 * @author Samuel Andersson
 */
public class Player {

    public static final int NOPLAYER = 0;
    public static final int PLAYERONE = 1;
    public static final int PLAYERTWO = 2;

    /**
     * 0 = Spectator, or no player 1 = Player one 2 = Player two
     */
    private int playerID;

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
     * @param id
     *            The player ID. {@link Player#NOPLAYER},
     *            {@link Player#PLAYERONE} or {@link Player#PLAYERTWO}
     */
    public Player(String name, int id) {
        this.name = name;
        setID(id);
        setColor(Board.NOPLAYER);
    }

    public int getID() {
        return playerID;
    }

    public void setID(int id) {
        if (id == 0 || id == 1 || id == 2) {
            playerID = id;
        } else {
            throw new IllegalArgumentException("PlayerID set to unknown value "
                    + id);
        }
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
        if (color == Board.BLACKPLAYER)
            return "Black";
        else if (color == Board.WHITEPLAYER) {
            return "White";
        }
        return "None";
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
