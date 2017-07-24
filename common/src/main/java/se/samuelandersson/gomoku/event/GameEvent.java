package se.samuelandersson.gomoku.event;

import se.samuelandersson.gomoku.GomokuGame;

public interface GameEvent
{
  boolean isAborted();

  void setAborted(final boolean aborted, final String reason);
  
  String getAbortReason();
  
  GomokuGame getGame();
}
