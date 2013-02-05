package gomoku.logic;

import static org.trew.log.Log.*;

/**
 * A Board represents a Gomoku board. The board size restrictions is 40x40.
 *
 * @author Samuel Andersson
 *
 */
public class Board {

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

    /**
     * Basic interface for an action on the board.
     * DO NOT FORGET TO ADD AN EMPTY CONSTRUCTOR!
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
         * performed. The action can be undone by calling {@link #undoAction(Board)}.
         *
         * @param board
         *            the board to modify
         * @throws IllegalActionException
         *             If the action could not be fulfilled
         */
        public void doAction(Board board) throws IllegalActionException;

        /**
         * Undoes the action on the board if it was completed in {@link #doAction(Board)}
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
    static public class PlacePiece implements BoardAction {

        protected int player;
        protected int x;
        protected int y;
        protected boolean done;

        /**
         * For kryonet
         */
        @SuppressWarnings("unused")
        private PlacePiece() {
        }

        public PlacePiece(int player, int x, int y) {
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
                if (!board.tryXAndX(3, player, x, y, true)) {
                    throw new IllegalActionException(
                            "Unable to place because of Three And Three-rule.");
                }
            }
            if (board.config.useFourAndFour()) {
                if (!board.tryXAndX(4, player, x, y, false)) {
                    throw new IllegalActionException(
                            "Unable to place because of Four And Four-rule.");
                }
            }

            board.setPiece(x, y, player);
            done = true;
        }

        @Override
        public void undoAction(Board board) {
            if (done) {
                board.setPiece(x, y, NOPLAYER);
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
        reset();
    }

    /**
     * Reset the board, making it all empty spaces. Also reset the
     * actionRecorder.
     */
    public void reset() {
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

    /**
     * Does an action on the board. This is most likely an instance of
     * {@link PlacePiece}
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
    public PlacePiece placePiece(Player player, int x, int y)
            throws IllegalActionException {
        return placePiece(player.getColor(), x, y);
    }

    /**
     * Wrapper function for "Three and Three" and "Four and four" rules.
     *
     * @param length
     *            the length to check for
     * @param player
     *            the player placing the piece
     * @param x
     *            the x location of the piece
     * @param y
     *            the y location of the piece
     * @param open
     *            if the lines are allowed to be open or not(free space on both
     *            ends)
     * @return true if placement is successful
     */
    private boolean tryXAndX(int length, int player, int x, int y, boolean open) {
        // the algorithm checks four lines with x,y as it's center. The lines
        // are horizontal, vertical, top-left to bottom-right diagonal and
        // top-right to bottom-left diagonal.
        int lines = 0;

        // ******** HORIZONTAL CHECK ********* //
        int curLength = 1;
        boolean beginningOfLine = false;
        int xPos = x;
        while (!beginningOfLine) {
            xPos--;
            int piece = getPiece(xPos, y);
            if (piece == Board.NOPLAYER) {
                beginningOfLine = true;
            } else if (piece != player) { // aka enemy
                if (open)
                    break; // no open line here
                else
                    beginningOfLine = true;
            } else {
                curLength++;
                if (curLength > length) { // longer than provided length, means
                                          // we can't count this line
                    break;
                }
            }
        }
        xPos = x;
        while (beginningOfLine) {
            xPos++;
            int piece = getPiece(xPos, y);
            if (piece == Board.NOPLAYER) {
                if (curLength == length) {
                    lines++;
                }
                break;
            } else if (piece != player) {
                if (!open)
                    if (curLength == length)
                        lines++;
                break;
            } else {
                curLength++;
                if (curLength > length) {
                    break;
                }
            }
        }

        // ******** VERTICAL CHECK ********* //
        curLength = 1;
        beginningOfLine = false;
        int yPos = y;
        while (!beginningOfLine) {
            yPos--;
            int piece = getPiece(x, yPos);
            if (piece == Board.NOPLAYER) {
                beginningOfLine = true;
            } else if (piece != player) { // aka enemy
                if (open)
                    break; // no open line here
                else
                    beginningOfLine = true;
            } else {
                curLength++;
                if (curLength > length) { // longer than provided length, means
                                          // we can't count this line
                    break;
                }
            }
        }
        yPos = y;
        while (beginningOfLine) {
            yPos++;
            int piece = getPiece(x, yPos);
            if (piece == Board.NOPLAYER) {
                if (curLength == length) {
                    lines++;
                }
                break;
            } else if (piece != player) {
                if (!open)
                    if (curLength == length)
                        lines++;
                break;
            } else {
                curLength++;
                if (curLength > length) {
                    break;
                }
            }
        }

        // ****** TOP LEFT TO BOTTOM RIGHT ****** //
        curLength = 1;
        beginningOfLine = false;
        xPos = x;
        yPos = y;
        while (!beginningOfLine) {
            xPos--;
            yPos--;
            int piece = getPiece(xPos, yPos);
            if (piece == Board.NOPLAYER) {
                beginningOfLine = true;
            } else if (piece != player) { // aka enemy
                if (open)
                    break; // no open line here
                else
                    beginningOfLine = true;
            } else {
                curLength++;
                if (curLength > length) { // longer than provided length, means
                                          // we can't count this line
                    break;
                }
            }
        }
        xPos = x;
        yPos = y;
        while (beginningOfLine) {
            xPos++;
            yPos++;
            int piece = getPiece(xPos, yPos);
            if (piece == Board.NOPLAYER) {
                if (curLength == length) {
                    lines++;
                }
                break;
            } else if (piece != player) {
                if (!open)
                    if (curLength == length)
                        lines++;
                break;
            } else {
                curLength++;
                if (curLength > length) {
                    break;
                }
            }
        }

        // ****** TOP RIGHT TO BOTTOM LEFT ****** //
        curLength = 1;
        beginningOfLine = false;
        xPos = x;
        yPos = y;
        while (!beginningOfLine) {
            xPos++;
            yPos--;
            int piece = getPiece(xPos, yPos);
            if (piece == Board.NOPLAYER) {
                beginningOfLine = true;
            } else if (piece != player) { // aka enemy
                if (open)
                    break; // no open line here
                else
                    beginningOfLine = true;
            } else {
                curLength++;
                if (curLength > length) { // longer than provided length, means
                                          // we can't count this line
                    break;
                }
            }
        }
        xPos = x;
        yPos = y;
        while (beginningOfLine) {
            xPos--;
            yPos++;
            int piece = getPiece(xPos, yPos);
            if (piece == Board.NOPLAYER) {
                if (curLength == length) {
                    lines++;
                }
                break;
            } else if (piece != player) {
                if (!open)
                    if (curLength == length)
                        lines++;
                break;
            } else {
                curLength++;
                if (curLength > length) {
                    break;
                }
            }
        }
        if (lines > 1) {
            debug("Lines: " + lines);
        }

        return lines < 2;
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
    public PlacePiece placePiece(int player, int x, int y)
            throws IllegalActionException {
        PlacePiece pp = new PlacePiece(player, x, y);
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
        if (player != Board.BLACKPLAYER && player != Board.WHITEPLAYER && player != Board.NOPLAYER) {
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
