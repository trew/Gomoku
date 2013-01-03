package tictactoe.logic;

import static com.esotericsoftware.minlog.Log.*;

public class Game {

	private Board board;

	private Player turn;

	private Player red;
	private Player blue;

	public Game() {
		board = new Board();
		red = new Player("Red", Board.REDPLAYER);
		blue = new Player("Blue", Board.BLUEPLAYER);
		turn = red;
	}

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
		// overloading, but here we can rely on the colors instead, which is
		// comparison by value.
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

	public void switchTurn() {
		if (turn == red)
			setTurn(blue);
		else
			setTurn(red);
	}

	public void setTurn(Player player) {
		if (player != red && player != blue)
			return;
		debug("Game", "Turn set to " + turn.getName());
		turn = player;
	}

	public Player getTurn() {
		return turn;
	}

	public Player getRed() {
		return red;
	}

	public Player getBlue() {
		return blue;
	}

	public Player getPlayer(int color) {
		if (color == Board.REDPLAYER)
			return red;
		if (color == Board.BLUEPLAYER)
			return blue;
		return null;
	}

	public void updateBoard(Board board) {
		debug("Game", "Updating board");
		this.board.updateBoard(board);
	}

	public Board getBoard() {
		return board;
	}

}
