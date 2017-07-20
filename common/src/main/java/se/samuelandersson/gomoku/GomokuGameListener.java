package se.samuelandersson.gomoku;

import se.samuelandersson.gomoku.event.GameEvent;

public interface GomokuGameListener
{
  public void preEvent(GameEvent event);
  public void onEvent(GameEvent event);
}
