package se.samuelandersson.gomoku.net;

public class HandshakeServerPacket
{
  private boolean singleGameServer;
  private boolean ready;

  public HandshakeServerPacket()
  {
  }

  public HandshakeServerPacket(final boolean singleGameServer, final boolean ready)
  {
    this.singleGameServer = singleGameServer;
    this.ready = ready;
  }

  public boolean isSingleGameServer()
  {
    return singleGameServer;
  }
  
  public boolean isReady()
  {
    return this.ready;
  }

  @Override
  public String toString()
  {
    return String.format("HandshakeServerPacket<%s,%s>", this.singleGameServer, this.ready);
  }
}
