package gomoku.client.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

public class Button extends AbstractComponent {

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

    /**
     * Create a button with automatic width/height depending on the size of the
     * text.
     */
    public Button(GUIContext container, String text, int x, int y) {
        this(container, text, null, x, y);
    }

    /**
     * Create a button with automatic width/height depending on the size of the
     * text.
     */
    public Button(GUIContext container, String text, int x, int y,
            int minWidth, int minHeight) {
        this(container, text, null, x, y, minWidth, minHeight);
    }

    /**
     * Create a new button with an image and no text. This implies the image
     * contains text or a description about what the button does.
     */
    public Button(GUIContext container, Image image, int x, int y, int width,
            int height) {
        super(container);
        constructorHack("", image, new Rectangle(x, y, width, height));
    }

    /**
     * Create a button using a text string and a background image. The text
     * padding will default to width: 10px and height: 5px.
     */
    public Button(GUIContext container, String text, Image backgroundImage,
            int x, int y) {
        super(container);
        font = container.getDefaultFont();
        textWidth = font.getWidth(text);
        textYModifier = font.getHeight(text) - font.getLineHeight();
        textHeight = font.getHeight(text) + textYModifier;
        textHeightPadding = 5;
        textWidthPadding = 10;
        constructorHack(text, backgroundImage, new Rectangle(x, y, textWidth
                + textWidthPadding * 2, textHeight + textHeightPadding * 2));
    }

    /**
     * Create a button using a text string and a background image.
     */
    public Button(GUIContext container, String text, Image backgroundImage,
            int x, int y, int minWidth, int minHeight) {
        super(container);
        font = container.getDefaultFont();
        textWidth = font.getWidth(text);
        textYModifier = font.getHeight(text) - font.getLineHeight();
        textHeight = font.getHeight(text) + textYModifier;
        textWidthPadding = minWidth - textWidth;
        textHeightPadding = minHeight - textHeight;
        if (textWidthPadding < 0)
            textWidthPadding = 0;
        if (textHeightPadding < 0)
            textHeightPadding = 0;
        constructorHack(text, backgroundImage, new Rectangle(x, y, textWidth
                + textWidthPadding * 2, textHeight + textHeightPadding * 2));
    }

    /**
     * The final destination for all constructors
     * http://stackoverflow.com/questions/1168345/why-does-this-and-super-have -
     * to-be-the-first-statement-in-a-constructor
     * 
     * @param text
     * @param image
     * @param area
     */
    private void constructorHack(String text, Image image, Rectangle area) {
        this.area = area;
        this.text = text;

        enabled = true;

        normalImage = image;
        mouseOverImage = image;
        mouseDownImage = image;
        disabledImage = image;
        currentImage = normalImage;

        normalColor = Color.white;
        mouseOverColor = Color.yellow;
        mouseDownColor = Color.red;
        disabledColor = Color.lightGray;
        currentColor = normalColor;

        Input input = container.getInput();
        over = area.contains(input.getMouseX(), input.getMouseY());
    }

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        Color old = g.getColor();
        if (currentImage != null) {
            g.texture(area, currentImage, true);
        } else {
            g.setColor(currentColor);
            g.fill(area);
        }
        if (text != "") {
            font.drawString(area.getX() + textWidthPadding, area.getY()
                    + textHeightPadding + textYModifier, text, Color.black);
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
    }

    public void enable() {
        enabled = true;
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        if (enabled && area.contains(x, y)) {
            mouseDown = true;
        }
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
        if (enabled && mouseDown && area.contains(x, y)) {
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

    @Override
    public void setLocation(int x, int y) {
        if (area != null) {
            area.setLocation(x, y);
        }
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
