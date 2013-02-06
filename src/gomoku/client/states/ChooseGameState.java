package gomoku.client.states;

import gomoku.client.GomokuClient;

import gomoku.client.gui.Button;
import gomoku.net.GameListPacket;
import gomoku.net.GenericRequestPacket;
import gomoku.net.InitialServerDataPacket;
import gomoku.net.JoinGamePacket;
import gomoku.net.Request;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import TWLSlick.RootPane;

import com.esotericsoftware.kryonet.Connection;

import de.matthiasmann.twl.CallbackWithReason;
import de.matthiasmann.twl.ListBox;
import de.matthiasmann.twl.ListBox.CallbackReason;
import de.matthiasmann.twl.model.SimpleChangableListModel;

import static org.trew.log.Log.*;

public class ChooseGameState extends GomokuNetworkGameState {

    static public class GameListObject {
        public String name;
        public int id;

        public GameListObject(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            if (name != null)
                return name;
            return "<Empty>";
        }
    }

    private Button createNewGameButton;
    private Button joinGameButton;
    private Button backButton;

    private GomokuClient gomokuClient;

    private ListBox<SimpleChangableListModel<GameListObject>> gameList;
    private SimpleChangableListModel<GameListObject> gameListModel;

    private static final long UPDATETIME = 10000; // update list every 10
                                                  // seconds
    private long updateTimer;

    public ChooseGameState() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public RootPane createRootPane() {
        RootPane rp = super.createRootPane();

        gameListModel = new SimpleChangableListModel<GameListObject>();
        gameList = new ListBox(gameListModel);

        gameList.setPosition(80, 50);
        gameList.setSize(620, 300);

        rp.add(gameList);
        return rp;
    }

    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {
        gomokuClient = game;

        Image cgBtn = new Image("res/buttons/creategamebutton.png");
        createNewGameButton = new Button(cgBtn, 80, 360) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                enterState(CREATEGAMESTATE);
            }
        };
        Image jgBtn = new Image("res/buttons/joingamebutton.png");
        joinGameButton = new Button(jgBtn, 360, 360) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                joinGame();
            }
        };
        joinGameButton.setRightX(gameList.getX() + gameList.getWidth());
        joinGameButton.disable();

        gameList.addCallback(new CallbackWithReason<ListBox.CallbackReason>() {
            @Override
            public void callback(CallbackReason reason) {
                if (reason.actionRequested())
                    joinGame();
                else if (gameList.getSelected() >= 0) {
                    joinGameButton.enable();
                } else {
                    joinGameButton.disable();
                }
            }
        });

        Image bBtn = new Image("res/buttons/backbutton.png");
        backButton = new Button(bBtn, 250, 500) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    enterState(MAINMENUSTATE);
                }
            }
        };
        backButton.setCenterX(container.getWidth() / 2);

        addListener(createNewGameButton);
        addListener(joinGameButton);
        addListener(backButton);
    }

    public void joinGame() {
        try {
            int id = gameListModel.getEntry(gameList.getSelected()).id;
            if (id >= 0)
                gomokuClient.client.sendTCP(new JoinGamePacket(id));
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    @Override
    public void enter(GameContainer container, GomokuClient game)
            throws SlickException {
        game.client.sendTCP(new GenericRequestPacket(Request.GameList));
        updateTimer = UPDATETIME;

    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        createNewGameButton.render(container, g);
        joinGameButton.render(container, g);
        backButton.render(container, g);
    }

    public void addGame(String gameName, int gameID) {
        gameListModel.addElement(new GameListObject(gameID, gameName));
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
        updateTimer -= delta;
        if (updateTimer < 0) {
            game.client.sendTCP(new GenericRequestPacket(Request.GameList));
            updateTimer = UPDATETIME;
        }
    }

    /**
     * Clear the current gamelist and replace it with the new list
     */
    @Override
    protected void handleGameList(Connection connection, GameListPacket glp) {
        debug("Received GameListPacket");
        if (glp.gameID.length != glp.gameName.length) {
            warn("GameListPacket-list not of same size.");
            return;
        }
        gameListModel.clear();
        for (int x = 0; x < glp.gameID.length; x++) {
            addGame(glp.gameName[x], glp.gameID[x]);
        }
    }

    /**
     * The server said we're good to go, prepare the GamePlayState and enter it.
     */
    @Override
    protected void handleInitialServerData(Connection connection,
            InitialServerDataPacket isdp) {
        ((GameplayState) gomokuClient.getState(GAMEPLAYSTATE)).setInitialData(
                isdp.getBoard(), isdp.getConfig(), isdp.getColor(),
                isdp.getTurn(), isdp.getPlayerList());
        enterState(GAMEPLAYSTATE);
    }

    @Override
    public int getID() {
        return CHOOSEGAMESTATE;
    }

}
