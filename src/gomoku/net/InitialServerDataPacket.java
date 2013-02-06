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

    private int swap2state;

    /** The color the player will receive */
    private int playerID;

    /** The current turn ID*/
    private int turnID;

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
     * @param playerID
     *            The ID the player will receive
     * @param turnID
     *            The ID of the player with current turn
     * @param opponentName
     *            The name of the opponent
     */
    public InitialServerDataPacket(Board board, GomokuConfig config, int swap2state, int playerID, int turnID,
            String[] playerList) {
        this.board = board;
        this.config = config;
        this.swap2state = swap2state;
        this.playerID = playerID;
        this.turnID = turnID;
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

    public int getSwap2State() {
        return swap2state;
    }

    public GomokuConfig getConfig() {
        return config;
    }
    /**
     * Returns the player id
     *
     * @return the player id
     */
    public int getID() {
        return playerID;
    }

    /**
     * Returns the color of the player in turn
     *
     * @return the color of the player in turn
     */
    public int getTurn() {
        return turnID;
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
