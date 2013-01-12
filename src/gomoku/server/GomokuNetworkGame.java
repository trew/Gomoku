package gomoku.server;

import static com.esotericsoftware.minlog.Log.debug;
import static com.esotericsoftware.minlog.Log.error;
import static com.esotericsoftware.minlog.Log.info;
import static gomoku.net.Request.BoardUpdate;
import static gomoku.net.Request.ClearBoard;
import static gomoku.net.Request.GetTurn;
import static gomoku.net.Request.PlayerList;

import java.util.HashMap;

import gomoku.logic.Board;
import gomoku.logic.GomokuGame;
import gomoku.net.BoardPacket;
import gomoku.net.GenericRequestPacket;
import gomoku.net.InitialClientDataPacket;
import gomoku.net.InitialServerDataPacket;
import gomoku.net.NotifyTurnPacket;
import gomoku.net.PlacePiecePacket;
import gomoku.net.PlayerListPacket;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class GomokuNetworkGame {

    private static int IDCOUNTER = 1;

    private int blackID;

    private int whiteID;

    /** The list of connected players */
    private HashMap<Integer, String> playerList;

    /** The list of all spectators */
    private HashMap<Integer, String> spectators;

    private GomokuGame game;

    private GomokuServer gomokuServer;
    private Server server;

    private int id;

    private String name;

    public GomokuNetworkGame(GomokuServer gomokuServer, Server server, String name, int width,
            int height) {
        this.gomokuServer = gomokuServer;
        this.server = server;
        game = new GomokuGame(width, height);
        id = IDCOUNTER;
        IDCOUNTER++;
        this.name = name;

        playerList = new HashMap<Integer, String>();
        spectators = new HashMap<Integer, String>();
    }

    public int getNextColor() {
        if (blackID == 0) {
            return Board.BLACKPLAYER;
        } else if (whiteID == 0) {
            return Board.WHITEPLAYER;
        }
        return Board.NOPLAYER;
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

    public int join(Connection conn, String name) {
        int playerColor = Board.NOPLAYER;
        if (blackID == 0) {
            blackID = conn.getID();
            game.getBlack().setName(name);
            playerColor = Board.BLACKPLAYER;
            info("GomokuNetworkGame", name + " joined game " + this.name
                    + " as black.");
        } else if (whiteID == 0) {
            whiteID = conn.getID();
            game.getWhite().setName(name);
            playerColor = Board.WHITEPLAYER;
            info("GomokuNetworkGame", name + " joined game " + this.name
                    + " as white.");
        } else {
            spectators.put(conn.getID(), name);
            info("GomokuNetworkGame", name + " joined game " + this.name
                    + " as spectator.");
        }
        playerList.put(conn.getID(), name);
        broadcast(conn, new PlayerListPacket(getPlayerList()));
        return playerColor;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

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

    public void disconnected(Connection conn) {
        if (conn.getID() == blackID)
            blackID = 0;
        else if (conn.getID() == whiteID)
            whiteID = 0;
        else
            spectators.remove(conn.getID());

        // if we actually removed a player, broadcast change to rest
        if (playerList.remove(conn.getID()) != null) {
            info("GomokuServer", conn.getID() + " disconnected from game "
                    + name);
            broadcast(conn, new PlayerListPacket(getPlayerList()));
        }

        if (blackID == 0 && whiteID == 0 && spectators.isEmpty()) {
            info("GomokuNetworkGame", "Ending game: " + name);
            gomokuServer.endGame(this);
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
    public void received(Connection conn, Object obj) {

        if (obj instanceof PlacePiecePacket) {
            handlePlacePiece(conn, (PlacePiecePacket) obj);

        } else if (obj instanceof GenericRequestPacket) {
            handleGenericRequest(conn, (GenericRequestPacket) obj);

        } else if (obj instanceof InitialClientDataPacket) {
            // handleInitialClientData(conn, (InitialClientDataPacket) obj);
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
            broadcast(conn, ppp);
        } else {
            // placement was not possible, update the board at client
            conn.sendTCP(new BoardPacket(game.getBoard()));
            error("GomokuServer", "Couldn't place there! Pos: " + ppp.x + ", "
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
    @SuppressWarnings("unused")
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

        broadcast(conn, new PlayerListPacket(getPlayerList()));
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

        } else {
            error("GomokuServer", "GenericRequestPacket of unknown type: "
                    + grp.getRequest());
        }
    }

}
