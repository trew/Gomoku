package gomoku.net;

import gomoku.logic.Player;

/**
 * Contains information about the current turn
 *
 * @author Samuel Andersson
 */
public class NotifyTurnPacket {

    /** The player ID of the current turnholder */
    private int id;

    /** Empty constructor for Kryonet */
    public NotifyTurnPacket() {
    }

    /**
     * Create a new packet containing turn information
     *
     * @param id
     *            The ID of the player holding the turn
     * @throws IllegalArgumentException
     *             Indicates an invalid turn ID
     */
    public NotifyTurnPacket(int id) throws IllegalArgumentException {
        setID(id);
    }

    /**
     * Get the turnholder ID
     *
     * @return The turnholder ID
     */
    public int getID() {
        return id;
    }

    /**
     * Set the turnholder color
     *
     * @param id
     *            The ID of the turnholder
     * @throws IllegalArgumentException
     *             Indicates an invalid turn id
     */
    public void setID(int id) throws IllegalArgumentException {
        if (id == Player.PLAYERONE || id == Player.PLAYERTWO) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Turn ID cannot be: \""
                    + id + "\"");
        }
    }
}
