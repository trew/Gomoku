package gomoku.logic;

/**
 * A Board represents a Gomoku board.
 *
 * @author Samuel Andersson
 *
 */
public class Board {

	/** The value representing no player */
	public static final int NOPLAYER = 0;

	/** THe value representing a black player */
	public static final int BLACKPLAYER = 1;

	/** The value representing a white player */
	public static final int WHITEPLAYER = 2;

	/** The structure containing the board data */
	protected int[] board;

	/** The width of the board */
	protected int width;

	/** The height of the board */
	protected int height;

	/** Empty constructor for Kryonet */
	@SuppressWarnings("unused")
	private Board() {
	}

	/**
	 * Construct a new Gomoku board
	 */
	public Board(int width, int height) {
		this.width = width;
		this.height = height;
		reset();
	}

	/**
	 * Reset the board, making it all empty spaces
	 */
	public void reset() {
		board = new int[width * height];
	}

	/**
	 * Replace the current board with a new board
	 *
	 * @param board
	 *            The source board which will replace the current board
	 */
	public void replaceBoard(Board board) {
		this.board = board.board;
		width = board.width;
		height = board.height;
	}

	/**
	 * Place a new piece on the board
	 *
	 * @see #placePiece(int, int, int)
	 */
	public boolean placePiece(Player player, int x, int y) {
		return placePiece(player.getColor(), x, y);
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
	 * @return True if piece was placed
	 */
	public boolean placePiece(int player, int x, int y) {
		if (getPiece(x, y) != Board.NOPLAYER)
			return false;

		setPiece(x, y, player);
		return true;
	}

	/**
	 * Returns the player color for given position on the board. If either of
	 * the arguments is invalid, i.e. "x" being larger than board width, the
	 * function will return 0.
	 *
	 * @param x
	 *            The x location
	 * @param y
	 *            The y location
	 * @return The player color for the given position. 0 = Empty, 1 = Player 1,
	 *         2 = Player 2.
	 */
	public int getPiece(int x, int y) {
		if (x < 0 || x > width || y < 0 || y > height) {
			return 0;
		}
		return board[x + width * y];
	}

	/**
	 * Place a piece on the board
	 *
	 * @param x
	 *            The x location for the piece
	 * @param y
	 *            The y location for the piece
	 * @param player
	 *            The player color for the piece
	 * @throws IllegalArgumentException
	 *             Indicates that the piece was out of bounds or the player
	 *             color wasn't valid.
	 */
	private void setPiece(int x, int y, int player)
			throws IllegalArgumentException {
		if (x < 0 || x > width || y < 0 || y > height) {
			throw new IllegalArgumentException("Position out of bounds. X: "
					+ x + ", Y: " + y);
		}
		if (player != Board.BLACKPLAYER && player != Board.WHITEPLAYER) {
			throw new IllegalArgumentException("Unknown value of player: \""
					+ player + "\".");
		}

		board[x + width * y] = player;
	}

	/**
	 * Returns the current width of the board
	 *
	 * @return the current width of the board
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the current height of the board
	 *
	 * @return the current height of the board
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the integer array containing the data of the board
	 *
	 * @return the integer array containing the data of the board
	 */
	public int[] getBoard() {
		return board;
	}

}
