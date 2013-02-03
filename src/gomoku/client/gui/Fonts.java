package gomoku.client.gui;

import java.awt.Color;
import java.util.HashMap;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public abstract class Fonts {

    static private HashMap<String, HashMap<Integer, UnicodeFont>> fonts = new HashMap<String, HashMap<Integer, UnicodeFont>>();

    static private String defaultResource = "res/fonts/Monda-Regular.ttf";
    static private Color defaultColor = Color.white;
    static private int defaultSize = 18;

    private Fonts() {
    }

    public static UnicodeFont getDefaultFont() {
        return getFont(defaultResource, defaultSize, defaultColor);
    }

    public static UnicodeFont getDefaultFont(int size) {
        return getFont(defaultResource, size, defaultColor);
    }

    public static UnicodeFont getDefaultFont(int size, Color color) {
        return getFont(defaultResource, size, color);
    }

    public static UnicodeFont getDefaultFont(Color color) {
        return getFont(defaultResource, defaultSize, color);
    }

    public static UnicodeFont getFont(String resource) {
        return getFont(resource, defaultSize, defaultColor);
    }

    public static UnicodeFont getFont(String resource, int size) {
        return getFont(resource, size, defaultColor);
    }

    @SuppressWarnings("unchecked")
    public static UnicodeFont getFont(String resource, int size, Color color) {
        UnicodeFont font = null;
        HashMap<Integer, UnicodeFont> step = fonts.get(resource);
        if (step != null)
            font = step.get(size);
        if (font == null)
            font = createFont(resource, size);

        if (color == null)
            color = defaultColor;
        font.getEffects().add(new ColorEffect(color));
        return font;
    }

    @SuppressWarnings("unchecked")
    private static UnicodeFont createFont(String resource, int size) {
        UnicodeFont ucf = null;
        try {
            ucf = new UnicodeFont(resource, size, false, false);
            ucf.addAsciiGlyphs();
            ucf.getEffects().add(new ColorEffect());
            ucf.loadGlyphs();

            // Add font to global list
            HashMap<Integer, UnicodeFont> step = new HashMap<Integer, UnicodeFont>(
                    1);
            step.put(size, ucf);
            fonts.put(resource, step);

        } catch (SlickException e) {
            e.printStackTrace();
        }
        return ucf;
    }
}
