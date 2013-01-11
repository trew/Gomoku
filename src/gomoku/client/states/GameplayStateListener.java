package gomoku.client.states;

import gomoku.logic.Board;
import gomoku.logic.Player;
import gomoku.net.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static com.esotericsoftware.minlog.Log.*;
import static gomoku.net.Request.*;

/**
 * The listener towards the server for the GamePlayState. This will update the
 * board and move pieces around.
 *
 * @author Samuel Andersson
 *
 */
public class GameplayStateListener extends Listener {

    /** The state holding this listener */
    private GameplayState state;

    /**
     * Create a new listener
     *
     * @param state
     *            The state holding this listener
     */
    public GameplayStateListener(GameplayState state) {
        this.state = state;
    }

    @Override
    public void received(Connection connection, Object object) {

        // locate the type of packet we received
        if (object instanceof PlacePiecePacket) {
            handlePlacePiece(connection, (PlacePiecePacket) object);

        } else if (object instanceof BoardPacket) {
            handleBoard(connection, (BoardPacket) object);

        } else if (object instanceof NotifyTurnPacket) {
            handleNotifyTurn(connection, (NotifyTurnPacket) object);

        } else if (object instanceof InitialServerDataPacket) {
            handleInitialServerData(connection,
                    (InitialServerDataPacket) object);

        } else if (object instanceof PlayerListPacket) {
            handlePlayerList(connection, (PlayerListPacket) object);
        }
    }

    private void handlePlacePiece(Connection conn, PlacePiecePacket ppp) {
        if (!state.gomokuGame.placePiece(ppp.x, ppp.y, ppp.playerColor)) {
            warn("GameplayStateListener",
                    "Piece couldn't be placed, requesting board update");
            conn.sendTCP(new GenericRequestPacket(BoardUpdate));

        } else {
            Player player = state.gomokuGame.getPlayer(ppp.playerColor);
            info("GameplayStateListener", player.getColorName()
                    + " piece placed on " + ppp.x + ", " + ppp.y);
        }
    }

    private void handleBoard(Connection conn, BoardPacket bp) {
        // let's update our board with the board of the server
        state.gomokuGame.replaceBoard(bp.getBoard());
        info("GameplayStateListener", "Board updated");
    }

    private void handleNotifyTurn(Connection conn, NotifyTurnPacket ntp) {
        state.gomokuGame.setTurn(state.gomokuGame.getPlayer(ntp.getColor()));
        info("GameplayStateListener", "Notified about turn: "
                + state.gomokuGame.getTurn().getName());
    }

    private void handleInitialServerData(Connection conn,
            InitialServerDataPacket idp) {
        state.setInitialData(idp.getBoard(), idp.getColor(), idp.getTurn(),
                idp.getPlayerList());
        info("GameplayStateListener", "Received initial data from server.");
    }

    private void handlePlayerList(Connection conn, PlayerListPacket plp) {
        debug("GameplayStateListene", "Updated playerlist");
        state.setPlayerList(plp.players);

        // the first two spots is reserved for black and white. If we're not the
        // specified color, update it.
        if (state.me.getColor() != Board.BLACKPLAYER) {
            state.gomokuGame.getBlack().setName(plp.players[0]);
        }
        if (state.me.getColor() != Board.WHITEPLAYER) {
            state.gomokuGame.getWhite().setName(plp.players[1]);
        }
    }
}
