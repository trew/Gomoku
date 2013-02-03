package gomoku.client;

import java.net.URL;

import gomoku.client.states.ChooseGameState;
import gomoku.client.states.ConnectState;
import gomoku.client.states.CreateGameState;
import gomoku.client.states.GameplayState;
import gomoku.client.states.MainMenuState;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;

import TWLSlick.TWLStateBasedGame;

import com.esotericsoftware.kryonet.Client;
import org.trew.log.Log;

import static org.trew.log.Log.*;

/**
 * The main entry class for the Gomoku client.
 *
 * @author Samuel Andersson
 *
 */
public class GomokuClient extends TWLStateBasedGame {

    /* ********** STATIC ********** */
    /** The width of the screen */
    private static final int WIDTH = 800;

    /** The height of the screen */
    private static final int HEIGHT = 600;

    /** The target max frame rate */
    private static final int TARGET_FPS = 60;

    /* ********** END STATIC ********** */

    /** The network client */
    public Client client;

    private String playerName;
    private Image background;

    public Image getBackground() {
        return background;
    }
    public void setBackground(Image bg) {
        background = bg;
    }

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
        this.addState(new MainMenuState());
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
            Log.setLevel(LEVEL_TRACE);

            // create the StateBasedGame to be passed to the container
            GomokuClient game = new GomokuClient();

            // create the container
            AppGameContainer container = new AppGameContainer(game);

            // set display mode and configurations
            container.setDisplayMode(WIDTH, HEIGHT, false);
            container.setTargetFrameRate(TARGET_FPS);
            container.setShowFPS(false);

            // start the game
            container.start();

            // stop the client before exiting
            game.client.stop();

        } catch (SlickException e) {
            if (TRACE)
                trace(e);
            else
                error(e.getMessage());
        }
    }

    @Override
    protected URL getThemeURL() {
        return ResourceLoader.getResource("res/theme.xml");
    }
}
