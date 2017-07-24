package se.samuelandersson.gomoku.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.Player;
import se.samuelandersson.gomoku.net.CreateGamePacket;
import se.samuelandersson.gomoku.net.GameListPacket;
import se.samuelandersson.gomoku.net.HandshakeClientPacket;
import se.samuelandersson.gomoku.net.HandshakeServerPacket;
import se.samuelandersson.gomoku.net.InitialClientDataPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.JoinGamePacket;
import se.samuelandersson.gomoku.net.RegisterPackets;
import se.samuelandersson.gomoku.net.Request;

public class GomokuServer extends Listener
{
  private static final Logger log = LoggerFactory.getLogger(GomokuServer.class);

  /** The Kryonet server */
  private final Server server = new Server();

  /** The games that the server runs */
  public final Map<Integer, GomokuNetworkGame> games = new HashMap<>();

  /** The list of connected players */
  private final Map<Integer, String> playerList = new HashMap<>();

  /** The list of all spectators */
  private final Map<Integer, String> spectators = new HashMap<>();

  /** List that keeps track of which game contains which player */
  private final Map<Integer, GomokuNetworkGame> playerInGame = new HashMap<>();
  private int port;
  private boolean singleGameServer;

  /**
   * Create a new GomokuServer
   */
  public GomokuServer(int port, boolean singleGameServer)
  {
    RegisterPackets.register(server.getKryo());

    this.port = port;
    this.singleGameServer = singleGameServer;
    this.server.addListener(this);
  }

  /**
   * Start the server and begin listening on provided port
   */
  public void start() throws IOException
  {
    log.info("Starting server ...");
    server.start(); // new thread started
    server.bind(this.port);
    log.info("Server running on *:" + this.port);
  }

  /**
   * Stop the server thread
   */
  public void stop()
  {
    log.info("Exiting server ...");
    server.stop();
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
   * @param sourceConnection The connection that triggered this broadcast. May be null, in which case all server
   *          connections will receive the object.
   * @param object The object that will be broadcasted
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
          log.debug("Received InitialClientData: " + initialClientDataPacket);
        }

        handleInitialClientData(conn, initialClientDataPacket);
      }
      else if (obj instanceof HandshakeClientPacket)
      {
        final HandshakeClientPacket handshakeClientPacket = (HandshakeClientPacket) obj;
        if (log.isDebugEnabled())
        {
          log.debug("Received HandshakeClientPacket: " + handshakeClientPacket);
        }

        this.handleHandshakeClient(conn, handshakeClientPacket);
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
    else if (request == Request.JOIN_SINGLE_GAME_SERVER)
    {
      if (this.singleGameServer)
      {
        if (!this.games.isEmpty())
        {
          this.handleJoinGamePacket(connection,
                                    new JoinGamePacket(this.games.entrySet().iterator().next().getKey().intValue()));
        }
      }
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
  
  private void handleHandshakeClient(Connection conn, HandshakeClientPacket handshakeClientPacket)
  {
    boolean ready = !this.singleGameServer || !this.games.isEmpty();

    conn.sendTCP(new HandshakeServerPacket(this.singleGameServer, ready));
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
    String playerName = playerList.get(conn.getID());

    GomokuNetworkGame networkGame = new GomokuNetworkGame(this, server, cgp.getConfig());
    log.info(playerName + " created new game \"" + cgp.getConfig().getName() + "\".");

    games.put(networkGame.getID(), networkGame);
    playerInGame.put(conn.getID(), networkGame);
    Player player = networkGame.join(conn, playerName);

    GomokuGame game = networkGame.getGame();
    GomokuBoard board = game.getBoard();
    GomokuConfig config = game.getConfig();

    InitialServerDataPacket isdp = new InitialServerDataPacket(board,
                                                               config,
                                                               player.getColor(),
                                                               game.getCurrentTurnPlayer().getColor(),
                                                               networkGame.getPlayerList());
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
    Player player = game.join(conn, playerList.get(conn.getID()));

    GomokuBoard board = game.getGame().getBoard();
    List<Player> playerList = game.getPlayerList();

    Color turnColor = game.getGame().getCurrentTurnPlayer().getColor();
    GomokuConfig config = game.getGame().getConfig();

    InitialServerDataPacket isdp = new InitialServerDataPacket(board, config, player.getColor(), turnColor, playerList);
    this.sendTCP(conn, isdp);

    String playerName = this.playerList.get(conn.getID());
    if (player.getColor() == Color.NONE)
    {
      spectators.put(conn.getID(), playerName);
    }
  }

}
