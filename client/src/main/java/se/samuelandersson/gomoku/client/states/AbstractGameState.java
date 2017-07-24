package se.samuelandersson.gomoku.client.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.Settings;
import se.samuelandersson.gomoku.client.Settings.Setting;

public class AbstractGameState implements InputProcessor, GameState
{
  public static final int CONNECTGAMESTATE = 1;
  public static final int CHOOSEGAMESTATE = 2;
  public static final int CREATEGAMESTATE = 3;
  public static final int GAMEPLAYSTATE = 4;
  public static final int MAINMENUSTATE = 5;
  public static final int PAUSEMENUSTATE = 6;
  public static final int OPTIONSMENUSTATE = 7;

  public static final int VIEWPORT_WIDTH = 1280;
  public static final int VIEWPORT_HEIGHT = 720;

  private Stage stage;
  private OrthographicCamera stageCamera;
  private Table table;

  private Viewport stageViewport;
  private SpriteBatch batch;
  private InputMultiplexer inputMultiplexer;

  private GomokuClient application;
  
  private Image background;

  public AbstractGameState(GomokuClient application)
  {
    this.application = application;
    this.batch = new SpriteBatch();
    this.stageCamera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    this.stageViewport = new ScreenViewport(this.stageCamera);
    this.stage = new Stage(stageViewport, batch);
    this.stage.setDebugAll(Settings.getInstance().getBoolean(Setting.DEBUG));

    inputMultiplexer = new InputMultiplexer();
    inputMultiplexer.addProcessor(stage);
    inputMultiplexer.addProcessor(this);
  }
  
  @Override
  public InputProcessor getInputProcessor()
  {
    return this.inputMultiplexer;
  }

  @Override
  public void initialize()
  {
    this.background = new Image(Assets.getInstance().getDrawable("background"));
    this.stage.addActor(this.background);
  }

  @Override
  public void updateOnce(float delta)
  {
  }

  @Override
  public void update(float delta)
  {
    stage.act(delta);
  }

  @Override
  public void render()
  {
    Gdx.gl.glClearColor(0, 0, 0, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
    this.stage.draw();
  }

  @Override
  public void dispose()
  {
    this.stage.dispose();
  }

  @Override
  public void resize(final int width, final int height)
  {
  }

  @Override
  public void show()
  {
    Gdx.input.setInputProcessor(this.getInputProcessor());
  }

  @Override
  public void hide()
  {
  }

  public Stage getStage()
  {
    return stage;
  }

  public Table getTable()
  {
    if (table == null)
    {
      table = new Table(Assets.getInstance().getSkin());
      table.setFillParent(true);
    }

    return table;
  }

  public SpriteBatch getBatch()
  {
    return batch;
  }

  public InputMultiplexer getInputMultiplexer()
  {
    return inputMultiplexer;
  }

  public GomokuClient getApplication()
  {
    return application;
  }
  
  @Override
  public boolean keyDown(int keycode)
  {
    return false;
  }

  @Override
  public boolean keyUp(int keycode)
  {
    return false;
  }

  @Override
  public boolean keyTyped(char character)
  {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button)
  {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button)
  {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer)
  {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY)
  {
    return false;
  }

  @Override
  public boolean scrolled(int amount)
  {
    return false;
  }
}
