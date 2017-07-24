package se.samuelandersson.gomoku.client.entities;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.client.Assets;

public class BoardSlot extends Button
{
  private int x;
  private int y;

  private Image overlay;

  /**
   * Package protected because this entity belongs to {@link BoardEntity}.
   */
  BoardSlot(int x, int y)
  {
    super(Assets.getInstance().getSkin(), "slot");
    this.x = x;
    this.y = y;
  }

  private void removeCurrentPiece()
  {
    if (this.overlay != null)
    {
      this.overlay.remove();
    }
  }

  private void initializeOverlayImage()
  {
    if (this.overlay == null)
    {
      this.overlay = new Image(Assets.getInstance().getDrawable("piece-white"));
      this.overlay.setTouchable(Touchable.disabled);
    }

  }

  public void setPiece(Color color)
  {
    this.removeCurrentPiece();

    if (color != Color.NONE)
    {
      this.initializeOverlayImage();

      if (color == Color.BLACK)
      {
        this.overlay.setDrawable(Assets.getInstance().getDrawable("piece-black"));
      }
      else if (color == Color.WHITE)
      {
        this.overlay.setDrawable(Assets.getInstance().getDrawable("piece-white"));
      }

      // scaling uneven sizes looks weird, so it should needs to be adjusted slightly
      if (this.getWidth() % 2 == 1)
      {
        this.add(this.overlay).size(this.getWidth() - 1, this.getHeight() - 1).padRight(1).padTop(1).center();
      }
      else
      {
        this.add(this.overlay).size(this.getWidth(), this.getHeight()).center();
      }

      // for some reason the overlay slowly drifts to the right when canceling and replace a slot if this is not done...
      this.validate();
      this.overlay.setPosition(0, 0);
    }
  }

  public int getSlotX()
  {
    return this.x;
  }

  public int getSlotY()
  {
    return this.y;
  }
}
