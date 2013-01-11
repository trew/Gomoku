package gomoku.net;

import gomoku.logic.Board;

/**
 * A packet containing a board data.
 *
 * @author Samuel Andersson
 */
public class BoardPacket {

    /** The board data */
    private Board board;

    /** Empty constructor for Kryonet */
    public BoardPacket() {
    }

    /**
     * Create a new board packet from a {@link Board}
     *
     * @param board
     *            The board to get data from
     */
    public BoardPacket(Board board) {
        this.board = board;
    }

    /**
     * Returns the board from this packet
     *
     * @return the board from this packet
     */
    public Board getBoard() {
        return board;
    }
}
