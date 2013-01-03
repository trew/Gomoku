package tictactoe.net;

import tictactoe.logic.Board;
import tictactoe.logic.Piece;

public class BoardPacket {
	public Piece[][] board;

	public BoardPacket() {
	}

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

	public Board asBoard() {
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
