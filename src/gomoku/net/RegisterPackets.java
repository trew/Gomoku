package gomoku.net;

import gomoku.logic.Board;
import gomoku.logic.Board.PlacePieceBoardAction;
import gomoku.logic.GomokuConfig;
import gomoku.logic.GomokuGame.PlacePieceGameAction;
import gomoku.logic.Swap2.Swap2ChooseColorAction;
import gomoku.logic.Swap2.Swap2PlacePieceGameAction;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

/**
 * For kryonets {@link Client} and {@link Server}. Used to register all classes
 * that are passed between clients and servers. They must be registered on both
 * ends in the same order, so both will use this function.
 *
 * @author Samuel Andersson
 */
public abstract class RegisterPackets {

    /**
     * Register classes, called by {@link Client}s and {@link Server}s that will
     * pass information between them.
     *
     * @param kryo
     *            The kryo object where we register the classes
     */
    public static void register(Kryo kryo) {
        kryo.register(GameActionPacket.class);
        kryo.register(Board.class);
        kryo.register(int[].class);
        kryo.register(BoardPacket.class);
        kryo.register(GenericRequestPacket.class);
        kryo.register(NotifyTurnPacket.class);
        kryo.register(InitialClientDataPacket.class);
        kryo.register(InitialServerDataPacket.class);
        kryo.register(PlayerListPacket.class);
        kryo.register(String[].class);
        kryo.register(CreateGamePacket.class);
        kryo.register(JoinGamePacket.class);
        kryo.register(GameListPacket.class);
        kryo.register(VictoryPacket.class);
        kryo.register(GomokuConfig.class);
        kryo.register(PlacePieceBoardAction.class);
        kryo.register(PlacePieceGameAction.class);
        kryo.register(Swap2PlacePieceGameAction.class);
        kryo.register(Swap2ChooseColorAction.class);
    }
}
