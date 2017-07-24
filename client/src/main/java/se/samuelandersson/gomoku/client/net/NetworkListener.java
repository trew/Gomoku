package se.samuelandersson.gomoku.client.net;

import com.esotericsoftware.kryonet.Connection;

public interface NetworkListener
{
  default void connected(Connection connection)
  {
  }

  default void disconnected(Connection connection)
  {
  }

  default void received(Connection connection, Object object)
  {
  }

  default void idle(Connection connection)
  {
  }
}
