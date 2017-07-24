package se.samuelandersson.gomoku.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.samuelandersson.gomoku.Player;

public class PlayerListPacket
{
  private List<Player> playerList;
  
  @SuppressWarnings("unused")
  private PlayerListPacket()
  {
  }

  public PlayerListPacket(List<Player> players)
  {
    this.playerList = new ArrayList<>(players);
  }

  @Override
  public String toString()
  {
    return String.format("PlayerList<%s>", this.playerList);
  }
  
  public List<Player> getPlayerList()
  {
    return Collections.unmodifiableList(this.playerList);
  }
}
