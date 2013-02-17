package gomoku.client.gui;

import gomoku.logic.Board;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.ParameterMap;
import de.matthiasmann.twl.ThemeInfo;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.Image;
import de.matthiasmann.twl.renderer.AnimationState.StateKey;

/**
 * A slot on the {@link BoardWidget}. Functionality is pretty much equal to
 * {@link Button}. When clicked, it will fire callbacks back to its owner
 * BoardWidget.
 *
 * @author Samuel Andersson
 */
public class BoardSlot extends Widget {

    private static final StateKey STATE_HOVER = StateKey.get("hover");
    private static final StateKey STATE_ARMED = StateKey.get("active");

    private static final int STATE_MASK_HOVER = 1;
    private static final int STATE_MASK_PRESSED = 2;
    private static final int STATE_MASK_ARMED = 4;

    private int state;

    private String pieceName;
    private Image piece;
    private ParameterMap pieces;
    private BoardWidget owner;
    private int x, y;

    /**
     * Create a new boardslot that has a given position which it will send back
     * to the owner on callbacks.
     *
     * @param owner
     *            the owner which to send callbacks to
     */
    public BoardSlot(BoardWidget owner, int x, int y) {
        this.owner = owner;
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the color of the piece on this slot
     *
     * @see Board#BLACKPLAYER
     * @see Board#WHITEPLAYER
     */
    public void setPiece(int color) {
        if (color == Board.BLACKPLAYER)
            this.pieceName = "black";
        else if (color == Board.WHITEPLAYER)
            this.pieceName = "white";
        else
            throw new IllegalArgumentException("color must be black or white: "
                    + color);
        findImage();
    }

    /**
     * Finds the image for the piece of the slot. Can set the piece to null.
     */
    private void findImage() {
        if (pieceName == null || pieces == null) {
            piece = null;
        } else {
            piece = pieces.getImage(pieceName);
        }
    }

    /**
     * Workaround function that is called in
     * {@link BoardWidget#setChildrenNotHovered()}
     */
    void setNotHovered() {
        getAnimationState().setAnimationState(STATE_HOVER, false);
    }

    /**
     * Helper function that sets a specific state bit
     */
    protected void setStateBit(int mask, boolean set) {
        if (set) {
            state |= mask;
        } else {
            state &= ~mask;
        }
    }

    public boolean isHover() {
        return (state & STATE_MASK_HOVER) != 0;
    }

    public boolean isPressed() {
        return (state & STATE_MASK_PRESSED) != 0;
    }

    public boolean isArmed() {
        return (state & STATE_MASK_ARMED) != 0;
    }

    /**
     * Will fire callbacks if the pressed state is released and the button was
     * armed.
     */
    public void setPressed(boolean pressed) {
        if (pressed != isPressed()) {
            boolean fireAction = !pressed && isArmed();
            setStateBit(STATE_MASK_PRESSED, pressed);
            if (fireAction) {
                owner.fireCallbacks(x, y);
            }
        }
    }

    public void setArmed(boolean armed) {
        if (armed != isArmed()) {
            setStateBit(STATE_MASK_ARMED, armed);
            getAnimationState().setAnimationState(STATE_ARMED, armed);
        }
    }

    public void setHover(boolean hover) {
        if (hover != isHover()) {
            setStateBit(STATE_MASK_HOVER, hover);
            getAnimationState().setAnimationState(STATE_HOVER, hover);
        }
    }

    @Override
    protected boolean handleEvent(Event evt) {
        if (evt.isMouseEventNoWheel()) {
            boolean hover = (evt.getType() != Event.Type.MOUSE_EXITED)
                    && isMouseInside(evt);
            setHover(hover);
            setArmed(hover && isPressed());
        }

        switch (evt.getType()) {
        case MOUSE_BTNDOWN: {
            setPressed(true);
            setArmed(true);
            return true;
        }
        case MOUSE_BTNUP: {
            setPressed(false);
            setArmed(false);
            return true;
        }
        default:
            break;
        }

        return evt.isMouseEventNoWheel();
    }

    @Override
    protected void paintWidget(GUI gui) {
        if (piece != null) {
            piece.draw(getAnimationState(), getInnerX(), getInnerY(),
                    getInnerWidth(), getInnerHeight());
        }
    }

    @Override
    protected void applyTheme(ThemeInfo themeInfo) {
        super.applyTheme(themeInfo);
        pieces = themeInfo.getParameterMap("pieces");
        findImage();
    }
}
