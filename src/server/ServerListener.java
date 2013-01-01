package server;

import tictactoe.Board;
import net.*;
import static net.GenericRequestPacket.Request.*;

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

	private TTTServer server;
	private Board board;

	/**
	 * Create a listener for the server
	 *
	 * @param server
	 *            The server which we exist in
	 * @param board
	 *            The board we'll manipulate
	 */
	public ServerListener(TTTServer server, Board board) {
		this.server = server;
		this.board = board;
	}

	/**
	 * Called when an object has been received from the remote end of the
	 * connection. This method dispatches to relevant methods depending on the
	 * packet type. Logs an error if packet is of unknown type.
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

		} else if (obj instanceof MovePiecePacket) {
			HandleMovePiecePacket(conn, (MovePiecePacket) obj);

		} else if (obj instanceof GenericRequestPacket) {
			HandleGenericRequestPacket(conn, (GenericRequestPacket) obj);

		} else {
			error("TTTServer", "Packet received of unknown type: "
					+ obj.getClass().getSimpleName());
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
		if (board.placePiece(ppp.player, ppp.x, ppp.y)) {
			server.broadcast(conn, ppp);
			info("TTTServer", "Player: " + ppp.player + ", Pos: " + ppp.x
					+ ", " + ppp.y);
		} else {
			// placement was not possible, update the board at client
			conn.sendTCP(new BoardPacket(board));
			info("TTTServer", "Couldn't place there! Pos: " + ppp.x + ", "
					+ ppp.y);
		}
	}

	/**
	 * Move a piece on the board and notify other connections
	 *
	 * @param conn
	 *            The connection that sent us the packet
	 * @param mpp
	 *            The packet to process
	 */
	private void HandleMovePiecePacket(Connection conn, MovePiecePacket mpp) {
		if (board.movePiece(mpp.x1, mpp.y1, mpp.x2, mpp.y2)) {
			server.broadcast(conn, mpp);
			info("TTTServer", "MovePiecePacket, Pos1: " + mpp.x1 + ", "
					+ mpp.y1 + " - Pos2: " + mpp.x2 + ", " + mpp.y2);
		} else {
			// move was not possible, update the board at client
			conn.sendTCP(new BoardPacket(board));
			info("TTTServer", "Couldn't move piece! Pos1: " + mpp.x1 + ", "
					+ mpp.y1 + " - Pos2: " + mpp.x2 + ", " + mpp.y2);
		}
	}

	/**
	 * Handle a generic request, such as BoardUpdate or ClearBoard
	 *
	 * @param conn
	 *            The connection that sent us the packet
	 * @param grp
	 *            The packet to process
	 */
	private void HandleGenericRequestPacket(Connection conn,
			GenericRequestPacket grp) {
		if (grp.request == BoardUpdate) {
			BoardPacket bp = new BoardPacket(board);
			conn.sendTCP(bp);

		} else if (grp.request == ClearBoard) {
			board.reset();
			BoardPacket bp = new BoardPacket(board);
			server.broadcast(null, bp);

		} else {
			error("TTTServer", "GenericRequestPacket of unknown type: "
					+ grp.request);
		}
	}
}
