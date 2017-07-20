package se.samuelandersson.gomoku.client.states;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * A DelegateListener will return all events from a regular Listener to the
 * corresponding functions in the GomokuGameState that was passed to the
 * listener on creation.
 *
 * @author Samuel Andersson
 */
public class DelegateListener extends Listener
{
  private AbstractNetworkGameState delegate;

  public DelegateListener(AbstractNetworkGameState delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public void connected(Connection conn)
  {
    delegate.connected(conn);
  }

  @Override
  public void disconnected(Connection conn)
  {
    delegate.disconnected(conn);
  }

  @Override
  public void received(Connection conn, Object obj)
  {
    delegate.received(conn, obj);
  }

  @Override
  public void idle(Connection conn)
  {
    delegate.idle(conn);
  }
}
