package tictactoe.server;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.*;

import tictactoe.logic.Game;
import tictactoe.net.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import static com.esotericsoftware.minlog.Log.*;
import com.martiansoftware.jsap.*;

/**
 * Server of the Tic-tac-toe game<br />
 * <br />
 * Possible arguments<br />
 * <b>--port</b> <i>PORT</i> - The port number which we'll run the server on<br />
 * <b>--swing</b> - Whether we should run with swing or use standard console.
 * (Swing is always used on windows)<br />
 *
 * @author Samuel Andersson
 */
public class TTTServer {

	/**
	 * The port which this server is listening on. Can be set by providing
	 * --port to the application command line
	 *
	 * @see #parseArgs(String[])
	 */
	private static int PORT;

	/**
	 * Whether we are going to use Swing as our console
	 *
	 * @see #parseArgs(String[])
	 */
	private static boolean SWING;

	/**
	 * The Kryonet server
	 */
	private Server server;

	/**
	 * The listener for the open connections. Each connection will have its own
	 * listener.
	 */
	private ServerListener listener;

	/**
	 * The game that the server runs
	 */
	public Game game;

	public boolean redPlayerConnected;
	public boolean bluePlayerConnected;

	/**
	 * The JFrame if we're using Swing console
	 */
	private JFrame frame;

	private static TTTServer tttserver = null;

	/**
	 * Create a new TTTServer
	 */
	public TTTServer() {
		server = new Server();
		game = new Game();
		listener = new ServerListener(this);
		frame = null;
		redPlayerConnected = false;
		bluePlayerConnected = false;
	}

	/**
	 * Initialize the server, add the ServerListener and register kryo classes.
	 *
	 * @see ServerListener
	 */
	public void init() {
		server.addListener(listener);

		RegisterPackets.register(server.getKryo());
	}

	/**
	 * Start the server and begin listening on provided port
	 *
	 * @see #PORT
	 */
	public void start() {
		server.start();
		try {
			server.bind(PORT);
		} catch (IOException e) {
			if (TRACE)
				trace("TTTServer", e);
			else
				error("TTTServer", "Error: " + e.getMessage());
		}
	}

	/**
	 * Stop the server and exit cleanly.
	 */
	public void exit() {
		info("TTTServer", "Exiting server");
		server.stop();
		System.exit(0);
	}

	/**
	 * Reset the board and broadcast change to all connections
	 */
	public void resetGame() {
		game.reset();
		broadcast(null, new BoardPacket(game.getBoard()));
		info("TTTServer", "Board was reset");
	}

	public void notifyTurn() {
		broadcast(null, new NotifyTurnPacket(game.getTurn().getColor()));
	}

	/**
	 * Broadcast a packet to all connections except provided source. We won't
	 * send to the source connection because that client has already made
	 * necessary changes.
	 *
	 * @param sourceConnection
	 *            The connection that triggered this broadcast
	 * @param object
	 *            The object that will be broadcasted
	 */
	public void broadcast(Connection sourceConnection, Object object) {
		debug("TTTServer", "Broadcasting " + object.getClass().getSimpleName());
		if (sourceConnection == null) {
			server.sendToAllTCP(object);
		} else {
			server.sendToAllExceptTCP(sourceConnection.getID(), object);
		}
	}

	/**
	 * Parse command line arguments that was passed to the application upon
	 * startup.
	 *
	 * @param args
	 *            The arguments passed to the application
	 */
	public static void parseArgs(String[] args) {
		JSAP jsap = new JSAP();
		FlaggedOption swingOpt = new FlaggedOption("swing")
				.setStringParser(JSAP.BOOLEAN_PARSER).setDefault("true")
				.setLongFlag("swing");
		FlaggedOption portOpt = new FlaggedOption("port")
				.setStringParser(JSAP.INTEGER_PARSER).setDefault("9123")
				.setLongFlag("port");
		try {
			jsap.registerParameter(swingOpt);
			jsap.registerParameter(portOpt);

			JSAPResult config = jsap.parse(args);
			SWING = config.getBoolean("swing");
			PORT = config.getInt("port");
		} catch (JSAPException e) {
			if (TRACE)
				trace("TTTServer", e);
			else
				error("TTTServer", "Error parsing arguments: " + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * The main entry point of the server
	 *
	 * @param args
	 *            Any arguments passed to the server
	 */
	public static void main(String[] args) {
		Log.set(LEVEL_INFO);

		parseArgs(args);

		// override if using windows
		if (System.getProperty("os.name").startsWith("Windows")) {
			SWING = true;
		}

		tttserver = new TTTServer();
		if (SWING) {
			tttserver.frame = new JFrame();
			tttserver.frame.setAutoRequestFocus(true);
			tttserver.frame.add(new JLabel(" Output "), BorderLayout.NORTH);

			JTextArea ta = new JTextArea();
			Console co = new Console(ta);
			PrintStream ps = new PrintStream(co);
			System.setOut(ps);
			System.setErr(ps);

			tttserver.frame.add(new JScrollPane(ta));

			tttserver.frame.setMinimumSize(new Dimension(350, 300));

			TrayIcon icon = null;
			if (SystemTray.isSupported()) {
				SystemTray tray = SystemTray.getSystemTray();
				Image image = Toolkit
						.getDefaultToolkit()
						.getImage(
								"C:/Users/samuel/Documents/dev/tictactoe_multi/src/res/tray.gif");

				PopupMenu menu = new PopupMenu();
				MenuItem exitItem = new MenuItem("Exit server");
				exitItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tttserver.exit();
					}
				});
				MenuItem resetItem = new MenuItem("Reset board");
				resetItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						tttserver.resetGame();
					}
				});
				menu.add(resetItem);
				menu.add(exitItem);
				icon = new TrayIcon(image, "Server", menu);
				icon.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						tttserver.frame.setExtendedState(Frame.NORMAL);
						tttserver.frame.setVisible(true);
						tttserver.frame.toFront();
					}
				});
				try {
					tray.add(icon);
				} catch (AWTException e1) {
					e1.printStackTrace();
				}
			}
			tttserver.frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					tttserver.exit();
					System.exit(0);
				}

				@Override
				public void windowIconified(WindowEvent e) {
					tttserver.frame.setVisible(false);
				}
			});
			tttserver.frame.pack();
			tttserver.frame.setVisible(true);
		}

		tttserver.init();
		tttserver.start();
	}

}
