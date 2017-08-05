package se.samuelandersson.gomoku.client.entities;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.client.Assets;

public class BoardSlot extends ImageButton
{
  private int x;
  private int y;

  private ImageButtonStyle emptyStyle;
  private ImageButtonStyle blackStyle;
  private ImageButtonStyle whiteStyle;

  /**
   * Package protected because this entity belongs to {@link BoardEntity}.
   */
  BoardSlot(int x, int y)
  {
    super(Assets.getInstance().getSkin(), "slot-none");
    this.emptyStyle = Assets.getInstance().getSkin().get("slot-none", ImageButtonStyle.class);
    this.blackStyle = Assets.getInstance().getSkin().get("slot-black", ImageButtonStyle.class);
    this.whiteStyle = Assets.getInstance().getSkin().get("slot-white", ImageButtonStyle.class);
    this.x = x;
    this.y = y;
  }
  
  public void setPiece(Color color)
  {
    if (color == Color.BLACK)
    {
      this.setStyle(this.blackStyle);
    }
    else if (color == Color.WHITE)
    {
      this.setStyle(this.whiteStyle);
    }
    else if (color == Color.NONE)
    {
      setStyle(this.emptyStyle);
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
