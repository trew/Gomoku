package se.samuelandersson.gomoku.client;

import static se.samuelandersson.gomoku.client.Arguments.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import se.samuelandersson.gomoku.client.states.GameState;

public class StateApplicationListener implements ApplicationListener
{
  private static final Logger log = LoggerFactory.getLogger(StateApplicationListener.class);

  public static boolean DEBUG = false;

  protected static class StateHolder
  {
    protected StateHolder parent;
    protected boolean renderParent;

    protected GameState state;

    public StateHolder(final GameState state)
    {
      this(state, null, false);
    }

    public StateHolder(final GameState state, final StateHolder parent, final boolean renderParent)
    {
      this.state = state;
      this.parent = parent;
      this.renderParent = renderParent;
    }
  }

  private StateHolder currentState;
  private StateHolder nextState;

  private ShapeRenderer shapeRenderer;

  /** The target max frame rate */
  public static final int TARGET_FPS = 60;

  private float accumulator;
  public static final float TIMESTEP = 1.f / TARGET_FPS;
  private float speed = 1f;

  /** Lowercase name to be used for files, etc */
  public static String getSimpleName()
  {
    return "gomoku";
  }

  @Override
  public void create()
  {
    Assets.getInstance().load();
    shapeRenderer = new ShapeRenderer();
  }

  public ShapeRenderer getShapeRenderer()
  {
    return shapeRenderer;
  }

  protected void enterState(StateHolder state)
  {
    if (currentState != null)
    {
      this.exitState(currentState);
    }

    currentState = state;
    currentState.state.show();
  }

  protected void exitState(StateHolder state)
  {
    state.state.hide();
  }

  private void renderState(StateHolder state)
  {
    if (state.renderParent)
    {
      this.renderState(state.parent);
    }

    state.state.render();
  }

  @Override
  public void render()
  {
    // Update states, use while loop because entering a state might trigger a new state change
    while (nextState != null)
    {
      final StateHolder newState = nextState;
      nextState = null;
      enterState(newState);
    }

    if (this.currentState != null)
    {
      float delta = Gdx.graphics.getRawDeltaTime();
      accumulator += delta;
      boolean updateOnce = (accumulator > TIMESTEP / speed);

      while (accumulator > TIMESTEP / speed)
      {
        this.currentState.state.update(TIMESTEP);
        accumulator -= TIMESTEP / speed;
      }

      if (updateOnce)
      {
        this.currentState.state.updateOnce(TIMESTEP);
      }

      renderState(this.currentState);
    }
  }

  @Override
  public void dispose()
  {
    Assets.getInstance().dispose();
  }

  @Override
  public void pause()
  {
  }

  @Override
  public void resume()
  {
  }

  public void exit()
  {
    Gdx.app.exit();
  }

  @Override
  public void resize(int width, int height)
  {
    log.debug("Resizing to " + width + "x" + height);
    if (this.currentState != null)
    {
      this.currentState.state.resize(width, height);
    }
  }

  public void setNextState(Class<? extends GameState> state)
  {
    throw new IllegalStateException("This must be implemented in a subclass");
  }
  
  public void setNextState(GameState state)
  {
    this.setNextState(state, false, false);
  }

  public void setNextState(GameState state, boolean rememberParent, boolean renderParent)
  {
    assertNotNull("state", state);
    if (rememberParent)
    {
      nextState = new StateHolder(state, this.currentState, renderParent);
    }
    else
    {
      nextState = new StateHolder(state);
    }
  }

  public void exitCurrentState()
  {
    if (this.currentState.parent == null)
    {
      throw new IllegalStateException("Unable to exit current state without a parent state");
    }

    this.nextState = this.currentState.parent;
  }

  public GameState getCurrentState()
  {
    return this.currentState.state;
  }
}
