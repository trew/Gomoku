package se.samuelandersson.gomoku.action.impl;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.action.BoardAction;

public abstract class AbstractBoardAction extends AbstractGameAction implements BoardAction
{
  protected int x;
  protected int y;
  
  protected AbstractBoardAction()
  {
  }
  
  public AbstractBoardAction(final Color color, final int x, final int y)
  {
    super(color);
    this.x = x;
    this.y = y;
  }

  @Override
  public int getX()
  {
    return this.x;
  }

  @Override
  public int getY()
  {
    return this.y;
  }
}
