package se.samuelandersson.gomoku.net;

public class GameListPacket
{
  public int[] gameID;
  public String[] gameName;

  private transient String toStrCache;

  @SuppressWarnings("unused")
  private GameListPacket()
  {
  }

  public GameListPacket(final int[] gameID, final String[] gameName)
  {
    this.gameID = gameID;
    this.gameName = gameName;
  }

  @Override
  public String toString()
  {
    if (this.toStrCache == null)
    {
      StringBuilder sb = new StringBuilder("GameList[");
      for (int i = 0; i < gameID.length; i++)
      {
        sb.append(gameID[i]).append(":").append(gameName[i]);
        if (i < gameID.length)
        {
          sb.append(",");
        }
      }
      sb.append("]");

      this.toStrCache = sb.toString();
    }

    return this.toStrCache;
  }
}
