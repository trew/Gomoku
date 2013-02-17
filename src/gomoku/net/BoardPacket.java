package gomoku.net;

import gomoku.logic.Board;
import gomoku.logic.GomokuConfig;

/**
 * A packet containing a board data.
 *
 * @author Samuel Andersson
 */
public class BoardPacket {

    /** The board data */
    private int[] board;

    private GomokuConfig config;

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
        this.board = board.getBoardData();
        config = board.getConfig();
    }

    /**
     * Returns the board from this packet
     *
     * @return the board from this packet
     */
    public Board getBoard() {
        Board board = new Board(config);
        board.setBoardData(this.board);
        return board;
    }
}
