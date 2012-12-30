package tictactoe;

import net.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * The listener towards the server for the GamePlayState. This will update the
 * board and move pieces around.
 *
 * @author Samuel Andersson
 *
 */
public class GameplayStateListener extends Listener {

	GameplayState state;
	Board board;

	public GameplayStateListener(GameplayState state, Board board) {
		this.state = state;
		this.board = board;
	}

	@Override
	public void received(Connection connection, Object object) {

		// locate the type of packet we received
		if (object instanceof PlacePiecePacket) {
			PlacePiecePacket ppp = (PlacePiecePacket) object;

			// force placement of piece, since server is asking
			board.placePiece(ppp.player, ppp.x, ppp.y, true);

		} else if (object instanceof MovePiecePacket) {
			MovePiecePacket mpp = (MovePiecePacket) object;

			// force movement of piece, since server is asking
			// if there is already a piece on the place we're moving to, the
			// piece will be replaced
			board.movePiece(mpp.x1, mpp.y1, mpp.x2, mpp.y2, true);

		} else if (object instanceof BoardPacket) {
			// let's update our board with the board of the server
			BoardPacket bp = (BoardPacket) object;
			board.updateBoard(bp.board);
		}
	}
}
