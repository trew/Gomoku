package gomoku.client.states;

import static gomoku.net.Request.*;

import gomoku.client.GomokuClient;
import gomoku.client.gui.BoardComponent;
import gomoku.client.gui.Button;
import gomoku.client.gui.Fonts;
import gomoku.logic.Board;
import gomoku.logic.GomokuConfig;
import gomoku.logic.GomokuGame.GameAction;
import gomoku.logic.IllegalActionException;
import gomoku.logic.Player;
import gomoku.logic.GomokuGame;
import gomoku.net.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryonet.Connection;

import static org.trew.log.Log.*;

/**
 * The playing state of the Gomoku game.
 *
 * @author Samuel Andersson
 */
public class GameplayState extends GomokuNetworkGameState {

    /** Contains the game logic */
    public GomokuGame gomokuGame;

    /** The player */
    public Player me;

    /** The board displayed */
    private BoardComponent boardComponent;
    private Button confirmMoveButton;
    private Button cancelMoveButton;

    private Image versus;
    private Image nametag;
    private Image infobar;
    private Image messagebox;

    private boolean loading;
    private boolean gameOver;
    private int gameOverVictoryState;

    private String[] playerList;

    private String errorMsg;
    private long errorTimer;

    private boolean pendingMove;
    private GameAction pendingAction;

    public boolean initialLoading() {
        return loading;
    }

    public void setInitialData(Board board, GomokuConfig config,
            int playerColor, int turn, String[] playerList) {
        // create a new game
        this.playerList = playerList;
        gomokuGame = new GomokuGame(board, config, true);
        gomokuGame.setTurn(gomokuGame.getPlayer(turn));
        setupPlayers(playerColor);

        boardComponent.setBoard(board);

        loading = false;
        gameOverVictoryState = 0;
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
            playerList[0] = getGame().getProperties().getProperty("playername",
                    "(none)");
            gomokuGame.getWhite().setName(playerList[1]);

        } else if (playerColor == Board.WHITEPLAYER) {
            me = gomokuGame.getWhite();
            playerList[1] = getGame().getProperties().getProperty("playername",
                    "(none)");
            gomokuGame.getBlack().setName(playerList[0]);

        } else {
            me = new Player("", Board.NOPLAYER);
            gomokuGame.getBlack().setName(playerList[0]);
            gomokuGame.getWhite().setName(playerList[1]);
        }

