package se.samuelandersson.gomoku.client.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.client.GomokuClient;

public class MainMenuState extends AbstractGameState
{
  private Image gomokuTitle;

  private Button multiPlayerButton;
  private Button optionsButton;
  private Button exitButton;

  public MainMenuState()
  {
  }

  @Override
  public RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    multiPlayerButton = new Button("Play Multiplayer");
    multiPlayerButton.setSize(300, 50);
    multiPlayerButton.setPosition(250, 200);
    rp.add(multiPlayerButton);

    optionsButton = new Button("Options");
    optionsButton.setSize(300, 50);
    optionsButton.setPosition(250, 280);
    optionsButton.setEnabled(false);
    rp.add(optionsButton);

    exitButton = new Button("Exit Game");
    exitButton.setSize(300, 50);
    exitButton.setPosition(250, 360);
    rp.add(exitButton);

    return rp;
  }

  @Override
  public void init(final GameContainer container, final GomokuClient game) throws SlickException
  {
    final GameContainer gamecontainer = container;

    gomokuTitle = new Image("gomoku.png");

    multiPlayerButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        enterState(CONNECTGAMESTATE);
      }
    });

    optionsButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        enterState(OPTIONSMENUSTATE, (AbstractGameState) game.getCurrentState());
      }
    });

    exitButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        gamecontainer.exit();
      }
    });
  }

  @Override
  public void render(GameContainer container, GomokuClient game, Graphics g) throws SlickException
  {
    g.drawImage(gomokuTitle, 16, 30);
  }

  @Override
  public void update(GameContainer container, GomokuClient game, int delta) throws SlickException
  {
  }

  @Override
  public void enter(GameContainer container, GomokuClient game) throws SlickException
  {
  }

  @Override
  public void leave(GameContainer container, GomokuClient game) throws SlickException
  {
  }

  @Override
  public int getID()
  {
    return MAINMENUSTATE;
  }

}
