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
		if (turn == player) {
			if (board.placePiece(player, x, y)) {
				switchTurn();
				return true;
			}
			info("TTTServer#Game", "Couldn't place on " + x + ", " + y);
			return false;
		}
		info("TTTServer#Game", "Not " + player.getName() + "'s turn!");
		return false;
	}

	/**
	 * Move a piece and switch player turn. You can only move pieces if the
	 * board is filled entirely
	 *
	 * @see Board#movePiece(int, int, int, int)
	 */
	public boolean movePiece(int x1, int y1, int x2, int y2, Player player) {
		if (turn == player) {
			if (board.movePiece(x1, y1, x2, y2)) {
				switchTurn();
				return true;
			}
		}
		return false;
	}

	public Player getPieceOwner(int x, int y) {
		int clr = board.getPlayer(x, y);
		if (clr == Board.REDPLAYER) return red;
		if (clr == Board.BLUEPLAYER) return blue;
		return null;
	}

	public void switchTurn() {
		if (turn == red)
			turn = blue;
		else
			turn = red;
	}

	public void setTurn(Player player) {
		if (player != red && player != blue) return;
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
		if (color == Board.REDPLAYER) return red;
		if (color == Board.BLUEPLAYER) return blue;
		return null;
	}


	public void updateBoard(Board board) {
		this.board.updateBoard(board);
	}

	public Board getBoard() {
		return board;
	}

}
