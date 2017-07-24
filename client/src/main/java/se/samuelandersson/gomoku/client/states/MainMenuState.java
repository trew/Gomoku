package se.samuelandersson.gomoku.client.states;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;

public class MainMenuState extends MenuState
{
  private Button multiPlayerButton;
  private Button optionsButton;
  private Button exitButton;

  public MainMenuState(GomokuClient app)
  {
    super(app);
  }

  @Override
  public void initialize()
  {
    super.initialize();

    Skin skin = Assets.getInstance().getSkin();
    this.multiPlayerButton = new TextButton("Play Multiplayer", skin);
    this.multiPlayerButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        final GomokuClient application = MainMenuState.this.getApplication();
        application.setNextState(application.getState(JoinOrStartServerState.class));
      }
    });

    this.optionsButton = new TextButton("Options", skin);
    this.optionsButton.setDisabled(true);
    this.optionsButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        final GomokuClient application = MainMenuState.this.getApplication();
        application.setNextState(application.getState(OptionsMenuState.class), true, false);
      }
    });

    this.exitButton = new TextButton("Exit Game", skin);
    this.exitButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        MainMenuState.this.getApplication().exit();
      }
    });

    this.getTable().add(this.multiPlayerButton);
    this.getTable().row();
    this.getTable().add(this.optionsButton);
    this.getTable().row();
    this.getTable().add(this.exitButton);

    this.getStage().addActor(this.getTable());
  }
  
  @Override
  public void show()
  {
    super.show();
    
    if (this.getApplication().getClient().isConnected())
    {
      this.getApplication().getClient().disconnect();
    }
    
    if (this.getApplication().getServer() != null)
    {
      this.getApplication().stopServer();
    }
  }
}
