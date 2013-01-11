package gomoku.client.gui;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

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

        int y = getY() + 3;
        int textHeight = 0;
        String text;
        g.setColor(Color.black);
        if (games.isEmpty()) {
            g.drawString("No games available", getX() + 3, y);
        } else {
            for (Entry<Integer, String> entry : games.entrySet()) {
                text = entry.getValue() + " - GameID: " + entry.getKey();
                textHeight = g.getFont().getHeight(text);
                g.drawString(text, getX(), y);
                y += textHeight;
            }
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

    /**
     * Returns true if the ID exists in the gamelist
     *
     * @param id
     *            the id to be checked
     * @return true if the ID exists in the gamelist
     */
    public boolean validID(int id) {
        return games.containsKey(id);
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
