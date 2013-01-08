package gomoku.net;

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
     * @param playerColor
     *            The player color of the player placing the piece
     */
    public PlacePiecePacket(int x, int y, int playerColor) {
        this.x = x;
        this.y = y;
        this.playerColor = playerColor;
    }
}
