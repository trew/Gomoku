package se.samuelandersson.gomoku.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

public class NetworkClient extends Client
{
  private static final Logger log = LoggerFactory.getLogger(NetworkClient.class);

  @Override
  public int sendTCP(Object obj)
  {
    if (log.isDebugEnabled())
    {
      if (obj.getClass().getName().startsWith("se.samuelandersson"))
      {
        log.debug("Sending {}", obj.toString());
      }
    }

    return super.sendTCP(obj);
  }

  public int sendTCP(Connection connection, Object obj)
  {
    if (log.isDebugEnabled())
    {
      log.debug("Sending {}", obj.toString());
    }

    return connection.sendTCP(obj);
  }
}
