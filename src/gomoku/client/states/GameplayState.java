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
	public GomokuGame game;

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
		if (board == null) {
			error("GameplayState", "Received no information about the board.");
			return;
		}
		game = new GomokuGame(board);
		game.setTurn(game.getPlayer(turn));
		setPlayer(playerColor);

		boardComponent.setBoard(board);

		loading = false;
	}

	public void setPlayer(int playerColor) {
		if (playerColor == Board.REDPLAYER)
			me = game.getRed();
		else if (playerColor == Board.BLUEPLAYER)
			me = game.getBlue();
		else {
			error("GameplayState", "Color couldn't bet set!");
			return;
		}
		info("GameplayState", "Color set to " + me.getName());

	}

	@Override
	public void init(GameContainer container, final GomokuClient game)
			throws SlickException {

		loading = true; // will be set to false once we receive data from the
						// server

		// add the board
		boardComponent = new BoardComponent(container, null, 100, 50, 25, 10,
				10) {
			@Override
			public void squareClicked(int x, int y) {
				if (me == null || !myTurn())
					return;
				placePiece(game, x, y);
			}
		};

		// add network listener
		listener = new GameplayStateListener(this);
		game.client.addListener(listener);
	}

	@Override
	public void enter(GameContainer container, GomokuClient game)
			throws SlickException {
		game.client.sendTCP(new GenericRequestPacket(InitialData));
	}

	/**
	 * Try to place a new piece on provided position. If successful client-side,
	 * send a packet to server trying to do the same thing.
	 *
	 * @param game
	 *            The game which we place the piece in
	 * @param x
	 *            The x location for the new piece
	 * @param y
	 *            The y location for the new piece
	 */
	public void placePiece(GomokuClient game, int x, int y) {
		if (this.game.placePiece(x, y, me)) {
			game.client.sendTCP(new PlacePiecePacket(x, y, me));
		}
	}

	/**
	 * Whether it's our turn or not
	 *
	 * @return True if it's our turn
	 */
	public boolean myTurn() {
		return me == game.getTurn();
	}

	public void setBoardSize(int width, int height) {
		boardComponent.setDisplaySize(width, height, 25);
	}

	@Override
	public void update(GameContainer container, GomokuClient game, int delta)
			throws SlickException {

		if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			container.exit();
		}

		/* *** NETWORK RELATED INPUT *** */
		if (game.client.isConnected()) {
			if (container.getInput().isKeyPressed(Input.KEY_F5)) {
				game.client.sendTCP(new GenericRequestPacket(BoardUpdate));
			}

			// ctrl is pressed
			if (container.getInput().isKeyDown(Input.KEY_LCONTROL)
					|| container.getInput().isKeyDown(Input.KEY_RCONTROL)) {

				// clear board
				if (container.getInput().isKeyPressed(Input.KEY_C)) {
					game.client.sendTCP(new GenericRequestPacket(ClearBoard));
				}
			}
		}
		/* *** END NETWORK RELATED INPUT *** */
	}

	@Override
	public void render(GameContainer container, GomokuClient game, Graphics g)
			throws SlickException {
		// draw the board
		if (loading) {
			g.drawString("Loading...", 200, 200);
		} else {
			boardComponent.render(container, g);
		}
	}

	@Override
	public int getID() {
		return 1;
	}

}
