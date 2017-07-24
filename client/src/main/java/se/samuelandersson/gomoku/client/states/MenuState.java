package se.samuelandersson.gomoku.client.states;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;

public class MenuState extends AbstractGameState
{
  private Image gomokuTitle;

  public MenuState(GomokuClient app)
  {
    super(app);
  }

  @Override
  public void initialize()
  {
    super.initialize();

    gomokuTitle = new Image(Assets.getInstance().getDrawable("gomokuTitle"));
    gomokuTitle.setOrigin(0.5f, 0.5f);
    gomokuTitle.setPosition(50, 450);

    this.getStage().addActor(gomokuTitle);
  }

  @Override
  public Table getTable()
  {
    Table table = super.getTable();
    table.defaults().space(10).fill();
    table.padTop(100);
    return table;
  }
}
