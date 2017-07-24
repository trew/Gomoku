package se.samuelandersson.gomoku.client.entities;

import com.badlogic.gdx.scenes.scene2d.Event;

public class BoardEvent extends Event
{
  private int x;
  private int y;

  public BoardEvent(int x, int y)
  {
    this.x = x;
    this.y = y;
  }

  public int getX()
  {
    return x;
  }

  public void setX(int x)
  {
    this.x = x;
  }

  public int getY()
  {
    return y;
  }

  public void setY(int y)
  {
    this.y = y;
  }
}
