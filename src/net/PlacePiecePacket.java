package net;

import tictactoe.logic.Player;

public class PlacePiecePacket {
	public int x;
	public int y;
	public Player player;

	public PlacePiecePacket() {}
	public PlacePiecePacket(int x, int y, Player player) {
		this.x = x;
		this.y = y;
		this.player = player;
	}
}
