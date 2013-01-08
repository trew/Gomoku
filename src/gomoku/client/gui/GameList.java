package gomoku.client.gui;

import java.util.LinkedHashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public class GameList extends AbstractComponent {

    private Rectangle area;

    private LinkedHashMap<Integer, String> games;

    private int selectedID;

    public GameList(GUIContext container, int x, int y, int w, int h) {
        super(container);
        area = new Rectangle(x, y, w, h);
        games = new LinkedHashMap<Integer, String>();
        selectedID = -1;
    }

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        Color old = g.getColor();

        g.setColor(Color.white);
        g.fill(area);

        int y = getY();
        g.setColor(Color.black);
        for (String gameName : games.values()) {
            g.drawString(gameName, getX(), y);
            y += 20;
        }

        g.setColor(old);
    }

    public void clear() {
        games.clear();
    }

    public void add(String gameName, int gameID) {
        games.put(gameID, gameName);
    }

    public void remove(int gameID) {
        games.remove(gameID);
    }

    public int getSelectedID() {
        return selectedID;
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
