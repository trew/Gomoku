package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.net.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Connection;

public abstract class GomokuGameState extends BasicGameState {

    public static final int CONNECTGAMESTATE = 1;
    public static final int CHOOSEGAMESTATE = 2;
    public static final int CREATEGAMESTATE = 3;
    public static final int GAMEPLAYSTATE = 4;
    public static final int MAINMENUSTATE = 5;

    /**
     * @see BasicGameState#init(GameContainer, StateBasedGame)
     */
    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        init(container, (GomokuClient) game);
    }

    /**
     * Initialize the state. It should load any resources it needs at this stage
     *
     * @param container
     *            The container holding the game
     * @param game
     *            The Gomoku game holding this state
     * @see BasicGameState#init(GameContainer, StateBasedGame)
     */
    public abstract void init(GameContainer container, GomokuClient game)
            throws SlickException;

    /**
     * @see BasicGameState#render(GameContainer, StateBasedGame, Graphics)
     */
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        render(container, (GomokuClient) game, g);
    }

    /**
     * Render this state to the game's graphics context
     *
     * @param container
     *            The container holding the game
     * @param game
     *            The Gomoku game holding this state
     * @param g
     *            The graphics context to render to
     * @see BasicGameState#render(GameContainer, StateBasedGame, Graphics)
     */
    public abstract void render(GameContainer container, GomokuClient game,
            Graphics g) throws SlickException;

    /**
     * @see BasicGameState#update(GameContainer, StateBasedGame, int)
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        update(container, (GomokuClient) game, delta);

    }

    /**
     * Update the state's logic based on the amount of time thats passed
     *
     * @param container
     *            The container holding the game
     * @param game
     *            The Gomoku game holding this state
     * @param delta
     *            The amount of time thats passed in millisecond since last
     *            update
     * @see BasicGameState#update(GameContainer, StateBasedGame, int)
     */
    public abstract void update(GameContainer container, GomokuClient game,
            int delta) throws SlickException;

    /**
     * @see BasicGameState#enter(GameContainer, StateBasedGame)
     */
    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        enter(container, (GomokuClient) game);
    }

    /**
     * Notification that we've entered this game state
     *
     * @param container
     *            The container holding the game
     * @param game
     *            The Gomoku game holding this state
     * @throws SlickException
     *             Indicates an internal error that will be reported through the
     *             standard framework mechanism
     * @see #enter(GameContainer, StateBasedGame)
     */
    public abstract void enter(GameContainer container, GomokuClient game)
            throws SlickException;

    @Override
    public void leave(GameContainer container, StateBasedGame game)
            throws SlickException {
        leave(container, (GomokuClient) game);
    }

    /**
     * Notification that we're leaving this game state
     *
     * @param container
     *            The container holding the game
     * @param game
     *            The Gomoku game holding this state
     * @throws SlickException
     *             Indicates an internal error that will be reported through the
     *             standard framework mechanism
     * @see #leave(GameContainer, StateBasedGame)
     */
    public abstract void leave(GameContainer container, GomokuClient game)
            throws SlickException;

    /**
     * Calculate the left X position for centering something within borders
     *
     * @param x1
     *            The left position of the border
     * @param x2
     *            The right position of the border
     * @param width
     *            The width of the object being centered
     * @return The left position
     */
    public int center(float x1, float x2, float width) {
        return (int) (x1 + (x2 - x1) / 2 - width / 2);
    }

    public void connected(Connection connection) {
    }

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
        else if (object instanceof PlacePiecePacket)
            handlePlacePiece(connection, (PlacePiecePacket) object);
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
    protected void handlePlacePiece(Connection connection, PlacePiecePacket ppp) {
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