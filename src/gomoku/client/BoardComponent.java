package gomoku.client;

import gomoku.logic.Board;
import gomoku.logic.Piece;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;


import static com.esotericsoftware.minlog.Log.*;

/**
 * A component displaying a Gomoku board. The board component handles input
 * and rendering while {@link Board} handles the logic.
 *
 * @author Samuel Andersson
 */
public class BoardComponent extends AbstractComponent {

	/** The x location of this component in pixels */
	protected int x;

	/** The y location of this component in pixels */
	protected int y;

	/**
	 * The width of this component in pixels (this is currently adjusted to fit
	 * perfectly with the amount of squares that can fit)
	 */
	protected int width;

	/**
	 * The height of this component in pixels (this is currently adjusted to fit
	 * perfectly with the amount of squares that can fit)
	 */
	protected int height;

	/** The left border of the board to display */
	protected int leftBorder;

	/** The top border of the board to display */
	protected int topBorder;

	/** The amount of squares to display horizontally */
	protected int displayWidth;

	/** The amount of squares to display vertically */
	protected int displayHeight;

	/** Indicates whether the display size of the board is locked */
	protected boolean sizeLocked;

	/** The margin between pieces and the square edge (in pixels) */
	protected int pieceMargin;

	/** The board */
	protected Board board;

	/** The cross image */
	protected Image cross;

	/** The circle image */
	protected Image circle;

	/** The rectangle used to bind images to */
	private Rectangle rect;

	/** The current position of the mouse on the board (in squares) */
	private int xPosOnBoard;

	/** The current position of the mouse on the board (in squares) */
	private int yPosOnBoard;

	/**
	 * The string used to display the current position of the mouse on the board
	 */
	private String posOnBoard;

	/**
	 * Create a new board component
	 *
	 * @see #BoardComponent(GUIContext, Board, int, int, int, int, int, int,
	 *      int, int, int)
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
	 *            The margin between the board piece and the square edge
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

		rect = new Rectangle(0, 0, 10, 10);
		try {
			cross = new Image("res/cross.png");
			circle = new Image("res/circle.png");
		} catch (SlickException e) {
			if (TRACE)
				trace("BoardComponent", e);
			else
				error("BoardComponent",
						"Image couldn't be loaded! " + e.getMessage());
		}
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
	public void keyPressed(int c, char ch) {
		if (c == Input.KEY_ADD) {
			leftBorder++;
			topBorder++;
			setDisplaySize(displayWidth - 2, displayHeight - 2);
		} else if (c == Input.KEY_MINUS) {
			leftBorder--;
			topBorder--;
			setDisplaySize(displayWidth + 2, displayHeight + 2);
		} else if (c == Input.KEY_UP) {
			topBorder--;
		} else if (c == Input.KEY_DOWN) {
			topBorder++;
		} else if (c == Input.KEY_LEFT) {
			leftBorder--;
		} else if (c == Input.KEY_RIGHT) {
			leftBorder++;
		} else {
			if (input.isKeyDown(Input.KEY_LCONTROL)
					|| input.isKeyDown(Input.KEY_RCONTROL)) {
				if (c == Input.KEY_F) {
					setDisplaySize(-1, -1);
				}
			}
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		if (Rectangle.contains(newx, newy, this.x, this.y, width, height)) {
			setPositionOnBoard(getMouseXPositionOnBoard(newx),
					getMouseYPositionOnBoard(newy));
		}
	}

	@Override
	public void mouseReleased(int button, int x, int y) {
		if (Rectangle.contains(x, y, this.x, this.y, width, height)) {
			squareClicked(getMouseXPositionOnBoard(x),
					getMouseYPositionOnBoard(y));
		}
	}

	/**
	 * Notification that a square was clicked on the board
	 *
	 * @param x
	 *            The x location on the board
	 * @param y
	 *            The y location on the board
	 */
	public void squareClicked(int x, int y) {
	}

	/**
	 * @see AbstractComponent#render(GUIContext, Graphics)
	 */
	@Override
	public void render(GUIContext container, Graphics g) throws SlickException {
		Color oldColor = g.getColor();
		g.pushTransform();
		g.translate(this.x, this.y);

		// draw border
		g.setColor(Color.green);
		g.drawRect(0, 0, width, height);

		g.setColor(Color.cyan);
		// draw grid lines
		for (int x = 1; x < displayWidth; x++) {
			int xPos = x * (width / displayWidth);
			g.drawLine(xPos, 0, xPos, height);
		}
		for (int y = 1; y < displayHeight; y++) {
			int yPos = y * (height / displayHeight);
			g.drawLine(0, yPos, width, yPos);
		}

		// draw objects inside the grid lines
		for (int x = 0; x < displayWidth; x++) {
			for (int y = 0; y < displayHeight; y++) {
				Piece piece = board.getPiece(x + leftBorder, y + topBorder);
				if (piece == null)
					continue;

				if (piece.getPlayerColor() == Board.REDPLAYER) {
					drawCircle(x, y, g);
				} else {
					drawCross(x, y, g);
				}
			}
		}

		// print position-on-board information
		g.popTransform();
		g.setColor(Color.white);
		g.drawString(posOnBoard, 50, container.getHeight() - 30);
		g.setColor(oldColor);
	}

