package se.samuelandersson.gomoku.client.states;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.esotericsoftware.kryonet.Connection;

import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.net.PacketHandler;
import se.samuelandersson.gomoku.net.GameListPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.JoinGamePacket;
import se.samuelandersson.gomoku.net.Request;

public class ChooseGameState extends MenuState implements PacketHandler
{

  private static final Logger log = LoggerFactory.getLogger(ChooseGameState.class);

  private Button createNewGameButton;
  private Button joinGameButton;
  private Button backButton;

  private List<GameListObject> gameList;

  public ChooseGameState(GomokuClient app)
  {
    super(app);
  }

  @Override
  public void initialize()
  {
    super.initialize();

    Skin skin = Assets.getInstance().getSkin();

    gameList = new List<GameListObject>(skin, "gameList");

    createNewGameButton = new TextButton("Create Game", skin);
    this.createNewGameButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        final GomokuClient app = ChooseGameState.this.getApplication();
        app.setNextState(app.getState(CreateGameState.class));
      }
    });

    joinGameButton = new TextButton("Join Game", skin);
    joinGameButton.setDisabled(true);
    this.joinGameButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        ChooseGameState.this.joinGame();
      }
    });

    backButton = new TextButton("Back", skin);
    this.backButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        final GomokuClient app = ChooseGameState.this.getApplication();
        app.getClient().disconnect();

        if (app.getServer() != null)
        {
          app.stopServer();
          app.setNextState(MainMenuState.class);
        }
        else
        {
          app.setNextState(JoinOrStartServerState.class);
        }
      }
    });

    this.gameList.addListener(new ClickListener(Input.Buttons.LEFT)
    {
      @Override
      public void clicked(InputEvent event, float x, float y)
      {
        if (this.getTapCount() > 1)
        {
          joinGame();
        }

        if (gameList.getSelectedIndex() >= 0)
        {
          joinGameButton.setDisabled(false);
        }
        else
        {
          joinGameButton.setDisabled(true);
        }
      }
    });

    this.getTable().add(new Label("Games:", skin)).left().padBottom(0).spaceBottom(0);
    this.getTable().row();
    this.getTable().add(this.gameList).minSize(200, 100).padTop(0).spaceTop(0);
    this.getTable().row();
    this.getTable().add(this.createNewGameButton);
    this.getTable().row();
    this.getTable().add(this.joinGameButton);
    this.getTable().row();
    this.getTable().add(this.backButton);

    this.getStage().addActor(this.getTable());
  }

  public void joinGame()
  {
    GameListObject selected = this.gameList.getSelected();
    if (selected != null)
    {
      int id = selected.id;
      if (id >= 0)
      {
        this.getApplication().getClient().sendTCP(new JoinGamePacket(id));
      }
    }
  }

  @Override
  public void show()
  {
    super.show();

    this.getApplication().getClient().sendTCP(Request.GET_GAME_LIST);
  }

  @Override
  public boolean keyUp(int keycode)
  {
    if (keycode == Input.Keys.F5)
    {
      this.getApplication().getClient().sendTCP(Request.GET_GAME_LIST);
      return true;
    }
    else if (keycode == Input.Keys.ESCAPE)
    {
      this.gameList.setSelectedIndex(-1);
      this.joinGameButton.setDisabled(true);
    }

    return false;
  }

  /**
   * Clear the current gamelist and replace it with the new list
   */
  @Override
  public void handleGameList(Connection connection, GameListPacket glp)
  {
    log.debug("Received GameListPacket");
    if (glp.gameID.length != glp.gameName.length)
    {
      log.warn("GameListPacket-list not of same size: {} != {}", glp.gameID.length, glp.gameName.length);
      return;
    }

    ArrayList<GameListObject> elements = new ArrayList<GameListObject>();
    for (int x = 0; x < glp.gameID.length; x++)
    {
      elements.add(new GameListObject(glp.gameID[x], glp.gameName[x]));
    }

    this.gameList.setItems(elements.toArray(new GameListObject[elements.size()]));
    this.gameList.setSelectedIndex(-1);
    this.joinGameButton.setDisabled(true);
  }

  /**
   * The server said we're good to go, prepare the GamePlayState and enter it.
   */
  @Override
  public void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
  {
    final GameplayState gps = this.getApplication().getState(GameplayState.class);
    gps.setInitialData(isdp.getBoard(),
                       isdp.getConfig(),
                       isdp.getPlayerColor(),
                       isdp.getPlayerColorCurrentTurn(),
                       isdp.getPlayerList());
    this.getApplication().setNextState(gps);
  }

  public static class GameListObject
  {
    public String name;
    public int id;

    public GameListObject(int id, String name)
    {
      this.id = id;
      this.name = name;
    }

    @Override
    public boolean equals(Object arg0)
    {
      if (arg0 instanceof GameListObject)
      {
        GameListObject o = (GameListObject) arg0;
        return (this.id == o.id && this.name.equals(o.name));
      }

      return false;
    }

    @Override
    public String toString()
    {
      if (name != null)
      {
        return name;
      }

      return "<Empty>";
    }
  }
}
