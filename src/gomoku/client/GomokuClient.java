package gomoku.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import gomoku.client.gui.Fonts;
import gomoku.client.states.ChooseGameState;
import gomoku.client.states.ConnectState;
import gomoku.client.states.CreateGameState;
import gomoku.client.states.GameplayState;
import gomoku.client.states.MainMenuState;
import gomoku.client.states.OptionsMenuState;
import gomoku.client.states.PauseMenu;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
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

    protected Properties properties;
    private String propertiesFilename = "settings.properties";

    public Properties getProperties() {
        return properties;
    }

    /** Used if a properties-file is missing, and we're attempting to create one */
    private boolean isCreatingPropertiesFile = false;

    protected void loadProperties() {
        info("Reading configuration file");
        InputStream is;
        try {
            properties = new Properties();
            is = new FileInputStream(propertiesFilename);
            try {
                properties.load(is);
            } catch (IOException e) {
                error(e);
            }
        } catch (FileNotFoundException e1) {
            if (!isCreatingPropertiesFile) {
                warn("Couldn't find settings file, attempting to create one.");
                isCreatingPropertiesFile = true;
                storeProperties();
                loadProperties();
            } else {
                error(e1);
            }
        }

    }

    public void storeProperties() {

        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream(propertiesFilename);
        } catch (FileNotFoundException e) {
            // create new file
            File file = new File(propertiesFilename);
            try {
                fileos = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                error(e1);
            }
        }

        if (fileos != null) {
            try {
                properties.store(fileos, "Gomoku Settings");
            } catch (IOException e) {
                error(e);
            }
        } else {
            error("Could not save configuration file: " + propertiesFilename);
        }
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
        loadProperties();

        Fonts.loadAngelCodeFonts("res/fonts/messagebox", "res/fonts/nametag");
        Fonts.loadFonts(14, 16, 18, 24, 32);
        container.setDefaultFont(Fonts.getDefaultFont());

        this.addState(new MainMenuState());
        this.addState(new ConnectState());
        this.addState(new ChooseGameState());
        this.addState(new CreateGameState());
        this.addState(new GameplayState());
        this.addState(new PauseMenu());
        this.addState(new OptionsMenuState());
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
            Input.disableControllers();
            container.setDisplayMode(WIDTH, HEIGHT, false);
            container.setTargetFrameRate(TARGET_FPS);
            container.setShowFPS(false);
            container.setForceExit(false); // we want to call functions after the game is cleaned up

            // start the game
            container.start();

            // stop the client before exiting
            game.client.stop();
            game.storeProperties();

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
