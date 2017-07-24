package se.samuelandersson.gomoku.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.GomokuGameListener;
import se.samuelandersson.gomoku.Player;
import se.samuelandersson.gomoku.event.GameEvent;
import se.samuelandersson.gomoku.event.GameOverEvent;
import se.samuelandersson.gomoku.exception.IllegalActionException;
import se.samuelandersson.gomoku.impl.GomokuGameImpl;
import se.samuelandersson.gomoku.net.BoardPacket;
import se.samuelandersson.gomoku.net.GameActionPacket;
import se.samuelandersson.gomoku.net.NotifyTurnPacket;
import se.samuelandersson.gomoku.net.PlayerListPacket;
import se.samuelandersson.gomoku.net.Request;
import se.samuelandersson.gomoku.net.VictoryPacket;

/**
 * A game of Gomoku, delegated by the GomokuServer.
 *
 * @author Samuel Andersson
 *
 */
public class GomokuNetworkGame implements GomokuGameListener
{
  private static final Logger log = LoggerFactory.getLogger(GomokuNetworkGame.class);

  /** Global ID counter for games */
  private static int IDCOUNTER = 1;

  /** Connection ID for the first player */
  private int playerOneConnID;

  /** Connection ID for the second player */
  private int playerTwoConnID;

  /** The list of connected players */
  private Map<Integer, Player> playerList;

  /** The list of all spectators */
  private Map<Integer, Player> spectators;

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
   *          the server holding this game
   * @param server
   *          the kryonet server
   * @param name
   *          the name of the game
   * @param width
   *          the width of the board
   * @param height
   *          the height of the board
   */
  public GomokuNetworkGame(GomokuServer gomokuServer, Server server, GomokuConfig config)
  {
    this.gomokuServer = gomokuServer;
    this.server = server;
    this.name = config.getName();
    game = new GomokuGameImpl(config);
    game.addListener(this);
    isEnding = false;
    id = IDCOUNTER++;

    playerList = new HashMap<Integer, Player>();
    spectators = new HashMap<Integer, Player>();
  }

  @Override
  public void preEvent(GameEvent event)
  {
  }

  @Override
  public void onEvent(GameEvent event)
  {
    if (event instanceof GameOverEvent)
    {
      GameOverEvent gameOverEvent = (GameOverEvent) event;
      broadcast(null, new VictoryPacket(gameOverEvent.getColor()));
    }
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
  public void broadcast(Connection conn, Object obj)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Broadcasting: {}", obj.toString());
    }

