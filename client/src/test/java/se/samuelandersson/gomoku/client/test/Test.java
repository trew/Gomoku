package se.samuelandersson.gomoku.client.test;

import java.io.File;
import java.net.URL;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

import de.matthiasmann.twl.slick.TWLStateBasedGame;

public class Test extends TWLStateBasedGame
{
  public static void main(String[] args)
  {
    System.setProperty("org.lwjgl.librarypath", new File("lib/lwjgl-2.9.3/native/windows").getAbsolutePath());

    Test test = new Test();
    try
    {
      AppGameContainer container = new AppGameContainer(test);

      // set display mode and configurations
      Input.disableControllers();
      container.setDisplayMode(800, 600, false);
      container.setTargetFrameRate(60);
      container.setShowFPS(false);
      container.setForceExit(false);

      // start the game
      container.start();

    }
    catch (SlickException e)
    {
      e.printStackTrace();
    }
  }

  public Test()
  {
    super("Test");
  }

  @Override
  public void initStatesList(GameContainer container) throws SlickException
  {
    addState(new MyTestState());
  }

  @Override
  protected URL getThemeURL()
  {
    return ResourceLoader.getResource("theme/theme.xml");
  }

}