	/**
	 * Draw a circle on the provided position on the board
	 *
	 * @see #drawImage(Image, int, int, Graphics)
	 */
	private void drawCircle(int x, int y, Graphics g) {
		drawImage(circle, x, y, g);
	}

	/**
	 * Draw a cross on the provided position on the board
	 *
	 * @see #drawImage(Image, int, int, Graphics)
	 */
	private void drawCross(int x, int y, Graphics g) {
		drawImage(cross, x, y, g);
	}

	/**
	 * Draw an image to a location on the board
	 *
	 * @param image
	 *            The image to be drawn
	 * @param x
	 *            The x location on the board
	 * @param y
	 *            The y location on the board
	 * @param g
	 *            The graphics context to draw on
	 */
	private void drawImage(Image image, int x, int y, Graphics g) {
		g.setColor(Color.white);
		// get the relative x position
		int xPos = (x + 1) * (width / displayWidth)
				- (width / (2 * displayWidth));
		// get the relative y position
		int yPos = (y + 1) * (height / displayHeight)
				- (height / (2 * displayHeight));

		rect.setCenterX(xPos);
		rect.setCenterY(yPos);
		image.setImageColor(0, 0, 1);
		g.texture(rect, image, true);
	}

	/**
	 * Get the x position on the board where the mouse is
	 *
	 * @param mouseX
	 *            The mouse X location (in pixels)
	 * @return The corresponding x position on the board
	 */
	public int getMouseXPositionOnBoard(int mouseX) {
		for (int x = 0; x < displayWidth; x++) {
			if (mouseX < this.x + ((x + 1) * width / displayWidth))
				return x + leftBorder;
		}
		return displayWidth + leftBorder;
	}

	/**
	 * Get the y position on the board where the mouse is
	 *
	 * @param mouseY
	 *            The mouse Y location (in pixels)
	 * @return The corresponding y position on the board
	 */
	public int getMouseYPositionOnBoard(int mouseY) {
		for (int y = 0; y < displayHeight; y++) {
			if (mouseY < this.y + ((y + 1) * height / displayHeight))
				return y + topBorder;
		}
		return displayHeight + topBorder;
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
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Get the display height of the board
	 *
	 * @return The display height of the board
	 */
	public int getDisplayHeight() {
		return displayHeight;
	}

	/**
	 * Get the display width of the board
	 *
	 * @return The display width of the board
	 */
	public int getDisplayWidth() {
		return displayWidth;
	}

	/**
	 * Set the display width of the board
	 *
	 * @param width
	 *            The display width of the board
	 */
	public void setDisplayWidth(int width) {
		setDisplaySize(width, displayHeight);
	}

	/**
	 * Set the display height of the board
	 *
	 * @param height
	 *            The display height of the board
	 */
	public void setDisplayHeight(int height) {
		setDisplaySize(displayWidth, height);
	}

	/**
	 * Set the size of the board to display
	 *
	 * @param width
	 *            The width of the board to display
	 * @param height
	 *            The height of the board to display
	 */
	public void setDisplaySize(int width, int height) {
		if (sizeLocked)
			return;
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

	/**
	 * Get whether the display size is locked
	 *
	 * @return True if the display size is locked
	 */
	public boolean getSizeLock() {
		return sizeLocked;
	}

	/**
	 * Set whether the display size is locked
	 *
	 * @param lock
	 *            True if the size is to be locked
	 */
	public void setSizeLock(boolean lock) {
		sizeLocked = lock;
	}

	/**
	 * Get the margin between pieces and their square edge
	 *
	 * @return The margin between pieces and their square edge
	 */
	public int getPieceMargin() {
		return pieceMargin;
	}

	/**
	 * Set the margin between pieces and their square edge
	 *
	 * @param margin
	 *            The margin between pieces their square edge
	 */
	public void setPieceMargin(int margin) {
		pieceMargin = margin;
		rect.setSize(width / displayWidth - margin * 2, height / displayHeight
				- margin * 2);
	}

	/**
	 * If the mouse position on the board changed, update
	 *
	 * @param x
	 *            The x location on the board
	 * @param y
	 *            The y location on the board
	 */
	private void setPositionOnBoard(int x, int y) {
		if (x != xPosOnBoard || y != yPosOnBoard) {
			xPosOnBoard = x;
			yPosOnBoard = y;
			posOnBoard = "Pos: " + x + ", " + y;
		}
	}
}