    for (Integer id : playerList.keySet())
    {
      if (conn == null || id != conn.getID())
      {
        server.sendToTCP(id, obj);
      }
    }
  }

  /**
   * Called when a player wants to join the game. The player will receive an
   * ID automatically, and that ID will be returned from the function.
   *
   * @param conn
   *          the connection that wants to join the game
   * @param player
   *          the player at the remote endpoint
   * @return the player object of the joined player
   */
  public Player join(Connection conn, String playerName)
  {
    Player player = new Player(playerName, Color.NONE);
    if (playerOneConnID == 0)
    {
      playerOneConnID = conn.getID();
      player.setColor(Color.BLACK);
      game.getPlayerOne().setFrom(player);
      log.info(player + " joined game " + this.name + " as player one (black).");
    }
    else if (playerTwoConnID == 0)
    {
      playerTwoConnID = conn.getID();
      player.setColor(Color.WHITE);
      game.getPlayerTwo().setFrom(player);
      log.info(player + " joined game " + this.name + " as player two (white).");
    }
    else
    {
      spectators.put(conn.getID(), player);
      log.info(player + " joined game " + this.name + " as spectator.");
    }

    playerList.put(conn.getID(), player);
    broadcast(conn, new PlayerListPacket(getPlayerList()));

    return player;
  }

  /**
   * A player disconnected or left the game
   *
   * @param conn
   *          the connection
   * @param disconnect
   *          whether the player disconnected
   */
  private void leave(Connection conn, boolean disconnect)
  {
    gomokuServer.leaveGame(conn.getID());

    if (conn.getID() == playerOneConnID)
    {
      playerOneConnID = 0;
    }
    else if (conn.getID() == playerTwoConnID)
    {
      playerTwoConnID = 0;
    }
    else
    {
      spectators.remove(conn.getID());
    }

    // if we actually removed a player, broadcast change to rest
    if (playerList.remove(conn.getID()) != null)
    {
      if (disconnect)
      {
        log.info(conn.getID() + " disconnected from game " + name);
      }
      else
      {
        log.info(conn.getID() + " left the game " + name);
      }

      broadcast(conn, new PlayerListPacket(getPlayerList()));
    }

    if (playerOneConnID == 0 && playerTwoConnID == 0 && spectators.isEmpty() && !isEnding)
    {
      log.info("Ending game: " + name);
      isEnding = true;
      gomokuServer.endGame(this);
    }
  }

  /**
   * Returns the ID of this game
   *
   * @return the ID of this game
   */
  public int getID()
  {
    return id;
  }

  /**
   * Returns the name of this game
   *
   * @return the name of this game
   */
  public String getName()
  {
    return name;
  }

  /**
   * Returns the game logic object
   *
   * @return the game logic object
   */
  public GomokuGame getGame()
  {
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
  public List<Player> getPlayerList()
  {
    List<Player> players = new ArrayList<>();

    // add black to the first position
    if (playerOneConnID != 0)
    {
      players.add(playerList.get(playerOneConnID));
    }
    if (playerTwoConnID != 0)
    {
      players.add(playerList.get(playerTwoConnID));
    }

    for (Player p : spectators.values())
    {
      players.add(p);
    }

    return players;
  }

  /**
   * Called when a player disconnects from this game. Will remove the player
   * from any lists and set blackID and whiteID to 0 if needed. If no players
   * (or spectators) are left in the game, the game will end and be closed.
   *
   * @param conn
   *          the disconnected connection
   */
  public void disconnected(Connection conn)
  {
    leave(conn, true);
  }

  /**
   * Called when an object has been received from the remote end of the
   * connection. This method dispatches to relevant methods depending on the
   * packet type.
   *
   * @param conn
   *          The connection that sent us the packet
   * @param obj
   *          The packet to process
   * @see #handleBoardAction(Connection, GameActionPacket)
   * @see #handleRequest(Connection, GenericRequestPacket)
   */
  public void received(Connection conn, Object obj)
  {
    if (obj instanceof GameActionPacket)
    {
      final GameActionPacket gameActionPacket = (GameActionPacket) obj;
      if (log.isDebugEnabled())
      {
        log.debug("Received GameAction: " + gameActionPacket.getAction().getColor() + ": " +
                  gameActionPacket.getAction().getClass().getSimpleName());
      }
      handleBoardAction(conn, gameActionPacket);
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

  /**
   * Handles a board action and notifies other connections upon success
   *
   * @param conn
   *          The connection that sent us the packet
   * @param ppp
   *          The packet to process
   */
  private void handleBoardAction(Connection conn, GameActionPacket ppp)
  {
    Color playerColor = Color.NONE;
    if (playerOneConnID == conn.getID())
    {
      playerColor = Color.BLACK;
    }
    else if (playerTwoConnID == conn.getID())
    {
      playerColor = Color.WHITE;
    }
    else
    { // player is a spectator. he cannot place
      log.error("Board action received from spectator player", new RuntimeException());
      return;
    }

    // not the players turn
    if (playerColor != game.getCurrentTurnPlayer().getColor())
    {
      throw new IllegalStateException("Unable to handle game action from player: " + playerColor);
    }

    try
    {
      ppp.getAction().doAction(game);
      ppp.getAction().confirmAction(game);
      broadcast(conn, ppp);
    }
    catch (IllegalActionException e)
    {
      // placement was not possible, update the board at client
      log.error("Illegal Action", e);
      this.gomokuServer.sendTCP(conn, new BoardPacket(game.getBoard()));
    }
  }

  /**
   * Handle a generic request, such as BoardUpdate or ClearBoard i.e.
   *
   * @param conn
   *          The connection that sent us the packet
   * @param grp
   *          The packet to process
   */
  private void handleRequest(Connection connection, Request request)
  {
    if (request == Request.UPDATE_BOARD)
    {
      this.gomokuServer.sendTCP(connection, new BoardPacket(game.getBoard()));
    }
    else if (request == Request.CLEAR_BOARD)
    {
      game.reset();
      broadcast(null, new BoardPacket(game.getBoard()));
      broadcast(null, new NotifyTurnPacket(game.getCurrentTurnPlayer().getColor()));
    }
    else if (request == Request.GET_TURN)
    {
      this.gomokuServer.sendTCP(connection, new NotifyTurnPacket(game.getCurrentTurnPlayer().getColor()));
    }
    else if (request == Request.GET_PLAYER_LIST)
    {
      this.gomokuServer.sendTCP(connection, new PlayerListPacket(getPlayerList()));
    }
    else if (request == Request.LEAVE_GAME)
    {
      leave(connection, false);
    }
    else
    {
      log.error("Request of unknown type: " + request);
    }
  }

}
