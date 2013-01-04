package tictactoe.logic;

import static com.esotericsoftware.minlog.Log.*;

/**
 * Contains game logic for a different version of tictactoe The board is
 * infinite, and a player needs 5 in a row to win. The game keeps track of the
 * board, the players and whose turn it is. It will also have methods indicating
 * victory or defeat.
 *
 * @author Samuel Andersson
 */
public class Game {

	/** The board */
	private Board board;

	/** Whose turn it is */
	private Player turn;

	/** The red player */
	private Player red;

	/** The blue player */
	private Player blue;

	/**
	 * Create a new game
	 */
	public Game() {
		board = new Board();
		red = new Player("Red", Board.REDPLAYER);
		blue = new Player("Blue", Board.BLUEPLAYER);
		turn = red;
	}

	/**
	 * Reset the game and set player turn to red
	 */
	public void reset() {
		board.reset();
		turn = red;
	}

	/**
	 * Place a piece and switch player turn
	 *
	 * @see Board#placePiece(int, int, int)
	 */
	public boolean placePiece(int x, int y, Player player) {
		// not possible to compare "turn == player" because it is a reference
		// comparison. it would've been possible in C++ with operator
		// overloading, but here we must rely on comparison by value, which is
		// possible using the colors.
		if (turn.getColor() == player.getColor()) {
			if (board.placePiece(player, x, y)) {
				switchTurn();
				return true;
			}
			info("Game", "Couldn't place on " + x + ", " + y);
			return false;
		}
		debug("Game", "Not " + player.getName() + "'s turn!");
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
		Piece piece = board.getPiece(x, y);
		if (piece == null)
			return null;
		int clr = piece.getPlayerColor();
		if (clr == Board.REDPLAYER)
			return red;
		if (clr == Board.BLUEPLAYER)
			return blue;
		return null;
	}

	/**
	 * Swap turns between red and blue
	 */
	public void switchTurn() {
		if (turn == red)
			setTurn(blue);
		else
			setTurn(red);
	}

	/**
	 * Set turn to provided player
	 *
	 * @param player
	 *            The player who is going to get the turn
	 */
	public void setTurn(Player player) {
		if (player != red && player != blue)
			return;
		debug("Game", "Turn set to " + turn.getName());
		turn = player;
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
	 * Get the red player
	 *
	 * @return The red player
	 */
	public Player getRed() {
		return red;
	}

	/**
	 * Get the blue player
	 *
	 * @return The blue player
	 */
	public Player getBlue() {
		return blue;
	}

	/**
	 * Get a player depending on provided color
	 *
	 * @param color
	 *            The player color
	 * @return Red if provided color is {@link Board#REDPLAYER}, Blue if
	 *         provided color is {@link Board#BLUEPLAYER}
	 * @throws IllegalArgumentException
	 *             Indicates a value other than {@link Board#REDPLAYER} or
	 *             {@link Board#BLUEPLAYER}
	 */
	public Player getPlayer(int color) throws IllegalArgumentException {
		if (color == Board.REDPLAYER)
			return red;
		if (color == Board.BLUEPLAYER)
			return blue;
		throw new IllegalArgumentException("No player with this color: \""
				+ color + "\".");
	}

	/**
	 * Replace the current board with the new board
	 *
	 * @param board
	 *            The board to replace the current one
	 */
	public void updateBoard(Board board) {
		debug("Game", "Updating board");
		this.board.updateBoard(board);
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
