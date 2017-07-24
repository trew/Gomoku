package se.samuelandersson.gomoku.client.states;

import com.badlogic.gdx.Input;

import se.samuelandersson.gomoku.client.GomokuClient;

public class OptionsMenuState extends AbstractGameState
{
  public OptionsMenuState(GomokuClient app)
  {
    super(app);
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
