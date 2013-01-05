package gomoku.logic;

import gomoku.util.HashMap2D;

import static com.esotericsoftware.minlog.Log.*;

/**
 * A Board represents a Gomoku board.
 *
 * @author Samuel Andersson
 *
 */
public class Board {

	/** The value representing no player */
	public static final int NOPLAYER = 0;

	/** THe value representing a red player */
	public static final int REDPLAYER = 1;

	/** The value representing a blue player */
	public static final int BLUEPLAYER = 2;

	/** The structure containing the board data */
	private HashMap2D<Integer, Integer, Piece> board;

	/** The utmost left x position on the board of a piece */
	private int leftBorder;

	/** The utmost top y position on the board of a piece */
	private int topBorder;

	/** The utmost right x position on the board of a piece */
	private int rightBorder;

	/** The utmost bottom y position on the board of a piece */
	private int bottomBorder;

	/** Indicates whether a piece is placed at all and the borders has been set */
	private boolean borderSet;

	/**
	 * Construct a new Gomoku board
	 */
	public Board() {
		reset();
	}

	/**
	 * Reset the board, making it all empty spaces
	 */
	public void reset() {
		board = new HashMap2D<Integer, Integer, Piece>();
		leftBorder = topBorder = rightBorder = bottomBorder = 0;
		borderSet = false;
	}

	/**
	 * Replace all pieces on this board with the pieces of the provided board
	 *
	 * @param board
	 *            The source board we update this board with
	 */
	public void updateBoard(Board board) {
		this.board = board.board;
		leftBorder = board.leftBorder;
		topBorder = board.topBorder;
		rightBorder = board.rightBorder;
		bottomBorder = board.bottomBorder;
		borderSet = board.borderSet;
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
		if (getPiece(x, y) != null)
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

	/**
	 * Place a piece on the board
	 *
	 * @param x
	 *            The x location for the piece
	 * @param y
	 *            The y location for the piece
	 * @param player
	 *            The player color for the piece
	 */
	private void setPiece(int x, int y, int player) {
		if (!borderSet) {
			leftBorder = rightBorder = x;
			topBorder = bottomBorder = y;
			borderSet = true;
		}
		if (x < leftBorder) {
			// update left border and width
			leftBorder = x;
			info("Board", "left border set to " + x);
		} else if (x > rightBorder) {
			rightBorder = x;
			info("Board", "right border set to " + x);
		}
		if (y < topBorder) {
			topBorder = y;
			info("Board", "top border set to " + y);
		} else if (y > bottomBorder) {
			bottomBorder = y;
			info("Board", "bottom border set to " + y);
		}
		board.put(x, y, new Piece(x, y, player));
	}

	/**
	 * Get the left border of the board
	 *
	 * @return The left border of the board
	 */
	public int getLeftBorder() {
		return leftBorder;
	}

	/**
	 * Get the top border of the board
	 *
	 * @return The top border of the board
	 */
	public int getTopBorder() {
		return topBorder;
	}

	/**
	 * Get the current width of the board
	 *
	 * @return The current width of the board
	 */
	public int getWidth() {
		if (!borderSet)
			return 0;
		return rightBorder - leftBorder + 1;
	}

	/**
	 * Get the current height of the board
	 *
	 * @return The current height of the board
	 */
	public int getHeight() {
		if (!borderSet)
			return 0;
		return bottomBorder - topBorder + 1;
	}

}
