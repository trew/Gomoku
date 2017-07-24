package se.samuelandersson.gomoku.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.samuelandersson.gomoku.ActionRecorder;
import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.GomokuGameListener;
import se.samuelandersson.gomoku.Player;
import se.samuelandersson.gomoku.event.GameEvent;
import se.samuelandersson.gomoku.event.GameOverEvent;
import se.samuelandersson.gomoku.event.SetPlayerTurnEvent;

/**
 * Contains game logic for Gomoku game. The game keeps track of the board, the
 * players and whose turn it is. It will also have methods indicating victory or
 * defeat.
 *
 * @author Samuel Andersson
 */
public class GomokuGameImpl implements GomokuGame
{
  private static final Logger log = LoggerFactory.getLogger(GomokuGameImpl.class);

  /** The board */
  private GomokuBoard board;

  /** Whose turn it is */
  Player turn;

  /** The first player */
  private Player playerOne;

  /** The second player */
  private Player playerTwo;

  boolean gameOver;

  private GomokuConfig config;

  /** The recorder of this game */
  protected ActionRecorder actionRecorder;

  private final List<GomokuGameListener> listeners = new ArrayList<>();

  /**
   * Create a new game with set width and height
   *
   * @param width
   *          the width of the board
   * @param height
   *          the height of the board
   */
  public GomokuGameImpl(GomokuConfig config)
  {
    this(new BoardImpl(config.getWidth(), config.getHeight()), config);
  }

  /**
   * Create a new game from a board
   *
   * @param board
   *          The board
   */
  public GomokuGameImpl(GomokuBoard board, GomokuConfig config)
  {
    this.board = board;

    playerOne = new Player("", Color.BLACK);
    playerTwo = new Player("", Color.WHITE);
    turn = playerOne;
    gameOver = false;

    // game rules
    this.config = config;
  }

  /**
   * Reset the game and set player turn to red
   */
  @Override
  public void reset()
  {
    if (log.isDebugEnabled())
    {
      log.debug("Resetting game");
    }

    board.reset();
    turn = playerOne;
    gameOver = false;
  }

  @Override
  public GomokuConfig getConfig()
  {
    return config;
  }

  @Override
  public void checkBoard(int x, int y)
  {
    /*
     * The algorithm will check four lines for victory based of the changed
     * position. Horizontally, vertically and two diagonal rows.
     */
    Player player = getPieceOwner(x, y);
    Color color = player.getColor();
    boolean victory = false;

    // horizontal check
    int length = board.count(color, x, y, 1, 0);
    if (length == config.getVictoryLength())
    {
      victory = true;
    }
    else if (config.getAllowOverlines() && length > config.getVictoryLength())
    {
      victory = true;
    }

    // vertical check
    length = board.count(color, x, y, 0, 1);
    if (length == config.getVictoryLength())
    {
      victory = true;
    }
    else if (config.getAllowOverlines() && length > config.getVictoryLength())
    {
      victory = true;
    }

    // topleft diagonal check
    length = board.count(color, x, y, 1, 1);
    if (length == config.getVictoryLength())
    {
      victory = true;
    }
    else if (config.getAllowOverlines() && length > config.getVictoryLength())
    {
      victory = true;
    }

    // topright diagonal check
    length = board.count(color, x, y, 1, -1);
    if (length == config.getVictoryLength())
    {
      victory = true;
    }
    else if (config.getAllowOverlines() && length > config.getVictoryLength())
    {
      victory = true;
    }

    if (victory)
    {
      if (log.isDebugEnabled())
      {
        log.debug("Game detected winner {}({})", player.getName(), player.getColor().getName());
      }

      gameOver = true;

      fireEvent(new GameOverEvent(this, player.getColor()));
    }
  }

  protected void fireEvent(final GameEvent event)
  {
    for (final GomokuGameListener listener : this.listeners)
    {
      listener.preEvent(event);
    }

    if (!event.isAborted())
    {
      for (final GomokuGameListener listener : this.listeners)
      {
        listener.onEvent(event);
      }
    }
  }

