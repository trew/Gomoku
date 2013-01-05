package gomoku.net;

import gomoku.logic.Board;
import gomoku.logic.Piece;

/**
 * A packet containing a board data. More specifically it is a 2D array of
 * {@link Piece} objects. That way piece positions is preserved because they can
 * often be negative X or negative Y, which is not possible on a normal 2D
 * array.
 *
 * @author Samuel Andersson
 */
public class BoardPacket {

	/** The board data */
	private Piece[][] board;

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
		int width = board.getWidth();
		int height = board.getHeight();
		this.board = new Piece[height][];
		for (int i = 0; i < height; i++) {
			this.board[i] = new Piece[width];
		}
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				this.board[row][col] = board
						.getPiece(col + board.getLeftBorder(),
								row + board.getTopBorder());
			}
		}
	}

	/**
	 * Create a {@link Board} from the data in this packet
	 *
	 * @return A board from the data in this packet
	 */
	public Board toBoard() {
		Board newBoard = new Board();
		for (int row = 0; row < board.length; row++) {
			for (int col = 0; col < board[row].length; col++) {
				Piece piece = board[row][col];
				if (piece == null) {
					continue;
				}
				if (!newBoard.placePiece(piece.getPlayerColor(), piece.getX(),
						piece.getY())) {
					continue;
				}
			}
		}
		return newBoard;
	}
}
