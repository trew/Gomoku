package gomoku.server;

import gomoku.logic.GomokuGame;
import gomoku.net.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintStream;

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
     * The game that the server runs
     */
    public GomokuGame game;

    /**
     * The JFrame if we're using Swing console
     */
    private JFrame frame;

    private static GomokuServer gomokuserver = null;

    /**
     * Create a new TTTServer
     */
    public GomokuServer() {
        server = new Server();
        game = new GomokuGame(15, 15);
        listener = new ServerListener(this);
        frame = null;
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
        info("GomokuServer", "Exiting server");
        server.stop();
        System.exit(0);
    }

    /**
     * Reset the board and broadcast change to all connections
     */
    public void resetGame() {
        game.reset();
        broadcast(null, new BoardPacket(game.getBoard()));
        debug("GomokuServer", "Board was reset");
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
        try {
            jsap.registerParameter(swingOpt);
            jsap.registerParameter(portOpt);

            JSAPResult config = jsap.parse(args);
            SWING = config.getBoolean("swing");
            PORT = config.getInt("port");
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
        Log.set(LEVEL_DEBUG);

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
                MenuItem resetItem = new MenuItem("Reset board");
                resetItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        gomokuserver.resetGame();
                    }
                });
                menu.add(resetItem);
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
