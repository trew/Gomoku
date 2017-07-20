package se.samuelandersson.gomoku.action;

/**
 * Basic interface for an action on the board. DO NOT FORGET TO ADD AN EMPTY CONSTRUCTOR!
 *
 * @author Samuel Andersson
 */
public interface BoardAction extends GameAction
{
  public int getX();

  public int getY();
}