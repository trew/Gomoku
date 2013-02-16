package gomoku.logic;

import static org.trew.log.Log.*;

import gomoku.logic.Board.PlacePieceBoardAction;

import java.util.ArrayList;

/**
 * Contains game logic for Gomoku game. The game keeps track of the board, the
 * players and whose turn it is. It will also have methods indicating victory or
 * defeat.
 *
 * @author Samuel Andersson
 */
public class GomokuGame {

    /** The board */
    private Board board;

    /** Whose turn it is */
    private Player turn;

    /** The first player */
    private Player playerOne;

    /** The second player */
    private Player playerTwo;

    private boolean gameOver;

    private GomokuConfig config;

    private Swap2 swap2;

    /** The recorder of this game */
    protected ActionRecorder actionRecorder;

    private ArrayList<GomokuGameListener> listeners;

    static public interface GameAction {
        public int getPlayerColor();

        public void doAction(GomokuGame game) throws IllegalActionException;

        public void confirmAction(GomokuGame game);

        public void undoAction(GomokuGame game);
    }

    static public class PlacePieceGameAction implements GameAction {

        private int player;
        private PlacePieceBoardAction boardAction;
        private boolean done;
        private boolean confirmed;
        private boolean waitForConfirm;

        /**
         * For kryonet
         */
        @SuppressWarnings("unused")
        private PlacePieceGameAction() {
        }

        public PlacePieceGameAction(int player, int x, int y,
                boolean waitForConfirm) {
            this.player = player;
            this.waitForConfirm = waitForConfirm;
            boardAction = new PlacePieceBoardAction(player, x, y);
            done = false;
            confirmed = false;
        }

        @Override
        public int getPlayerColor() {
            return player;
        }

        public PlacePieceBoardAction getBoardAction() {
            return boardAction;
        }

        @Override
        public void doAction(GomokuGame game) throws IllegalActionException {
            if (game.gameOver)
                throw new IllegalActionException(
                        "Game over. Cannot place piece.");
            // not possible to compare "turn == player" because it is a
            // reference
            // comparison. We must rely on comparison by value, which is
            // possible using the colors.
            if (game.turn.getColor() == player && (player != Board.NOPLAYER)) {
                boardAction.doAction(game.getBoard());
                done = true;
                if (!waitForConfirm)
                    confirmAction(game);
            } else {
                throw new IllegalActionException("Not "
                        + game.getPlayerFromColor(player).getColorName()
                        + "'s turn!");
            }

        }

        @Override
        public void confirmAction(GomokuGame game) {
            if (confirmed)
                return;
            game.checkBoard(boardAction.getX(), boardAction.getY());
            game.switchTurn();
            confirmed = true;
        }

        @Override
        public void undoAction(GomokuGame game) {
            if (done) {
                boardAction.undoAction(game.getBoard());
                if (confirmed)
                    game.switchTurn();
            }
        }

    }

    /**
     * Create a new game with set width and height
     *
     * @param width
     *            the width of the board
     * @param height
     *            the height of the board
     */
    public GomokuGame(GomokuConfig config, boolean record) {
        this(new Board(config), config, record);
    }

    /**
     * Create a new game from a board
     *
     * @param board
     *            The board
     */
    public GomokuGame(Board board, GomokuConfig config, boolean record) {
        this.board = board;

        playerOne = new Player("", Player.PLAYERONE);
        playerTwo = new Player("", Player.PLAYERTWO);
        turn = playerOne;
        gameOver = false;

        // game rules
        this.config = config;

        if (config.useSwap2()) {
            swap2 = new Swap2();
        } else {
            playerOne.setColor(Board.BLACKPLAYER);
            playerTwo.setColor(Board.WHITEPLAYER);
        }

        if (record) {
            actionRecorder = new ActionRecorder();
            this.board.registerRecorder(actionRecorder);
        }

        listeners = new ArrayList<GomokuGameListener>();
    }

    /**
     * Reset the game and set player turn to red
     */
    public void reset() {
        board.reset();
        turn = playerOne;
        gameOver = false;
        if (config.useSwap2()) {
            swap2 = new Swap2();
        } else {
            playerOne.setColor(Board.BLACKPLAYER);
            playerTwo.setColor(Board.WHITEPLAYER);
        }
    }

    public GomokuConfig getConfig() {
        return config;
    }

    public Swap2 getSwap2() {
        return swap2;
    }

