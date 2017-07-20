package se.samuelandersson.gomoku.net;

import se.samuelandersson.gomoku.GomokuConfig;

public class CreateGamePacket
{
  private GomokuConfig config;

  @SuppressWarnings("unused")
  private CreateGamePacket()
  {
  }

  public CreateGamePacket(GomokuConfig config)
  {
    this.config = config;
  }

  public GomokuConfig getConfig()
  {
    return config;
  }

  @Override
  public String toString()
  {
    return String.format("CreateGame<%s>", this.config);
  }
}
