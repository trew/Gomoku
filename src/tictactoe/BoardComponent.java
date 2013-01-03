package tictactoe;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Ellipse;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

import tictactoe.logic.Board;
import tictactoe.logic.Piece;

public class BoardComponent extends AbstractComponent {

	private int x;
	private int y;
	private int width;
	private int height;

	private int leftBorder;
	private int topBorder;
	private int displayWidth;
	private int displayHeight;

	private int pieceMargin;

	private Board board;

	private Ellipse ellipse;

	private int xPosOnBoard;
	private int yPosOnBoard;
	private String posOnBoard;

	private boolean sizeLocked;

	/**
	 * Create a new board component
	 *
	 * @see #BoardComponent(GUIContext, Board, int, int, int, int, int, int, int, int, int)
	 */
	public BoardComponent(GUIContext container, Board board, int x, int y,
			int width, int height, int pieceMargin) {
		this(container, board, x, y, width, height, -2, -2, 5, 5, pieceMargin);
	}

	/**
	 * Create a new board component
	 *
	 * @param container
	 *            The container holding this component
	 * @param board
	 *            The board which we pull data from
	 * @param x
	 *            The x location of this component
	 * @param y
	 *            The y location of this component
	 * @param width
	 *            The width in pixels
	 * @param height
	 *            The height in pixels
	 * @param topBorder
	 *            The top border of the board to display
	 * @param leftBorder
	 *            The left border of the board to display
	 * @param displayWidth
	 *            The number of squares to display horizontally
	 * @param displayHeight
	 *            The number of squares to display vertically
	 * @param pieceMargin
	 *            The margin between the board piece and the square border
	 */
	public BoardComponent(GUIContext container, Board board, int x, int y,
			int width, int height, int topBorder, int leftBorder,
			int displayWidth, int displayHeight, int pieceMargin) {
		super(container);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.board = board;

		ellipse = new Ellipse(0, 0, 10, 10);

		xPosOnBoard = 0;
		yPosOnBoard = 0;
		posOnBoard = "Pos: ---";

		sizeLocked = false;

		this.leftBorder = leftBorder;
		this.topBorder = topBorder;
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;

		this.width -= this.width % displayWidth;
		this.height -= this.height % displayHeight;

		this.pieceMargin = pieceMargin;
		setPieceMargin(pieceMargin);
	}

	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		g.pushTransform();

		// draw border
		g.setColor(Color.green);
		g.drawRect(x, y, width, height);

		g.setColor(Color.cyan);
		// draw grid lines
		for (int x = 1; x < displayWidth; x++) {
			int xPos = this.x + x * (width / displayWidth);
			g.drawLine(xPos, this.y, xPos, this.y + height);
		}
		for (int y = 1; y < displayHeight; y++) {
			int yPos = this.y + y * (height / displayHeight);
			g.drawLine(this.x, yPos, this.x + width, yPos);
		}


		// draw objects inside the grid lines
		for (int x = 0; x < displayWidth; x++) {
			for (int y = 0; y < displayHeight; y++) {
				Piece piece = board.getPiece(x + leftBorder, y + topBorder);
				if (piece == null)
					continue;

				// get the relative x position
				int xPos = (x + 1) * (width / displayWidth)
						- (width / (2 * displayWidth));
				// get the relative y position
				int yPos = (y + 1) * (height / displayHeight)
						- (height / (2 * displayHeight));

				if (piece.getPlayerColor() == Board.REDPLAYER) {
					g.setColor(Color.red);
				} else {
					g.setColor(Color.blue);
				}
				ellipse.setCenterX(this.x + xPos);
				ellipse.setCenterY(this.y + yPos);
				g.fill(ellipse);
			}
		}

		// print position-on-board information
		g.popTransform();
		g.setColor(Color.white);
		g.drawString(posOnBoard, 50, container.getHeight() - 30);
		String borders = "Top: " + topBorder + "Left: " + leftBorder
				+ "Width: " + displayWidth + "Height: " + displayHeight;
		g.drawString("Borders: " + borders, 100, 10);
	}

	public int getMouseXPositionOnBoard(int mouseX) {
		for (int x = 0; x < displayWidth; x++) {
			if (mouseX < this.x + (x + 1) * width / displayWidth)
				return x + leftBorder;
		}
		return displayWidth + leftBorder;
	}

	public int getMouseYPositionOnBoard(int mouseY) {
		for (int y = 0; y < displayHeight; y++) {
			if (mouseY < this.y + (y + 1) * height / displayHeight)
				return y + topBorder;
		}
		return displayHeight + topBorder;
	}

	private void setPositionOnBoard(int x, int y) {
		if (x != xPosOnBoard || y != yPosOnBoard) {
			xPosOnBoard = x;
			yPosOnBoard = y;
			posOnBoard = "Pos: " + x + ", " + y;
		}
	}

	public void setPieceMargin(int margin) {
		pieceMargin = margin;
		ellipse.setRadius1((this.width / displayWidth) / 2 - margin);
		ellipse.setRadius2((this.height / displayHeight) / 2 - margin);
	}

	public void setDisplaySize(int width, int height) {
		if (sizeLocked) return;
		if (width > 0 && height > 0) {
			displayWidth = width;
			displayHeight = height;
		} else {
			// adjust to fit
			leftBorder = board.getLeftBorder() - 1;
			topBorder = board.getTopBorder() - 1;
			displayWidth = board.getWidth() + 2;
			displayHeight = board.getHeight() + 2;
			if (displayWidth < 3) {
				displayWidth = 3;
			}
			if (displayHeight < 3) {
				displayHeight = 3;
			}
		}
		setPieceMargin(pieceMargin);
	}

	public void setDisplayWidth(int width) {
		setDisplaySize(width, displayHeight);
	}

	public void setDisplayHeight(int height) {
		setDisplaySize(displayWidth, height);
	}

	public int getDisplayWidth() {
		return displayWidth;
	}
	public int getDisplayHeight() {
		return displayHeight;
	}

	public void setSizeLock(boolean lock) {
		sizeLocked = lock;
	}

	public boolean getSizeLock() {
		return sizeLocked;
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if (Rectangle.contains(newx, newy, this.x, this.y, width, height)) {
			setPositionOnBoard(getMouseXPositionOnBoard(newx),
					getMouseYPositionOnBoard(newy));
		}
	}

	public int getPieceMargin() {
		return pieceMargin;
	}

	@Override
	public void keyPressed(int c, char ch) {
		if (c == Input.KEY_ADD) {
			leftBorder++;
			topBorder++;
			setDisplaySize(displayWidth-2, displayHeight-2);
		} else if (c == Input.KEY_MINUS) {
			leftBorder--;
			topBorder--;
			setDisplaySize(displayWidth+2, displayHeight+2);
		} else if (c == Input.KEY_UP) {
			topBorder--;
		} else if (c == Input.KEY_DOWN) {
			topBorder++;
		} else if (c == Input.KEY_LEFT) {
			leftBorder--;
		} else if (c == Input.KEY_RIGHT) {
			leftBorder++;
		} else {
			if (input.isKeyDown(Input.KEY_LCONTROL) || input.isKeyDown(Input.KEY_RCONTROL)) {
				if (c == Input.KEY_F) {
					setDisplaySize(-1, -1);
				}
			}
		}
	}

	@Override
	public void mousePressed(int button, int x, int y) {
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if (Rectangle.contains(x, y, this.x, this.y, width, height)) {
			squareClicked(getMouseXPositionOnBoard(x),
					getMouseYPositionOnBoard(y));
		}
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
	}

	@Override
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	public void squareClicked(int x, int y) {
	}
}
