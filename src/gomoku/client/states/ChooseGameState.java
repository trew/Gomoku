package gomoku.client.states;

import gomoku.client.GomokuClient;

import gomoku.client.gui.Button;
import gomoku.client.gui.GameList;
import gomoku.client.gui.TextField;
import gomoku.net.GameListPacket;
import gomoku.net.InitialServerDataPacket;
import gomoku.net.JoinGamePacket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryonet.Connection;

import static org.trew.log.Log.*;

public class ChooseGameState extends GomokuNetworkGameState {

    private GameList gameList;
    private TextField selectedGameIDField;

    private Button createNewGameButton;
    private Button joinGameButton;
    private Button backButton;

    private GomokuClient gomokuClient;

    private BounceListener listener;

    public ChooseGameState() {
    }

    /**
     * When entering this game state, request initial data from the server such
     * as the board, our player color and the current turn
     */
    @Override
    public void enter(GameContainer container, GomokuClient gomokuClient)
            throws SlickException {
        gomokuClient.client.addListener(listener);
    }

    @Override
    public void leave(GameContainer container, GomokuClient game)
            throws SlickException {
        gomokuClient.client.removeListener(listener);
    }

    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {
        listener = new BounceListener(this);
        gomokuClient = game;

        gameList = new GameList(container, 80, 50, 640, 300);
        Image textfield = new Image("res/textfield.png");
        selectedGameIDField = new TextField(container, textfield,
                container.getDefaultFont(), 535, 435, 50);

        Image cgBtn = new Image("res/buttons/creategamebutton.png");
        createNewGameButton = new Button(cgBtn, 80, 360) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                gomokuClient.enterState(CREATEGAMESTATE);
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
        Image bBtn = new Image("res/buttons/backbutton.png");
        backButton = new Button(bBtn, 250, 500) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    game.enterState(MAINMENUSTATE);
                }
            }
        };
        backButton.setCenterX(container.getWidth() / 2);

        addListener(selectedGameIDField);
        addListener(createNewGameButton);
        addListener(joinGameButton);
        addListener(backButton);
    }

    public void joinGame() {
        // int selectedID = gameList.getSelectedID();
        int selectedID = -1;
        try {
            selectedID = Integer.parseInt(selectedGameIDField.getText());
        } catch (NumberFormatException e) {
            return;
        }
        if (gameList.validID(selectedID)) {
            gomokuClient.client.sendTCP(new JoinGamePacket(selectedID));
        }
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        g.drawImage(game.getBackground(), 0, 0);

        gameList.render(container, g);
        createNewGameButton.render(container, g);
        joinGameButton.render(container, g);
        g.drawString("GameID:", 460, 440);
        selectedGameIDField.render(container, g);
        backButton.render(container, g);
    }

    public void addGame(String gameName, int gameID) {
        gameList.add(gameName, gameID);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
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
        gameList.clear();
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
                isdp.getBoard(), isdp.getConfig(), isdp.getColor(), isdp.getTurn(),
                isdp.getPlayerList());
        gomokuClient.enterState(GAMEPLAYSTATE);
    }

    @Override
    public int getID() {
        return CHOOSEGAMESTATE;
    }

}
