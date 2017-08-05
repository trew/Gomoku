package se.samuelandersson.gomoku.client.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.net.Request;

public class PauseMenu extends AbstractGameState
{
  private Button continueButton;
  private Button optionsButton;
  private Button exitToMenuButton;
  private Button exitToOSButton;

  private ShapeRenderer backgroundRenderer;

  public PauseMenu(GomokuClient app)
  {
    super(app);
  }

  @Override
  public void initialize()
  {
    Skin skin = Assets.getInstance().getSkin();

    backgroundRenderer = new ShapeRenderer();

    continueButton = new TextButton("Continue", skin);
    continueButton.setSize(300, 50);
    continueButton.setPosition(250, 150);
    continueButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        PauseMenu.this.getApplication().exitCurrentState();
      }
    });

    optionsButton = new TextButton("Options", skin);
    optionsButton.setSize(300, 50);
    optionsButton.setPosition(250, 220);
    optionsButton.setDisabled(true);
    optionsButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        GomokuClient app = PauseMenu.this.getApplication();
        app.setNextState(app.getState(OptionsMenuState.class), true, true);
      }
    });

    exitToMenuButton = new TextButton("Exit to Menu", skin);
    exitToMenuButton.setSize(300, 50);
    exitToMenuButton.setPosition(250, 290);
    exitToMenuButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        GomokuClient app = PauseMenu.this.getApplication();
        app.getClient().sendTCP(Request.LEAVE_GAME);
        app.setNextState(app.getState(MainMenuState.class));
      }
    });

    exitToOSButton = new TextButton("Exit to OS", skin);
    exitToOSButton.setSize(300, 50);
    exitToOSButton.setPosition(250, 360);
    exitToOSButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        PauseMenu.this.getApplication().exit();
      }
    });

    this.getTable().defaults().minWidth(200).space(10).fill();
    this.getTable().add(continueButton);
    this.getTable().row();
    this.getTable().add(optionsButton);
    this.getTable().row();
    this.getTable().add(exitToMenuButton);
    this.getTable().row();
    this.getTable().add(exitToOSButton);

    this.getStage().addActor(this.getTable());
  }

  @Override
  public void render()
  {
    Gdx.gl.glEnable(GL20.GL_BLEND);
    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    backgroundRenderer.begin(ShapeType.Filled);
    backgroundRenderer.setColor(0, 0, 0, 0.5f);
    backgroundRenderer.rect(0, 0, GomokuClient.WIDTH, GomokuClient.HEIGHT);
    backgroundRenderer.end();
    Gdx.gl.glDisable(GL20.GL_BLEND);

    this.getStage().draw();
  }

  @Override
  public boolean keyUp(int keycode)
  {
    if (keycode == Input.Keys.ESCAPE)
    {
      this.getApplication().exitCurrentState();
      return true;
    }

    return super.keyUp(keycode);
  }
}
