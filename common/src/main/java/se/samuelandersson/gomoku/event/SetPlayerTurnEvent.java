package se.samuelandersson.gomoku.event;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuGame;

public class SetPlayerTurnEvent extends AbstractGameEvent
{
  private Color color;

  public SetPlayerTurnEvent(GomokuGame game, Color color)
  {
    super(game);
    this.color = color;
  }

  public Color getColor()
  {
    return color;
  }
}
