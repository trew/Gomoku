package gomoku.client.gui;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.GUIContext;

public class TextField extends AbstractComponent {
    /** The key repeat interval */
    private static final int INITIAL_KEY_REPEAT_INTERVAL = 400;
    /** The key repeat interval */
    private static final int KEY_REPEAT_INTERVAL = 50;

    private Rectangle area;
    private Rectangle textArea;

    private Font font;
    private Color fontColor;

    private Image left;
    private Image mid;
    private Image right;

    private Input input;

    private String value;
    private int maxLength = 500;
    private boolean focus;
    private boolean enabled;

    private long repeatTimer;

    private int cursorPos;

    private int lastKey = -1;
    private char lastChar = 0;

    // for copy-paste
    private String oldText;
    private int oldCursorPos;

    public TextField(GUIContext container, Image background, Font font, int x,
            int y, int width) {
        this.font = font;
        input = container.getInput();

        int imgWidth = background.getWidth();
        left = background.getSubImage(0, 0, (imgWidth + 1) / 2,
                background.getHeight());
        right = background.getSubImage((imgWidth - 1) / 2, 0,
                (imgWidth + 1) / 2, background.getHeight());
        mid = background.getSubImage((imgWidth - 1) / 2, 0, 1,
                background.getHeight());

        int textAreaWidth = width - (left.getWidth() + right.getWidth());
        if (textAreaWidth < 1) {
            textAreaWidth = 1;
            width = left.getWidth() + right.getWidth() + 1;
        }

        textArea = new Rectangle(x + left.getWidth(), y
                + background.getHeight() / 2 - font.getLineHeight() / 2,
                textAreaWidth, font.getLineHeight());

        area = new Rectangle(x, y, width, background.getHeight());

        fontColor = Color.black;
        value = "";
        focus = false;
        enabled = true;
    }

    public String getText() {
        return value;
    }

    public void setText(String text) {
        value = text;
        if (cursorPos > value.length())
            cursorPos = value.length();
    }

    @Override
    public void render(GUIContext container, Graphics g) throws SlickException {
        if (lastKey != -1) {
            if (input.isKeyDown(lastKey)) {
                if (repeatTimer < System.currentTimeMillis()) {
                    repeatTimer = System.currentTimeMillis()
                            + KEY_REPEAT_INTERVAL;
                    keyPressed(lastKey, lastChar);
                }
            } else {
                lastKey = -1;
            }
        }
        // save values
        Color oldColor = g.getColor();
        Font oldFont = g.getFont();

        g.drawImage(left, getX(), getY());
        g.drawImage(right, getX() + textArea.getWidth() + left.getWidth(),
                getY());

        for (int i = 0; i < textArea.getWidth(); i++) {
            g.drawImage(mid, getX() + left.getWidth() + i, getY());
        }

        // limit the clip of the text area
        Rectangle oldClip = g.getWorldClip();
        g.setClip(textArea);

        int cpos = font.getWidth(value.substring(0, cursorPos));

        // modify the cursor position slightly
        int cCharWidth = 0;
        int cposmod = 0;
        if (cursorPos < value.length()) {
            cCharWidth = font.getWidth(value
                    .substring(cursorPos, cursorPos + 1));
        } else {
            cCharWidth = font.getWidth("_");
        }
        cposmod += (cCharWidth - font.getWidth("_")) / 2;
        if (cposmod < 0)
            cposmod = 0;

        int tx = 0;
        if (cpos > textArea.getWidth()) {
            tx = ((int) textArea.getWidth()) - cpos - font.getWidth("_");
        }

        g.translate(tx + 2, 0);
        g.setColor(fontColor);
        g.setFont(font);
        g.drawString(value, textArea.getX(), textArea.getY());

        if (hasFocus())
            g.drawString("_", textArea.getX() + cpos + cposmod + 1,
                    textArea.getY() + 1);

        g.translate(-tx - 2, 0);

        // restore old values
        g.setColor(oldColor);
        g.setFont(oldFont);
        g.clearWorldClip();
        g.setClip(oldClip);

    }

