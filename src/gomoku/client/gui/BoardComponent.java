package gomoku.client.gui;

import gomoku.logic.Board;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

import static org.trew.log.Log.*;

/**
 * A component displaying a Gomoku board. The board component handles input and
 * rendering while {@link Board} handles the logic.
 *
 * @author Samuel Andersson
 */
public class BoardComponent extends AbstractComponent {

    /**
     * This area is currently calculated by multiplying the provided
     * square-width/height with the amount of squares to display on the
     * width/height.
     */
    protected Rectangle area;

    /** The left border of the board to display */
    protected int leftBorder;

    /** The top border of the board to display */
    protected int topBorder;

    /** The amount of squares to display horizontally */
    protected int displayWidth;

    /** The amount of squares to display vertically */
    protected int displayHeight;

    /** The size of each individual square on the board */
    protected int squareSize;

    /** The board */
    protected Board board;

    /** The black piece image */
    protected Image black;

    /** The white piece image */
    protected Image white;

    /** The board square image */
    protected Image squareImage;

    /** The rectangle used to bind images to */
    private Rectangle pieceRect;

    /** The rectangle used to create the board background */
    private Rectangle bgRect;

    /** Whether the board is changeable */
    private boolean changeable;

    /**
     * Create a new board component with the default width of 5 and height of 5.
     *
     * @see #BoardComponent(GUIContext, Board, int, int, int, int, int)
     */
    public BoardComponent(GUIContext container, Board board, int x, int y,
            int squareSize) {
        this(container, board, x, y, squareSize, 5, 5);
    }

    /**
     * Create a new board component. The squaresize and the amount of squares to
     * display determines the actual size of this component.
     *
     * @param container
     *            The container holding this component
     * @param board
     *            The board which we pull data from
     * @param x
     *            The x location of this component
     * @param y
     *            The y location of this component
     * @param squareSize
     *            The size of each individual square on the board
     * @param displayWidth
     *            The number of squares to display horizontally
     * @param displayHeight
     *            The number of squares to display vertically
     */
    public BoardComponent(GUIContext container, Board board, int x, int y,
            int squareSize, int displayWidth, int displayHeight) {
        super(container);
        this.area = new Rectangle(x, y, squareSize * displayWidth, squareSize
                * displayHeight);
        this.board = board;

        this.squareSize = squareSize;
        pieceRect = new Rectangle(0, 0, 10, 10);
        bgRect = new Rectangle(0, 0, squareSize, squareSize);
        try {
            black = new Image("res/black.png");
            white = new Image("res/white.png");
            squareImage = new Image("res/boardsquare.png");
        } catch (SlickException e) {
            if (TRACE)
                trace(e);
            else
                error("Image couldn't be loaded! " + e.getMessage());
        }
        this.leftBorder = 0;
        this.topBorder = 0;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        changeable = true;

        pieceRect.setSize(getWidth() / displayWidth - 3 * 2, getHeight()
                / displayHeight - 3 * 2);
    }

    /**
     * Set the board which this component will represent. Setting this might
     * change the displaySize of the component if the board being shown is
     * smaller than current displaySize.
     *
     * @param board
     *            the board which this component will represent
     */
    public void setBoard(Board board) {
        boolean changed = false;
        if (board.getWidth() < displayWidth) {
            displayWidth = board.getWidth();
            changed = true;
        }
        if (board.getHeight() < displayHeight) {
            displayHeight = board.getHeight();
            changed = true;
        }
        if (changed) {
            setDisplaySize(displayWidth, displayHeight, squareSize);
        }
        this.board = board;
    }

    /**
     * Used to scroll the board using the arrow keys or VIM keys (HJKL)
     */
    @Override
    public void keyPressed(int c, char ch) {
        if (board != null) {
            if (c == Input.KEY_UP || c == Input.KEY_K) {
                if (--topBorder < 0)
                    topBorder = 0;
            } else if (c == Input.KEY_DOWN || c == Input.KEY_J) {
                if (topBorder + displayHeight + 1 <= board.getHeight())
                    topBorder++;
            } else if (c == Input.KEY_LEFT || c == Input.KEY_H) {
                if (--leftBorder < 0)
                    leftBorder = 0;
            } else if (c == Input.KEY_RIGHT || c == Input.KEY_L) {
                if (leftBorder + displayWidth + 1 <= board.getWidth())
                    leftBorder++;
            }
        }
    }

