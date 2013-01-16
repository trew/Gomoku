package gomoku.server;

import static gomoku.net.Request.BoardUpdate;
import static gomoku.net.Request.ClearBoard;
import static gomoku.net.Request.GetTurn;
import static gomoku.net.Request.PlayerList;
import static gomoku.net.Request.LeaveGame;

import java.util.HashMap;

import gomoku.logic.Board;
import gomoku.logic.GomokuGame;
import gomoku.logic.GomokuGameListener;
import gomoku.net.BoardPacket;
import gomoku.net.GenericRequestPacket;
import gomoku.net.NotifyTurnPacket;
import gomoku.net.PlacePiecePacket;
import gomoku.net.PlayerListPacket;
import gomoku.net.VictoryPacket;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import static org.trew.log.Log.*;

/**
 * A game of Gomoku, delegated by the GomokuServer.
 *
 * @author Samuel Andersson
 *
 */
public class GomokuNetworkGame implements GomokuGameListener {

    /** Global ID counter for games */
    private static int IDCOUNTER = 1;

    /** Connection ID for the player playing black */
    private int blackID;

    /** Connection ID for the player playing white */
    private int whiteID;

    /** The list of connected players */
    private HashMap<Integer, String> playerList;

    /** The list of all spectators */
    private HashMap<Integer, String> spectators;

    /** The game logic */
    private GomokuGame game;

    /** The server holding this game */
    private GomokuServer gomokuServer;

    /** The kryonet server */
    private Server server;

    /** The game ID */
    private int id;

    /** The game name */
    private String name;

    private boolean isEnding;

    /**
     * Create a new game of Gomoku.
     *
     * @param gomokuServer
     *            the server holding this game
     * @param server
     *            the kryonet server
     * @param name
     *            the name of the game
     * @param width
     *            the width of the board
     * @param height
     *            the height of the board
     */
    public GomokuNetworkGame(GomokuServer gomokuServer, Server server,
            String name, int width, int height) {
        this.gomokuServer = gomokuServer;
        this.server = server;
        this.name = name;
        game = new GomokuGame(width, height);
        game.addListener(this);
        isEnding = false;
        id = IDCOUNTER++;

        playerList = new HashMap<Integer, String>();
        spectators = new HashMap<Integer, String>();
    }

    @Override
    public void gameOver(int winner) {
        debug("Game Over!");
        broadcast(null, new VictoryPacket((short)(game.getTurn().getColor())));
    }

    /**
     * Broadcast a packet to all connections except provided source. We won't
     * send to the source connection because that client has already made
     * necessary changes.
     *
     * @param sourceConnection
     *            The connection that triggered this broadcast
     * @param object
     *            The object that will be broadcasted
     */
    public void broadcast(Connection conn, Object obj) {
        for (Integer id : playerList.keySet()) {
            if (conn == null || id != conn.getID())
                server.sendToTCP(id, obj);
        }
    }

    /**
     * Called when a player wants to join the game. The player will receive a
     * color automatically, and that color will be returned from the function.
     *
     * @param conn
     *            the connection that wants to join the game
     * @param name
     *            the name of the player at the remote endpoint
     * @return the playercolor that the player received
     */
    public int join(Connection conn, String name) {
        int playerColor = Board.NOPLAYER;
        if (blackID == 0) {
            blackID = conn.getID();
            game.getBlack().setName(name);
            playerColor = Board.BLACKPLAYER;
            info(name + " joined game " + this.name + " as black.");
        } else if (whiteID == 0) {
            whiteID = conn.getID();
            game.getWhite().setName(name);
            playerColor = Board.WHITEPLAYER;
            info(name + " joined game " + this.name + " as white.");
        } else {
            spectators.put(conn.getID(), name);
            info(name + " joined game " + this.name + " as spectator.");
        }
        playerList.put(conn.getID(), name);
        broadcast(conn, new PlayerListPacket(getPlayerList()));
        return playerColor;
    }

    /**
     * A player disconnected or left the game
     *
     * @param conn
     *            the connection
     * @param disconnect
     *            whether the player disconnected
     */
    private void leave(Connection conn, boolean disconnect) {
        if (conn.getID() == blackID)
            blackID = 0;
        else if (conn.getID() == whiteID)
            whiteID = 0;
        else
            spectators.remove(conn.getID());

        // if we actually removed a player, broadcast change to rest
        if (playerList.remove(conn.getID()) != null) {
            if (disconnect)
                info(conn.getID() + " disconnected from game " + name);
            else
                info(conn.getID() + " left the game " + name);
            broadcast(conn, new PlayerListPacket(getPlayerList()));
        }

        if (blackID == 0 && whiteID == 0 && spectators.isEmpty() && !isEnding) {
            info("Ending game: " + name);
            isEnding = true;
            gomokuServer.endGame(this);
        }
    }

    /**
     * Returns the ID of this game
     *
     * @return the ID of this game
     */
    public int getID() {
        return id;
    }

    /**
     * Returns the name of this game
     *
     * @return the name of this game
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the game logic object
     *
     * @return the game logic object
     */
    public GomokuGame getGame() {
        return game;
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

    /**
     * Called when a player disconnects from this game. Will remove the player
     * from any lists and set blackID and whiteID to 0 if needed. If no players
     * (or spectators) are left in the game, the game will end and be closed.
     *
     * @param conn
     *            the disconnected connection
     */
    public void disconnected(Connection conn) {
        leave(conn, true);
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
     * @see #handleGenericRequest(Connection, GenericRequestPacket)
     */
    public void received(Connection conn, Object obj) {

        if (obj instanceof PlacePiecePacket) {
            handlePlacePiece(conn, (PlacePiecePacket) obj);
        } else if (obj instanceof GenericRequestPacket) {
            handleGenericRequest(conn, (GenericRequestPacket) obj);
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
            debug(playerList.get(conn.getID()) + " placed a piece on " + ppp.x
                    + ", " + ppp.y);
            broadcast(conn, ppp);
        } else {
            // placement was not possible, update the board at client
            conn.sendTCP(new BoardPacket(game.getBoard()));
            error("Couldn't place there! Pos: " + ppp.x + ", " + ppp.y);
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
    private void handleGenericRequest(Connection conn, GenericRequestPacket grp) {

        if (grp.getRequest() == BoardUpdate) {
            BoardPacket bp = new BoardPacket(game.getBoard());
            conn.sendTCP(bp);

        } else if (grp.getRequest() == ClearBoard) {
            game.reset();
            BoardPacket bp = new BoardPacket(game.getBoard());
            broadcast(null, bp);
            broadcast(null, new NotifyTurnPacket(game.getTurn().getColor()));

        } else if (grp.getRequest() == GetTurn) {
            conn.sendTCP(new NotifyTurnPacket(game.getTurn().getColor()));

        } else if (grp.getRequest() == PlayerList) {
            conn.sendTCP(new PlayerListPacket(getPlayerList()));

        } else if (grp.getRequest() == LeaveGame) {
            leave(conn, false);

        } else {
            error("GenericRequestPacket of unknown type: " + grp.getRequest());
        }
    }

}
