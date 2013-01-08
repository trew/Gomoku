package gomoku.net;

/**
 * A packet that the client sends to the server upon entering the game. The
 * server will respond with a {@link InitialServerDataPacket}. the game board,
 * the color for the player and the turn. Upon receiving this packet, the client
 * knows it has all the data it needs to start displaying the board etc.
 * 
 * @author Samuel Andersson
 */
public class InitialClientDataPacket {

    /** The player name */
    private String name;

    /** Empty constructor for Kryonet */
    public InitialClientDataPacket() {
    }

    /**
     * Create a new initial data packet
     * 
     * @param name
     *            The player name
     */
    public InitialClientDataPacket(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
