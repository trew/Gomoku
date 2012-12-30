package net;

import tictactoe.Board;

public class BoardPacket {
	public int[] board;

	public BoardPacket() {}
	public BoardPacket(Board board) {
		this.board = new int[9];
		int i = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				this.board[i] = board.getPlayer(x, y);
				i++;
			}
		}
	}
	public BoardPacket(int[] board) {
		this.board = new int[9];
		for (int i = 0; i < 9; i++) {
			this.board[i] = board[i];
		}
	}
}
