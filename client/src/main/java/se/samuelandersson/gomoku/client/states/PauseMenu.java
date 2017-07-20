package se.samuelandersson.gomoku.client.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.DynamicImage;
import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.net.Request;

public class PauseMenu extends AbstractGameState
{
  private Button continueButton;
  private Button optionsButton;
  private Button exitToMenuButton;
  private Button exitToOSButton;

  public PauseMenu()
  {
    setRenderingParent(true);
  }

  @Override
  protected RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    continueButton = new Button("Continue");
    continueButton.setSize(300, 50);
    continueButton.setPosition(250, 150);
    rp.add(continueButton);

    optionsButton = new Button("Options");
    optionsButton.setSize(300, 50);
    optionsButton.setPosition(250, 220);
    optionsButton.setEnabled(false);
    rp.add(optionsButton);

    exitToMenuButton = new Button("Exit to Menu");
    exitToMenuButton.setSize(300, 50);
    exitToMenuButton.setPosition(250, 290);
    rp.add(exitToMenuButton);

    exitToOSButton = new Button("Exit to OS");
    exitToOSButton.setSize(300, 50);
    exitToOSButton.setPosition(250, 360);
    rp.add(exitToOSButton);

    return rp;
  }

  @Override
  public void init(final GameContainer container, final GomokuClient game) throws SlickException
  {
    continueButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        exitState();
      }
    });
    optionsButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        AbstractNetworkGameState optionsState = ((AbstractNetworkGameState) game.getState(OPTIONSMENUSTATE));
        optionsState.setForwarding(true);
        optionsState.setStateToForwardTo((AbstractNetworkGameState) game.getState(GAMEPLAYSTATE));
        enterState(OPTIONSMENUSTATE, (AbstractGameState) game.getCurrentState());
      }
    });
    exitToMenuButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        getGame().getNetworkClient().sendTCP(Request.LEAVE_GAME);
        enterState(MAINMENUSTATE);
      }
    });
    exitToOSButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        container.exit();
      }
    });
  }

  @Override
  public void leave(GameContainer container, GomokuClient game) throws SlickException
  {
    AbstractNetworkGameState optionsState = ((AbstractNetworkGameState) game.getState(OPTIONSMENUSTATE));
    optionsState.setForwarding(false);
    optionsState.setStateToForwardTo(null);
  }

  @Override
  public void update(GameContainer container, GomokuClient game, int delta) throws SlickException
  {
    Input input = container.getInput();

    if (input.isKeyPressed(Input.KEY_ESCAPE))
    {
      exitState();
    }
  }

  @Override
  public void render(GameContainer container, GomokuClient game, Graphics g) throws SlickException
  {
  }

  @Override
  public int getID()
  {
    return PAUSEMENUSTATE;
  }

}
