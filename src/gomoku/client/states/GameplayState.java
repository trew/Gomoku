package gomoku.client.states;

import static gomoku.net.Request.*;

import gomoku.client.GomokuClient;
import gomoku.client.gui.BoardComponent;
import gomoku.logic.Board;
import gomoku.logic.Player;
import gomoku.logic.GomokuGame;
import gomoku.net.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryonet.Connection;

import static com.esotericsoftware.minlog.Log.*;

/**
 * The playing state of the Gomoku game.
 *
 * @author Samuel Andersson
 */
public class GameplayState extends GomokuGameState {

    /** Contains the game logic */
    public GomokuGame gomokuGame;

    public GomokuClient client;

    /** The player */
    public Player me;

    /** The board displayed */
    private BoardComponent boardComponent;

    /** Enables network events to be passed to this state */
    private BounceListener listener;

    private boolean loading;

    private String[] playerList;

    public boolean initialLoading() {
        return loading;
    }

    public void setInitialData(Board board, int playerColor, int turn,
            String[] playerList) {
        // create a new game
        this.playerList = playerList;
        gomokuGame = new GomokuGame(board);
        gomokuGame.setTurn(gomokuGame.getPlayer(turn));
        setupPlayers(playerColor);

        boardComponent.setBoard(board);

        loading = false;
    }

    /**
     * Setup names and values for players. If this client receives black, makes
     * sure the white player receives the correct name.
     *
     * @param playerColor
     *            The player color being given to this client
     */
    public void setupPlayers(int playerColor) {

        if (playerColor == Board.BLACKPLAYER) {
            me = gomokuGame.getBlack();
            playerList[0] = client.getPlayerName();
            gomokuGame.getWhite().setName(playerList[1]);

        } else if (playerColor == Board.WHITEPLAYER) {
            me = gomokuGame.getWhite();
            playerList[1] = client.getPlayerName();
            gomokuGame.getBlack().setName(playerList[0]);

        } else {
            error("GameplayState", "Color couldn't bet set!");
            return;
        }

        me.setName(client.getPlayerName());
        info("GameplayState", "Color set to " + me.getColorName());
    }

    public String[] getPlayerList() {
        return playerList;
    }

    public void setPlayerList(String[] playerList) {
        this.playerList = playerList;

        if (me.getColor() == Board.BLACKPLAYER) {
            gomokuGame.getWhite().setName(playerList[1]);
        } else if (me.getColor() == Board.WHITEPLAYER) {
            gomokuGame.getBlack().setName(playerList[0]);
        }
    }

    @Override
    public void init(GameContainer container, final GomokuClient gomokuClient)
            throws SlickException {

        loading = true; // will be set to false once we receive data from the
                        // server

        client = gomokuClient;
        client.setPlayerName("(none)");

        // add the board
        boardComponent = new BoardComponent(container, null, 100, 50, 30, 15,
                15) {
            @Override
            public void squareClicked(int x, int y) {
                if (me == null || !myTurn())
                    return;
                placePiece(gomokuClient, x, y);
            }
        };

        listener = new BounceListener(this);
    }

    @Override
    public void enter(GameContainer container, GomokuClient gomokuClient)
            throws SlickException {
        // add network listener
        gomokuClient.client.addListener(listener);
    }

    @Override
    public void leave(GameContainer container, GomokuClient gomokuClient)
            throws SlickException {
        gomokuClient.client.removeListener(listener);
    }

    /**
     * Try to place a new piece on provided position. If successful client-side,
     * send a packet to server trying to do the same thing.
     *
     * @param gomokuClient
     *            The game which we place the piece in
     * @param x
     *            The x location for the new piece
     * @param y
     *            The y location for the new piece
     */
    public void placePiece(GomokuClient gomokuClient, int x, int y) {
        if (this.gomokuGame.placePiece(x, y, me.getColor())) {
            gomokuClient.client.sendTCP(new PlacePiecePacket(x, y, me
                    .getColor()));
        }
    }

    /**
     * Whether it's our turn or not
     *
     * @return True if it's our turn
     */
    public boolean myTurn() {
        return me == gomokuGame.getTurn();
    }

    public void setBoardSize(int width, int height) {
        boardComponent.setDisplaySize(width, height, 25);
    }

    @Override
    public void update(GameContainer container, GomokuClient gomokuClient,
            int delta) throws SlickException {

        if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            container.exit();
        }