    /**
     * Will trigger {@link #squareClicked(int, int)} if left mouse button was
     * clicked and mouse is within board area.
     */
    @Override
    public void mouseReleased(int button, int x, int y) {
        if (button == 0 && area.contains(x, y) && changeable) {
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
     * Render a border around the whole board. Draw a red border if we're at the
     * edge, draw green if the board is scrollable that way
     *
     * @param g
     *            The graphics context to which we'll render this border
     */
    private void renderBorder(Graphics g) {
        if (board == null) {
            g.setColor(Color.black);
            g.drawRect(0, 0, area.getWidth(), area.getHeight());
            return;
        }
        // left
        if (leftBorder == 0)
            g.setColor(Color.black);
        else
            g.setColor(Color.green);
        g.drawLine(0, 0, 0, getHeight());

        // top
        if (topBorder == 0)
            g.setColor(Color.black);
        else
            g.setColor(Color.green);
        g.drawLine(0, 0, getWidth(), 0);

        // bottom
        if (topBorder + displayHeight >= board.getHeight())
            g.setColor(Color.black);
        else
            g.setColor(Color.green);
        g.drawLine(0, getHeight(), getWidth(), getHeight());

        // right
        if (leftBorder + displayWidth >= board.getWidth())
            g.setColor(Color.black);
        else
            g.setColor(Color.green);
        g.drawLine(getWidth(), 0, getWidth(), getHeight());
    }

    /**
     * @see AbstractComponent#render(GUIContext, Graphics)
     */
    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        Color oldColor = g.getColor();
        g.pushTransform();
        g.translate(area.getX(), area.getY());

        g.setColor(Color.white);
        for (int x = 0; x < displayWidth; x++) {
            for (int y = 0; y < displayHeight; y++) {
                bgRect.setLocation(x * bgRect.getWidth(),
                        y * bgRect.getHeight());
                g.texture(bgRect, squareImage, true);
            }
        }

        g.setColor(Color.black);
        // draw grid lines
        for (int x = 1; x < displayWidth; x++) {
            int xPos = x * (getWidth() / displayWidth);
            g.drawLine(xPos, 0, xPos, getHeight());
        }
        for (int y = 1; y < displayHeight; y++) {
            int yPos = y * (getHeight() / displayHeight);
            g.drawLine(0, yPos, getWidth(), yPos);
        }

        if (board != null) {
            // draw objects inside the grid lines
            for (int x = 0; x < displayWidth; x++) {
                for (int y = 0; y < displayHeight; y++) {
                    int piece = board.getPiece(x + leftBorder, y + topBorder);
                    if (piece == Board.BLACKPLAYER) {
                        drawBlackPiece(x, y, g);
                    } else if (piece == Board.WHITEPLAYER) {
                        drawWhitePiece(x, y, g);
                    }
                }
            }
        }

        renderBorder(g);

        // print position-on-board information
        g.popTransform();
        g.setColor(oldColor);
    }

    /**
     * Draw a black piece on the provided position on the board
     *
     * @see #drawPiece(Image, int, int, Graphics)
     */
    private void drawBlackPiece(int x, int y, Graphics g) {
        drawPiece(black, x, y, g);
    }

    /**
     * Draw a white piece on the provided position on the board
     *
     * @see #drawPiece(Image, int, int, Graphics)
     */
    private void drawWhitePiece(int x, int y, Graphics g) {
        drawPiece(white, x, y, g);
    }

    /**
     * Draw a piece to a location on the board
     *
     * @param image
     *            The piece image to be drawn
     * @param x
     *            The x location on the board
     * @param y
     *            The y location on the board
     * @param g
     *            The graphics context to draw on
     */
    private void drawPiece(Image image, int x, int y, Graphics g) {
        g.setColor(Color.white);
        // get the relative x position
        int xPos = (x + 1) * (getWidth() / displayWidth)
                - (getWidth() / (2 * displayWidth));
        // get the relative y position
        int yPos = (y + 1) * (getHeight() / displayHeight)
                - (getHeight() / (2 * displayHeight));

        pieceRect.setCenterX(xPos);
        pieceRect.setCenterY(yPos);
        image.setImageColor(0, 0, 1);
        g.texture(pieceRect, image, true);
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
            if (mouseX < getX() + ((x + 1) * getWidth() / displayWidth))
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
            if (mouseY < getY() + ((y + 1) * getHeight() / displayHeight))
                return y + topBorder;
        }
        return displayHeight + topBorder;
    }

    @Override
    public int getX() {
        return (int) area.getX();
    }

    @Override
    public int getY() {
        return (int) area.getY();
    }

    @Override
    public void setLocation(int x, int y) {
        if (area != null)
            area.setLocation(x, y);
    }

    @Override
    public int getWidth() {
        return (int) area.getWidth();
    }

    @Override
    public int getHeight() {
        return (int) area.getHeight();
    }

    public boolean getChangeable() {
        return changeable;
    }

    public void setChangeable(boolean changeable) {
        this.changeable = changeable;
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
     * Set the size of the board to display
     *
     * @param width
     *            The width of the board to display
     * @param height
     *            The height of the board to display
     */
    public void setDisplaySize(int width, int height, int squareSize) {
        if (squareSize <= 5) {
            return;
        }
        if (width > 0 && height > 0) {
            displayWidth = width;
            displayHeight = height;
        } else if (board != null) {
            displayWidth = board.getWidth();
            displayHeight = board.getHeight();
        } else {
            // board was null and bad width/height was given, I should really
            // slap the caller
            return;
        }
        area.setWidth(squareSize * displayWidth);
        area.setHeight(squareSize * displayHeight);
        bgRect.setSize(squareSize, squareSize);
    }
}
