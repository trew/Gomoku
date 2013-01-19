package gomoku.logic;

public class IllegalMoveException extends Exception {

    private static final long serialVersionUID = 1L;

    public IllegalMoveException(String arg0) {
        super(arg0);
    }
}
