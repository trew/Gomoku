package gomoku.client.gui;

import java.awt.Rectangle;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public class CheckBox extends AbstractComponent {

    private Rectangle area;

    public CheckBox(GUIContext container) {
        super(container);
        area = new Rectangle();
    }

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
    }

    @Override
    public void setLocation(int x, int y) {
        if (area != null)
            area.setLocation(x, y);
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
    public int getWidth() {
        return (int) area.getWidth();
    }

    @Override
    public int getHeight() {
        return (int) area.getHeight();
    }

}
