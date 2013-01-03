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

/**
 * The main entry class for the Tic-tac-toe client.
 *
 * @author Samuel Andersson
 *
 */
public class Tictactoe extends StateBasedGame {

	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;
	private static final boolean FULLSCREEN = false;
	private static final int TARGET_FPS = 60;

	public Client client;

	public static String ADDRESS;
	public static int PORT;

	public Tictactoe() {
		super("Tic tac toe");
		client = new Client();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		this.addState(new ConnectState()); // stateID: 0
		this.addState(new GameplayState()); // stateID: 1
	}

	public static void ParseArgs(String[] args) throws JSAPException {
		JSAP jsap = new JSAP();
		FlaggedOption addrOpt = new FlaggedOption("address")
							.setDefault("127.0.0.1")
							.setLongFlag("address");
		FlaggedOption portOpt = new FlaggedOption("port")
							.setStringParser(JSAP.INTEGER_PARSER)
							.setDefault("9123")
							.setLongFlag("port");
		jsap.registerParameter(addrOpt);
		jsap.registerParameter(portOpt);

		JSAPResult result = jsap.parse(args);
		ADDRESS = result.getString("address");
		Log.info("Tictactoe", "Using \"" + ADDRESS + "\" as remote address");
		PORT = result.getInt("port");
		Log.info("Tictactoe", "Using \"" + PORT + "\" as port number");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Log.set(Log.LEVEL_DEBUG);
			ParseArgs(args);

			Tictactoe game = new Tictactoe();

			AppGameContainer container = new AppGameContainer(game);

			container.setDisplayMode(WIDTH, HEIGHT, FULLSCREEN);
			container.setTargetFrameRate(TARGET_FPS);

			container.start();
			game.client.stop();

		} catch (SlickException e) {
			e.printStackTrace();
		} catch (JSAPException e) {
			Log.error("Tictactoe", "Error parsing commandline arguments");
			System.exit(-1);
		}
	}
}
