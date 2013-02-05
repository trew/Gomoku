package gomoku.client.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import gomoku.client.GomokuClient;
import gomoku.net.*;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

/**
 * A NetworkGameState adds a BounceListener to the client which enables the
 * state to have its own callback functions from the network.
 *
 * @author Samuel Andersson
 *
 */
public abstract class GomokuNetworkGameState extends GomokuGameState {

    BounceListener listener;

    /**
     * Initialize the Network state by adding creating a {@link BounceListener}
     */
    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        super.init(container, game);
        listener = new BounceListener(this);
    }

    /**
     * Adds the {@link BounceListener} to the {@link Client} that exists in
     * {@link GomokuClient}. Proceeds by calling
     * {@link GomokuGameState#enter(GameContainer, GomokuClient)}
     *
     * @throws IllegalArgumentException
     *             if the StateBasedGame isn't a {@link GomokuClient}
     */
    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        super.enter(container, game);
        if (!(game instanceof GomokuClient)) {
            throw new IllegalArgumentException("game must be a GomokuClient");
        }
        GomokuClient gomokuClient = (GomokuClient) game;
        if (gomokuClient.client != null)
            gomokuClient.client.addListener(listener);
        enter(container, gomokuClient);
    }

    /**
     * Removes the {@link BounceListener} to the {@link Client} that exists in
     * {@link GomokuClient}. Proceeds by calling
     * {@link GomokuGameState#leave(GameContainer, GomokuClient)}
     */
    @Override
    public void leave(GameContainer container, StateBasedGame game)
            throws SlickException {
        super.leave(container, game);
        if (!(game instanceof GomokuClient)) {
            throw new IllegalArgumentException("game must be a GomokuClient");
        }
        GomokuClient gomokuClient = (GomokuClient) game;
        if (gomokuClient.client != null)
            gomokuClient.client.removeListener(listener);
        leave(container, gomokuClient);
    }

    /**
     * Called when the client is connected to the remote end.
     *
     * @param connection
     */
    public void connected(Connection connection) {
    }

    /**
     * Called when the client is disconnected from the remote end.
     *
     * @param connection
     */
    public void disconnected(Connection connection) {
    }

    /**
     * This function is called from {@link BounceListener}s. It will distribute
     * the call out to specific functions depending on the packet type. The
     * function name is "handle&lt;PacketType&gt;". Packets can be found in
     * {@link gomoku.net}.
     *
     * @param connection
     *            the connection that sent the packet
     * @param object
     *            the object
     */
    public void received(Connection connection, Object object) {
        if (object instanceof BoardPacket)
            handleBoard(connection, (BoardPacket) object);
        else if (object instanceof GameListPacket)
            handleGameList(connection, (GameListPacket) object);
        else if (object instanceof GenericRequestPacket)
            handleGenericRequest(connection, (GenericRequestPacket) object);
        else if (object instanceof InitialServerDataPacket)
            handleInitialServerData(connection,
                    (InitialServerDataPacket) object);
        else if (object instanceof NotifyTurnPacket)
            handleNotifyTurn(connection, (NotifyTurnPacket) object);
        else if (object instanceof BoardActionPacket)
            handleBoardAction(connection, (BoardActionPacket) object);
        else if (object instanceof PlayerListPacket)
            handlePlayerList(connection, (PlayerListPacket) object);
        else if (object instanceof VictoryPacket)
            handleVictory(connection, (VictoryPacket) object);
    }

    /**
     * Handles how a received BoardPacket should be treated.
     *
     * @param connection
     *            the connection that sent the BoardPacket
     * @param bp
     *            the BoardPacket
     */
    protected void handleBoard(Connection connection, BoardPacket bp) {
    }

    /**
     * Handles how a received GameListPacket should be treated.
     *
     * @param connection
     *            the connection that sent the GameListPacket
     * @param glp
     *            the GameListPacket
     */
    protected void handleGameList(Connection connection, GameListPacket glp) {
    }

    /**
     * Handles how a received GenericRequestPacket should be treated.
     *
     * @param connection
     *            the connection that sent the GenericRequestPacket
     * @param grp
     *            the GenericRequestPacket
     */
    protected void handleGenericRequest(Connection connection,
            GenericRequestPacket grp) {
    }

    /**
     * Handles how a received InitialServerDataPacket should be treated.
     *
     * @param connection
     *            the connection that sent the InitialServerDataPacket
     * @param isdp
     *            the InitialServerDataPacket
     */
    protected void handleInitialServerData(Connection connection,
            InitialServerDataPacket isdp) {
    }

    /**
     * Handles how a received NotifyTurnPacket should be treated.
     *
     * @param connection
     *            the connection that sent the NotifyTurnPacket
     * @param ntp
     *            the NotifyTurnPacket
     */
    protected void handleNotifyTurn(Connection connection, NotifyTurnPacket ntp) {
    }

    /**
     * Handles how a received PlacePiecePacket should be treated.
     *
     * @param connection
     *            the connection that sent the PlacePiecePacket
     * @param ppp
     *            the PlacePiecePacket
     */
    protected void handleBoardAction(Connection connection, BoardActionPacket ppp) {
    }

    /**
     * Handles how a received PlayerListPacket should be treated.
     *
     * @param connection
     *            the connection that sent the PlayerListPacket
     * @param plp
     *            the PlayerListPacket
     */
    protected void handlePlayerList(Connection connection, PlayerListPacket plp) {
    }

    /**
     * Handles how a received VictoryPacket should be treated.
     *
     * @param connection
     *            the connection that sent the VictoryPacket
     * @param vp
     *            the VictoryPacket
     */
    protected void handleVictory(Connection connection, VictoryPacket vp) {
    }

    public void idle(Connection connection) {
    }
}