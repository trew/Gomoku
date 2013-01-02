package tictactoe.states;

import net.*;
import static net.GenericRequestPacket.Request.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Circle;

import tictactoe.Tictactoe;
import tictactoe.logic.Game;
import tictactoe.logic.Player;

public class GameplayState extends TTTGameState {

	/** Contains the game logic */
	public Game game;

	public Player me;

	private Circle piece;

	public boolean MyTurn() {
		return me == game.getTurn();
	}

	private GameplayStateListener listener;

	@Override
	public void init(GameContainer container, Tictactoe game)
			throws SlickException {
		piece = new Circle(0, 0, 40);
		this.game = new Game();

		// add network listener
		listener = new GameplayStateListener(this);
		game.client.addListener(listener);
	}

	public int getMouseXPositionOnBoard(GameContainer container) {
		int width = container.getWidth();
		int mouseX = container.getInput().getMouseX();

		if (mouseX < width / 3) {
			return 0;
		} else if (mouseX < 2 * width / 3) {
			return 1;
		} else {
			return 2;
		}
	}

	public int getMouseYPositionOnBoard(GameContainer container) {
		int height = container.getHeight();
		int mouseY = container.getInput().getMouseY();

		if (mouseY < height / 3) {
			return 0;
		} else if (mouseY < 2 * height / 3) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public void enter(GameContainer container, Tictactoe game)
			throws SlickException {
		GenericRequestPacket grp = new GenericRequestPacket(BoardUpdate);
		game.client.sendTCP(grp);
		grp = new GenericRequestPacket(GetColor);
		game.client.sendTCP(grp);
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

		if (me == null || !MyTurn()) return;

		if (container.getInput().isMousePressed(0)) {
			int boardX = getMouseXPositionOnBoard(container);
			int boardY = getMouseYPositionOnBoard(container);
			Player player = this.game.getPieceOwner(boardX, boardY);
			if (player == null) {
				// place a new piece
				placePiece(game, boardX, boardY);
			}
		}
	}

	@Override
	public void render(GameContainer container, Tictactoe game, Graphics g)
			throws SlickException {
		// draw the board
		g.drawLine(0, container.getHeight() / 3, container.getWidth(),
				container.getHeight() / 3);
		g.drawLine(0, 2 * (container.getHeight() / 3), container.getWidth(),
				2 * (container.getHeight() / 3));

		g.drawLine(container.getWidth() / 3, 0, container.getWidth() / 3,
				container.getHeight());
		g.drawLine(2 * (container.getWidth() / 3), 0,
				2 * (container.getWidth() / 3), container.getHeight());

		// draw the pieces of the board
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				piece.setCenterX((x) * (container.getWidth() / 3)
						+ (container.getWidth() / 6));
				piece.setCenterY((y) * (container.getHeight() / 3)
						+ (container.getHeight() / 6));
				if (this.game.getPieceOwner(x, y) == this.game.getRed()) {
					g.fill(piece, new GradientFill(0, 0, Color.red, 1, 1,
							Color.red));
				} else if (this.game.getPieceOwner(x, y) == this.game.getBlue()) {
					g.fill(piece, new GradientFill(0, 0, Color.blue, 1, 1,
							Color.blue));
				}
			}
		}

		int x = getMouseXPositionOnBoard(container);
		int y = getMouseYPositionOnBoard(container);

		String s = "Pos: " + x + ", " + y;
		g.drawString(s, container.getWidth() / 2 - 50,
				container.getHeight() - 50);
	}

	@Override
	public int getID() {
		return 1;
	}

}
