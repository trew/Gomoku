package tictactoe.states;

import tictactoe.logic.Board;
import tictactoe.logic.Player;
import tictactoe.net.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static tictactoe.net.GenericRequestPacket.Request.*;
import static com.esotericsoftware.minlog.Log.*;

/**
 * The listener towards the server for the GamePlayState. This will update the
 * board and move pieces around.
 *
 * @author Samuel Andersson
 *
 */
public class GameplayStateListener extends Listener {

	GameplayState state;

	public GameplayStateListener(GameplayState state) {
		this.state = state;
	}

	@Override
	public void received(Connection connection, Object object) {

		// locate the type of packet we received
		if (object instanceof PlacePiecePacket) {
			PlacePiecePacket ppp = (PlacePiecePacket) object;

			Player player = state.game.getPlayer(ppp.playerColor);
			if (!state.game.placePiece(ppp.x, ppp.y, player)) {
				warn("GameplayStateListener",
						"Piece couldn't be placed, requesting board update");
				connection.sendTCP(new GenericRequestPacket(BoardUpdate));
			} else {
				info("GameplayStateListener", player.getName()
						+ " piece placed on " + ppp.x + ", " + ppp.y);
			}

		} else if (object instanceof BoardPacket) {
			// let's update our board with the board of the server
			BoardPacket bp = (BoardPacket) object;
			state.game.updateBoard(bp.asBoard());
			info("GameplayStateListener", "Board updated");

		} else if (object instanceof SetColorPacket) {
			// set our color as requested by server
			SetColorPacket scp = (SetColorPacket) object;
			if (scp.color == Board.REDPLAYER)
				state.me = state.game.getRed();
			else if (scp.color == Board.BLUEPLAYER)
				state.me = state.game.getBlue();
			info("GameplayStateListener", "Color set to " + state.me.getName());

		} else if (object instanceof NotifyTurnPacket) {
			NotifyTurnPacket ntp = (NotifyTurnPacket) object;
			state.game.setTurn(state.game.getPlayer(ntp.color));
			info("GameplayStateListener", "Notified about turn: "
					+ state.game.getTurn().getName());
		}
	}
}
