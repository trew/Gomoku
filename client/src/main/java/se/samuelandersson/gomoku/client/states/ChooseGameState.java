package se.samuelandersson.gomoku.client.states;

import java.util.ArrayList;
import java.util.Collection;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.CallbackWithReason;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ListBox.CallbackReason;
import de.matthiasmann.twl.model.SimpleListModel;
import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.net.GameListPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.JoinGamePacket;
import se.samuelandersson.gomoku.net.Request;

public class ChooseGameState extends AbstractNetworkGameState
{

  private static final Logger log = LoggerFactory.getLogger(ChooseGameState.class);

  private Button createNewGameButton;
  private Button joinGameButton;
  private Button backButton;

  private GomokuClient gomokuClient;

  private ListBox<GameListModel> gameList;
  private GameListModel gameListModel;

  public ChooseGameState()
  {
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    gameListModel = new GameListModel();
    gameList = new ListBox(gameListModel);

    gameList.setPosition(80, 50);
    gameList.setSize(620, 300);

    createNewGameButton = new Button("Create Game");
    createNewGameButton.setPosition(80, 360);
    createNewGameButton.setSize(290, 60);
    rp.add(createNewGameButton);

    joinGameButton = new Button("Join Game");
    joinGameButton.setPosition(405, 360);
    joinGameButton.setSize(290, 60);
    joinGameButton.setEnabled(false);
    rp.add(joinGameButton);

    backButton = new Button("Back");
    backButton.setPosition(250, 500);
    backButton.setSize(300, 60);
    rp.add(backButton);

    rp.add(gameList);

    return rp;
  }

  @Override
  public void init(GameContainer container, final GomokuClient game) throws SlickException
  {
    gomokuClient = game;

    createNewGameButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        enterState(CREATEGAMESTATE);
      }
    });

    joinGameButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        joinGame();
      }
    });

    gameList.addCallback(new CallbackWithReason<ListBox.CallbackReason>()
    {
      @Override
      public void callback(CallbackReason reason)
      {
        if (reason.actionRequested())
        {
          joinGame();
        }

        else if (gameList.getSelected() >= 0)
        {
          joinGameButton.setEnabled(true);
        }
        else
        {
          joinGameButton.setEnabled(false);
        }
      }
    });

    backButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        enterState(MAINMENUSTATE);
      }
    });
  }

  public void joinGame()
  {
    int id = gameListModel.getEntry(gameList.getSelected()).id;
    if (id >= 0)
    {
      gomokuClient.getNetworkClient().sendTCP(new JoinGamePacket(id));
    }
  }

  @Override
  public void enter(GameContainer container, GomokuClient game) throws SlickException
  {
    game.getNetworkClient().sendTCP(Request.GET_GAME_LIST);
  }

  @Override
  public void render(GameContainer container, GomokuClient game, Graphics g) throws SlickException
  {
  }

  @Override
  public void update(GameContainer container, GomokuClient game, int delta) throws SlickException
  {
    Input input = container.getInput();

    if (input.isKeyPressed(Input.KEY_F5))
    {
      game.getNetworkClient().sendTCP(Request.GET_GAME_LIST);
    }
  }

  /**
   * Clear the current gamelist and replace it with the new list
   */
  @Override
  protected void handleGameList(Connection connection, GameListPacket glp)
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

    gameListModel.updateElements(elements);
  }

  /**
   * The server said we're good to go, prepare the GamePlayState and enter it.
   */
  @Override
  protected void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
  {
    ((GameplayState) gomokuClient.getState(GAMEPLAYSTATE)).setInitialData(isdp.getBoard(),
                                                                          isdp.getConfig(),
                                                                          isdp.getID(),
                                                                          isdp.getTurn(),
                                                                          isdp.getPlayerList(),
                                                                          isdp.getPlayerOneColor(),
                                                                          isdp.getPlayerTwoColor());
    enterState(GAMEPLAYSTATE);
  }

  @Override
  public int getID()
  {
    return CHOOSEGAMESTATE;
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

  public static class GameListModel extends SimpleListModel<GameListObject>
  {
    private ArrayList<GameListObject> content;

    public GameListModel()
    {
      this.content = new ArrayList<GameListObject>();
    }

    @Override
    public int getNumEntries()
    {
      return content.size();
    }

    @Override
    public GameListObject getEntry(int index)
    {
      return content.get(index);
    }

    public void updateElements(Collection<GameListObject> elements)
    {
      // remove all current elements that is not
      int entriesRemovedFirst = -1;
      int entriesRemovedLast = -1;
      int entriesAddedFirst = -1;
      int entriesAddedLast = -1;
      int entryI = -1;
      ArrayList<GameListObject> elementsToRemove = new ArrayList<GameListObject>();
      for (GameListObject element : content)
      {
        if (!elements.contains(element))
        {
          elementsToRemove.add(element);
          entryI = content.indexOf(element);

          if (entryI < entriesRemovedFirst || entriesRemovedFirst < 0)
          {
            entriesRemovedFirst = entryI;
          }

          if (entryI > entriesRemovedLast || entriesRemovedLast < 0)
          {
            entriesRemovedLast = entryI;
          }
        }
      }
      content.removeAll(elementsToRemove);

      if (entriesRemovedFirst >= 0 && entriesRemovedLast >= 0)
      {
        fireEntriesDeleted(entriesRemovedFirst, entriesRemovedLast);
      }

      for (GameListObject element : elements)
      {
        if (!content.contains(element))
        {
          content.add(element);
          entryI = content.indexOf(element);

          if (entryI < entriesAddedFirst || entriesAddedFirst < 0)
          {
            entriesAddedFirst = entryI;
          }

          if (entryI > entriesAddedLast || entriesAddedLast < 0)
          {
            entriesAddedLast = entryI;
          }
        }
      }

      if (entriesAddedFirst >= 0 && entriesAddedLast >= 0)
      {
        fireEntriesInserted(entriesAddedFirst, entriesAddedLast);
      }
    }
  }
}