    private boolean hasFocus() {
        return focus;
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

    @Override
    public void setLocation(int x, int y) {
        if (area != null && textArea != null) {
            area.setLocation(x, y);
            textArea.setLocation(x + left.getWidth(),
                    y + left.getHeight() / 2 - font.getLineHeight() / 2);
        }
    }

    public void setCenterX(float x) {
        if (area != null && textArea != null) {
            area.setCenterX(x);
            textArea.setCenterX(x);
        }
    }

    public void setCenterY(float y) {
        if (area != null && textArea != null) {
            area.setCenterY(y);
            textArea.setCenterY(y);
        }
    }

    public float getCenterX() {
        if (area != null)
            return area.getCenterX();
        return 0;
    }

    public float getCenterY() {
        if (area != null)
            return area.getCenterY();
        return 0;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
        focus = false;
    }

    public void setCursorPos(int pos) {
        cursorPos = pos;
        if (cursorPos < 0)
            cursorPos = 0;
        if (cursorPos > value.length())
            cursorPos = value.length();
    }

    /**
     * Do the paste into the field, overrideable for custom behaviour
     *
     * @param text
     *            The text to be pasted in
     */
    protected void doPaste(String text) {
        recordOldPosition();

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\r')
                break;
            keyPressed(-1, text.charAt(i));
        }
    }

    /**
     * Record the old position and content
     */
    protected void recordOldPosition() {
        oldText = getText();
        oldCursorPos = cursorPos;
    }

    /**
     * Do the undo of the paste, overrideable for custom behaviour
     *
     * @param oldCursorPos
     *            before the paste
     * @param oldText
     *            The text before the last paste
     */
    protected void doUndo(int oldCursorPos, String oldText) {
        if (oldText != null) {
            setText(oldText);
            setCursorPos(oldCursorPos);
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        if (hasFocus() && enabled) {
            if (key != -1) {
                if (key == Input.KEY_V) {
                    if (input.isKeyPressed(Input.KEY_LCONTROL)
                            || input.isKeyPressed(Input.KEY_RCONTROL)) {
                        String text = Sys.getClipboard();
                        if (text != null)
                            doPaste(text);
                        return;
                    }
                }
                if (key == Input.KEY_Z) {
                    if (input.isKeyPressed(Input.KEY_LCONTROL)
                            || input.isKeyPressed(Input.KEY_RCONTROL)) {
                        if (oldText != null)
                            doUndo(oldCursorPos, oldText);
                        return;
                    }
                }

                // control keys don't come through here
                if (input.isKeyDown(Input.KEY_LCONTROL)
                        || input.isKeyDown(Input.KEY_RCONTROL)) {
                    return;
                }
            }

            if (lastKey != key) {
                lastKey = key;
                repeatTimer = System.currentTimeMillis()
                        + INITIAL_KEY_REPEAT_INTERVAL;
            } else {
                repeatTimer = System.currentTimeMillis() + KEY_REPEAT_INTERVAL;
            }
            lastChar = c;

            if (key == Input.KEY_LEFT) {
                if (cursorPos > 0) {
                    cursorPos--;
                }
            } else if (key == Input.KEY_RIGHT) {
                if (cursorPos < value.length()) {
                    cursorPos++;
                }
            } else if (key == Input.KEY_BACK) {
                if ((cursorPos > 0) && (value.length() > 0)) {
                    if (cursorPos < value.length()) {
                        value = value.substring(0, cursorPos - 1)
                                + value.substring(cursorPos);
                    } else {
                        value = value.substring(0, cursorPos - 1);
                    }
                    cursorPos--;
                }
            } else if (key == Input.KEY_DELETE) {
                if (value.length() > cursorPos) {
                    value = value.substring(0, cursorPos)
                            + value.substring(cursorPos + 1);
                }
            } else if (key == Input.KEY_RETURN) {
            } else if (c != Keyboard.CHAR_NONE && key != Input.KEY_TAB
                    && value.length() < maxLength) {
                if (cursorPos < value.length()) {
                    value = value.substring(0, cursorPos) + c
                            + value.substring(cursorPos);
                } else {
                    value = value.substring(0, cursorPos) + c;
                }
                cursorPos++;
            }

        }
    }

    @Override
    public void mousePressed(int button, int x, int y) {
        focus = button == 0 && area.contains(x, y) && enabled;
        lastKey = -1;
    }

}
