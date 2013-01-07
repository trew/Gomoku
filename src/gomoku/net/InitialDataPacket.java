package gomoku.net;

import gomoku.logic.Board;

/**
 * A packet that the client requests from the server upon connection. It sends
 * the game board, the color for the player and the turn. Upon receiving this
 * packet, the client knows it has all the data it needs to start displaying the
 * board etc.
 * 
 * @author Samuel Andersson
 */
public class InitialDataPacket {

    /** The board of the game */
    private Board board;

    /** The color the player will receive */
    private int playerColor;

    /** The current turn */
    private int turn;

    /** The opponent name */
    private String opponentName;

    /** Empty constructor for Kryonet */
    public InitialDataPacket() {
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
    public InitialDataPacket(Board board, int playerColor, int turn,
            String opponentName) {
        this.board = board;
        this.playerColor = playerColor;
        this.turn = turn;
        this.opponentName = opponentName;
    }

    /**
     * Returns the board
     * 
     * @return the board
     */
    public Board getBoard() {
        return board;
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
     * Returns the opponent name
     * 
     * @return the opponent name
     */
    public String getOpponentName() {
        return opponentName;
    }
}
