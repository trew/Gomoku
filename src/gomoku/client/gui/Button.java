package gomoku.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.util.InputAdapter;

public class Button extends InputAdapter {

    private Image currentImage;
    private Image normalImage;
    private Image mouseOverImage;
    private Image mouseDownImage;
    private Image disabledImage;

    private Rectangle area;

    private boolean over;

    private boolean mouseDown;

    private Color currentColor;
    private Color normalColor;
    private Color mouseOverColor;
    private Color mouseDownColor;
    private Color disabledColor;

    private String text;
    private Font font;
    private int textWidth;
    private int textHeight;
    private int textHeightPadding;
    private int textYModifier;
    private int textWidthPadding;

    private boolean enabled;
    private boolean visible;

    /**
     * Create a button with automatic width/height depending on the size of the
     * text.
     */
    public Button(String text, Font font, int x, int y) {
        this(text, font, null, x, y);
    }

    /**
     * Create a button using a text string and a background image. The text
     * padding will default to width: 10px and height: 5px.
     */
    public Button(String text, Font font, Image backgroundImage, int x, int y) {
        this.font = font;
        textWidth = font.getWidth(text);
        textYModifier = font.getHeight(text) - font.getLineHeight();
        textHeight = font.getHeight(text) + textYModifier;
        textHeightPadding = 5;
        textWidthPadding = 10;
        constructorHack(text, backgroundImage, null, null, null, new Rectangle(
                x, y, textWidth + textWidthPadding * 2, textHeight
                        + textHeightPadding * 2));
    }

    public Button(Image button, int x, int y) {
        this(button, x, y, 4);
    }
    public Button(Image button, int x, int y, int images) {
        int imageHeight = button.getHeight() / images;
        Image btn = button.getSubImage(0, 0, button.getWidth(), imageHeight);
        Image hover = button.getSubImage(0, imageHeight, button.getWidth(), imageHeight);
        Image click = button.getSubImage(0, imageHeight*2, button.getWidth(), imageHeight);
        Image disabled = button.getSubImage(0, imageHeight*3, button.getWidth(), imageHeight);
        constructorHack(null, btn, hover, click, disabled, new Rectangle(x, y, button.getWidth(), imageHeight));
    }

    public Button(Image button, Image buttonHover, Image buttonClick,
            Image buttonDisabled, int x, int y) {
        if (button == null) {
            throw new IllegalArgumentException("Button image cannot be null.");
        }
        constructorHack(null, button, buttonHover, buttonClick, buttonDisabled,
                new Rectangle(x, y, button.getWidth(), button.getHeight()));
    }

    /**
     * The final destination for all constructors
     * http://stackoverflow.com/questions/1168345/why-does-this-and-super-have -
     * to-be-the-first-statement-in-a-constructor
     *
     * @param text
     *            the text to be displayed on the button
     * @param image
     *            the image background
     * @param area
     *            the area which the button occupies
     */
    private void constructorHack(String text, Image button, Image hover,
            Image click, Image disabled, Rectangle area) {
        this.area = area;
        this.text = text;

        enabled = true;
        visible = true;

        normalImage = button;
        mouseOverImage = hover;
        mouseDownImage = click;
        disabledImage = disabled;
        currentImage = normalImage;

        normalColor = Color.white;
        mouseOverColor = Color.yellow;
        mouseDownColor = Color.red;
        disabledColor = Color.lightGray;
        currentColor = normalColor;

        over = false;
    }

    public void render(GUIContext container, Graphics g) throws SlickException {
        if (!isVisible()) return;

        Color old = g.getColor();
        if (currentImage != null) {
            g.texture(area, currentImage, true);
        } else {
            g.setColor(currentColor);
            g.fill(area);
        }
        if (text != null) {
            font.drawString(area.getX() + textWidthPadding, area.getY()
                    + textHeightPadding + textYModifier, text);
        }
        updateImage();
        g.setColor(old);
    }

    private void updateImage() {
        if (!over) {
            if (!enabled) {
                currentImage = disabledImage;
            } else {
                currentImage = normalImage;
                currentColor = normalColor;
            }
        } else {
            if (!enabled) {
                currentColor = disabledColor;
            } else if (mouseDown) {
                currentImage = mouseDownImage;
                currentColor = mouseDownColor;
            } else {
                currentImage = mouseOverImage;
                currentColor = mouseOverColor;
            }
        }
    }

    public void disable() {
        enabled = false;
        currentImage = disabledImage;
    }

    public void enable() {
        enabled = true;
        if (currentImage == disabledImage) {
            updateImage();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isEnabled() {
        return enabled && visible;
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (isEnabled() && area.contains(x, y)) {
            mouseDown = true;
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (isEnabled() && mouseDown && area.contains(x, y)) {
            buttonClicked(button, x, y);
        }
        mouseDown = false;
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        over = area.contains(newx, newy);
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        over = area.contains(newx, newy);
    }

    public void buttonClicked(int button, int x, int y) {
    }

    public void setLocation(int x, int y) {
        if (area != null) {
            area.setLocation(x, y);
        }
    }

    public void setCenterX(int x) {
        area.setCenterX(x);
    }

    public void setCenterY(int y) {
        area.setCenterY(y);
    }

    public void setRightX(int x) {
        area.setX(x - getWidth());
    }

    public void setBottomY(int y) {
        area.setY(y - getHeight());
    }

    public int getX() {
        return (int) area.getX();
    }

    public int getY() {
        return (int) area.getY();
    }

    public int getWidth() {
        return (int) area.getWidth();
    }

    public int getHeight() {
        return (int) area.getHeight();
    }
}
