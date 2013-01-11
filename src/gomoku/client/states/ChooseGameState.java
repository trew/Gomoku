package gomoku.client.states;

import gomoku.client.GomokuClient;

import gomoku.client.gui.Button;
import gomoku.client.gui.GameList;
import gomoku.net.GameListPacket;
import gomoku.net.InitialClientDataPacket;
import gomoku.net.InitialServerDataPacket;
import gomoku.net.JoinGamePacket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryonet.Connection;

import static com.esotericsoftware.minlog.Log.*;

public class ChooseGameState extends GomokuGameState {

    private GameList gameList;

    private Button createNewGameButton;
    private Button joinGameButton;

    private GomokuClient gomokuClient;

    public ChooseGameState() {
    }

    /**
     * When entering this game state, request initial data from the server such
     * as the board, our player color and the current turn
     */
    @Override
    public void enter(GameContainer container, GomokuClient gomokuClient)
            throws SlickException {
        gomokuClient.client.sendTCP(new InitialClientDataPacket(gomokuClient
                .getPlayerName()));
    }

    @Override
    public void init(GameContainer container, GomokuClient game)
            throws SlickException {
        // a bouncelistener will return all events to the corresponding
        // functions in this state (connected, received, etc)
        game.client.addListener(new BounceListener(this));
        gomokuClient = game;

        gameList = new GameList(container, 100, 50, 600, 400);

        createNewGameButton = new Button(container, "Create new game", 100, 460) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                gomokuClient.enterState(CREATEGAMESTATE);
            }
        };
        joinGameButton = new Button(container, "Join Game", 300, 460) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                joinGame();
            }
        };
    }

    public void joinGame() {
        int selectedID = gameList.getSelectedID();
        if (selectedID > 0) {
            gomokuClient.client.sendTCP(new JoinGamePacket(selectedID));
        }
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {

        gameList.render(container, g);
        createNewGameButton.render(container, g);
        joinGameButton.render(container, g);
    }

    public void addGame(String gameName, int gameID) {
        gameList.add(gameName, gameID);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {

    }

    @Override
    protected void handleGameList(Connection connection, GameListPacket glp) {
        debug("ChooseGameState", "Received GameListPacket");
        if (glp.gameID.length != glp.gameName.length) {
            warn("ChooseGameState", "GameListPacket-list not of same size.");
            return;
        }
        gameList.clear();
        for (int x = 0; x < glp.gameID.length; x++) {
            addGame(glp.gameName[x], glp.gameID[x]);
        }
    }

    @Override
    protected void handleInitialServerData(Connection connection, InitialServerDataPacket isdp) {
        ((GameplayState) gomokuClient.getState(GAMEPLAYSTATE))
        .setInitialData(isdp.getBoard(), isdp.getColor(),
                isdp.getTurn(), isdp.getPlayerList());
        gomokuClient.enterState(GAMEPLAYSTATE);
    }


    @Override
    public int getID() {
        return CHOOSEGAMESTATE;
    }

    @Override
    public void leave(GameContainer container, GomokuClient game)
            throws SlickException {
    }

}
