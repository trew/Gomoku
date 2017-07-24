package se.samuelandersson.gomoku.client.net;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;

public interface NetworkClient
{
  boolean isConnected();

  void start();

  void stop();

  void connect(int timeout, String host, int port) throws IOException;

  void disconnect();

  int sendTCP(Object obj);

  int sendTCP(Connection connection, Object obj);

  void addListener(NetworkListener listener);

  void removeListener(NetworkListener listener);

  void addPacketHandler(PacketHandler handler);

  void removePacketHandler(PacketHandler handler);
}
