package tictactoe;

import net.*;
import static net.GenericRequestPacket.Request.*;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.fills.GradientFill;
import org.newdawn.slick.geom.Circle;

public class GameplayState extends TTTGameState {

	private Board board;

	private Circle piece;

	private int selectedColor;

	private boolean pieceIsSelected;
	private int selectedPieceX;
	private int selectedPieceY;

	private GameplayStateListener listener;

	@Override
	public void init(GameContainer container, Tictactoe game)
			throws SlickException {
		board = new Board();
		piece = new Circle(0, 0, 40);
		selectedColor = Board.REDPLAYER;
		selectedPieceX = -1;
		selectedPieceY = -1;
		pieceIsSelected = false;

		// add network listener
		listener = new GameplayStateListener(this, board);
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

	public void selectPiece(int x, int y) {
		selectedColor = board.getPlayer(x, y);
		pieceIsSelected = true;
		selectedPieceX = x;
		selectedPieceY = y;
	}

	public void deselectPiece() {
		pieceIsSelected = false;
		selectedPieceX = -1;
		selectedPieceY = -1;
	}

	@Override
	public void enter(GameContainer container, Tictactoe game)
			throws SlickException {
		GenericRequestPacket grp = new GenericRequestPacket(BoardUpdate);
		game.client.sendTCP(grp);
	}

	public void movePiece(Tictactoe game, int x1, int y1, int x2, int y2) {
		if (board.movePiece(x1, y1, x2, y2)) {
			game.client.sendTCP(new MovePiecePacket(x1, y1, x2, y2));
		}
	}

	public void placePiece(Tictactoe game, int x, int y, int player) {
		if (board.placePiece(selectedColor, x, y)) {
			game.client.sendTCP(new PlacePiecePacket(x, y, player));
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

		if (pieceIsSelected) {
			// place piece
			if (container.getInput().isMousePressed(0)) {
				int x = getMouseXPositionOnBoard(container);
				int y = getMouseYPositionOnBoard(container);
				movePiece(game, selectedPieceX, selectedPieceY, x, y);
				deselectPiece();
			}

			// deselect piece
			if (container.getInput().isMousePressed(1)) {
				deselectPiece();
			}
		} else {
			// swap selected color
			if (container.getInput().isMousePressed(1)) {
				selectedColor = selectedColor == Board.REDPLAYER ? Board.BLUEPLAYER
						: Board.REDPLAYER;
			}

			// select piece, if possible
			if (container.getInput().isMousePressed(0)) {
				int boardX = getMouseXPositionOnBoard(container);
				int boardY = getMouseYPositionOnBoard(container);
				int clr = board.getPlayer(boardX, boardY);
				if (clr != Board.NOPLAYER) {
					selectPiece(boardX, boardY);
				} else {
					// place a new piece
					placePiece(game, boardX, boardY, selectedColor);
				}
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
				if (pieceIsSelected
						&& (x == selectedPieceX && y == selectedPieceY))
					continue;
				piece.setCenterX((x) * (container.getWidth() / 3)
						+ (container.getWidth() / 6));
				piece.setCenterY((y) * (container.getHeight() / 3)
						+ (container.getHeight() / 6));
				if (board.getPlayer(x, y) == Board.REDPLAYER) {
					g.fill(piece, new GradientFill(0, 0, Color.red, 1, 1,
							Color.red));
				} else if (board.getPlayer(x, y) == Board.BLUEPLAYER) {
					g.fill(piece, new GradientFill(0, 0, Color.blue, 1, 1,
							Color.blue));
				}
			}
		}

		// draw selected piece at mouse
		if (pieceIsSelected) {
			piece.setCenterX(container.getInput().getMouseX());
			piece.setCenterY(container.getInput().getMouseY());
			if (selectedColor == Board.REDPLAYER) {
				g.fill(piece,
						new GradientFill(0, 0, Color.red, 1, 1, Color.red));
			} else if (selectedColor == Board.BLUEPLAYER) {
				g.fill(piece, new GradientFill(0, 0, Color.blue, 1, 1,
						Color.blue));
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
