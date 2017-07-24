package se.samuelandersson.gomoku;

public interface GomokuGame
{
  GomokuBoard getBoard();

  GomokuConfig getConfig();

  void addListener(GomokuGameListener listener);

  void removeListener(GomokuGameListener listener);

  Player getPlayerOne();

  Player getPlayerTwo();

  Player getPlayer(final Color color);

  Player getCurrentTurnPlayer();

  void setCurrentTurnPlayer(final Player player);

  void reset();

  /**
   * Checks for victory and calls listeners if someone won
   *
   * @param x the x position modified that forced victory check
   * @param y the y position modified that forced victory check
   */
  void checkBoard(int x, int y);

  void switchTurn();

  boolean isGameOver();
}
