package gomoku.client.gui;

import java.util.HashMap;

import org.newdawn.slick.AngelCodeFont;
import org.newdawn.slick.Font;
import org.newdawn.slick.SlickException;

public abstract class Fonts {

    static private HashMap<String, Font> angelCodeFonts = new HashMap<String, Font>();

    private Fonts() {
    }

    public static Font getDefaultFont() {
        return getAngelCodeFont("res/fonts/default");
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
}
