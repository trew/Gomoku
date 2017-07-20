package se.samuelandersson.gomoku.client;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.matthiasmann.twl.slick.TWLStateBasedGame;
import se.samuelandersson.gomoku.client.gui.Fonts;
import se.samuelandersson.gomoku.client.states.AbstractGameState;
import se.samuelandersson.gomoku.client.states.ChooseGameState;
import se.samuelandersson.gomoku.client.states.ConnectState;
import se.samuelandersson.gomoku.client.states.CreateGameState;
import se.samuelandersson.gomoku.client.states.GameplayState;
import se.samuelandersson.gomoku.client.states.MainMenuState;
import se.samuelandersson.gomoku.client.states.OptionsMenuState;
import se.samuelandersson.gomoku.client.states.PauseMenu;

/**
 * The main entry class for the Gomoku client.
 *
 * @author Samuel Andersson
 *
 */
public class GomokuClient extends TWLStateBasedGame
{
  private static final Logger log = LoggerFactory.getLogger(GomokuClient.class);

  /* ********** STATIC ********** */
  /** The width of the screen */
  private static final int WIDTH = 800;

  /** The height of the screen */
  private static final int HEIGHT = 600;

  /** The target max frame rate */
  private static final int TARGET_FPS = 60;

  /* ********** END STATIC ********** */

  /** The network client */
  private NetworkClient client;

  private Settings settings;

  /**
   * Create a new game client
   */
  public GomokuClient()
  {
    super("Gomoku");
    client = new NetworkClient();
  }

  public NetworkClient getNetworkClient()
  {
    return this.client;
  }

  public void setNetworkClient(final NetworkClient client)
  {
    this.client = client;
  }

  public Settings getSettings()
  {
    return this.settings;
  }

  /**
   * @see StateBasedGame#initStatesList(GameContainer)
   */
  @Override
  public void initStatesList(GameContainer container) throws SlickException
  {
    settings = new Settings();
    settings.loadProperties();

    Fonts.loadAngelCodeFonts("fonts/messagebox", "fonts/nametag", "fonts/default");
    container.setDefaultFont(Fonts.getDefaultFont());

    this.addState(new MainMenuState());
    this.addState(new ConnectState());
    this.addState(new ChooseGameState());
    this.addState(new CreateGameState());
    this.addState(new GameplayState());
    this.addState(new PauseMenu());
    this.addState(new OptionsMenuState());
  }

  /**
   * The main entry point of the game client
   *
   * @param args
   *          The arguments passed to the application
   * @throws URISyntaxException
   * @throws MalformedURLException
   * @see #parseArgs(String[])
   */
  public static void main(String[] args) throws MalformedURLException, URISyntaxException
  {
    try
    {
      // create the StateBasedGame to be passed to the container
      GomokuClient game = new GomokuClient();

      System.setProperty("org.lwjgl.librarypath", new File("lib/lwjgl-2.9.3/native/windows").getAbsolutePath());
      // create the container
      AppGameContainer container = new AppGameContainer(game);

      // set display mode and configurations
      Input.disableControllers();
      container.setDisplayMode(WIDTH, HEIGHT, false);
      container.setTargetFrameRate(TARGET_FPS);
      container.setShowFPS(false);
      container.setAlwaysRender(true);
      container.setUpdateOnlyWhenVisible(false);
      container.setForceExit(false); // we want to call functions after the game is cleaned up

      // start the game
      try
      {
        container.start();
      }
      finally
      {
        // stop the client before exiting
        game.client.stop();
        game.settings.storeProperties();
      }
    }
    catch (SlickException e)
    {
      log.error("Error", e.getMessage());
    }
  }
  
  @Override
  protected URL getThemeURL()
  {
    return ResourceLoader.getResource("theme/theme.xml");
  }
}
