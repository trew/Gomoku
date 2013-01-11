package gomoku.client;

import gomoku.client.states.ChooseGameState;
import gomoku.client.states.ConnectState;
import gomoku.client.states.CreateGameState;
import gomoku.client.states.GameplayState;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

import static com.esotericsoftware.minlog.Log.*;

/**
 * The main entry class for the Gomoku client.
 *
 * @author Samuel Andersson
 *
 */
public class GomokuClient extends StateBasedGame {

    /* ********** STATIC ********** */
    /** The width of the screen */
    private static final int WIDTH = 800;

    /** The height of the screen */
    private static final int HEIGHT = 600;

    /** Fullscreen or not? */
    private static final boolean FULLSCREEN = false;

    /** The target max frame rate */
    private static final int TARGET_FPS = 60;

    /* ********** END STATIC ********** */

    /** The network client */
    public Client client;

    private String playerName;

    public void setPlayerName(String name) {
        playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }

    /**
     * Create a new game client
     */
    public GomokuClient() {
        super("Gomoku");
        client = new Client();
    }

    /**
     * @see StateBasedGame#initStatesList(GameContainer)
     */
    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        this.addState(new ConnectState());
        this.addState(new ChooseGameState());
        this.addState(new CreateGameState());
        this.addState(new GameplayState());
    }

    /**
     * The main entry point of the game client
     *
     * @param args
     *            The arguments passed to the application
     * @see #parseArgs(String[])
     */
    public static void main(String[] args) {
        try {
            Log.set(LEVEL_DEBUG);

            // create the StateBasedGame to be passed to the container
            GomokuClient game = new GomokuClient();

            // create the container
            AppGameContainer container = new AppGameContainer(game);

            // set display mode and configurations
            container.setDisplayMode(WIDTH, HEIGHT, FULLSCREEN);
            container.setTargetFrameRate(TARGET_FPS);

            // start the game
            container.start();

            // stop the client before exiting
            game.client.stop();

        } catch (SlickException e) {
            if (TRACE)
                trace("GomokuClient", e);
            else
                error("GomokuClient", e.getMessage());
        }
    }
}
