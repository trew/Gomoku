package net;

import tictactoe.logic.Board;

public class BoardPacket {
	public Board board;

	public BoardPacket() {}
	public BoardPacket(Board board) {
		this.board = board;
	}
}
