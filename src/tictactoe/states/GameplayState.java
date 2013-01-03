package tictactoe.states;

import static tictactoe.net.GenericRequestPacket.Request.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import tictactoe.BoardComponent;
import tictactoe.Tictactoe;
import tictactoe.logic.Game;
import tictactoe.logic.Player;
import tictactoe.net.*;

public class GameplayState extends TTTGameState {

	/** Contains the game logic */
	public Game game;

	public Player me;

	private BoardComponent boardComponent;

	public boolean MyTurn() {
		return me == game.getTurn();
	}

	private GameplayStateListener listener;

	@Override
	public void init(GameContainer container, final Tictactoe game)
			throws SlickException {
		this.game = new Game();

		boardComponent = new BoardComponent(container, this.game.getBoard(),
				100, 50, 400, 400, 0, 0, 15, 15, 4) {
			@Override
			public void squareClicked(int x, int y) {
				if (me == null || !MyTurn()) return;
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
		game.client.sendTCP(new GenericRequestPacket(GetColorAndTurn)); // turn is
																	// received
																	// here
	}

	public void placePiece(Tictactoe game, int x, int y) {
		if (this.game.placePiece(x, y, me)) {
			game.client.sendTCP(new PlacePiecePacket(x, y, me));
		}
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
