package se.samuelandersson.gomoku.net;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.impl.BoardImpl;

/**
 * A packet that the client requests from the server upon connection. It sends
 * the game board, the color for the player and the turn. Upon receiving this
 * packet, the client knows it has all the data it needs to start displaying the
 * board etc.
 *
 * @author Samuel Andersson
 */
public class InitialServerDataPacket
{
  /** The board of the game */
  private int[] board;

  private GomokuConfig config;

  /** The color the player will receive */
  private int playerID;

  /** The current turn ID */
  private int turnID;

  private Color playerOneColor;
  private Color playerTwoColor;

  /** The currently connected players */
  private String[] playerList;
  
  /** Empty constructor for Kryonet */
  public InitialServerDataPacket()
  {
  }

  /**
   * Create a new initial data packet
   *
   * @param board
   *          The board
   * @param playerID
   *          The ID the player will receive
   * @param turnID
   *          The ID of the player with current turn
   * @param opponentName
   *          The name of the opponent
   */
  public InitialServerDataPacket(GomokuBoard board, GomokuConfig config, int playerID, int turnID,
      String[] playerList, Color playerOneColor, Color playerTwoColor)
  {
    this.board = board.getBoardData();
    this.config = config;
    this.playerID = playerID;
    this.turnID = turnID;
    this.playerList = playerList;
    this.playerOneColor = playerOneColor;
    this.playerTwoColor = playerTwoColor;
  }

  /**
   * Returns the board
   *
   * @return the board
   */
  public GomokuBoard getBoard()
  {
    BoardImpl board = new BoardImpl(config.getWidth(), config.getHeight());
    board.setBoardData(this.board);

    return board;
  }

  public GomokuConfig getConfig()
  {
    return config;
  }
  
  /**
   * Returns the player id
   *
   * @return the player id
   */
  public int getID()
  {
    return playerID;
  }

  /**
   * Returns the color of the player in turn
   *
   * @return the color of the player in turn
   */
  public int getTurn()
  {
    return turnID;
  }

  /**
   * Returns the currently connected players
   *
   * @return the currently connected players
   */
  public String[] getPlayerList()
  {
    return playerList;
  }

  public Color getPlayerOneColor()
  {
    return playerOneColor;
  }

  public Color getPlayerTwoColor()
  {
    return playerTwoColor;
  }

  @Override
  public String toString()
  {
    return String.format("InitialServerData<%s,%s,%s,%s,%s>",
                         this.config,
                         this.turnID,
                         this.playerOneColor.getName(),
                         this.playerTwoColor.getName(),
                         this.playerID);
  }
}
