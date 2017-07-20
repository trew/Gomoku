package se.samuelandersson.gomoku.net;

import se.samuelandersson.gomoku.Color;

public class VictoryPacket
{
  public Color victoryColor;

  @SuppressWarnings("unused")
  private VictoryPacket()
  {
  }

  /**
   *
   * @param victory the victory value. 0 = loss, 1 = win, 2 = draw.
   */
  public VictoryPacket(Color victoryColor)
  {
    this.victoryColor = victoryColor;
  }

  @Override
  public String toString()
  {
    return String.format("Victory<%s>", this.victoryColor.getName());
  }
}
