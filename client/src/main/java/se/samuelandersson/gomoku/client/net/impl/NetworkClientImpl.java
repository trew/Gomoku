package se.samuelandersson.gomoku.client.net.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import se.samuelandersson.gomoku.client.net.NetworkClient;
import se.samuelandersson.gomoku.client.net.NetworkListener;
import se.samuelandersson.gomoku.client.net.PacketHandler;
import se.samuelandersson.gomoku.net.BoardPacket;
import se.samuelandersson.gomoku.net.GameActionPacket;
import se.samuelandersson.gomoku.net.GameListPacket;
import se.samuelandersson.gomoku.net.HandshakeClientPacket;
import se.samuelandersson.gomoku.net.HandshakeServerPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.NotifyTurnPacket;
import se.samuelandersson.gomoku.net.PlayerListPacket;
import se.samuelandersson.gomoku.net.RegisterPackets;
import se.samuelandersson.gomoku.net.Request;
import se.samuelandersson.gomoku.net.VictoryPacket;

public class NetworkClientImpl implements NetworkClient
{
  private Set<NetworkListener> listeners = new LinkedHashSet<>();
  private Set<NetworkListener> listenersToAdd = new LinkedHashSet<>();
  private Set<NetworkListener> listenersToRemove = new LinkedHashSet<>();
  private Set<PacketHandler> packetHandlers = new LinkedHashSet<>();
  private Set<PacketHandler> packetHandlersToAdd = new LinkedHashSet<>();
  private Set<PacketHandler> packetHandlersToRemove = new LinkedHashSet<>();

  private Queue<Runnable> executionQueue = new LinkedBlockingQueue<>();

  private Client client = new Client();

  private boolean inLoop = false;

  private void begin()
  {
    inLoop = true;
  }

  private void end()
  {
    inLoop = false;

    for (NetworkListener listener : this.listenersToAdd)
    {
      this.listeners.add(listener);
    }

    for (NetworkListener listener : this.listenersToRemove)
    {
      this.listeners.remove(listener);
    }

    for (PacketHandler listener : this.packetHandlersToAdd)
    {
      this.packetHandlers.add(listener);
    }

    for (PacketHandler listener : this.packetHandlersToRemove)
    {
      this.packetHandlers.remove(listener);
    }
  }

  public NetworkClientImpl()
  {
    RegisterPackets.register(client.getKryo());

    client.addListener(new Listener()
    {
      @Override
      public void connected(Connection connection)
      {
        executionQueue.add(new Runnable()
        {
          @Override
          public void run()
          {
            begin();
            listeners.forEach((listener) -> listener.connected(connection));
            end();
          }
        });
      }

      @Override
      public void disconnected(Connection connection)
      {
        executionQueue.add(new Runnable()
        {
          @Override
          public void run()
          {
            begin();
            listeners.forEach((listener) -> listener.disconnected(connection));
            end();
          }
        });
      }

      @Override
      public void idle(Connection connection)
      {
        executionQueue.add(new Runnable()
        {
          @Override
          public void run()
          {
            begin();
            listeners.forEach((listener) -> listener.idle(connection));
            end();
          }
        });
      }

      @Override
      public void received(Connection connection, Object object)
      {
        executionQueue.add(new Runnable()
        {
          @Override
          public void run()
          {
            begin();
            listeners.forEach((listener) -> listener.received(connection, object));

            packetHandlers.forEach((handler) -> {
              if (object instanceof BoardPacket)
              {
                handler.handleBoard(connection, (BoardPacket) object);
              }
              else if (object instanceof GameListPacket)
              {
                handler.handleGameList(connection, (GameListPacket) object);
              }
              else if (object instanceof Request)
              {
                handler.handleRequest(connection, (Request) object);
              }
              else if (object instanceof InitialServerDataPacket)
              {
                handler.handleInitialServerData(connection, (InitialServerDataPacket) object);
              }
              else if (object instanceof NotifyTurnPacket)
              {
                handler.handleNotifyTurn(connection, (NotifyTurnPacket) object);
              }
              else if (object instanceof GameActionPacket)
              {
                handler.handleGameAction(connection, (GameActionPacket) object);
              }
              else if (object instanceof PlayerListPacket)
              {
                handler.handlePlayerList(connection, (PlayerListPacket) object);
              }
              else if (object instanceof VictoryPacket)
              {
                handler.handleVictory(connection, (VictoryPacket) object);
              }
              else if (object instanceof HandshakeClientPacket)
              {
                handler.handleHandshakeClient(connection, (HandshakeClientPacket) object);
              }
              else if (object instanceof HandshakeServerPacket)
              {
                handler.handleHandshakeServer(connection, (HandshakeServerPacket) object);
              }
            });
            end();
          }
        });
      }
    });
  }

  @Override
  public void processExecutionQueue()
  {
    Iterator<Runnable> it = this.executionQueue.iterator();
    while (it.hasNext())
    {
      it.next().run();
      it.remove();
    }
  }

  @Override
  public boolean isConnected()
  {
    return client.isConnected();
  }

  @Override
  public void start()
  {
    client.start();
  }

  @Override
  public void stop()
  {
    client.stop();
  }

  @Override
  public void connect(int timeout, String host, int port) throws IOException
  {
    client.connect(timeout, host, port);
  }

  @Override
  public void disconnect()
  {
    this.client.close();
  }

  @Override
  public void sendTCP(Object obj)
  {
    client.sendTCP(obj);
  }

  @Override
  public void sendTCP(Connection connection, Object obj)
  {
    connection.sendTCP(obj);
  }

  @Override
  public void addListener(NetworkListener listener)
  {
    if (this.inLoop)
    {
      this.listenersToAdd.add(listener);
    }
    else
    {
      this.listeners.add(listener);
    }
  }

  @Override
  public void removeListener(NetworkListener listener)
  {
    if (this.inLoop)
    {
      this.listenersToRemove.add(listener);
    }
    else
    {
      this.listeners.remove(listener);
    }
  }

  @Override
  public void addPacketHandler(PacketHandler handler)
  {
    if (this.inLoop)
    {
      this.packetHandlersToAdd.add(handler);
    }
    else
    {
      this.packetHandlers.add(handler);
    }
  }

  @Override
  public void removePacketHandler(PacketHandler handler)
  {
    if (this.inLoop)
    {
      this.packetHandlersToRemove.add(handler);
    }
    else
    {
      this.packetHandlers.remove(handler);
    }
  }
}