    /**
     * Checks for victory and calls listeners if someone won
     *
     * @param x
     *            the x position modified that forced victory check
     * @param y
     *            the y position modified that forced victory check
     */
    private void checkBoard(int x, int y) {
        /*
         * The algorithm will check four lines for victory based of the changed
         * position. Horizontally, vertically and two diagonal rows.
         */
        Player player = getPieceOwner(x, y);
        int color = player.getColor();
        boolean victory = false;

        // horizontal check
        int length = board.count(color, x, y, 1, 0);
        if (length == config.getVictoryLength()) {
            victory = true;
        } else if (config.getAllowOverlines()
                && length > config.getVictoryLength()) {
            victory = true;
        }

        // vertical check
        length = board.count(color, x, y, 0, 1);
        if (length == config.getVictoryLength()) {
            victory = true;
        } else if (config.getAllowOverlines()
                && length > config.getVictoryLength()) {
            victory = true;
        }

        // topleft diagonal check
        length = board.count(color, x, y, 1, 1);
        if (length == config.getVictoryLength()) {
            victory = true;
        } else if (config.getAllowOverlines()
                && length > config.getVictoryLength()) {
            victory = true;
        }

        // topright diagonal check
        length = board.count(color, x, y, 1, -1);
        if (length == config.getVictoryLength()) {
            victory = true;
        } else if (config.getAllowOverlines()
                && length > config.getVictoryLength()) {
            victory = true;
        }

        if (victory) {
            debug("Game detected winner + " + player.getName() + "("
                    + player.getColorName() + ")" + ". Notifying listeners.");
            gameOver = true;
            for (GomokuGameListener listener : listeners) {
                listener.gameOver(player.getColor());
            }
        }
    }

    /**
     * Place a piece and switch player turn
     *
     * @param x
     *            the x location of the piece
     * @param y
     *            the y location of the piece
     * @param player
     *            the player placing the piece
     * @return true if piece was placed
     */
    public PlacePieceGameAction placePiece(int x, int y, Player player,
            boolean waitForConfirm) throws IllegalActionException {
        if (player == null)
            throw new IllegalArgumentException("Player cannot be null");
        PlacePieceGameAction action = new PlacePieceGameAction(
                player.getColor(), x, y, waitForConfirm);
        action.doAction(this);
        return action;
    }

    /**
     * Get the owner of the piece placed on provided position
     *
     * @param x
     *            The x location of the piece
     * @param y
     *            The y location of the piece
     * @return The player owning the piece on x, y. Null if empty.
     */
    public Player getPieceOwner(int x, int y) {
        int piece = board.getPiece(x, y);
        if (piece == playerOne.getColor())
            return playerOne;
        if (piece == playerTwo.getColor())
            return playerTwo;
        return null;
    }

    public void setColor(int playerID, int color) {
        debug("Setting color of " + playerID + " to " + color);
        int otherColor = Board.BLACKPLAYER;
        if (color == Board.BLACKPLAYER)
            otherColor = Board.WHITEPLAYER;

        if (playerID == Player.PLAYERONE) {
            playerOne.setColor(color);
            playerTwo.setColor(otherColor);
        } else if (playerID == Player.PLAYERTWO) {
            playerOne.setColor(otherColor);
            playerTwo.setColor(color);
        }

    }

    /**
     * Swap turns between black and white
     */
    public void switchTurn() {
        if (gameOver)
            return;

        if (turn == playerOne)
            setTurn(playerTwo);
        else
            setTurn(playerOne);
    }

    /**
     * Set turn to provided player
     *
     * @param player
     *            The player who is going to get the turn
     */
    public void setTurn(Player player) {
        setTurn(player.getID());
    }

    /**
     * Set turn to player with provided color
     *
     * @param playerID
     *            the provided player color
     */
    public void setTurn(int playerID) {
        if (gameOver)
            return;
        if (playerID == playerOne.getID()) {
            turn = playerOne;
            trace("Turn set to player " + turn.getID() + "(" + turn.getName()
                    + ")");
        } else if (playerID == playerTwo.getID()) {
            turn = playerTwo;
            trace("Turn set to player " + turn.getID() + "(" + turn.getName()
                    + ")");
        }
    }

    /**
     * Get the player who has the turn
     *
     * @return The player who has the turn
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * Returns the first player
     *
     * @return the first player
     */
    public Player getPlayerOne() {
        return playerOne;
    }

    /**
     * Returns the second player
     *
     * @return the second player
     */
    public Player getPlayerTwo() {
        return playerTwo;
    }

    /**
     * Returns a player depending on provided color
     *
     * @param color
     *            The player color
     * @return a player depending on provided color
     * @throws IllegalArgumentException
     *             Indicates a value other than the color of player one or
     *             player two.
     */
    public Player getPlayerFromColor(int color) {
        if (color == playerOne.getColor())
            return playerOne;
        if (color == playerTwo.getColor())
            return playerTwo;
        throw new IllegalArgumentException("No player with this color: \""
                + color + "\".");
    }

    public Player getPlayer(int id) {
        if (id == playerOne.getID()) {
            return playerOne;
        } else if (id == playerTwo.getID()) {
            return playerTwo;
        }
        return null;
    }

    /**
     * Replace the current board with the new board
     *
     * @param board
     *            The board to replace the current one
     */
    public void replaceBoard(Board board) {
        trace("Replacing board");
        this.board.replaceBoard(board);
    }

    /**
     * Get the current board
     *
     * @return The current board
     */
    public Board getBoard() {
        return board;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void addListener(GomokuGameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GomokuGameListener listener) {
        listeners.remove(listener);
    }

}
