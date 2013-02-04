package gomoku.client.gui;

import java.util.HashMap;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

public abstract class Fonts {

    static private HashMap<Integer, Font> fonts = new HashMap<Integer, Font>();

    static private HashMap<String, Font> angelCodeFonts = new HashMap<String, Font>();
    static private String defaultResource = "res/fonts/Monda-Regular.ttf";
    static private int defaultSize = 18;

    private Fonts() {
    }

    public static Font getDefaultFont() {
        return getFont(defaultResource, defaultSize);
    }

    public static Font getDefaultFont(int size) {
        return getFont(defaultResource, size);
    }

    public static Font getFont(String resource) {
        return getFont(resource, defaultSize);
    }

    public static Font getFont(String resource, int size) {
        Font font = null;
        font = fonts.get(size);
        if (font == null)
            font = createFont(resource, size);
        return font;
    }

    @SuppressWarnings("unchecked")
    private static Font createFont(String resource, int size) {
        UnicodeFont ucf = null;
        try {
            ucf = new UnicodeFont(resource, size, false, false);
            ucf.addAsciiGlyphs();
            ucf.getEffects().add(new ColorEffect());
            ucf.loadGlyphs();

            // Add font to global list
            //HashMap<Integer, UnicodeFont> step = new HashMap<Integer, UnicodeFont>();
            fonts.put(size, ucf);
            //fonts.put(resource, step);

        } catch (SlickException e) {
            e.printStackTrace();
        }
        return ucf;
    }

    public static Font getAngelCodeFont(String res) {
        Font font = angelCodeFonts.get(res);
        if (font == null)
            font = createAngelCodeFont(res);
        return font;
    }

    private static Font createAngelCodeFont(String res) {
        try {
            AngelCodeFont font = new AngelCodeFont(res.concat(".fnt"), res.concat("_00.png"));
            angelCodeFonts.put(res, font);
            return font;
        } catch (SlickException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void loadAngelCodeFonts(String...ress) {
        for (String res : ress) {
            getAngelCodeFont(res);
        }
    }

    public static void loadFonts(int... sizes) {
        for (int size : sizes ) {
            getFont(defaultResource, size);
        }
    }
}
