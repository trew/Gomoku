package gomoku.server;

import gomoku.logic.Board;
import gomoku.net.CreateGamePacket;
import gomoku.net.GameListPacket;
import gomoku.net.GenericRequestPacket;
import gomoku.net.InitialClientDataPacket;
import gomoku.net.InitialServerDataPacket;
import gomoku.net.JoinGamePacket;

import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * The listener for connections to the server. It contains the server and the
 * board which it will manipulate during connection interactions.
 *
 * @author Samuel Andersson
 */
public class ServerListener extends Listener {

    /** The list of connected players */
    private HashMap<Integer, String> playerList;

    /** The list of all spectators */
    private HashMap<Integer, String> spectators;

    /** List that keeps track of which game contains which player */
    private HashMap<Integer, GomokuNetworkGame> playerInGame;

    /** The server in which this listener exists */
    private GomokuServer gomokuServer;

    /**
     * /** Create a listener for the server
     *
     * @param server
     *            The server which we exist in
     * @param board
     *            The board we'll manipulate
     */
    public ServerListener(GomokuServer server) {
        gomokuServer = server;
        playerList = new HashMap<Integer, String>();
        spectators = new HashMap<Integer, String>();
        playerInGame = new HashMap<Integer, GomokuNetworkGame>();
    }

    /**
     * Notification that a connection has disconnected. Remove any connected
     * players from our player lists.
     */
    @Override
    public void disconnected(Connection connection) {
        GomokuNetworkGame game = playerInGame.get(connection.getID());
        if (game != null) {
            game.disconnected(connection);
        }
        playerList.remove(connection.getID());
        spectators.remove(connection.getID());
        playerInGame.remove(connection.getID());
    }

    @Override
    public void received(Connection conn, Object obj) {
        GomokuNetworkGame game = playerInGame.get(conn.getID());
        if (game != null) {
            game.received(conn, obj);
        } else {
            // player hasn't joined a game yet.
            if (obj instanceof InitialClientDataPacket) {
                handleInitialClientData(conn, (InitialClientDataPacket) obj);
            } else if (obj instanceof CreateGamePacket) {
                handleCreateGamePacket(conn, (CreateGamePacket) obj);
            } else if (obj instanceof JoinGamePacket) {
                handleJoinGamePacket(conn, (JoinGamePacket) obj);
            } else if (obj instanceof GenericRequestPacket) {
                handleGenericRequestPacket(conn, (GenericRequestPacket) obj);
            }
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
        playerList.put(conn.getID(), playerName);

        // send a list of open games
        conn.sendTCP(new GameListPacket(gomokuServer.games));
    }

    private void handleCreateGamePacket(Connection conn, CreateGamePacket cgp) {
        // TODO: Fix option to choose between white, black and spectator
        int playerColor = 0;
        String playerName = playerList.get(conn.getID());
        if (cgp.ownerReceivesBlack) {
            playerColor = Board.BLACKPLAYER;
        } else {
            playerColor = Board.WHITEPLAYER;
        }
        GomokuNetworkGame newGame = new GomokuNetworkGame(gomokuServer,
                cgp.name, cgp.width, cgp.height);

        gomokuServer.games.put(newGame.getID(), newGame);
        playerInGame.put(conn.getID(), newGame);
        playerColor = newGame.join(conn, playerName);

        InitialServerDataPacket isdp = new InitialServerDataPacket(newGame
                .getGame().getBoard(), playerColor, newGame.getGame().getTurn()
                .getColor(), newGame.getPlayerList());
        conn.sendTCP(isdp);
    }

    private void handleJoinGamePacket(Connection conn, JoinGamePacket jgp) {
        GomokuNetworkGame game = gomokuServer.games.get(jgp.gameID);
        if (game == null)
            return;
        playerInGame.put(conn.getID(), game);
        int playerColor = game.join(conn, playerList.get(conn.getID()));

        Board board = game.getGame().getBoard();
        String[] playerList = game.getPlayerList();

        int turn = game.getGame().getTurn().getColor();
        InitialServerDataPacket isdp = new InitialServerDataPacket(board,
                playerColor, turn, playerList);
        conn.sendTCP(isdp);

        if (playerColor == Board.NOPLAYER) {
            spectators.put(conn.getID(), this.playerList.get(conn.getID()));
        }
    }

    private void handleGenericRequestPacket(Connection conn,
            GenericRequestPacket grp) {
    }
}
