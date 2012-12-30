package tictactoe;

/**
 * A Board represents a tic tac toe board.
 *
 * @author Samuel Andersson
 *
 */
public class Board {

	private int[] board;

	public static final int NOPLAYER = 0;
	public static final int REDPLAYER = 1;
	public static final int BLUEPLAYER = 2;

	/**
	 * Construct a new tic tac toe board, 3x3 in size.
	 */
	public Board() {
		board = new int[9];
	}

	/**
	 * Reset the board, making it all empty spaces
	 */
	public void init() {
		for (int i = 0; i < 9; i++) {
			board[i] = NOPLAYER;
		}
	}

	/**
	 * Checks whether the piece is within the game board
	 *
	 * @param x
	 *            The x location to check
	 * @param y
	 *            The y location to check
	 * @return True if the piece is located on the board
	 */
	private boolean correctPosition(int x, int y) {
		return (x >= 0 && x <= 2 && y >= 0 && y <= 3);
	}

	/**
	 * Check whether the player value is valid
	 *
	 * @param player
	 *            The player value
	 * @return True if the player value is valid
	 */
	private boolean correctPlayer(int player) {
		return player > 0 && player < 3;
	}

	/**
	 * Replace all pieces on this board with the pieces of the provided board
	 *
	 * @param board
	 *            The source board we update this board with
	 */
	public void updateBoard(Board board) {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				setPlayer(x, y, board.getPlayer(x, y));
			}
		}
	}
	public void updateBoard(int[] board) {
		for (int i = 0; i < 9; i++) {
			this.board[i] = board[i];
		}
	}

	/**
	 * Move a piece to a new location
	 * @see #movePiece(int, int, int, int, boolean)
	 */
	public boolean movePiece(int x1, int y1, int x2, int y2) {
		return movePiece(x1, y1, x2, y2, false);
	}

	/**
	 * Move a piece to a new location. Pieces can only be moved to locations
	 * without a piece already
	 *
	 * @param x1
	 *            The x location for the piece being moved
	 * @param y1
	 *            The y location for the piece being moved
	 * @param x2
	 *            The x location for the destination
	 * @param y2
	 *            The y location for the destination
	 * @param force
	 * 			  Whether we will force movement
	 * @return True if piece was successfully moved
	 */
	public boolean movePiece(int x1, int y1, int x2, int y2, boolean force) {
		if (!correctPosition(x1, y1))
			return false;
		if (!correctPosition(x2, y2))
			return false;

		int player = getPlayer(x1, y1);
		if (!force && getPlayer(x2, y2) != NOPLAYER)
			return false;
		setPlayer(x1, y1, NOPLAYER);
		setPlayer(x2, y2, player);
		return true;
	}
	/**
	 * Swap two pieces on the board
	 *
	 * @param x1
	 *            The x location of the first piece
	 * @param y1
	 *            The y location of the first piece
	 * @param x2
	 *            The x location of the second piece
	 * @param y2
	 *            The y location of the second piece
	 */
	public void swapPieces(int x1, int y1, int x2, int y2) {
		if (!correctPosition(x1, y1))
			return;
		if (!correctPosition(x2, y2))
			return;

		int player1 = getPlayer(x1, y1);
		int player2 = getPlayer(x2, y2);
		setPlayer(x1, y1, player2);
		setPlayer(x2, y2, player1);
	}

	/**
	 * Place a new piece on the board
	 *
	 * @see tictactoe.Board#placePiece(int, int, int, boolean)
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
		if (!correctPosition(x, y))
			return false;
		if (!correctPlayer(player))
			return false;
		if (!force && getPlayer(x, y) != NOPLAYER)
			return false;

		setPlayer(x, y, player);
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
	public int getPlayer(int x, int y) {
		return board[x + 3 * y];
	}

	private void setPlayer(int x, int y, int player) {
		board[x + 3 * y] = player;
	}
}
