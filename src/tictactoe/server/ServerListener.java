package tictactoe.server;

import java.util.HashMap;

import tictactoe.logic.Board;
import tictactoe.logic.TictactoeGame;
import tictactoe.logic.Player;
import tictactoe.net.*;
import static tictactoe.net.Request.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static com.esotericsoftware.minlog.Log.*;

/**
 * The listener for connections to the server. It contains the server and the
 * board which it will manipulate during connection interactions.
 *
 * @author Samuel Andersson
 */
public class ServerListener extends Listener {

	/** The server containing this listener */
	private TTTServer server;

	/** The list of connected players */
	private HashMap<Integer, Player> playerList;

	/** the same game as in server */
	private TictactoeGame game;

	/**
	 * Create a listener for the server
	 *
	 * @param server
	 *            The server which we exist in
	 * @param board
	 *            The board we'll manipulate
	 */
	public ServerListener(TTTServer server) {
		this.server = server;
		game = server.game;
		playerList = new HashMap<Integer, Player>();
	}

	@Override
	public void connected(Connection connection) {
		if (!server.redPlayerConnected) {
			playerList.put(connection.getID(), game.getRed());
			server.redPlayerConnected = true;
			debug("TTTServer", "" + connection.toString()
					+ " received playercolor " + Board.REDPLAYER);
		} else if (!server.bluePlayerConnected) {
			playerList.put(connection.getID(), game.getBlue());
			server.bluePlayerConnected = true;
			debug("TTTServer", "" + connection.toString()
					+ " received playercolor " + Board.BLUEPLAYER);
		} else {
			debug("TTTServer", "" + connection.toString()
					+ " received no playercolor ");
		}
	}

	@Override
	public void disconnected(Connection connection) {
		if (playerList.get(connection.getID()) == game.getRed())
			server.redPlayerConnected = false;
		else if (playerList.get(connection.getID()) == game.getBlue())
			server.bluePlayerConnected = false;
		playerList.remove(connection.getID());
		debug("TTTServer", "Removed " + connection.getID() + " from playerlist");
	}

	/**
	 * Called when an object has been received from the remote end of the
	 * connection. This method dispatches to relevant methods depending on the
	 * packet type.
	 *
	 * @param conn
	 *            The connection that sent us the packet
	 * @param obj
	 *            The packet to process
	 * @see #HandlePlacePiecePacket(Connection, PlacePiecePacket)
	 * @see #HandleMovePiecePacket(Connection, MovePiecePacket)
	 * @see #HandleGenericRequestPacket(Connection, GenericRequestPacket)
	 */
	@Override
	public void received(Connection conn, Object obj) {

		if (obj instanceof PlacePiecePacket) {
			HandlePlacePiecePacket(conn, (PlacePiecePacket) obj);

		} else if (obj instanceof GenericRequestPacket) {
			HandleGenericRequestPacket(conn, (GenericRequestPacket) obj);

		}
	}

	/**
	 * Place a piece on the board and notify other connections
	 *
	 * @param conn
	 *            The connection that sent us the packet
	 * @param ppp
	 *            The packet to process
	 */
	private void HandlePlacePiecePacket(Connection conn, PlacePiecePacket ppp) {

		if (playerList.get(conn.getID()) != game.getTurn()) {
			conn.sendTCP(new NotifyTurnPacket(game.getTurn().getColor()));
			return;
		}

		if (game.placePiece(ppp.x, ppp.y, playerList.get(conn.getID()))) {
			debug("TTTServer", playerList.get(conn.getID()).getName()
					+ " placed a piece on " + ppp.x + ", " + ppp.y);
			server.broadcast(conn, ppp);
		} else {
			// placement was not possible, update the board at client
			conn.sendTCP(new BoardPacket(game.getBoard()));
			info("TTTServer", "Couldn't place there! Pos: " + ppp.x + ", "
					+ ppp.y);
		}
	}

	/**
	 * Handle a generic request, such as BoardUpdate or ClearBoard i.e.
	 *
	 * @param conn
	 *            The connection that sent us the packet
	 * @param grp
	 *            The packet to process
	 */
	private void HandleGenericRequestPacket(Connection conn,
			GenericRequestPacket grp) {
		if (grp.getRequest() == BoardUpdate) {
			BoardPacket bp = new BoardPacket(game.getBoard());
			conn.sendTCP(bp);

		} else if (grp.getRequest() == ClearBoard) {
			game.reset();
			BoardPacket bp = new BoardPacket(game.getBoard());
			server.broadcast(null, bp);
			server.broadcast(null, new NotifyTurnPacket(game.getTurn()
					.getColor()));

		} else if (grp.getRequest() == GetColorAndTurn) {
			conn.sendTCP(new SetColorPacket(playerList.get(conn.getID())
					.getColor()));
			conn.sendTCP(new NotifyTurnPacket(game.getTurn().getColor()));

		} else if (grp.getRequest() == GetTurn) {
			conn.sendTCP(new NotifyTurnPacket(game.getTurn().getColor()));

		} else {
			error("TTTServer", "GenericRequestPacket of unknown type: "
					+ grp.getRequest());
		}
	}
}
