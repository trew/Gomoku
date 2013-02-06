package gomoku.server;

import static gomoku.net.Request.BoardUpdate;
import static gomoku.net.Request.ClearBoard;
import static gomoku.net.Request.GetTurn;
import static gomoku.net.Request.PlayerList;
import static gomoku.net.Request.LeaveGame;
import static gomoku.net.Request.SkipChooseColor;

import java.util.HashMap;

import gomoku.logic.GomokuConfig;
import gomoku.logic.GomokuGame;
import gomoku.logic.GomokuGameListener;
import gomoku.logic.IllegalActionException;
import gomoku.logic.Player;
import gomoku.logic.Swap2;
import gomoku.net.BoardPacket;
import gomoku.net.GenericRequestPacket;
import gomoku.net.NotifyTurnPacket;
import gomoku.net.GameActionPacket;
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

    /** Connection ID for the first player */
    private int playerOneConnID;

    /** Connection ID for the second player */
    private int playerTwoConnID;

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
    public GomokuNetworkGame(GomokuServer gomokuServer, Server server, GomokuConfig config) {
        this.gomokuServer = gomokuServer;
        this.server = server;
        this.name = config.getName();
        game = new GomokuGame(config, false);
        game.addListener(this);
        isEnding = false;
        id = IDCOUNTER++;

        playerList = new HashMap<Integer, String>();
        spectators = new HashMap<Integer, String>();
    }

    @Override
    public void gameOver(int winner) {
        debug("Game Over!");
        broadcast(null, new VictoryPacket((short)(game.getTurn().getID())));
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
     * Called when a player wants to join the game. The player will receive an
     * ID automatically, and that ID will be returned from the function.
     *
     * @param conn
     *            the connection that wants to join the game
     * @param name
     *            the name of the player at the remote endpoint
     * @return the playercolor that the player received
     */
    public int join(Connection conn, String name) {
        int playerID = Player.NOPLAYER;
        if (playerOneConnID == 0) {
            playerOneConnID = conn.getID();
            game.getPlayerOne().setName(name);
            playerID = Player.PLAYERONE;
            info(name + " joined game " + this.name + " as player one.");
        } else if (playerTwoConnID == 0) {
            playerTwoConnID = conn.getID();
            game.getPlayerTwo().setName(name);
            playerID = Player.PLAYERTWO;
            info(name + " joined game " + this.name + " as player two.");
        } else {
            spectators.put(conn.getID(), name);
            info(name + " joined game " + this.name + " as spectator.");
        }
        playerList.put(conn.getID(), name);
        broadcast(conn, new PlayerListPacket(getPlayerList()));
        return playerID;
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
        if (conn.getID() == playerOneConnID)
            playerOneConnID = 0;
        else if (conn.getID() == playerTwoConnID)
            playerTwoConnID = 0;
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

        if (playerOneConnID == 0 && playerTwoConnID == 0 && spectators.isEmpty() && !isEnding) {
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
        if (playerOneConnID == 0)
            players[0] = "(none)";
        else
            players[0] = playerList.get(playerOneConnID);
        if (playerTwoConnID == 0)
            players[1] = "(none)";
        else
            players[1] = playerList.get(playerTwoConnID);

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
     * @see #handleBoardAction(Connection, GameActionPacket)
     * @see #handleGenericRequest(Connection, GenericRequestPacket)
     */
    public void received(Connection conn, Object obj) {

        if (obj instanceof GameActionPacket) {
            handleBoardAction(conn, (GameActionPacket) obj);
        } else if (obj instanceof GenericRequestPacket) {
            handleGenericRequest(conn, (GenericRequestPacket) obj);
        }
    }

    /**
     * Handles a board action and notifies other connections upon success
     *
     * @param conn
     *            The connection that sent us the packet
     * @param ppp
     *            The packet to process
     */
    private void handleBoardAction(Connection conn, GameActionPacket ppp) {

        int playerID = Player.NOPLAYER;
        if (playerOneConnID == conn.getID()) {
            playerID = Player.PLAYERONE;
        } else if (playerTwoConnID == conn.getID()) {
            playerID = Player.PLAYERTWO;
        } else { // player is a spectator. he cannot place
            return;
        }

        // not the players turn
        if (playerID != game.getTurn().getID()) {
            conn.sendTCP(new NotifyTurnPacket(game.getTurn().getID()));
            return;
        }

        try {
            ppp.action.doAction(game);
            broadcast(conn, ppp);
            ppp.action.confirmAction(game);
        } catch (IllegalActionException e) {
            // placement was not possible, update the board at client
            conn.sendTCP(new BoardPacket(game.getBoard()));
            error(e.getMessage());
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
            broadcast(null, new NotifyTurnPacket(game.getTurn().getID()));

        } else if (grp.getRequest() == GetTurn) {
            conn.sendTCP(new NotifyTurnPacket(game.getTurn().getID()));

        } else if (grp.getRequest() == PlayerList) {
            conn.sendTCP(new PlayerListPacket(getPlayerList()));

        } else if (grp.getRequest() == LeaveGame) {
            leave(conn, false);

        } else if (grp.getRequest() == SkipChooseColor) {
            Swap2 swap2 = game.getSwap2();
            if (swap2 != null) {
                swap2.nextState();
                broadcast(conn, grp);
            }
        } else {
            error("GenericRequestPacket of unknown type: " + grp.getRequest());
        }
    }

}
