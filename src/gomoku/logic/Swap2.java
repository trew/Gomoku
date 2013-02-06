package gomoku.logic;

import gomoku.logic.Board.PlacePieceBoardAction;
import gomoku.logic.GomokuGame.GameAction;

import static org.trew.log.Log.*;

public class Swap2 {

    /**
     * States: 0 = Player 1 places 1st black piece 1 = Player 1 places 1st white
     * piece 2 = Player 1 places 2nd black piece 3 = Player 2 chooses between
     * picking color or placing two pieces if Player 2 chooses to place pieces:
     * 4 = Player 2 places 2nd white piece 5 = Player 2 places 2nd black piece 6
     * = Black chooses color
     */
    private int state;

    protected boolean active;
    protected boolean choseToPlace;

    public Swap2() {
        state = 0;
        active = true;
    }

    /**
     * Returns whether the opening is still in progress
     *
     * @return whether the opening is still in progress
     */
    public boolean isActive() {
        return active;
    }

    private int getPlayerColorFromState() {
        if (state == 1 || state == 4)
            return Board.WHITEPLAYER;
        if (state == 3 || state == 6)
            return Board.NOPLAYER;
        return Board.BLACKPLAYER;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void nextState() {
        state++;
    }

    public void previousState() {
        state--;
    }

    static public class Swap2PlacePieceGameAction implements GameAction {

        protected int playerColor;
        protected PlacePieceBoardAction boardAction;

        /** For kryonet */
        @SuppressWarnings("unused")
        private Swap2PlacePieceGameAction() {
        }

        public Swap2PlacePieceGameAction(Swap2 swap2, int x, int y,
                boolean waitForConfirm) {
            playerColor = swap2.getPlayerColorFromState();
            boardAction = new PlacePieceBoardAction(playerColor, x, y);
        }

        public PlacePieceBoardAction getBoardAction() {
            return boardAction;
        }

        @Override
        public int getPlayerColor() {
            return playerColor;
        }

        @Override
        public void doAction(GomokuGame game) throws IllegalActionException {
            boardAction.doAction(game.getBoard());
        }

        @Override
        public void confirmAction(GomokuGame game) {
            game.getSwap2().nextState();
            if (game.getSwap2().state == 3)
                game.switchTurn();
            if (game.getSwap2().state == 6) {
                game.switchTurn();
            }
        }

        @Override
        public void undoAction(GomokuGame game) {
            boardAction.undoAction(game.getBoard());
        }

    }

    static public class Swap2ChooseColorAction implements GameAction {

        /** For kryonet */
        @SuppressWarnings("unused")
        private Swap2ChooseColorAction() {
        }

        protected int playerID;
        protected int color;

        public Swap2ChooseColorAction(GomokuGame game, int playerID, int color) {
            this.playerID = playerID;
            this.color = color;
        }

        @Override
        public int getPlayerColor() {
            return playerID;
        }

        @Override
        public void doAction(GomokuGame game) throws IllegalActionException {
            info("SWAP2: setting color of player " + playerID + " to " + color);
            game.setColor(playerID, color);
            game.setTurn(game.getPlayerFromColor(Board.WHITEPLAYER));
            game.getSwap2().active = false;
        }

        @Override
        public void confirmAction(GomokuGame game) {
            // cannot be confirmed
        }

        @Override
        public void undoAction(GomokuGame game) {
            // cannot be undone
        }

    }
    /**
     * Returns whether the player is opted to choose color or place two more
     * pieces
     *
     * @return
     */
    public boolean isChoosingColorOrPlace(Player player, GomokuGame game) {
        return game.getSwap2().active && player.getID() == game.getTurn().getID() && state == 3;
    }

    /**
     * Returns whether the player is opted to choose color
     *
     * @return
     */
    public boolean isChoosingColor(Player player, GomokuGame game) {
        return game.getSwap2().active && player.getID() == game.getTurn().getID() && state == 6;
    }
}
