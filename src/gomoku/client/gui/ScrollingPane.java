package gomoku.client.gui;

import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.Event.Type;
import de.matthiasmann.twl.ScrollPane;
import de.matthiasmann.twl.Widget;

/**
 * Workaround ScrollPane that sends calls
 * {@link BoardWidget#setChildrenNotHovered()} upon receiving the event
 * {@link Event.Type#MOUSE_EXITED}. Reason is that otherwise BoardSlots will
 * never receive that event when the mouse exits the scrollpane, and they will
 * stay in the hover-state.
 *
 * @author Samuel Andersson
 */
public class ScrollingPane extends ScrollPane {

    BoardWidget bw;

    public ScrollingPane(Widget widget, BoardWidget bw) {
        super(widget);
        this.bw = bw;
    }

    @Override
    protected boolean handleEvent(Event evt) {
        if (super.handleEvent(evt))
            return true;

        if (evt.isMouseEventNoWheel())
            if (evt.getType() == Type.MOUSE_ENTERED)
                return true; // must return true to receive more mouse events
            else if (evt.getType() == Type.MOUSE_EXITED)
                bw.setChildrenNotHovered();

        return false;
    }
}
