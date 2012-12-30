package server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tictactoe.Board;

import net.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import static com.esotericsoftware.minlog.Log.*;

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
	private static int PORT = 9123;

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
		kryo.register(int[].class);
	}

	public void start() {
		server.start();
		try {
			server.bind(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exit() {
		server.stop();
	}

	public void broadcast(Connection conn, Object obj) {
		server.sendToAllExceptTCP(conn.getID(), obj);
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

		boolean useSwing = false;
		if (System.getProperty("os.name").startsWith("Windows")) {
			useSwing = true;
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--port") && i != args.length) {
				i++;
				PORT = Integer.parseInt(args[i]);
			} else if (args[i].equals("--swing")) {
				useSwing = true;
			}
		}

		final TTTServer server;
		if (useSwing) {
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
		} else {
			server = new TTTServer();
		}

		server.init();
		server.start();
	}

}
