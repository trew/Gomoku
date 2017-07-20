package se.samuelandersson.gomoku.net;

import se.samuelandersson.gomoku.action.GameAction;

/**
 * A packet requesting to place a piece on a certain location on the board
 *
 * @author Samuel Andersson
 */
public class GameActionPacket
{
  private GameAction action;

  /** Empty constructor for Kryonet */
  @SuppressWarnings("unused")
  private GameActionPacket()
  {
  }

  /**
   * Create a new packet requesting piece placement
   *
   * @param x
   *          The x location of the piece
   * @param y
   *          The y location of the piece
   * @param playerColor
   *          The player color of the player placing the piece
   */
  public GameActionPacket(GameAction action)
  {
    this.action = action;
  }
  
  public GameAction getAction()
  {
    return this.action;
  }
  
  @Override
  public String toString()
  {
    return String.format("GameAction<%s>", this.action.toString());
  }
}
