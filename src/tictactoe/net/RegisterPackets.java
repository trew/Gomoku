package tictactoe.net;

import java.util.HashMap;

import tictactoe.logic.*;
import tictactoe.util.HashMap2D;

import com.esotericsoftware.kryo.Kryo;

public class RegisterPackets {
	private RegisterPackets() {}

	public static void register(Kryo kryo) {
		kryo.register(PlacePiecePacket.class);
		kryo.register(HashMap2D.class);
		kryo.register(HashMap.class);
		kryo.register(Integer.class);
		kryo.register(Piece.class);
		kryo.register(BoardPacket.class);
		kryo.register(Board.class);
		kryo.register(Player.class);
		kryo.register(GenericRequestPacket.class);
		kryo.register(SetColorPacket.class);
		kryo.register(NotifyTurnPacket.class);
		kryo.register(int[].class);
	}
}
