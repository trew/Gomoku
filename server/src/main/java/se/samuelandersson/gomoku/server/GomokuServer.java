package se.samuelandersson.gomoku.server;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import ch.qos.logback.core.net.server.ServerListener;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.Player;
import se.samuelandersson.gomoku.net.CreateGamePacket;
import se.samuelandersson.gomoku.net.GameListPacket;
import se.samuelandersson.gomoku.net.InitialClientDataPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.JoinGamePacket;
import se.samuelandersson.gomoku.net.RegisterPackets;
import se.samuelandersson.gomoku.net.Request;

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
public class GomokuServer extends Listener
{
  private static final Logger log = LoggerFactory.getLogger(GomokuServer.class);

  /**
   * The port which this server is listening on. Can be set by providing
   * --port to the application command line
   *
   * @see #parseArgs(String[])
   */
  private static int PORT;

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

  /**
   * Create a new GomokuServer
   */
  public GomokuServer()
  {
    server = new Server();
    games = new HashMap<Integer, GomokuNetworkGame>();
    playerList = new HashMap<Integer, String>();
    spectators = new HashMap<Integer, String>();
    playerInGame = new HashMap<Integer, GomokuNetworkGame>();

  }

  /**
   * Initialize the server, add the ServerListener and register kryo classes.
   *
   * @see ServerListener
   */
  public void init()
  {
    server.addListener(this);

    RegisterPackets.register(server.getKryo());
  }

  /**
   * Start the server and begin listening on provided port
   *
   * @see #PORT
   */
  public void start()
  {
    log.info("Starting server ...");
    server.start(); // new thread started
    try
    {
      server.bind(PORT);
      log.info("Server running on *:" + PORT);
    }
    catch (IOException e)
    {
      log.error("Error", e);
      exit();
    }
  }

  /**
   * Stop the server and exit cleanly.
   */
  public void exit()
  {
    log.info("Exiting server ...");
    server.stop();
    System.exit(0);
  }

  public void leaveGame(int connID)
  {
    playerInGame.remove(connID);
  }

  public void endGame(GomokuNetworkGame game)
  {
    games.remove(game.getID());
  }

  /**
   * Broadcast a packet to all connections except provided source. We won't
   * send to the source connection because that client has already made
   * necessary changes.
   *
   * @param sourceConnection
   *          The connection that triggered this broadcast
   * @param object
   *          The object that will be broadcasted
   */
  public void broadcast(Connection sourceConnection, Object object)
  {
    log.debug("Broadcasting {}", object);
    if (sourceConnection == null)
    {
      server.sendToAllTCP(object);
    }
    else
    {
      server.sendToAllExceptTCP(sourceConnection.getID(), object);
    }
  }

