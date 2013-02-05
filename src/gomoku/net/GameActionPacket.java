package gomoku.net;

import gomoku.logic.GomokuGame.GameAction;

/**
 * A packet requesting to place a piece on a certain location on the board
 *
 * @author Samuel Andersson
 */
public class GameActionPacket {

    public GameAction action;

    /** Empty constructor for Kryonet */
    @SuppressWarnings("unused")
    private GameActionPacket() {
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
    public GameActionPacket(GameAction action) {
        this.action = action;
    }
}
