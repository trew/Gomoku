package tictactoe.logic;

import tictactoe.util.HashMap2D;

/**
 * A Board represents a tic tac toe board.
 *
 * @author Samuel Andersson
 *
 */
public class Board {

	private HashMap2D<Integer, Integer, Piece> board;

	public static final int NOPLAYER = 0;
	public static final int REDPLAYER = 1;
	public static final int BLUEPLAYER = 2;

	/**
	 * Construct a new tic tac toe board
	 */
	public Board() {
		reset();
	}

	/**
	 * Reset the board, making it all empty spaces
	 */
	public void reset() {
		board = new HashMap2D<Integer, Integer, Piece>();
	}

	/**
	 * Replace all pieces on this board with the pieces of the provided board
	 *
	 * @param board
	 *            The source board we update this board with
	 */
	public void updateBoard(Board board) {
		this.board = board.board;
	}

	/**
	 * Place a new piece on the board
	 *
	 * @see #placePiece(int, int, int, boolean)
	 */
	public boolean placePiece(Player player, int x, int y) {
		return placePiece(player.getColor(), x, y, false);
	}

	/**
	 * Place a new piece on the board
	 *
	 * @see #placePiece(int, int, int, boolean)
	 */
	public boolean placePiece(int player, int x, int y) {
		return placePiece(player, x, y, false);
	}

	/**
	 * Place a new piece on the board. Pieces can only be placed on empty
	 * positions
	 *
	 * @param player
	 *            The player color of the piece
	 * @param x
	 *            The x location of the new piece
	 * @param y
	 *            The y location of the new piece
	 * @param force
	 *            Force placement to occur, even if a piece is already there
	 * @return True if piece was placed
	 */
	public boolean placePiece(int player, int x, int y, boolean force) {
		if (!force && getPiece(x, y) != null)
			return false;

		setPiece(x, y, player);
		return true;
	}

	/**
	 * Get the player color for given position on the board
	 *
	 * @param x
	 *            The x location
	 * @param y
	 *            The y location
	 * @return The player color for the given position. 0 = Empty, 1 = Player 1,
	 *         2 = Player 2.
	 */
	public Piece getPiece(int x, int y) {
		return board.get(x, y);
	}

	private void setPiece(int x, int y, int player) {
		board.put(x, y, new Piece(x, y, player));
	}
}
