package gomoku.client.gui;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.util.InputAdapter;

public abstract class AbstractComponent extends InputAdapter {

    public AbstractComponent() {
    }

    public abstract void render(GUIContext container, Graphics g) throws SlickException;

    public abstract int getX();

    public abstract int getY();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void setLocation(int x, int y);

}
