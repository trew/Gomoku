package gomoku.logic;

import java.util.ArrayList;

/**
 * A Board represents a Gomoku board. The board size restrictions is 40x40.
 *
 * @author Samuel Andersson
 *
 */
public class Board {

    static public abstract class ChangeListener {
        public abstract void callback(int color, int x, int y);
    }

    /** The value representing no player */
    public static final int NOPLAYER = 0;

    /** The value representing a black player */
    public static final int BLACKPLAYER = 1;

    /** The value representing a white player */
    public static final int WHITEPLAYER = 2;

    /** The structure containing the board data */
    protected int[] board;

    /** The Gomoku game configuration */
    protected GomokuConfig config;

    /** The recorder of this game */
    protected ActionRecorder recorder;

    protected ArrayList<ChangeListener> listeners;

    /**
     * Basic interface for an action on the board. DO NOT FORGET TO ADD AN EMPTY
     * CONSTRUCTOR!
     *
     * @author Samuel Andersson
     */
    static public interface BoardAction {
        public int getPlayer();

        public int getX();

        public int getY();

        /**
         * Performs the action on the board. If unsuccessful,
         * IllegalActionException will be thrown and the action should not be
         * performed. The action can be undone by calling
         * {@link #undoAction(Board)}.
         *
         * @param board
         *            the board to modify
         * @throws IllegalActionException
         *             If the action could not be fulfilled
         */
        public void doAction(Board board) throws IllegalActionException;

        /**
         * Undoes the action on the board if it was completed in
         * {@link #doAction(Board)}
         *
         * @param board
         *            the board to modify
         */
        public void undoAction(Board board);
    }

    /**
     * Action for placing a piece on the board
     *
     * @author Samuel Andersson
     */
    static public class PlacePieceBoardAction implements BoardAction {

        protected int player;
        protected int x;
        protected int y;
        protected boolean done;

        /**
         * For kryonet
         */
        @SuppressWarnings("unused")
        private PlacePieceBoardAction() {
        }

        public PlacePieceBoardAction(int player, int x, int y) {
            this.player = player;
            this.x = x;
            this.y = y;
            done = false;
        }

        @Override
        public int getPlayer() {
            return player;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public void doAction(Board board) throws IllegalActionException {
            if (board.getPiece(x, y) != Board.NOPLAYER)
                throw new IllegalActionException(
                        "That position is already occupied!");
            if (board.config.useThreeAndThree()) {
                if (!board.try3And3(player, x, y)) {
                    throw new IllegalActionException(
                            "Unable to place because of Three And Three-rule.");
                }
            }
            if (board.config.useFourAndFour()) {
                if (!board.try4And4(player, x, y)) {
                    throw new IllegalActionException(
                            "Unable to place because of Four And Four-rule.");
                }
            }

            board.setPiece(x, y, player);
            board.fireChangeListeners(player, x, y);
            done = true;
        }

        @Override
        public void undoAction(Board board) {
            if (done) {
                board.setPiece(x, y, NOPLAYER);
                board.fireChangeListeners(NOPLAYER, x, y);
                done = false;
            }
        }

    }

    protected void record(BoardAction action) {
        if (recorder != null)
            recorder.add(action);
    }

    public void registerRecorder(ActionRecorder recorder) {
        this.recorder = recorder;
    }

    /** Empty constructor for Kryonet */
    @SuppressWarnings("unused")
    private Board() {
    }

