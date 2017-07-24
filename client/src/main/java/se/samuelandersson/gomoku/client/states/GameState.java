package se.samuelandersson.gomoku.client.states;

import com.badlogic.gdx.InputProcessor;

public interface GameState
{
  InputProcessor getInputProcessor();
  
  void initialize();

  void dispose();

  void update(float delta);

  void updateOnce(float delta);

  void render();
  
  void hide();

  void show();

  void resize(int width, int height);
}
