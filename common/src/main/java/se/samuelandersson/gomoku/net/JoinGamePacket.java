package se.samuelandersson.gomoku.net;

public class JoinGamePacket
{
  public int gameID;

  @SuppressWarnings("unused")
  private JoinGamePacket()
  {
  }

  public JoinGamePacket(int gameID)
  {
    this.gameID = gameID;
  }

  @Override
  public String toString()
  {
    return String.format("JoinGame<%s>", this.gameID);
  }
}
