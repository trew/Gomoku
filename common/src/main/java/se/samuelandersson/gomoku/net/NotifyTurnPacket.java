package se.samuelandersson.gomoku.net;

import se.samuelandersson.gomoku.Color;

/**
 * Contains information about the current turn
 *
 * @author Samuel Andersson
 */
public class NotifyTurnPacket
{
  /** The player ID of the current turnholder */
  private Color color;

  /** Empty constructor for Kryonet */
  public NotifyTurnPacket()
  {
  }

  /**
   * Create a new packet containing turn information
   *
   * @param id
   *          The ID of the player holding the turn
   * @throws IllegalArgumentException
   *           Indicates an invalid turn ID
   */
  public NotifyTurnPacket(Color color) throws IllegalArgumentException
  {
    this.color = color;
  }

  /**
   * Get the turnholder ID
   *
   * @return The turnholder ID
   */
  public Color getID()
  {
    return this.color;
  }

  @Override
  public String toString()
  {
    return String.format("NotifyTurn<%s>", this.color);
  }
}
