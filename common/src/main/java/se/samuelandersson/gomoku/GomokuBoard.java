package se.samuelandersson.gomoku;

public interface GomokuBoard
{
  void reset();

  void setBoardData(int[] data);

  int[] getBoardData();

  Color getPiece(int x, int y);

  void setPiece(int x, int y, Color color);

  int getWidth();

  int getHeight();

  /**
   * Returns the number of pieces of the provided color in a row of provided
   * direction based of the provided position
   *
   * @param color
   * @param x
   * @param y
   * @param dirX -1, 0 or 1
   * @param dirY -1, 0 or 1
   * @return
   */
  int count(Color color, int x, int y, int dirX, int dirY);

  boolean try3And3(Color color, int x, int y);

  boolean try4And4(Color color, int x, int y);

  void setFrom(GomokuBoard board);
  
  void addListener(GomokuBoardListener listener);
  void removeListener(GomokuBoardListener listener);
}
