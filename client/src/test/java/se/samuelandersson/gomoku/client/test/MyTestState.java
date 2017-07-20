package se.samuelandersson.gomoku.client.test;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.client.gui.BoardWidget;
import se.samuelandersson.gomoku.client.gui.BoardWidget.Callback;
import se.samuelandersson.gomoku.impl.BoardImpl;
import se.samuelandersson.gomoku.client.gui.ScrollingPane;

public class MyTestState extends TestState
{

  private ScrollPane scrollpane;
  private BoardWidget boardwidget;

  private BoardImpl board;

  private boolean placingBlack;

  public MyTestState()
  {
  }

  @Override
  protected RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    boardwidget = new BoardWidget();
    scrollpane = new ScrollingPane(boardwidget, boardwidget);

    rp.add(scrollpane);
    return rp;
  }

  @Override
  protected void layoutRootPane()
  {
    boardwidget.setBoard(board);
    boardwidget.adjustSize();

    int scrollpaneWidth = Math.min(375, board.getWidth() * 25);
    int scrollpaneHeight = Math.min(375, board.getHeight() * 25);
    scrollpane.setInnerSize(scrollpaneWidth, scrollpaneHeight);
    scrollpane.setPosition(400 - scrollpane.getWidth() / 2, 300 - scrollpane.getHeight() / 2);
    scrollpane.updateScrollbarSizes();
    scrollpane.setScrollPositionX(scrollpane.getMaxScrollPosX() / 2);
    scrollpane.setScrollPositionY(scrollpane.getMaxScrollPosY() / 2);
  }

  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException
  {
    super.init(container, game);
    GomokuConfig config = new GomokuConfig("Test", 15, 15, 5, true, false, false);
    board = new BoardImpl(config.getWidth(), config.getHeight());
    boardwidget.addCallback(new Callback()
    {
      @Override
      public void callback(int x, int y)
      {
        Color color = placingBlack ? Color.BLACK : Color.WHITE;
        boardwidget.setPiece(color, x, y);
        placingBlack = !placingBlack;
      }
    });
  }

  @Override
  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
  {
  }

  @Override
  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
  {
  }

  @Override
  public int getID()
  {
    return 0;
  }

}
