package se.samuelandersson.gomoku.action.impl;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.action.GameAction;

public abstract class AbstractGameAction implements GameAction
{
  protected Color color;
  protected transient boolean confirmed = false;
  
  protected AbstractGameAction()
  {
  }

  public AbstractGameAction(final Color color)
  {
    this.color = color;
  }

  @Override
  public Color getColor()
  {
    return this.color;
  }

  @Override
  public boolean isConfirmed()
  {
    return this.confirmed;
  }
}
