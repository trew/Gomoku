package se.samuelandersson.gomoku.net;

public class HandshakeClientPacket
{
  /** The player name */
  private String name;

  public HandshakeClientPacket()
  {
  }
  
  public HandshakeClientPacket(final String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return String.format("HandshakeClientPacket<%s>", this.name);
  }
}
