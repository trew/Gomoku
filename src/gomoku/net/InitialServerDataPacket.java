package gomoku.net;

import gomoku.logic.Board;
import gomoku.logic.GomokuConfig;

/**
 * A packet that the client requests from the server upon connection. It sends
 * the game board, the color for the player and the turn. Upon receiving this
 * packet, the client knows it has all the data it needs to start displaying the
 * board etc.
 *
 * @author Samuel Andersson
 */
public class InitialServerDataPacket {

    /** The board of the game */
    private Board board;

    private GomokuConfig config;

    /** The color the player will receive */
    private int playerColor;

    /** The current turn */
    private int turn;

    /** The currently connected players */
    private String[] playerList;

    /** Empty constructor for Kryonet */
    public InitialServerDataPacket() {
    }

    /**
     * Create a new initial data packet
     *
     * @param board
     *            The board
     * @param playerColor
     *            The color the player will receive
     * @param turn
     *            The current turn
     * @param opponentName
     *            The name of the opponent
     */
    public InitialServerDataPacket(Board board, GomokuConfig config, int playerColor, int turn,
            String[] playerList) {
        this.board = board;
        this.config = config;
        this.playerColor = playerColor;
        this.turn = turn;
        this.playerList = playerList;
    }

    /**
     * Returns the board
     *
     * @return the board
     */
    public Board getBoard() {
        return board;
    }

    public GomokuConfig getConfig() {
        return config;
    }
    /**
     * Returns the player color
     *
     * @return the player color
     */
    public int getColor() {
        return playerColor;
    }

    /**
     * Returns the color of the player in turn
     *
     * @return the color of the player in turn
     */
    public int getTurn() {
        return turn;
    }

    /**
     * Returns the currently connected players
     *
     * @return the currently connected players
     */
    public String[] getPlayerList() {
        return playerList;
    }
}
