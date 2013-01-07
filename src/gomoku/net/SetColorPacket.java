package gomoku.net;

import gomoku.logic.Board;

/**
 * Set the player color
 * 
 * @author Samuel Andersson
 */
public class SetColorPacket {

    /** The player color */
    private int color;

    /** Empty constructor for Kryonet */
    public SetColorPacket() {
    }

    /**
     * Create a new packet containing player color information.
     * 
     * @see #setColor(int)
     */
    public SetColorPacket(int color) throws IllegalArgumentException {
        setColor(color);
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
     * Set the player color of this packet
     * 
     * @param color
     *            The color being set to the player
     * @throws IllegalArgumentException
     *             Indicates a player color not being {@link Board#BLACKPLAYER}
     *             or {@link Board#WHITEPLAYER}
     */
    public void setColor(int color) throws IllegalArgumentException {
        if (color == Board.BLACKPLAYER || color == Board.WHITEPLAYER) {
            this.color = color;
        } else {
            throw new IllegalArgumentException("Player color cannot be: \""
                    + color + "\".");
        }
    }
}
