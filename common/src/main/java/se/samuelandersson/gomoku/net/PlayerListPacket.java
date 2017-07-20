package se.samuelandersson.gomoku.net;

import java.util.Arrays;

public class PlayerListPacket
{
  public String[] players;

  @SuppressWarnings("unused")
  private PlayerListPacket()
  {
  }

  public PlayerListPacket(String[] players)
  {
    this.players = players;
  }

  @Override
  public String toString()
  {
    return String.format("PlayerList<%s>", Arrays.toString(this.players));
  }
}