  /**
   * Get the owner of the piece placed on provided position
   *
   * @param x The x location of the piece
   * @param y The y location of the piece
   * @return The player owning the piece on x, y. {@code null} if empty.
   */
  public Player getPieceOwner(int x, int y)
  {
    Color piece = board.getPiece(x, y);
    if (piece == playerOne.getColor())
    {
      return playerOne;
    }

    if (piece == playerTwo.getColor())
    {
      return playerTwo;
    }

    return null;
  }

  /**
   * Swap turns between black and white
   */
  @Override
  public void switchTurn()
  {
    if (gameOver)
    {
      log.error("Attempted to switch turn, but game was over", new RuntimeException());
      return;
    }

    if (turn == playerOne)
    {
      setCurrentTurnPlayer(playerTwo);
    }
    else
    {
      setCurrentTurnPlayer(playerOne);
    }
  }

  /**
   * Set turn to provided player
   *
   * @param player
   *          The player who is going to get the turn
   */
  @Override
  public void setCurrentTurnPlayer(Player player)
  {
    setTurn(player.getColor());
  }

  /**
   * Set turn to player with provided ID
   *
   * @param playerType the provided player ID
   */
  public void setTurn(Color color)
  {
    if (gameOver)
    {
      log.error(String.format("Attempted to set turn to %s but game was already over.", color.getName()),
                new RuntimeException());
      return;
    }

    Player nextTurnPlayer = null;

    if (color == playerOne.getColor())
    {
      nextTurnPlayer = playerOne;
    }
    else if (color == playerTwo.getColor())
    {
      nextTurnPlayer = playerTwo;
    }
    else
    {
      throw new IllegalArgumentException("Turn set to invalid color: " + color);
    }

    SetPlayerTurnEvent spte = new SetPlayerTurnEvent(this, nextTurnPlayer.getColor());
    fireEvent(spte);
    if (!spte.isAborted())
    {
      if (log.isDebugEnabled())
      {
        log.debug("Turn set to {}({})", turn.getColor(), turn.getName());
      }

      turn = nextTurnPlayer;
    }
  }

  /**
   * Get the player who has the turn
   *
   * @return The player who has the turn
   */
  @Override
  public Player getCurrentTurnPlayer()
  {
    return turn;
  }

  /**
   * Returns the first player
   *
   * @return the first player
   */
  @Override
  public Player getPlayerOne()
  {
    return playerOne;
  }

  /**
   * Returns the second player
   *
   * @return the second player
   */
  @Override
  public Player getPlayerTwo()
  {
    return playerTwo;
  }

  /**
   * Returns a player depending on provided color
   *
   * @param color
   *          The player color
   * @return a player depending on provided color
   * @throws IllegalArgumentException
   *           Indicates a value other than the color of player one or
   *           player two.
   */
  public Player getPlayerFromColor(Color color)
  {
    if (color == playerOne.getColor())
    {
      return playerOne;
    }
    if (color == playerTwo.getColor())
    {
      return playerTwo;
    }

    throw new IllegalArgumentException("No player with this color: \"" + color + "\".");
  }

  @Override
  public Player getPlayer(Color color)
  {
    if (color == playerOne.getColor())
    {
      return playerOne;
    }
    else if (color == playerTwo.getColor())
    {
      return playerTwo;
    }

    return null;
  }

  /**
   * Get the current board
   *
   * @return The current board
   */
  @Override
  public GomokuBoard getBoard()
  {
    return board;
  }

  @Override
  public boolean isGameOver()
  {
    return gameOver;
  }

  @Override
  public void addListener(GomokuGameListener listener)
  {
    listeners.add(listener);
  }

  @Override
  public void removeListener(GomokuGameListener listener)
  {
    listeners.remove(listener);
  }
}
