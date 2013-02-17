package gomoku.client.gui;

import gomoku.logic.Board;
import gomoku.logic.IllegalActionException;

import java.util.ArrayList;

import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;

/**
 * A widget that displays a gomoku board. Contains {@link BoardSlot}s which will
 * fire {@link Callback}s upon getting clicked.
 *
 * @author Samuel Andersson
 */
public class BoardWidget extends Widget {

    /**
     * Basic callback that's called when a {@link BoardSlot} on the board has
     * been clicked.
     */
    static public abstract class Callback {
        public abstract void callback(int x, int y);
    }

    private int numSlotsX;
    private int numSlotsY;
    private ArrayList<BoardSlot> slots;
    private int slotLength;

    private int slotSpacing;

    private Board board;

    private ArrayList<Callback> callbacks;

    /**
     * Calls BoardWidget(null)
     *
     * @see #BoardWidget(Board)
     */
    public BoardWidget() {
        this(null);
    }

    /**
     * Creates a new BoardWidget from a given {@link Board}. The size of the
     * widget will be determined based on the size of the given Board.
     *
     * @param board
     *            the board
     */
    public BoardWidget(Board board) {
        if (board != null)
            setBoard(board);
        else {
            slots = new ArrayList<BoardSlot>();
        }
        callbacks = new ArrayList<Callback>();
    }

    /**
     * Adds a callback for when BoardSlots is clicked.
     */
    public void addCallback(Callback cb) {
        if (cb == null)
            throw new NullPointerException("callback");
        callbacks.add(cb);
    }

    /**
     * @see #addCallback(Callback)
     */
    public void removeCallback(Callback cb) {
        callbacks.remove(cb);
    }

    protected void fireCallbacks(int x, int y) {
        for (Callback cb : callbacks) {
            cb.callback(x, y);
        }
    }

    /**
     * Sets the board for this widget. Calling this removes all current
     * {@link BoardSlot}s and replaces them with new slots. Previously added
     * callbacks are not affected.
     */
    public void setBoard(Board board) {
        if (board == null)
            throw new NullPointerException("board");

        this.board = board;
        this.numSlotsX = board.getWidth();
        this.numSlotsY = board.getHeight();
        this.slotLength = numSlotsX * numSlotsY;
        this.slots = new ArrayList<BoardSlot>(slotLength);

        for (int y = 0, i = 0; y < numSlotsY; y++) {
            for (int x = 0; x < numSlotsX; x++, i++) {
                slots.add(new BoardSlot(this, x, y));
                add(slots.get(i));
            }
        }
    }

    /**
     * Places a piece on the board that this widget contains.
     *
     * @see {@link Board#placePiece(int, int, int)}
     */
    public void setPiece(int color, int x, int y) throws IllegalActionException {
        if (board == null)
            throw new NullPointerException("board");
        board.placePiece(color, x, y);

        slots.get(y * numSlotsX + x).setPiece(color);
    }

    /**
     * Workaround function that is called when the mouse exits the ScrollPane
     * that contains this widget. Otherwise this widget does not receive
     * {@link Event.Type#MOUSE_EXITED} event. This is also why I created
     * {@link ScrollingPane}.
     */
    void setChildrenNotHovered() {
        for (BoardSlot s : slots) {
            s.setNotHovered();
        }
    }

    @Override
    public int getPreferredWidth() {
        return getBorderLeft() + getBorderRight() + getPreferredInnerWidth();
    }

    @Override
    public int getPreferredHeight() {
        return getBorderBottom() + getBorderTop() + getPreferredInnerHeight();
    }

    @Override
    public int getPreferredInnerWidth() {
        if (!slots.isEmpty())
            return (slots.get(0).getPreferredWidth() + slotSpacing) * numSlotsX
                    + slotSpacing;
        return 0;
    }

    @Override
    public int getPreferredInnerHeight() {
        if (!slots.isEmpty())
            return (slots.get(0).getPreferredHeight() + slotSpacing)
                    * numSlotsY + slotSpacing;
        return 0;
    }

    @Override
    public void applyTheme(ThemeInfo themeInfo) {
        super.applyTheme(themeInfo);
        slotSpacing = themeInfo.getParameter("slotSpacing", 0);
    }

    @Override
    protected void layout() {
        if (slots.isEmpty())
            return;

        int slotWidth = slots.get(0).getPreferredWidth();
        int slotHeight = slots.get(0).getPreferredHeight();

        for (int row = 0, y = getInnerY() + slotSpacing, i = 0; row < numSlotsY; row++) {
            for (int col = 0, x = getInnerX() + slotSpacing; col < numSlotsX; col++, i++) {
                slots.get(i).adjustSize();
                slots.get(i).setPosition(x, y);
                x += slotWidth + slotSpacing;
            }
            y += slotHeight + slotSpacing;
        }
    }
}