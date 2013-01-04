package tictactoe.states;

import static tictactoe.net.Request.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import tictactoe.BoardComponent;
import tictactoe.Tictactoe;
import tictactoe.logic.Game;
import tictactoe.logic.Player;
import tictactoe.net.*;

/**
 * The playing state of the tic tac toe game.
 *
 * @author Samuel Andersson
 */
public class GameplayState extends TTTGameState {

	/** Contains the game logic */
	public Game game;

	/** The player */
	public Player me;

	/** The board displayed */
	private BoardComponent boardComponent;

	/** The Network listener for this state */
	private GameplayStateListener listener;

	@Override
	public void init(GameContainer container, final Tictactoe game)
			throws SlickException {

		// create a new game
		this.game = new Game();

		// add the board
		boardComponent = new BoardComponent(container, this.game.getBoard(),
				100, 50, 400, 400, 0, 0, 15, 15, 4) {
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
	public void enter(GameContainer container, Tictactoe game)
			throws SlickException {
		game.client.sendTCP(new GenericRequestPacket(BoardUpdate));

		// turn is received here
		game.client.sendTCP(new GenericRequestPacket(GetColorAndTurn));
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
	public void placePiece(Tictactoe game, int x, int y) {
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

	@Override
	public void update(GameContainer container, Tictactoe game, int delta)
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
	public void render(GameContainer container, Tictactoe game, Graphics g)
			throws SlickException {
		// draw the board
		boardComponent.render(container, g);
	}

	@Override
	public int getID() {
		return 1;
	}

}
