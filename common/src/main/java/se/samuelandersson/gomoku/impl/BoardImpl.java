package se.samuelandersson.gomoku.impl;

import java.util.ArrayList;
import java.util.List;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuBoardListener;

/**
 * A Board represents a Gomoku board. The board size restrictions is 40x40.
 *
 * @author Samuel Andersson
 *
 */
public class BoardImpl implements GomokuBoard
{
  /** The structure containing the board data */
  private int[] board;
  private int width;
  private int height;

  private transient final List<GomokuBoardListener> listeners = new ArrayList<>();

  /** Empty constructor for Kryonet */
  @SuppressWarnings("unused")
  private BoardImpl()
  {
  }

  public BoardImpl(final int width, final int height)
  {
    this.width = width;
    this.height = height;
    reset();
  }

  @Override
  public void addListener(GomokuBoardListener listener)
  {
    listeners.add(listener);
  }

  @Override
  public void removeListener(GomokuBoardListener listener)
  {
    listeners.remove(listener);
  }

  public void fireListeners(Color color, int x, int y)
  {
    for (GomokuBoardListener listener : listeners)
    {
      listener.onChange(color, x, y);
    }
  }

  /**
   * Reset the board, making it all empty spaces. Also reset the
   * actionRecorder.
   */
  @Override
  public void reset()
  {
    board = new int[this.width * this.height];
  }

  /**
   * Replace the current board with a new board
   *
   * @param board
   *          The source board which will replace the current board
   */
  @Override
  public void setFrom(GomokuBoard board)
  {
    this.board = board.getBoardData();
    this.width = board.getWidth();
    this.height = board.getHeight();
  }

  @Override
  public void setBoardData(int[] data)
  {
    board = data;
  }

  @Override
  public int[] getBoardData()
  {
    return board;
  }

  /**
   * Returns true of all the pieces surrounding the line is empty
   *
   * @return true of all the pieces surrounding the line is empty
   */
  protected boolean isOpen(Color color, int x, int y, int dirX, int dirY)
  {
    int xpos, ypos;
    xpos = x + dirX;
    ypos = y + dirY;

    do
    {
      // if we reached the edge of the board:
      // this line is not open if there's something other than NOPLAYER
      if (xpos == 0 || xpos == getWidth() - 1)
      {
        if (getPiece(xpos, ypos) != Color.NONE)
        {
          return false;
        }
      }
      if (ypos == 0 || ypos == getHeight() - 1)
      {
        if (getPiece(xpos, ypos) != Color.NONE)
        {
          return false;
        }
      }

      if (getPiece(xpos, ypos) == Color.NONE)
      {
        break;
      }
      else if (getPiece(xpos, ypos) != color)
      { // enemy
        return false;
      }

      xpos += dirX;
      ypos += dirY;

    } while (xpos >= 0 && xpos < getWidth() && ypos >= 0 && ypos < getHeight());

    // now check the other side
    xpos = x - dirX;
    ypos = y - dirY;

    do
    {
      // if we reached the edge of the board:
      // this line is not open if there's something other than NOPLAYER
      if (xpos == 0 || xpos == getWidth() - 1)
      {
        if (getPiece(xpos, ypos) != Color.NONE)
        {
          return false;
        }
      }
      if (ypos == 0 || ypos == getHeight() - 1)
      {
        if (getPiece(xpos, ypos) != Color.NONE)
        {
          return false;
        }
      }

      if (getPiece(xpos, ypos) == Color.NONE)
      {
        break;
      }
      else if (getPiece(xpos, ypos) != color)
      { // enemy
        return false;
      }
      xpos -= dirX;
      ypos -= dirY;
    } while (xpos >= 0 && xpos < getWidth() && ypos >= 0 && ypos < getHeight());

    return true;
  }

  @Override
  public int count(Color color, int x, int y, int dirX, int dirY)
  {
    int ct = 1;
    int xpos, ypos; // position to be examined
    xpos = x + dirX;
    ypos = y + dirY;

    while (xpos >= 0 && xpos < getWidth() && ypos >= 0 && ypos < getHeight() && getPiece(xpos, ypos) == color)
    {
      ct++;
      xpos += dirX;
      ypos += dirY;
    }

    // check opposite direction too
    xpos = x - dirX;
    ypos = y - dirY;

    while (xpos >= 0 && xpos < getWidth() && ypos >= 0 && ypos < getHeight() && getPiece(xpos, ypos) == color)
    {
      ct++;
      xpos -= dirX;
      ypos -= dirY;
    }

    return ct;
  }

  @Override
  public boolean try3And3(Color color, int x, int y)
  {
    int ct = 0;
    // horizontal
    if (x > 0 && x < getWidth() - 1 && count(color, x, y, -1, 0) == 3 && isOpen(color, x, y, 1, 0))
    {
      ct++;
    }

    // vertical
    if (y > 0 && y < getHeight() - 1 && count(color, x, y, 0, -1) == 3 && isOpen(color, x, y, 0, 1))
    {
      ct++;
    }

    // topleft to bottomdown
    if (x > 0 && x < getWidth() - 1 && y > 0 && y < getHeight() - 1 && count(color, x, y, -1, -1) == 3 &&
        isOpen(color, x, y, 1, 1))
    {
      ct++;
    }

    // topright to bottomleft
    if (x > 0 && x < getWidth() - 1 && y > 0 && y < getHeight() - 1 && count(color, x, y, 1, -1) == 3 &&
        isOpen(color, x, y, 1, 1))
    {
      ct++;
    }

    return ct < 2;
  }

  @Override
  public boolean try4And4(Color color, int x, int y)
  {
    int ct = 0;
    // horizontal
    if (count(color, x, y, -1, 0) == 4)
    {
      ct++;
    }

    // vertical
    if (count(color, x, y, 0, -1) == 4)
    {
      ct++;
    }

    // topleft to bottomdown
    if (count(color, x, y, -1, -1) == 4)
    {
      ct++;
    }

    // topright to bottomleft
    if (count(color, x, y, 1, -1) == 4)
    {
      ct++;
    }

    return ct < 2;
  }

  /**
   * Returns the player color for given position on the board. If either of
   * the arguments is invalid, i.e. "x" being larger than board width, the
   * function will return NONE.
   *
   * @param x The x location
   * @param y The y location
   * @return The player color for the given position.
   */
  @Override
  public Color getPiece(int x, int y)
  {
    if (x < 0 || x >= this.width || y < 0 || y >= this.height)
    {
      return Color.NONE;
    }

    return Color.valueOf(board[x + this.width * y]);
  }

  /**
   * Place a piece on the board
   *
   * @param x
   *          The x location for the piece
   * @param y
   *          The y location for the piece
   * @param color
   *          The player color for the piece
   */
  @Override
  public void setPiece(int x, int y, Color color)
  {
    if (x < 0 || x > this.width || y < 0 || y > this.height)
    {
      throw new IllegalArgumentException("Position out of bounds. X: " + x + ", Y: " + y);
    }

    board[x + this.width * y] = color.getId();
    
    this.fireListeners(color, x, y);
  }

  /**
   * Returns the current width of the board
   *
   * @return the current width of the board
   */
  @Override
  public int getWidth()
  {
    return this.width;
  }

  /**
   * Returns the current height of the board
   *
   * @return the current height of the board
   */
  @Override
  public int getHeight()
  {
    return this.height;
  }
}
