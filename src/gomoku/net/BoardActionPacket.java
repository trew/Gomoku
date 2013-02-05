package gomoku.net;

import gomoku.logic.Board.BoardAction;

/**
 * A packet requesting to place a piece on a certain location on the board
 *
 * @author Samuel Andersson
 */
public class BoardActionPacket {

    public BoardAction action;

    /** Empty constructor for Kryonet */
    public BoardActionPacket() {
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
    public BoardActionPacket(BoardAction action) {
        this.action = action;
    }
}
