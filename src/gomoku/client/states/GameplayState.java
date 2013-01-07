package gomoku.client.states;

import static gomoku.net.Request.*;

import gomoku.client.BoardComponent;
import gomoku.client.GomokuClient;
import gomoku.logic.Board;
import gomoku.logic.Player;
import gomoku.logic.GomokuGame;
import gomoku.net.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import static com.esotericsoftware.minlog.Log.*;

/**
 * The playing state of the Gomoku game.
 * 
 * @author Samuel Andersson
 */
public class GameplayState extends GomokuGameState {

    /** Contains the game logic */
    public GomokuGame gomokuGame;

    /** The player */
    public Player me;

    /** The board displayed */
    private BoardComponent boardComponent;

    /** The Network listener for this state */
    private GameplayStateListener listener;

    private boolean loading;

    public boolean initialLoading() {
        return loading;
    }

    public void setInitialData(Board board, int playerColor, int turn) {
        // create a new game
        gomokuGame = new GomokuGame(board);
        gomokuGame.setTurn(gomokuGame.getPlayer(turn));
        setPlayer(playerColor);

        boardComponent.setBoard(board);

        loading = false;
    }

    public void setPlayer(int playerColor) {
        if (playerColor == Board.BLACKPLAYER)
            me = gomokuGame.getBlack();
        else if (playerColor == Board.WHITEPLAYER)
            me = gomokuGame.getWhite();
        else {
            error("GameplayState", "Color couldn't bet set!");
            return;
        }
        info("GameplayState", "Color set to " + me.getName());

    }

    @Override
    public void init(GameContainer container, final GomokuClient gomokuClient)
            throws SlickException {

        loading = true; // will be set to false once we receive data from the
                        // server

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

        // add network listener
        listener = new GameplayStateListener(this);
        gomokuClient.client.addListener(listener);
    }

    /**
     * When entering this game state, request initial data from the server such
     * as the board, our player color and the current turn
     */
    @Override
    public void enter(GameContainer container, GomokuClient gomokuClient)
            throws SlickException {
        gomokuClient.client.sendTCP(new GenericRequestPacket(InitialData));
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
        if (this.gomokuGame.placePiece(x, y, me)) {
            gomokuClient.client.sendTCP(new PlacePiecePacket(x, y, me));
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
            String status = "Status: ";
            if (!myTurn()) {
                status += "waiting for opponent";
            } else {
                status += "waiting for move";
            }
            g.drawString(status, xPos, 120);

        }
    }

    @Override
    public int getID() {
        return 1;
    }

}
