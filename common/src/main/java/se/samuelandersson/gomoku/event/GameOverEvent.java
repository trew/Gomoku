package se.samuelandersson.gomoku.event;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuGame;

public class GameOverEvent extends AbstractGameEvent
{
  private Color color;

  public GameOverEvent(final GomokuGame game, Color color)
  {
    super(game);
    this.color = color;
  }

  public Color getColor()
  {
    return this.color;
  }
}
