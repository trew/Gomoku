package se.samuelandersson.gomoku;

public interface GomokuBoardListener
{
  void onChange(Color color, int x, int y);
}