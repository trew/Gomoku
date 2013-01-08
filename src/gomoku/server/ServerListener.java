package gomoku.server;

import gomoku.logic.Board;
import gomoku.logic.GomokuGame;
import gomoku.net.*;

import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static com.esotericsoftware.minlog.Log.*;
import static gomoku.net.Request.*;

/**
 * The listener for connections to the server. It contains the server and the
 * board which it will manipulate during connection interactions.
 * 
 * @author Samuel Andersson
 */
public class ServerListener extends Listener {

    /** The server containing this listener */
    private GomokuServer server;

    /** The black player connection ID */
    private int blackID;

    /** The white player connection ID */
    private int whiteID;

    /** The list of connected players */
    private HashMap<Integer, String> playerList;

    /** The list of all spectators */
    private HashMap<Integer, String> spectators;

    /** the same game as in server */
    private GomokuGame game;

    /**
     * Create a listener for the server
     * 
     * @param server
     *            The server which we exist in
     * @param board
     *            The board we'll manipulate
     */
    public ServerListener(GomokuServer server) {
        this.server = server;
        game = server.game;
        playerList = new HashMap<Integer, String>();
        spectators = new HashMap<Integer, String>();
        blackID = 0;
        whiteID = 0;
    }

    /**
     * Returns a list of connected players. The first position is reserved for
     * the black player. If no black player is connected the first spot will
     * contain "(none)". The second position is reserved for the white player.
     * If no white player is connected the second spot will contain "(none)".
     * The rest of the list will be spectators.
     * 
     * @return a list of connected players
     */
    public String[] getPlayerList() {
        String[] players = new String[spectators.size() + 2];

        // add black to the first position
        if (blackID == 0)
            players[0] = "(none)";
        else
            players[0] = playerList.get(blackID);
        if (whiteID == 0)
            players[1] = "(none)";
        else
            players[1] = playerList.get(whiteID);

        int x = 2;
        for (String p : spectators.values()) {
            players[x++] = p;
        }
        return players;
    }

    @Override
    public void connected(Connection connection) {
    }

    /**
     * Notification that a connection has disconnected. Remove any connected
     * players from our player lists.
     */
    @Override
    public void disconnected(Connection connection) {
        if (connection.getID() == blackID)
            blackID = 0;
        else if (connection.getID() == whiteID)
            whiteID = 0;
        else
            spectators.remove(connection.getID());

        // if we actually removed a player, broadcast change to rest
        if (playerList.remove(connection.getID()) != null) {
            debug("GomokuServer", "Removed " + connection.getID()
                    + " from playerlist");
            server.broadcast(connection, new PlayerListPacket(getPlayerList()));
        }
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
     * @see #handlePlacePiece(Connection, PlacePiecePacket)
     * @see #HandleMovePiecePacket(Connection, MovePiecePacket)
     * @see #handleGenericRequest(Connection, GenericRequestPacket)
     */
    @Override
    public void received(Connection conn, Object obj) {

        if (obj instanceof PlacePiecePacket) {
            handlePlacePiece(conn, (PlacePiecePacket) obj);

        } else if (obj instanceof GenericRequestPacket) {
            handleGenericRequest(conn, (GenericRequestPacket) obj);

        } else if (obj instanceof InitialClientDataPacket) {
            handleInitialClientData(conn, (InitialClientDataPacket) obj);
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
    private void handlePlacePiece(Connection conn, PlacePiecePacket ppp) {

        int playerColor = Board.NOPLAYER;
        if (blackID == conn.getID()) {
            playerColor = Board.BLACKPLAYER;
        } else if (whiteID == conn.getID()) {
            playerColor = Board.WHITEPLAYER;
        } else { // player is a spectator. he cannot place
            return;
        }

        // not the players turn
        if (playerColor != game.getTurn().getColor()) {
            conn.sendTCP(new NotifyTurnPacket(game.getTurn().getColor()));
            return;
        }

        if (game.placePiece(ppp.x, ppp.y, playerColor)) {
            debug("GomokuServer", playerList.get(conn.getID())
                    + " placed a piece on " + ppp.x + ", " + ppp.y);
            server.broadcast(conn, ppp);
        } else {
            // placement was not possible, update the board at client
            conn.sendTCP(new BoardPacket(game.getBoard()));
            info("GomokuServer", "Couldn't place there! Pos: " + ppp.x + ", "
                    + ppp.y);
        }
    }

    /**
     * This packet is treated as the confirmation that the client has connected
     * and wants to play. This function will delegate a player spot in the game
     * to the client if there is one free, otherwise the client will be told to
     * spectate.
     * 
     * @param conn
     *            The connection that sent us the packet
     * @param icdp
     *            The initial data from the client
     */
    private void handleInitialClientData(Connection conn,
            InitialClientDataPacket icdp) {
        String playerName = icdp.getName();
        int playerColor = Board.NOPLAYER;

        if (blackID == 0) {
            // tell player to receive black
            game.getBlack().setName(playerName);
            playerColor = Board.BLACKPLAYER;
            blackID = conn.getID();

        } else if (whiteID == 0) {
            // tell player to receive white
            game.getWhite().setName(playerName);
            playerColor = Board.WHITEPLAYER;
            whiteID = conn.getID();

        } else {
            // tell player to spectate
            spectators.put(conn.getID(), playerName);
        }
        playerList.put(conn.getID(), playerName);

        int turnColor = game.getTurn().getColor();

        InitialServerDataPacket isdp = new InitialServerDataPacket(
                game.getBoard(), playerColor, turnColor, getPlayerList());
        conn.sendTCP(isdp);

        server.broadcast(conn, new PlayerListPacket(getPlayerList()));
    }

    /**
     * Handle a generic request, such as BoardUpdate or ClearBoard i.e.
     * 
     * @param conn
     *            The connection that sent us the packet
     * @param grp
     *            The packet to process
     */
    private void handleGenericRequest(Connection conn, GenericRequestPacket grp) {

        if (grp.getRequest() == BoardUpdate) {
            BoardPacket bp = new BoardPacket(game.getBoard());
            conn.sendTCP(bp);

        } else if (grp.getRequest() == ClearBoard) {
            game.reset();
            BoardPacket bp = new BoardPacket(game.getBoard());
            server.broadcast(null, bp);
            server.broadcast(null, new NotifyTurnPacket(game.getTurn()
                    .getColor()));

        } else if (grp.getRequest() == GetTurn) {
            conn.sendTCP(new NotifyTurnPacket(game.getTurn().getColor()));

        } else if (grp.getRequest() == PlayerList) {
            conn.sendTCP(new PlayerListPacket(getPlayerList()));

        } else {
            error("GomokuServer", "GenericRequestPacket of unknown type: "
                    + grp.getRequest());
        }
    }
}
