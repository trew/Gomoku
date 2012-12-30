package net;

public class PlacePiecePacket {
	public int x;
	public int y;
	public int player;

	public PlacePiecePacket() {}
	public PlacePiecePacket(int x, int y, int player) {
		this.x = x;
		this.y = y;
		this.player = player;
	}
}
