package se.samuelandersson.gomoku.event;

import se.samuelandersson.gomoku.GomokuGame;

public class AbstractGameEvent implements GameEvent
{
  private boolean aborted = false;
  private String abortReason;
  private GomokuGame game;

  public AbstractGameEvent(final GomokuGame game)
  {
  }
  
  @Override
  public void setAborted(boolean aborted, final String abortReason)
  {
    this.aborted = aborted;
    this.abortReason = abortReason;
  }
  
  @Override
  public boolean isAborted()
  {
    return this.aborted;
  }
  
  public String getAbortReason()
  {
    return this.abortReason;
  }

  @Override
  public GomokuGame getGame()
  {
    return this.game;
  }
}
