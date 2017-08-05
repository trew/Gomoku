package se.samuelandersson.gomoku.client;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main
{
  @SuppressWarnings("unused")
  public static void main(String[] args)
  {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.width = GomokuClient.WIDTH;
    cfg.height = GomokuClient.HEIGHT;
    cfg.backgroundFPS = StateApplicationListener.TARGET_FPS;
    cfg.foregroundFPS = StateApplicationListener.TARGET_FPS;
    cfg.fullscreen = false;
    cfg.resizable = true;
    cfg.title = "Gomoku";

    new LwjglApplication(new GomokuClient(), cfg);
  }
}
