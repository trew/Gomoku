package server;

import java.io.IOException;

import tictactoe.Board;

import net.*;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;

/**
 * Server of the Tic-tac-toe game
 *
 * @author Samuel Andersson
 */
public class TTTServer {

	/** The port which this server is listening on */
	private static final int PORT = 9123;

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

	public void broadcast(Object obj) {
		server.sendToAllTCP(obj);
	}

	/**
	 * The main entry point of the server
	 *
	 * @param args
	 *            Any arguments passed to the server
	 */
	public static void main(String[] args) {
		TTTServer srv = new TTTServer();
		srv.init();
		srv.start();
	}

}
