package se.samuelandersson.gomoku.action;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.action.impl.AbstractBoardAction;
import se.samuelandersson.gomoku.exception.IllegalActionException;

/**
 * Action for placing a piece on the board
 *
 * @author Samuel Andersson
 */
public class PlacePieceAction extends AbstractBoardAction
{
  /**
   * For kryonet
   */
  protected PlacePieceAction()
  {
  }

  public PlacePieceAction(Color color, int x, int y)
  {
    super(color, x, y);
  }

  @Override
  public void doAction(GomokuGame game) throws IllegalActionException
  {
    if (game.getBoard().getPiece(x, y) != Color.NONE)
    {
      throw new IllegalActionException("That position is already occupied!");
    }

    if (game.getConfig().useThreeAndThree())
    {
      if (!game.getBoard().try3And3(color, x, y))
      {
        throw new IllegalActionException("Unable to place because of Three And Three-rule.");
      }
    }

    if (game.getConfig().useFourAndFour())
    {
      if (!game.getBoard().try4And4(color, x, y))
      {
        throw new IllegalActionException("Unable to place because of Four And Four-rule.");
      }
    }

    game.getBoard().setPiece(x, y, color);
  }

  @Override
  public void confirmAction(GomokuGame game)
  {
    game.checkBoard(this.x, this.y);
    if (!game.isGameOver())
    {
      game.switchTurn();
    }

    confirmed = true;
  }

  @Override
  public void undoAction(GomokuGame game)
  {
    game.getBoard().setPiece(x, y, Color.NONE);
  }

  @Override
  public String toString()
  {
    return String.format("PlacePiece<%s,%s,%s>", this.color, this.x, this.y);
  }
}