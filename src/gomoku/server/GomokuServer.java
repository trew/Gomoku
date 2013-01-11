package gomoku.server;

import gomoku.net.*;

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
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import javax.swing.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import static com.esotericsoftware.minlog.Log.*;
import com.martiansoftware.jsap.*;

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
public class GomokuServer {

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

    private static int LOGLEVEL = Log.LEVEL_INFO;

    /**
     * The Kryonet server
     */
    private Server server;

    /**
     * The listener for the open connections. Each connection will have its own
     * listener.
     */
    private ServerListener listener;

    /**
     * The games that the server runs
     */
    public HashMap<Integer, GomokuNetworkGame> games;

    /**
     * The JFrame if we're using Swing console
     */
    private JFrame frame;

    private static GomokuServer gomokuserver = null;

    /**
     * Create a new GomokuServer
     */
    public GomokuServer() {
        server = new Server();
        listener = new ServerListener(this);
        games = new HashMap<Integer, GomokuNetworkGame>();
        frame = null;
    }

    public Server getServer() {
        return server;
    }

    public void endGame(GomokuNetworkGame game) {
        games.remove(game.getID());
    }

    /**
     * Initialize the server, add the ServerListener and register kryo classes.
     *
     * @see ServerListener
     */
    public void init() {
        server.addListener(listener);

        RegisterPackets.register(server.getKryo());
    }

    /**
     * Start the server and begin listening on provided port
     *
     * @see #PORT
     */
    public void start() {
        info("GomokuServer", "Starting server ...");
        server.start();
        try {
            server.bind(PORT);
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
            if (loglevel == "trace" || loglevel == "1")
                LOGLEVEL = Log.LEVEL_TRACE;
            else if (loglevel == "debug" || loglevel == "2")
                LOGLEVEL = Log.LEVEL_DEBUG;
            else if (loglevel == "warn" || loglevel == "4")
                LOGLEVEL = Log.LEVEL_WARN;
            else if (loglevel == "error" || loglevel == "5")
                LOGLEVEL = Log.LEVEL_ERROR;
            else if (loglevel == "none" || loglevel == "0")
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

        gomokuserver = new GomokuServer();
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

        gomokuserver.init();
        gomokuserver.start();
    }

}