  public void sendTCP(Connection connection, Object obj)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Sending {}", obj);
    }

    connection.sendTCP(obj);
  }

  /**
   * Notification that a connection has disconnected. Remove any connected
   * players from our player lists.
   */
  @Override
  public void disconnected(Connection connection)
  {
    log.info(playerList.get(connection.getID()) + "(" + connection.getID() + ") disconnected.");
    GomokuNetworkGame game = playerInGame.get(connection.getID());
    if (game != null)
    {
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
  public void received(Connection conn, Object obj)
  {
    GomokuNetworkGame game = playerInGame.get(conn.getID());
    if (game != null)
    {
      game.received(conn, obj);
    }
    else
    {
      // player hasn't joined a game yet.
      if (obj instanceof InitialClientDataPacket)
      {
        final InitialClientDataPacket initialClientDataPacket = (InitialClientDataPacket) obj;
        if (log.isDebugEnabled())
        {
          log.debug("Received InitialClientData: " + initialClientDataPacket.getName());
        }

        handleInitialClientData(conn, initialClientDataPacket);
      }
      else if (obj instanceof CreateGamePacket)
      {
        final CreateGamePacket createGamePacket = (CreateGamePacket) obj;
        if (log.isDebugEnabled())
        {
          log.debug("Received CreateGame: " + createGamePacket.getConfig().toString());
        }

        handleCreateGamePacket(conn, createGamePacket);
      }
      else if (obj instanceof JoinGamePacket)
      {
        final JoinGamePacket joinGamePacket = (JoinGamePacket) obj;
        if (log.isDebugEnabled())
        {
          log.debug("Received joinGame: " + joinGamePacket.gameID);
        }

        handleJoinGamePacket(conn, joinGamePacket);
      }
      else if (obj instanceof Request)
      {
        final Request request = (Request) obj;
        if (log.isDebugEnabled())
        {
          log.debug("Received Request: " + request);
        }

        handleRequest(conn, request);
      }
    }
  }

  protected void handleRequest(Connection connection, Request request)
  {
    if (request == Request.GET_GAME_LIST)
    {
      // send a list of open games
      this.sendTCP(connection, this.createGameListPacket());
    }
  }

  protected GameListPacket createGameListPacket()
  {
    int[] gameID;
    String[] gameName;
    if (this.games == null)
    {
      gameID = new int[0];
      gameName = new String[0];
    }
    else
    {
      gameID = new int[this.games.size()];
      gameName = new String[this.games.size()];
      int i = 0;

      for (final GomokuNetworkGame g : this.games.values())
      {
        gameID[i] = g.getID();
        gameName[i] = g.getName();
        i++;
      }
    }

    return new GameListPacket(gameID, gameName);
  }

  /**
   * This packet is treated as the confirmation that the client has connected
   * and wants to play. This function will send a GameList back to the client
   * with games he can choose to play in or spectate.
   *
   * @param conn
   *          The connection that sent us the packet
   * @param icdp
   *          The initial data from the client
   */
  private void handleInitialClientData(Connection conn, InitialClientDataPacket icdp)
  {
    String playerName = icdp.getName();
    String ip = conn.getRemoteAddressTCP().getAddress().getHostAddress();
    log.info(playerName + "(" + ip + ", " + conn.getID() + ") has connected.");
    playerList.put(conn.getID(), playerName);
  }

  /**
   * Handles how a received CreateGamePacket should be treated.
   *
   * @param conn
   *          the connection that sent the CreateGamePacket
   * @param cgp
   *          the CreateGamePacket
   */
  private void handleCreateGamePacket(Connection conn, CreateGamePacket cgp)
  {
    // TODO: Fix option to choose between white, black and spectator
    int playerID = 0;
    String playerName = playerList.get(conn.getID());
    playerID = Player.PLAYERONE;

    GomokuNetworkGame networkGame = new GomokuNetworkGame(this, server, cgp.getConfig());
    log.info(playerName + " created new game \"" + cgp.getConfig().getName() + "\".");

    games.put(networkGame.getID(), networkGame);
    playerInGame.put(conn.getID(), networkGame);
    playerID = networkGame.join(conn, playerName);

    GomokuGame game = networkGame.getGame();
    GomokuBoard board = game.getBoard();
    GomokuConfig config = game.getConfig();

    InitialServerDataPacket isdp = new InitialServerDataPacket(board,
                                                               config,
                                                               playerID,
                                                               game.getCurrentTurnPlayer().getID(),
                                                               networkGame.getPlayerList(),
                                                               game.getPlayerOne().getColor(),
                                                               game.getPlayerTwo().getColor());
    this.sendTCP(conn, isdp);

    // broadcast all games
    broadcast(conn, this.createGameListPacket());
  }

  /**
   * Handles how a received JoinGamePacket should be treated.
   *
   * @param conn
   *          the connection that sent the JoinGamePacket
   * @param jgp
   *          the JoinGamePacket
   */
  private void handleJoinGamePacket(Connection conn, JoinGamePacket jgp)
  {
    GomokuNetworkGame game = games.get(jgp.gameID);

    if (game == null)
    {
      return;
    }
    playerInGame.put(conn.getID(), game);
    int playerID = game.join(conn, playerList.get(conn.getID()));

    GomokuBoard board = game.getGame().getBoard();
    String[] playerList = game.getPlayerList();

    int turnID = game.getGame().getCurrentTurnPlayer().getID();
    GomokuConfig config = game.getGame().getConfig();

    GomokuGame gomGame = game.getGame();
    InitialServerDataPacket isdp = new InitialServerDataPacket(board,
                                                               config,
                                                               playerID,
                                                               turnID,
                                                               playerList,
                                                               gomGame.getPlayerOne().getColor(),
                                                               gomGame.getPlayerTwo().getColor());
    this.sendTCP(conn, isdp);

    String playerName = this.playerList.get(conn.getID());
    if (playerID == Player.NOPLAYER)
    {
      spectators.put(conn.getID(), playerName);
    }
  }

  /**
   * Parse command line arguments that was passed to the application upon
   * startup.
   *
   * @param args
   *          The arguments passed to the application
   */
  public static void parseArgs(String[] args)
  {
    JSAP jsap = new JSAP();
    FlaggedOption portOpt = new FlaggedOption("port").setStringParser(JSAP.INTEGER_PARSER)
                                                     .setDefault("9123")
                                                     .setLongFlag("port");

    try
    {
      jsap.registerParameter(portOpt);

      JSAPResult config = jsap.parse(args);
      PORT = config.getInt("port");
    }
    catch (JSAPException e)
    {
      if (log.isTraceEnabled())
      {
        log.trace("Error", e);
      }
      else
      {
        log.error("Error parsing arguments: " + e.getMessage());
      }

      System.exit(-1);
    }
  }

  /**
   * The main entry point of the server
   *
   * @param args
   *          Any arguments passed to the server
   */
  public static void main(String[] args)
  {
    parseArgs(args);

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        log.info("Shutting down...");
      }
    }));

    final GomokuServer gomokuserver = new GomokuServer();
    gomokuserver.init();
    gomokuserver.start();
  }

}
