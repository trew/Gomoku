package tictactoe;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import tictactoe.states.ConnectState;
import tictactoe.states.GameplayState;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import com.martiansoftware.jsap.*;

import static com.esotericsoftware.minlog.Log.*;

/**
 * The main entry class for the Tic-tac-toe client.
 *
 * @author Samuel Andersson
 *
 */
public class Tictactoe extends StateBasedGame {

	/* ********** STATIC ********** */
	/** The width of the screen */
	private static final int WIDTH = 640;

	/** The height of the screen */
	private static final int HEIGHT = 480;

	/** Fullscreen or not? */
	private static final boolean FULLSCREEN = false;

	/** The target max frame rate */
	private static final int TARGET_FPS = 60;

	/** The address to the server */
	public static String ADDRESS;

	/** The port of the server */
	public static int PORT;

	/* ********** END STATIC ********** */

	/** The network client */
	public Client client;

	/**
	 * Create a new game client
	 */
	public Tictactoe() {
		super("Tic tac toe");
		client = new Client();
	}

	/**
	 * @see StateBasedGame#initStatesList(GameContainer)
	 */
	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		this.addState(new ConnectState()); // stateID: 0
		this.addState(new GameplayState()); // stateID: 1
	}

	/**
	 * Parse the arguments passed to the application
	 *
	 * @param args
	 *            The arguments
	 * @throws JSAPException
	 *             if command line couldn't be parsed correctly
	 */
	public static void parseArgs(String[] args) throws JSAPException {
		JSAP jsap = new JSAP();
		FlaggedOption addrOpt = new FlaggedOption("address").setDefault(
				"127.0.0.1").setLongFlag("address");
		FlaggedOption portOpt = new FlaggedOption("port")
				.setStringParser(JSAP.INTEGER_PARSER).setDefault("9123")
				.setLongFlag("port");
		jsap.registerParameter(addrOpt);
		jsap.registerParameter(portOpt);

		JSAPResult result = jsap.parse(args);
		ADDRESS = result.getString("address");
		info("Tictactoe", "Using \"" + ADDRESS + "\" as remote address");
		PORT = result.getInt("port");
		info("Tictactoe", "Using \"" + PORT + "\" as port number");
	}

	/**
	 * The main entry point of the game client
	 * @param args The arguments passed to the application
	 * @see #parseArgs(String[])
	 */
	public static void main(String[] args) {
		try {
			Log.set(LEVEL_DEBUG);
			parseArgs(args);

			// create the StateBasedGame to be passed to the container
			Tictactoe game = new Tictactoe();

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
			if (TRACE) trace("Tictactoe", e);
			else error("Tictactoe", e.getMessage());
		} catch (JSAPException e) {
			if (TRACE) trace("Tictactoe", e);
			else error("Tictactoe", "Error parsing commandline arguments");
		}
	}
}
