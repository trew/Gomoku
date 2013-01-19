package gomoku.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public class CheckBox extends AbstractComponent {

    private Rectangle area;
    private Image checkBoxImage;
    private Image activeImage;
    private Image hoveredImage;
    private Image checkedImage;

    private Circle mark;

    private boolean enabled;
    private boolean checked;
    private boolean pressed;
    private boolean active;
    private boolean hovered;

    public CheckBox(GUIContext container, int x, int y, int width, int height) {
        this(container, x, y, width, height, null, null, null, null);
    }

    public CheckBox(GUIContext container, int x, int y, int width, int height,
            Image checkbox, Image checkedMark) {
        this(container, x, y, width, height, checkbox, checkedMark, null, null);
    }

    public CheckBox(GUIContext container, int x, int y, int width, int height,
            Image checkbox, Image checkedMark, Image hovered, Image active) {
        super(container);
        enabled = true;
        area = new Rectangle(x, y, width, height);
        mark = new Circle(x + width / 2 + 1, y + height / 2 + 1, width / 2 - 2);
        checkBoxImage = checkbox;
        activeImage = active;
        hoveredImage = hovered;
        checkedImage = checkedMark;
    }

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        Color oldColor = g.getColor();

        if (checkBoxImage == null || checkedImage == null) {
            if (hovered)
                g.setColor(Color.lightGray);

            g.drawRect(getX(), getY(), getWidth(), getHeight());
            if (active) {
                g.setColor(Color.lightGray);
                g.fill(mark);

            } else if (checked) {
                g.setColor(Color.white);
                g.fill(mark);
            }
        } else {
            if (active && activeImage != null)
                g.texture(area, activeImage, true);
            else if (hovered && hoveredImage != null)
                g.texture(area, hoveredImage, true);
            else
                g.texture(area, checkBoxImage, true);

            if (checked)
                g.texture(area, checkedImage, true);

        }
        g.setColor(oldColor);
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public void mousePressed(int button, int x, int y) {
        if (enabled && area.contains(x, y)) {
            pressed = true;
            active = true;
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (enabled && pressed) {
            if (area.contains(x, y)) {
                checked = !checked;
            }
            active = false;
            pressed = false;
        }
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        if (enabled && pressed) {
            active = area.contains(newx, newy);
        }
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        hovered = area.contains(newx, newy) && enabled;
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