    /**
     * Construct a new Gomoku board and reset the recorder.
     *
     * @param width
     *            the width of the board
     * @param height
     *            the height of the board
     */
    public Board(GomokuConfig config) {
        this.config = config;
        listeners = new ArrayList<ChangeListener>();
        reset();
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    protected void fireChangeListeners(int color, int x, int y) {
        for (ChangeListener listener : listeners) {
            listener.callback(color, x, y);
        }
    }

    /**
     * Reset the board, making it all empty spaces. Also reset the
     * actionRecorder.
     */
    public void reset() {
        listeners.clear();
        board = new int[config.getWidth() * config.getHeight()];
    }

    /**
     * Replace the current board with a new board
     *
     * @param board
     *            The source board which will replace the current board
     */
    public void replaceBoard(Board board) {
        this.board = board.board;
        this.config = board.config;
    }

    public void setBoardData(int[] data) {
        board = data;
    }

    public int[] getBoardData() {
        return board;
    }

    public GomokuConfig getConfig() {
        return config;
    }

    /**
     * Does an action on the board. This is most likely an instance of
     * {@link PlacePieceBoardAction}
     *
     * @param action
     *            the action to be made
     * @throws IllegalActionException
     *             if the action couldn't be completed
     */
    protected void doAction(BoardAction action) throws IllegalActionException {
        try {
            action.doAction(this);
            record(action);
        } catch (IllegalActionException e) {
            throw e;
        }
    }

    /**
     * Place a new piece on the board
     *
     * @see #placePiece(int, int, int)
     */
    public PlacePieceBoardAction placePiece(Player player, int x, int y)
            throws IllegalActionException {
        return placePiece(player.getColor(), x, y);
    }

    /**
     * Returns true of all the pieces surrounding the line is empty
     *
     * @return true of all the pieces surrounding the line is empty
     */
    protected boolean isOpen(int color, int x, int y, int dirX, int dirY) {
        int xpos, ypos;
        xpos = x + dirX;
        ypos = y + dirY;
        do {
            // if we reached the edge of the board:
            // this line is not open if there's something other than NOPLAYER
            if (xpos == 0 || xpos == getWidth() - 1) {
                if (getPiece(xpos, ypos) != Board.NOPLAYER)
                    return false;
            }
            if (ypos == 0 || ypos == getHeight() - 1) {
                if (getPiece(xpos, ypos) != Board.NOPLAYER)
                    return false;
            }

            if (getPiece(xpos, ypos) == Board.NOPLAYER) {
                break;
            } else if (getPiece(xpos, ypos) != color) { // enemy
                return false;
            }

            xpos += dirX;
            ypos += dirY;

        } while (xpos >= 0 && xpos < getWidth() && ypos >= 0
                && ypos < getHeight());

        // now check the other side
        xpos = x - dirX;
        ypos = y - dirY;
        do {
            // if we reached the edge of the board:
            // this line is not open if there's something other than NOPLAYER
            if (xpos == 0 || xpos == getWidth() - 1) {
                if (getPiece(xpos, ypos) != Board.NOPLAYER)
                    return false;
            }
            if (ypos == 0 || ypos == getHeight() - 1) {
                if (getPiece(xpos, ypos) != Board.NOPLAYER)
                    return false;
            }

            if (getPiece(xpos, ypos) == Board.NOPLAYER) {
                break;
            } else if (getPiece(xpos, ypos) != color) { // enemy
                return false;
            }
            xpos -= dirX;
            ypos -= dirY;

        } while (xpos >= 0 && xpos < getWidth() && ypos >= 0
                && ypos < getHeight());
        return true;
    }

    /**
     * Returns the number of pieces of the provided color in a row of provided
     * direction based of the provided position
     *
     * @param color
     * @param x
     * @param y
     * @param dirX
     *            -1, 0 or 1
     * @param dirY
     *            -1, 0 or 1
     * @return
     */
    protected int count(int color, int x, int y, int dirX, int dirY) {
        int ct = 1;
        int xpos, ypos; // position to be examined
        xpos = x + dirX;
        ypos = y + dirY;
        while (xpos >= 0 && xpos < getWidth() && ypos >= 0
                && ypos < getHeight() && getPiece(xpos, ypos) == color) {
            ct++;
            xpos += dirX;
            ypos += dirY;
        }

        // check opposite direction too
        xpos = x - dirX;
        ypos = y - dirY;
        while (xpos >= 0 && xpos < getWidth() && ypos >= 0
                && ypos < getHeight() && getPiece(xpos, ypos) == color) {
            ct++;
            xpos -= dirX;
            ypos -= dirY;
        }
        return ct;
    }

    protected boolean try3And3(int player, int x, int y) {
        int ct = 0;
        // horizontal
        if (x > 0 && x < getWidth() - 1 && count(player, x, y, -1, 0) == 3
                && isOpen(player, x, y, 1, 0))
            ct++;

        // vertical
        if (y > 0 && y < getHeight() - 1 && count(player, x, y, 0, -1) == 3
                && isOpen(player, x, y, 0, 1))
            ct++;

        // topleft to bottomdown
        if (x > 0 && x < getWidth() - 1 && y > 0 && y < getHeight() - 1
                && count(player, x, y, -1, -1) == 3
                && isOpen(player, x, y, 1, 1))
            ct++;

        // topright to bottomleft
        if (x > 0 && x < getWidth() - 1 && y > 0 && y < getHeight() - 1
                && count(player, x, y, 1, -1) == 3
                && isOpen(player, x, y, 1, 1))
            ct++;

        return ct < 2;
    }

    protected boolean try4And4(int player, int x, int y) {
        int ct = 0;
        // horizontal
        if (count(player, x, y, -1, 0) == 4)
            ct++;

        // vertical
        if (count(player, x, y, 0, -1) == 4)
            ct++;

        // topleft to bottomdown
        if (count(player, x, y, -1, -1) == 4)
            ct++;

        // topright to bottomleft
        if (count(player, x, y, 1, -1) == 4)
            ct++;

        return ct < 2;
    }

    /**
     * Place a new piece on the board. Pieces can only be placed on empty
     * positions
     *
     * @param player
     *            The player color of the piece
     * @param x
     *            The x location of the new piece
     * @param y
     *            The y location of the new piece
     * @return True if piece was placed
     */
    public PlacePieceBoardAction placePiece(int player, int x, int y)
            throws IllegalActionException {
        PlacePieceBoardAction pp = new PlacePieceBoardAction(player, x, y);
        pp.doAction(this);
        return pp;
    }

    /**
     * Returns the player color for given position on the board. If either of
     * the arguments is invalid, i.e. "x" being larger than board width, the
     * function will return 0.
     *
     * @param x
     *            The x location
     * @param y
     *            The y location
     * @return The player color for the given position. 0 = Empty, 1 = Player 1,
     *         2 = Player 2.
     */
    public int getPiece(int x, int y) {
        if (x < 0 || x >= config.getWidth() || y < 0 || y >= config.getHeight()) {
            return 0;
        }
        return board[x + config.getWidth() * y];
    }

    /**
     * Place a piece on the board
     *
     * @param x
     *            The x location for the piece
     * @param y
     *            The y location for the piece
     * @param player
     *            The player color for the piece
     */
    private void setPiece(int x, int y, int player) {
        if (x < 0 || x > config.getWidth() || y < 0 || y > config.getHeight()) {
            throw new IllegalArgumentException("Position out of bounds. X: "
                    + x + ", Y: " + y);
        }
        if (player != Board.BLACKPLAYER && player != Board.WHITEPLAYER
                && player != Board.NOPLAYER) {
            throw new IllegalArgumentException("Unknown value of player: \""
                    + player + "\".");
        }

        board[x + config.getWidth() * y] = player;
    }

    /**
     * Returns the current width of the board
     *
     * @return the current width of the board
     */
    public int getWidth() {
        return config.getWidth();
    }

    /**
     * Returns the current height of the board
     *
     * @return the current height of the board
     */
    public int getHeight() {
        return config.getHeight();
    }
}
