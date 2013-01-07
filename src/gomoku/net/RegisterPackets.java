package gomoku.net;

import gomoku.logic.Board;

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
        kryo.register(PlacePiecePacket.class);
        kryo.register(Board.class);
        kryo.register(int[].class);
        kryo.register(BoardPacket.class);
        kryo.register(GenericRequestPacket.class);
        kryo.register(SetColorPacket.class);
        kryo.register(NotifyTurnPacket.class);
        kryo.register(InitialDataPacket.class);
    }
}
