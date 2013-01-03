package tictactoe.logic;

import tictactoe.util.HashMap2D;

import static com.esotericsoftware.minlog.Log.*;

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

	private int leftBorder;
	private int topBorder;
	private int rightBorder;
	private int bottomBorder;

	private boolean leftBorderSet;
	private boolean topBorderSet;


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
		leftBorder = topBorder = rightBorder = bottomBorder = 0;
		leftBorderSet = false;
		topBorderSet = false;
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
		leftBorderSet = board.leftBorderSet;
		topBorderSet = board.topBorderSet;
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
		if (!leftBorderSet) {
			leftBorder = rightBorder = x;
			leftBorderSet = true;
		}
		if (!topBorderSet) {
			topBorder = bottomBorder = y;
			topBorderSet = true;
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

	public int getLeftBorder() {
		return leftBorder;
	}
	public int getTopBorder() {
		return topBorder;
	}
	public int getWidth() {
		if (board.isEmpty()) return 0;
		return rightBorder - leftBorder + 1;
	}
	public int getHeight() {
		if (board.isEmpty()) return 0;
		return bottomBorder - topBorder + 1;
	}

}
