package gomoku.client.states;

import java.util.HashSet;

import gomoku.client.GomokuClient;

import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.InputListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import TWLSlick.RootPane;
import TWLSlick.TWLGameState;

public abstract class GomokuGameState extends TWLGameState {

    public static final int CONNECTGAMESTATE = 1;
    public static final int CHOOSEGAMESTATE = 2;
    public static final int CREATEGAMESTATE = 3;
    public static final int GAMEPLAYSTATE = 4;
    public static final int MAINMENUSTATE = 5;

    private HashSet<InputListener> listeners;

    private boolean pauseUpdate = false;
    private boolean pauseRender = false;

    private int nextState;

    public void addListener(InputListener listener) {
        listeners.add(listener);
    }

    public void removeListener(InputListener listener) {
        listeners.remove(listener);
    }

    @Override
    protected RootPane createRootPane() {
        RootPane rp = super.createRootPane();
        rp.setTheme("gomokuclient");
        return rp;
    }

    /**
     * @see BasicGameState#init(GameContainer, StateBasedGame)
     */
    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
        listeners = new HashSet<InputListener>();
        getRootPane();
        nextState = -1;
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
     * Synchronizes state changes because TWL use a new thread for each widget
     * action, thus freaking OpenGL out because it needs its context to be in
     * the same thread.
     *
     * @see BasicGameState#update(GameContainer, StateBasedGame, int)
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        if (nextState >= 0) {
            game.enterState(nextState);
            nextState = -1;
        } else {
            update(container, (GomokuClient) game, delta);
        }

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
        super.enter(container, game);
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
    public void enter(GameContainer container, GomokuClient game) throws SlickException {
    }

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
    public void leave(GameContainer container, GomokuClient game) throws SlickException {
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
    public int center(float x1, float x2, float objectWidth) {
        return (int) (x1 + (x2 - x1) / 2 - objectWidth / 2);
    }

    /**
     * Allowing state changes to happen in the main thread only. This method can
     * be called from any thread, and the state change will be invoked in the
     * main update function.
     *
     * @param stateID
     *            the state id to change to
     * @see #update(GameContainer, StateBasedGame, int)
     */
    public void enterState(int stateID) {
        nextState = stateID;
    }

    public void drawCenteredString(String text, int y, GUIContext container,
            Graphics g) {
        Font font = g.getFont();
        int textW = font.getWidth(text);
        g.drawString(text, center(0, container.getWidth(), textW), y);
    }

    @Override
    public void mouseWheelMoved(int change) {
        for (InputListener listener : listeners) {
            listener.mouseWheelMoved(change);
        }
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        for (InputListener listener : listeners) {
            listener.mouseClicked(button, x, y, clickCount);
        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        for (InputListener listener : listeners) {
            listener.mousePressed(button, x, y);
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        for (InputListener listener : listeners) {
            listener.mouseReleased(button, x, y);
        }
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        for (InputListener listener : listeners) {
            listener.mouseMoved(oldx, oldy, newx, newy);
        }
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        for (InputListener listener : listeners) {
            listener.mouseDragged(oldx, oldy, newx, newy);
        }
    }

    @Override
    public void setInput(Input input) {
    }

    @Override
    public boolean isAcceptingInput() {
        return true;
    }

    @Override
    public void inputEnded() {
    }

    @Override
    public void inputStarted() {
    }

    @Override
    public void keyPressed(int key, char c) {
        for (InputListener listener : listeners) {
            listener.keyPressed(key, c);
        }
    }

    @Override
    public void keyReleased(int key, char c) {
        for (InputListener listener : listeners) {
            listener.keyReleased(key, c);
        }
    }

    @Override
    public void controllerLeftPressed(int controller) {
    }

    @Override
    public void controllerLeftReleased(int controller) {
    }

    @Override
    public void controllerRightPressed(int controller) {
    }

    @Override
    public void controllerRightReleased(int controller) {
    }

    @Override
    public void controllerUpPressed(int controller) {
    }

    @Override
    public void controllerUpReleased(int controller) {
    }

    @Override
    public void controllerDownPressed(int controller) {
    }

    @Override
    public void controllerDownReleased(int controller) {
    }

    @Override
    public void controllerButtonPressed(int controller, int button) {
    }

    @Override
    public void controllerButtonReleased(int controller, int button) {
    }

    @Override
    public void pauseUpdate() {
        pauseUpdate = true;
    }

    @Override
    public void pauseRender() {
        pauseRender = true;
    }

    @Override
    public void unpauseUpdate() {
        pauseUpdate = false;
    }

    @Override
    public void unpauseRender() {
        pauseRender = false;
    }

    @Override
    public boolean isUpdatePaused() {
        return pauseUpdate;
    }

    @Override
    public boolean isRenderPaused() {
        return pauseRender;
    }

    @Override
    public void setUpdatePaused(boolean pause) {
        pauseUpdate = pause;
    }

    @Override
    public void setRenderPaused(boolean pause) {
        pauseRender = pause;
    }

}