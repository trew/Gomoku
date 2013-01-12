package gomoku.server;

import gomoku.logic.Board;
import gomoku.net.CreateGamePacket;
import gomoku.net.GameListPacket;
import gomoku.net.InitialClientDataPacket;
import gomoku.net.InitialServerDataPacket;
import gomoku.net.JoinGamePacket;
import gomoku.net.RegisterPackets;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import static com.esotericsoftware.minlog.Log.*;

/**
 * Server of the Gomoku game<br />
 * <br />
 * Possible arguments<br />
 * <b>--port</b> <i>PORT</i> - The port number which we'll run the server on<br />
 * <b>--swing</b> - Whether we should run with swing or use standard console.
 * (Swing is always used on windows)<br />
 *
 * @author Samuel Andersson
 */
public class GomokuServer extends Listener {

    /**
     * The port which this server is listening on. Can be set by providing
     * --port to the application command line
     *
     * @see #parseArgs(String[])
     */
    private static int PORT;

    /**
     * Whether we are going to use Swing as our console
     *
     * @see #parseArgs(String[])
     */
    private static boolean SWING;

    /** The log level for the server */
    private static int LOGLEVEL = Log.LEVEL_INFO;

    /** The Kryonet server */
    private Server server;

    /** The games that the server runs */
    public HashMap<Integer, GomokuNetworkGame> games;

    /** The list of connected players */
    private HashMap<Integer, String> playerList;

    /** The list of all spectators */
    private HashMap<Integer, String> spectators;

    /** List that keeps track of which game contains which player */
    private HashMap<Integer, GomokuNetworkGame> playerInGame;

    /** The JFrame if we're using Swing console */
    private JFrame frame;

    /**
     * Create a new GomokuServer
     */
    public GomokuServer() {
        server = new Server();
        games = new HashMap<Integer, GomokuNetworkGame>();
        frame = null;
        playerList = new HashMap<Integer, String>();
        spectators = new HashMap<Integer, String>();
        playerInGame = new HashMap<Integer, GomokuNetworkGame>();

    }

    /**
     * Initialize the server, add the ServerListener and register kryo classes.
     *
     * @see ServerListener
     */
    public void init() {
        server.addListener(this);

        RegisterPackets.register(server.getKryo());
    }

    /**
     * Start the server and begin listening on provided port
     *
     * @see #PORT
     */
    public void start() {
        info("GomokuServer", "Starting server ...");
        server.start(); // new thread started
        try {
            server.bind(PORT);
            info("GomokuServer", "Server running on *:" + PORT);
        } catch (IOException e) {
            if (TRACE)
                trace("GomokuServer", e);
            else
                error("GomokuServer", "Error: " + e.getMessage());
        }
    }

    /**
     * Stop the server and exit cleanly.
     */
    public void exit() {
        info("GomokuServer", "Exiting server ...");
        server.stop();
        System.exit(0);
    }

    public void endGame(GomokuNetworkGame game) {
        games.remove(game.getID());
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
    public void broadcast(Connection sourceConnection, Object object) {
        debug("GomokuServer", "Broadcasting "
                + object.getClass().getSimpleName());
        if (sourceConnection == null) {
            server.sendToAllTCP(object);
        } else {
            server.sendToAllExceptTCP(sourceConnection.getID(), object);
        }
    }

    /**
     * Notification that a connection has disconnected. Remove any connected
     * players from our player lists.
     */
    @Override
    public void disconnected(Connection connection) {
        info("GomokuServer", playerList.get(connection.getID()) + "("
                + connection.getID() + ") disconnected.");
        GomokuNetworkGame game = playerInGame.get(connection.getID());
        if (game != null) {
            game.disconnected(connection);
        }
        playerList.remove(connection.getID());
        spectators.remove(connection.getID());
        playerInGame.remove(connection.getID());
    }

    /**
     * Called when an object has been received from the remote end of this
     * connection. If the player on the remote end has joined a game, the object
     * will be sent to {@link GomokuNetworkGame#received(Connection, Object)},
     * and handled there.
     */
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
        String ip = conn.getRemoteAddressTCP().getAddress().getHostAddress();
        info("GomokuServer", playerName + "(" + ip + ", " + conn.getID()
                + ") has connected.");
        playerList.put(conn.getID(), playerName);

        // send a list of open games
        conn.sendTCP(new GameListPacket(games));
    }

    /**
     * Handles how a received CreateGamePacket should be treated.
     *
     * @param conn
     *            the connection that sent the CreateGamePacket
     * @param cgp
     *            the CreateGamePacket
     */
    private void handleCreateGamePacket(Connection conn, CreateGamePacket cgp) {
        // TODO: Fix option to choose between white, black and spectator
        int playerColor = 0;
        String playerName = playerList.get(conn.getID());
        if (cgp.ownerReceivesBlack) {
            playerColor = Board.BLACKPLAYER;
        } else {
            playerColor = Board.WHITEPLAYER;
        }
        GomokuNetworkGame newGame = new GomokuNetworkGame(this, server,
                cgp.name, cgp.width, cgp.height);
        info("GomokuServer", playerName + " created new game \"" + cgp.name
                + "\".");

        games.put(newGame.getID(), newGame);
        playerInGame.put(conn.getID(), newGame);
        playerColor = newGame.join(conn, playerName);

        InitialServerDataPacket isdp = new InitialServerDataPacket(newGame
                .getGame().getBoard(), playerColor, newGame.getGame().getTurn()
                .getColor(), newGame.getPlayerList());
        conn.sendTCP(isdp);
    }

