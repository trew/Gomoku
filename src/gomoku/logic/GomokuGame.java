package gomoku.logic;

import static com.esotericsoftware.minlog.Log.*;

/**
 * Contains game logic for Gomoku game. The game keeps track of the board, the
 * players and whose turn it is. It will also have methods indicating victory or
 * defeat.
 *
 * @author Samuel Andersson
 */
public class GomokuGame {

	/** The board */
	private Board board;

	/** Whose turn it is */
	private Player turn;

	/** The black player */
	private Player black;

	/** The white player */
	private Player white;

	/**
	 * Create a new game with set width and height
	 *
	 * @param width
	 *            the width of the board
	 * @param height
	 *            the height of the board
	 */
	public GomokuGame(int width, int height) {
		board = new Board(width, height);
		black = new Player("Black", Board.BLACKPLAYER);
		white = new Player("White", Board.WHITEPLAYER);
		turn = black;
	}

	/**
	 * Create a new game from a board
	 *
	 * @param board
	 *            The board
	 */
	public GomokuGame(Board board) {
		this.board = board;
		black = new Player("Black", Board.BLACKPLAYER);
		white = new Player("White", Board.WHITEPLAYER);
		turn = black;
	}

	/**
	 * Reset the game and set player turn to red
	 */
	public void reset() {
		board.reset();
		turn = black;
	}

	/**
	 * Place a piece and switch player turn
	 *
	 * @see Board#placePiece(int, int, int)
	 */
	public boolean placePiece(int x, int y, Player player) {
		// not possible to compare "turn == player" because it is a reference
		// comparison. We must rely on comparison by value, which is
		// possible using the colors.
		if (turn.getColor() == player.getColor()) {
			if (board.placePiece(player, x, y)) {
				switchTurn();
				return true;
			}
			info("GomokuGame", "Couldn't place on " + x + ", " + y);
			return false;
		}
		debug("GomokuGame", "Not " + player.getColorName() + "'s turn!");
		return false;
	}

	/**
	 * Get the owner of the piece placed on provided position
	 *
	 * @param x
	 *            The x location of the piece
	 * @param y
	 *            The y location of the piece
	 * @return The player owning the piece on x, y
	 */
	public Player getPieceOwner(int x, int y) {
		int piece = board.getPiece(x, y);
		if (piece == Board.BLACKPLAYER)
			return black;
		if (piece == Board.WHITEPLAYER)
			return white;
		return null;
	}

	/**
	 * Swap turns between black and white
	 */
	public void switchTurn() {
		if (turn == black)
			setTurn(white);
		else
			setTurn(black);
	}

	/**
	 * Set turn to provided player
	 *
	 * @param player
	 *            The player who is going to get the turn
	 */
	public void setTurn(Player player) {
		if (player != black && player != white)
			return;
		debug("GomokuGame", "Turn set to " + turn.getColorName());
		turn = player;
	}

	/**
	 * Set turn to player with provided color
	 *
	 * @param playerColor
	 *            the provided player color
	 */
	public void setTurn(int playerColor) {
		if (playerColor == Board.BLACKPLAYER) {
			turn = black;
			debug("GomokuGame", "Turn set to " + turn.getColorName());
		} else if (playerColor == Board.WHITEPLAYER) {
			turn = white;
			debug("GomokuGame", "Turn set to " + turn.getColorName());
		}
	}

	/**
	 * Get the player who has the turn
	 *
	 * @return The player who has the turn
	 */
	public Player getTurn() {
		return turn;
	}

	/**
	 * Returns the black player
	 *
	 * @return the black player
	 */
	public Player getBlack() {
		return black;
	}

	/**
	 * Returns the white player
	 *
	 * @return the white player
	 */
	public Player getWhite() {
		return white;
	}

	/**
	 * Get a player depending on provided color
	 *
	 * @param color
	 *            The player color
	 * @return Black if provided color is {@link Board#BLACKPLAYER}, white if
	 *         provided color is {@link Board#WHITEPLAYER}
	 * @throws IllegalArgumentException
	 *             Indicates a value other than {@link Board#BLACKPLAYER} or
	 *             {@link Board#WHITEPLAYER}
	 */
	public Player getPlayer(int color) throws IllegalArgumentException {
		if (color == Board.BLACKPLAYER)
			return black;
		if (color == Board.WHITEPLAYER)
			return white;
		throw new IllegalArgumentException("No player with this color: \""
				+ color + "\".");
	}

	/**
	 * Replace the current board with the new board
	 *
	 * @param board
	 *            The board to replace the current one
	 */
	public void replaceBoard(Board board) {
		debug("GomokuGame", "Replacing board");
		this.board.replaceBoard(board);
	}

	/**
	 * Get the current board
	 *
	 * @return The current board
	 */
	public Board getBoard() {
		return board;
	}

}
