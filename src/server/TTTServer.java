package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.*;

import tictactoe.Board;

import net.*;

import com.esotericsoftware.kryo.Kryo;
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

	/** The port which this server is listening on */
	private static int PORT;
	private static boolean SWING;

	private Server server;
	private ServerListener listener;

	private Board board;

	public TTTServer() {
		server = new Server();
		board = new Board();
		listener = new ServerListener(this, board);
	}

	public void init() {
		server.addListener(listener);

		Kryo kryo = server.getKryo();
		kryo.register(PlacePiecePacket.class);
		kryo.register(MovePiecePacket.class);
		kryo.register(BoardPacket.class);
		kryo.register(GenericRequestPacket.class);
		kryo.register(int[].class);
	}

	public void start() {
		server.start();
		try {
			server.bind(PORT);
		} catch (IOException e) {
			if (TRACE) trace("TTTServer", e);
			else error("TTTServer", "Error: " + e.getMessage());
		}
	}

	public void exit() {
		server.stop();
	}

	public void broadcast(Connection conn, Object obj) {
		if (conn == null) {
			server.sendToAllTCP(obj);
		} else {
			server.sendToAllExceptTCP(conn.getID(), obj);
		}
	}

	public static void parseArgs(String[] args) {
		JSAP jsap = new JSAP();
		FlaggedOption swingOpt = new FlaggedOption("swing")
						.setStringParser(JSAP.BOOLEAN_PARSER)
						.setDefault("true")
						.setLongFlag("swing");
		FlaggedOption portOpt = new FlaggedOption("port")
						.setStringParser(JSAP.INTEGER_PARSER)
						.setDefault("9123")
						.setLongFlag("port");
		try {
			jsap.registerParameter(swingOpt);
			jsap.registerParameter(portOpt);

			JSAPResult config = jsap.parse(args);
			SWING = config.getBoolean("swing");
			PORT = config.getInt("port");
		} catch (JSAPException e) {
			if (TRACE) trace("TTTServer", e);
			else error("TTTServer", "Error parsing arguments: " + e.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * The main entry point of the server
	 *
	 * @param args
	 *            Any arguments passed to the server
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Log.set(LEVEL_DEBUG);

		parseArgs(args);

		// override if using windows
		if (System.getProperty("os.name").startsWith("Windows")) {
			SWING = true;
		}

		final TTTServer server;
		if (SWING) {
			JFrame frame = new JFrame();
			frame.add(new JLabel(" Output "), BorderLayout.NORTH);

			JTextArea ta = new JTextArea();
			Console co = new Console(ta);
			PrintStream ps = new PrintStream(co);
			System.setOut(ps);
			System.setErr(ps);

			frame.add(new JScrollPane(ta));

			frame.setMinimumSize(new Dimension(350, 300));
			server = new TTTServer();

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					server.exit();
					System.exit(0);
				}
			});
			frame.pack();
			frame.setVisible(true);
		} else { //not using swing, output goes to default System.out/err
			server = new TTTServer();
		}

		server.init();
		server.start();
	}

}
