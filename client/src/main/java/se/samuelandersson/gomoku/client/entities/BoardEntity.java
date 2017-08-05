package se.samuelandersson.gomoku.client.entities;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuBoardListener;
import se.samuelandersson.gomoku.client.Assets;

public class BoardEntity extends Table
{
  private static final Logger log = LoggerFactory.getLogger(BoardEntity.class);

  private List<BoardSlot> slots;

  private GomokuBoard board;
  private ShapeRenderer renderer;

  public BoardEntity(ShapeRenderer renderer)
  {
    this.slots = new ArrayList<>();
    this.renderer = renderer;
    this.setBackground(Assets.getInstance().getDrawable("boardbg"));
  }

  public GomokuBoard getBoard()
  {
    return this.board;
  }

  public void drawLines()
  {
    int width = Math.min(30, (int) (450.f / board.getWidth()));
    int height = Math.min(30, (int) (450.f / board.getHeight()));
    int size = Math.min(width, height);

    renderer.setColor(0, 0, 0, 1);
    renderer.begin(ShapeType.Line);

    for (int y = 1; y < board.getHeight(); y++)
    {
      float x = this.getX();
      float yLoc = this.getY() + size * y;
      float x2 = x + size * board.getWidth();
      renderer.line(x, yLoc, x2, yLoc);
    }

    for (int x = 1; x < board.getWidth(); x++)
    {
      float xLoc = this.getX() + size * x;
      renderer.line(xLoc, this.getY(), xLoc, this.getY() + size * board.getHeight());
    }

    renderer.end();
  }

  public void setBoard(final GomokuBoard board)
  {
    if (log.isDebugEnabled())
    {
      log.debug("setBoard({})", board);
    }

    this.board = board;
    this.board.addListener(new GomokuBoardListener()
    {
      @Override
      public void onChange(final Color color, final int x, final int y)
      {
        BoardEntity.this.setPiece(color, x, y);
      }
    });

    this.clearChildren();
    this.slots.clear();

    for (int y = 0; y < board.getHeight(); y++)
    {
      for (int x = 0; x < board.getWidth(); x++)
      {
        BoardSlot slot = new BoardSlot(x, y);

        slot.addListener(new ChangeListener()
        {
          @Override
          public void changed(ChangeEvent event, Actor actor)
          {
            final BoardSlot boardSlot = (BoardSlot) actor;
            BoardEntity.this.fire(new BoardEvent(boardSlot.getSlotX(), boardSlot.getSlotY()));
          }
        });
        this.slots.add(slot);

        int width = Math.min(30, (int) (450.f / board.getWidth()));
        int height = Math.min(30, (int) (450.f / board.getHeight()));
        int size = Math.min(width, height);
        this.add(slot).size(size, size).pad(0).space(0);

        slot.setSize(size, size); // size must be set before setPiece
        slot.setPiece(board.getPiece(x, y));

      }

      this.row();
    }
  }

  public void setPiece(final Color color, final int x, final int y)
  {
    if (log.isDebugEnabled())
    {
      log.debug("setPiece(Color: {}, X: {}, Y: {})", color.getName(), x, y);
    }

    slots.get(y * this.board.getWidth() + x).setPiece(color);
  }
}