    /**
     * Handles how a received JoinGamePacket should be treated.
     *
     * @param conn
     *            the connection that sent the JoinGamePacket
     * @param jgp
     *            the JoinGamePacket
     */
    private void handleJoinGamePacket(Connection conn, JoinGamePacket jgp) {
        GomokuNetworkGame game = games.get(jgp.gameID);
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

        String playerName = this.playerList.get(conn.getID());
        if (playerColor == Board.NOPLAYER) {
            spectators.put(conn.getID(), playerName);
        }
    }

    /**
     * Parse command line arguments that was passed to the application upon
     * startup.
     *
     * @param args
     *            The arguments passed to the application
     */
    public static void parseArgs(String[] args) {
        JSAP jsap = new JSAP();
        FlaggedOption swingOpt = new FlaggedOption("swing")
                .setStringParser(JSAP.BOOLEAN_PARSER).setDefault("true")
                .setLongFlag("swing");
        FlaggedOption portOpt = new FlaggedOption("port")
                .setStringParser(JSAP.INTEGER_PARSER).setDefault("9123")
                .setLongFlag("port");
        FlaggedOption logOpt = new FlaggedOption("loglevel");
        logOpt.setDefault("info").setLongFlag("loglevel");

        try {
            jsap.registerParameter(swingOpt);
            jsap.registerParameter(portOpt);
            jsap.registerParameter(logOpt);

            JSAPResult config = jsap.parse(args);
            SWING = config.getBoolean("swing");
            PORT = config.getInt("port");
            String loglevel = config.getString("loglevel").toLowerCase();
            if (loglevel.equals("trace") || loglevel.equals("1"))
                LOGLEVEL = Log.LEVEL_TRACE;
            else if (loglevel.equals("debug") || loglevel.equals("2"))
                LOGLEVEL = Log.LEVEL_DEBUG;
            else if (loglevel.equals("warn") || loglevel.equals("4"))
                LOGLEVEL = Log.LEVEL_WARN;
            else if (loglevel.equals("error") || loglevel.equals("5"))
                LOGLEVEL = Log.LEVEL_ERROR;
            else if (loglevel.equals("none") || loglevel.equals("0"))
                LOGLEVEL = Log.LEVEL_NONE;
        } catch (JSAPException e) {
            if (TRACE)
                trace("GomokuServer", e);
            else
                error("GomokuServer",
                        "Error parsing arguments: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * The main entry point of the server
     *
     * @param args
     *            Any arguments passed to the server
     */
    public static void main(String[] args) {
        Log.set(LOGLEVEL);
        parseArgs(args);

        // override if using windows
        if (System.getProperty("os.name").startsWith("Windows")) {
            // SWING = true;
        }

        final GomokuServer gomokuserver = new GomokuServer();
        if (SWING) {
            gomokuserver.frame = new JFrame();
            gomokuserver.frame.setAutoRequestFocus(true);
            gomokuserver.frame.add(new JLabel(" Output "), BorderLayout.NORTH);

            JTextArea ta = new JTextArea();
            Console co = new Console(ta);
            PrintStream ps = new PrintStream(co);
            System.setOut(ps);
            System.setErr(ps);

            gomokuserver.frame.add(new JScrollPane(ta));

            gomokuserver.frame.setMinimumSize(new Dimension(350, 300));

            TrayIcon icon = null;
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                Image image = Toolkit
                        .getDefaultToolkit()
                        .getImage(
                                "C:/Users/samuel/Documents/dev/gomoku/src/res/tray.gif");

                PopupMenu menu = new PopupMenu();
                MenuItem exitItem = new MenuItem("Exit server");
                exitItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gomokuserver.exit();
                    }
                });
                menu.add(exitItem);
                icon = new TrayIcon(image, "Server", menu);
                icon.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        gomokuserver.frame.setExtendedState(Frame.NORMAL);
                        gomokuserver.frame.setVisible(true);
                        gomokuserver.frame.toFront();
                    }
                });
                try {
                    tray.add(icon);
                } catch (AWTException e1) {
                    e1.printStackTrace();
                }
            }
            gomokuserver.frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    gomokuserver.exit();
                    System.exit(0);
                }

                @Override
                public void windowIconified(WindowEvent e) {
                    gomokuserver.frame.setVisible(false);
                }
            });
            gomokuserver.frame.pack();
            gomokuserver.frame.setVisible(true);
        }

        Log.set(LOGLEVEL);

        String logName = "info";
        if (LOGLEVEL == 1)
            logName = "trace";
        else if (LOGLEVEL == 2)
            logName = "debug";
        info("GomokuServer", "Logging level set to \"" + logName + "\".");

        gomokuserver.init();
        gomokuserver.start();
    }

}