        /* *** NETWORK RELATED INPUT *** */
        if (gomokuClient.client.isConnected()) {
            if (container.getInput().isKeyPressed(Input.KEY_F5)) {
                gomokuClient.client.sendTCP(new GenericRequestPacket(
                        BoardUpdate));
            }

            // ctrl is pressed
            if (container.getInput().isKeyDown(Input.KEY_LCONTROL)
                    || container.getInput().isKeyDown(Input.KEY_RCONTROL)) {

                // clear board
                if (container.getInput().isKeyPressed(Input.KEY_C)) {
                    gomokuClient.client.sendTCP(new GenericRequestPacket(
                            ClearBoard));
                }
            }
        }
        /* *** END NETWORK RELATED INPUT *** */
    }

    @Override
    public void render(GameContainer container, GomokuClient gomokuClient,
            Graphics g) throws SlickException {
        g.setFont(container.getDefaultFont());

        // draw the board
        if (loading) {
            g.drawString("Loading...", 200, 200);
        } else {
            boardComponent.render(container, g);

            // draw game info
            int xPos = 600;
            g.drawString("Your name: " + me.getName(), xPos, 20);
            g.drawString("Your color: " + me.getColorName(), xPos, 40);
            g.drawString("Turn: " + (myTurn() ? "You" : "Opponent"), xPos, 60);
            g.drawString("Board size: " + gomokuGame.getBoard().getWidth()
                    + "x" + gomokuGame.getBoard().getHeight(), xPos, 80);
            g.drawString("Displaysize: " + boardComponent.getDisplayWidth()
                    + "x" + boardComponent.getDisplayHeight(), xPos, 100);

            g.drawString("Connected players", xPos, 120);
            g.drawString("-----------------", xPos, 140);
            int yPos = 160;
            for (String p : this.playerList) {
                if (p == "(none)")
                    return;
                g.drawString(p, xPos, yPos);
                yPos += 20;
            }
            String versus = gomokuGame.getBlack().getName() + " vs "
                    + gomokuGame.getWhite().getName();
            g.drawString(versus, 250, 10);

            String status = "";
            if (!myTurn()) {
                g.setColor(Color.red);
                status += "waiting for opponent";
            } else {
                g.setColor(Color.green);
                status += "Your turn!";
            }
            g.drawString(status, 250, 550);
            g.setColor(Color.white);

        }
    }


    @Override
    public void received(Connection connection, Object object) {

        // locate the type of packet we received
        if (object instanceof PlacePiecePacket) {
            handlePlacePiece(connection, (PlacePiecePacket) object);

        } else if (object instanceof BoardPacket) {
            handleBoard(connection, (BoardPacket) object);

        } else if (object instanceof NotifyTurnPacket) {
            handleNotifyTurn(connection, (NotifyTurnPacket) object);

        } else if (object instanceof InitialServerDataPacket) {
            handleInitialServerData(connection,
                    (InitialServerDataPacket) object);

        } else if (object instanceof PlayerListPacket) {
            handlePlayerList(connection, (PlayerListPacket) object);
        }
    }

    private void handlePlacePiece(Connection conn, PlacePiecePacket ppp) {
        if (!gomokuGame.placePiece(ppp.x, ppp.y, ppp.playerColor)) {
            warn("GameplayStateListener",
                    "Piece couldn't be placed, requesting board update");
            conn.sendTCP(new GenericRequestPacket(BoardUpdate));

        } else {
            Player player = gomokuGame.getPlayer(ppp.playerColor);
            info("GameplayStateListener", player.getColorName()
                    + " piece placed on " + ppp.x + ", " + ppp.y);
        }
    }

    private void handleBoard(Connection conn, BoardPacket bp) {
        // let's update our board with the board of the server
        gomokuGame.replaceBoard(bp.getBoard());
        info("GameplayStateListener", "Board updated");
    }

    private void handleNotifyTurn(Connection conn, NotifyTurnPacket ntp) {
        gomokuGame.setTurn(gomokuGame.getPlayer(ntp.getColor()));
        info("GameplayStateListener", "Notified about turn: "
                + gomokuGame.getTurn().getName());
    }

    private void handleInitialServerData(Connection conn,
            InitialServerDataPacket idp) {
        setInitialData(idp.getBoard(), idp.getColor(), idp.getTurn(),
                idp.getPlayerList());
        info("GameplayStateListener", "Received initial data from server.");
    }

    private void handlePlayerList(Connection conn, PlayerListPacket plp) {
        debug("GameplayStateListene", "Updated playerlist");
        setPlayerList(plp.players);

        // the first two spots is reserved for black and white. If we're not the
        // specified color, update it.
        if (me.getColor() != Board.BLACKPLAYER) {
            gomokuGame.getBlack().setName(plp.players[0]);
        }
        if (me.getColor() != Board.WHITEPLAYER) {
            gomokuGame.getWhite().setName(plp.players[1]);
        }
    }


    @Override
    public int getID() {
        return GAMEPLAYSTATE;
    }

}
