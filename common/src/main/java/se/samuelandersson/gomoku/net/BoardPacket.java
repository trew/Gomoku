package se.samuelandersson.gomoku.net;

import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.impl.BoardImpl;

/**
 * A packet containing a board data.
 *
 * @author Samuel Andersson
 */
public class BoardPacket
{
  /** The board data */
  private int[] board;

  private int width;
  private int height;

  /** Empty constructor for Kryonet */
  public BoardPacket()
  {
  }

  /**
   * Create a new board packet from a {@link BoardImpl}
   *
   * @param board
   *          The board to get data from
   */
  public BoardPacket(GomokuBoard board)
  {
    this.board = board.getBoardData();
    this.width = board.getWidth();
    this.height = board.getHeight();
  }

  /**
   * Returns the board from this packet
   *
   * @return the board from this packet
   */
  public GomokuBoard getBoard()
  {
    BoardImpl board = new BoardImpl(this.width, this.height);
    board.setBoardData(this.board);

    return board;
  }

  @Override
  public String toString()
  {
    return String.format("Board<%sx%s>", this.width, this.height);
  }
}
