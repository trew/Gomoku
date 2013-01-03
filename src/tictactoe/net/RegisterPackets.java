package tictactoe.net;

import tictactoe.logic.Piece;

import com.esotericsoftware.kryo.Kryo;

public class RegisterPackets {
	private RegisterPackets() {}

	public static void register(Kryo kryo) {
		kryo.register(PlacePiecePacket.class);
		kryo.register(Piece.class);
		kryo.register(Piece[].class);
		kryo.register(Piece[][].class);
		kryo.register(BoardPacket.class);
		kryo.register(GenericRequestPacket.class);
		kryo.register(SetColorPacket.class);
		kryo.register(NotifyTurnPacket.class);
	}
}
