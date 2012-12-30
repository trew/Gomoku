package server;

import tictactoe.Board;
import net.BoardPacket;
import net.MovePiecePacket;
import net.PlacePiecePacket;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

public class ServerListener extends Listener {

	private TTTServer server;
	private Board board;

	public ServerListener(TTTServer server, Board board) {
		this.server = server;
		this.board = board;
	}

	@Override
	public void received(Connection conn, Object obj) {
		if (obj instanceof PlacePiecePacket) {
			PlacePiecePacket ppp = (PlacePiecePacket) obj;
			if (board.placePiece(ppp.player, ppp.x, ppp.y)) {
				server.broadcast(conn, ppp);
				Log.info("Player: " + ppp.player + ", Pos: " + ppp.x + ", "
						+ ppp.y);
			} else {
				// placement was not possible, update the board at client
				conn.sendTCP(new BoardPacket(board));
				Log.info("Couldn't place there! Pos: " + ppp.x + ", " + ppp.y);
			}
		} else if (obj instanceof MovePiecePacket) {
			MovePiecePacket mpp = (MovePiecePacket) obj;
			if (board.movePiece(mpp.x1, mpp.y1, mpp.x2, mpp.y2)) {
				server.broadcast(conn, mpp);
				Log.info("MovePiecePacket", "Pos1: " + mpp.x1 + ", " + mpp.y1
						+ " - Pos2: " + mpp.x2 + ", " + mpp.y2);
			} else {
				// move was not possible, update the board at client
				conn.sendTCP(new BoardPacket(board));
				Log.info("Couldn't move piece!", "Pos1: " + mpp.x1 + ", "
						+ mpp.y1 + " - Pos2: " + mpp.x2 + ", " + mpp.y2);
			}

		} else if (obj instanceof BoardPacket) {
			// treat it as a request
			BoardPacket bp = new BoardPacket(board);
			conn.sendTCP(bp);
		}
	}

}