        me.setName(getGame().getProperties()
                .getProperty("playername", "(none)"));
        info("Color set to " + me.getColorName());
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
        } else {
            gomokuGame.getBlack().setName(playerList[0]);
            gomokuGame.getWhite().setName(playerList[1]);
        }
    }

    @Override
    public void init(GameContainer container, final GomokuClient gomokuClient)
            throws SlickException {

        loading = true; // will be set to false once we receive data from the
                        // server
        gameOver = false;
        pendingAction = null;
        pendingMove = false;

        errorMsg = "";

        // add the board
        boardComponent = new BoardComponent(container, null, 80, 70, 28, 15, 15) {
            @Override
            public void squareClicked(int x, int y) {
                if (me == null || !myTurn())
                    return;
                placePiece(gomokuClient, x, y);
            }
        };
        boardComponent.setCenterLocation(320, 300);

        Image ok = new Image("res/buttons/ok.png");
        Image cancel = new Image("res/buttons/cancel.png");
        confirmMoveButton = new Button(ok, 590, 482, 3) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0 && pendingMove && pendingAction != null) {
                    sendGameAction(gomokuClient, pendingAction);
                    pendingMove = false;
                }
            }
        };
        cancelMoveButton = new Button(cancel, 690, 482, 3) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0 && pendingMove && pendingAction != null) {
                    pendingAction.undoAction(gomokuGame);
                    pendingMove = false;
                }

            }
        };

        versus = new Image("res/versus.png");
        nametag = new Image("res/nametag.png");
        infobar = new Image("res/infobar.png");
        messagebox = new Image("res/bottommessagebox.png");

        addListener(boardComponent);
        addListener(confirmMoveButton);
        addListener(cancelMoveButton);
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
        if (pendingMove) return;
        try {
            boolean waitForConfirm = !gomokuClient.getProperties().getProperty("autoconfirm")
                    .equalsIgnoreCase("true");
            pendingAction = gomokuGame.placePiece(x, y, me.getColor(), waitForConfirm);
            if (waitForConfirm)
                pendingMove = true;
            else
                sendGameAction(gomokuClient, pendingAction);

        } catch (IllegalActionException e) {
            info(e.getMessage());
            setErrorMsg(e.getMessage());
        }
    }

    protected void sendGameAction(GomokuClient client, GameAction action) {
        client.client.sendTCP(new GameActionPacket(action));
        action.confirmAction(gomokuGame);
    }

    /**
     * Sets the error message to be displayed with a timer of 5 seconds.
     *
     * @param msg
     */
    protected void setErrorMsg(String msg) {
        errorMsg = msg;
        errorTimer = 5000;
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

        errorTimer -= delta;
        if (errorTimer < 0)
            errorTimer = 0;

        Input input = container.getInput();

        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            enterState(MAINMENUSTATE);
        }

        /* *** NETWORK RELATED INPUT *** */
        if (gomokuClient.client.isConnected()) {
            if (input.isKeyPressed(Input.KEY_F5)) {
                gomokuClient.client.sendTCP(new GenericRequestPacket(
                        BoardUpdate));
            }

            // ctrl is pressed
            if (input.isKeyDown(Input.KEY_LCONTROL)
                    || container.getInput().isKeyDown(Input.KEY_RCONTROL)) {

                // clear board
                if (input.isKeyPressed(Input.KEY_C) && !gameOver) {
                    gomokuClient.client.sendTCP(new GenericRequestPacket(
                            ClearBoard));
                }
            }
        }
        /* *** END NETWORK RELATED INPUT *** */
    }

    private static int rowYPos = 85;

    private void drawRow(String str, int x, Graphics g) {
        int textHeight = g.getFont().getHeight(str);
        g.drawString(str, x, rowYPos);
        rowYPos += textHeight + 1;
    }

    @Override
    public void render(GameContainer container, GomokuClient gomokuClient,
            Graphics g) throws SlickException {
        g.setFont(container.getDefaultFont());

        // draw the board
        if (loading) {
            g.drawString("Loading...", 200, 200);
        } else {
            g.drawImage(versus, 400 - 117 / 2, 0);
            g.drawImage(nametag, 20, 10);
            g.drawImage(nametag, 490, 10);
            g.drawImage(infobar, 800 - 225, (600 - 454) / 2 + 8);
            g.drawImage(messagebox, 0, 600 - 63);

            boardComponent.render(container, g);

            // draw game info
            g.setFont(Fonts.getDefaultFont(14));
            int xPos = 590;
            rowYPos = 85;
            drawRow("Your name: " + me.getName(), xPos, g);
            if (me.getColor() != Board.NOPLAYER) {
                drawRow("Your color: " + me.getColorName(), xPos, g);
                drawRow("Turn: " + (myTurn() ? "You" : "Opponent"), xPos, g);
            }
            drawRow("Board size: " + gomokuGame.getBoard().getWidth() + "x"
                    + gomokuGame.getBoard().getHeight(), xPos, g);
            drawRow("Displaysize: " + boardComponent.getDisplayWidth() + "x"
                    + boardComponent.getDisplayHeight(), xPos, g);

            drawRow("Connected players", xPos, g);
            drawRow("-----------------", xPos, g);
            for (String p : this.playerList) {
                if (p.equals("(none)"))
                    break;
                drawRow(p, xPos, g);
            }

            if (pendingMove) {
                g.setFont(Fonts.getAngelCodeFont("res/fonts/messagebox"));
                g.drawString("Confirm move?", 600, 440);
                confirmMoveButton.render(container, g);
                cancelMoveButton.setLocation(698, 482);
                cancelMoveButton.render(container, g);
            }

            // top
            g.setColor(Color.white);
            g.setFont(Fonts.getAngelCodeFont("res/fonts/nametag"));
            String name = gomokuGame.getBlack().getName();
            int stringWidth = g.getFont().getWidth(name);
            g.drawString(name, center(20, 20 + 290, stringWidth), 13);

            name = gomokuGame.getWhite().getName();
            stringWidth = g.getFont().getWidth(name);
            g.drawString(name, center(490, 490 + 290, stringWidth), 13);

            // error msg
            g.setColor(Color.black);
            g.setFont(Fonts.getAngelCodeFont("res/fonts/messagebox"));
            if (errorTimer > 0) {
                g.setColor(Color.red);
                stringWidth = g.getFont().getWidth(errorMsg);
                g.drawString(errorMsg, center(0, 800, stringWidth), 550);
            } else if (me.getColor() != Board.NOPLAYER) {
                String status = "";
                if (gameOver) {
                    g.setColor(Color.white);
                    status += "Game Over!";
                    if (gameOverVictoryState == 0) {
                        status += " You lost.";
                    } else if (gameOverVictoryState == 1) {
                        status += " You won!";
                    } else if (gameOverVictoryState == 2) {
                        status += " Draw!";
                    }
                } else if (!myTurn()) {
                    g.setColor(Color.red);
                    status += "waiting for opponent";
                } else {
                    g.setColor(Color.green);
                    status += "Your turn!";
                }
                stringWidth = g.getFont().getWidth(status);
                g.drawString(status, center(0, 800, stringWidth), 550);
            }

            g.setColor(Color.white);

        }
    }

    @Override
    protected void handleGameAction(Connection conn, GameActionPacket ppp) {
        try {
            ppp.action.doAction(gomokuGame);
            ppp.action.confirmAction(gomokuGame);
        } catch (IllegalActionException e) {
            warn("Piece couldn't be placed: " + e.getMessage()); //TODO fix
            info("Requesting boardupdate...");
            conn.sendTCP(new GenericRequestPacket(BoardUpdate));
        }
    }

    @Override
    protected void handleBoard(Connection conn, BoardPacket bp) {
        // let's update our board with the board of the server
        gomokuGame.replaceBoard(bp.getBoard());
        info("Board updated");
    }

    @Override
    protected void handleNotifyTurn(Connection conn, NotifyTurnPacket ntp) {
        gomokuGame.setTurn(gomokuGame.getPlayer(ntp.getColor()));
        info("Notified about turn: " + gomokuGame.getTurn().getName());
    }

    @Override
    protected void handlePlayerList(Connection conn, PlayerListPacket plp) {
        debug("Updated playerlist");
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

    /**
     * Handles what to do when the server sends information about who won. (not
     * necessarily us)
     */
    @Override
    protected void handleVictory(Connection conn, VictoryPacket vp) {
        debug("Victorystatus received: " + vp.victory);
        boardComponent.setChangeable(false);
        gameOver = true;

        gameOverVictoryState = 0;
        if (vp.victory == 0) { // draw
            gameOverVictoryState = 2;
        } else if (vp.victory == me.getColor()) { // victory
            gameOverVictoryState = 1;
        }
    }

    @Override
    public int getID() {
        return GAMEPLAYSTATE;
    }

}
