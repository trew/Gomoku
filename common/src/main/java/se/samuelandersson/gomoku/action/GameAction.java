package se.samuelandersson.gomoku.action;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.exception.IllegalActionException;

public interface GameAction
{
  Color getColor();

  void doAction(GomokuGame game) throws IllegalActionException;

  void undoAction(GomokuGame game);

  boolean isConfirmed();

  void confirmAction(GomokuGame game);
}