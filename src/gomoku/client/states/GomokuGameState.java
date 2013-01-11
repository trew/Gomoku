package gomoku.client.states;

import gomoku.client.GomokuClient;

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
    public void enter(GameContainer container, GomokuClient game)
            throws SlickException {
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game)
            throws SlickException {
        leave(container, (GomokuClient) game);
    }

    public void leave(GameContainer container, GomokuClient game)
            throws SlickException {
    }

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

    public void received(Connection connection, Object object) {
    }

    public void idle(Connection connection) {
    }

}