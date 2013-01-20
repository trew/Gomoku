package gomoku.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.trew.log.Log.*;

public class GomokuConfig {

    private String name;

    private int boardWidth;
    private int boardHeight;

    private int victoryLength;

    private boolean allowOverlines;

    private boolean threeAndThree;

    private boolean fourAndFour;

    private boolean swap2;

    public GomokuConfig() {
        name = "Gomoku";
        boardWidth = 15;
        boardHeight = 15;
        victoryLength = 5;
        allowOverlines = false;
        threeAndThree = false;
        fourAndFour = false;
        swap2 = false;
    }

    public GomokuConfig(String name, int width, int height, int victoryLength,
            boolean allowOverLines, boolean threeAndThree, boolean fourAndFour,
            boolean swap2) {
        this.name = name;
        this.boardWidth = width;
        this.boardHeight = height;
        this.victoryLength = victoryLength;
        this.allowOverlines = allowOverLines;
        this.threeAndThree = threeAndThree;
        this.fourAndFour = fourAndFour;
        this.swap2 = swap2;
    }

    static public GomokuConfig GomokuPreset() {
        return new GomokuConfig();
    }

    static public GomokuConfig CaroPreset() {
        return null;
    }

    static public GomokuConfig OmokPreset() {
        return new GomokuConfig("Omok", 15, 15, 5, true, true, false, false);
    }

    public int getVictoryLength() {
        return victoryLength;
    }

    public boolean getAllowOverlines() {
        return allowOverlines;
    }

    public int getHeight() {
        return boardHeight;
    }

    public int getWidth() {
        return boardWidth;
    }

    public boolean useSwap2() {
        return swap2;
    }

    public boolean useThreeAndThree() {
        return threeAndThree;
    }

    public boolean useFourAndFour() {
        return fourAndFour;
    }

    public String getName() {
        return name;
    }

    public void store(String filename) {
        Properties prop = new Properties();
        prop.setProperty("name", getName());
        prop.setProperty("width", String.valueOf(getWidth()));
        prop.setProperty("height", String.valueOf(getHeight()));
        prop.setProperty("victorylength", String.valueOf(getVictoryLength()));
        prop.setProperty("allowoverlines", String.valueOf(getAllowOverlines()));
        prop.setProperty("threeandthree", String.valueOf(useThreeAndThree()));
        prop.setProperty("fourandfour", String.valueOf(useFourAndFour()));
        prop.setProperty("swap2", String.valueOf(useSwap2()));

        FileOutputStream fileos = null;
        try {
            fileos = new FileOutputStream("presets/" + filename);
        } catch (FileNotFoundException e) {
            // create new file
            File file = new File("presets/" + filename);
            try {
                fileos = new FileOutputStream(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }

        if (fileos != null) {
            try {
                prop.store(fileos, "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            error("Could not save configuration file: " + filename);
        }
    }

    public void load(String filename) {
        InputStream is;
        try {
            is = new FileInputStream("presets/" + filename);
            Properties prop = new Properties();
            try {
                prop.load(is);
                name = prop.getProperty("name");
                boardWidth = Integer.valueOf(prop.getProperty("width"));
                boardHeight = Integer.valueOf(prop.getProperty("height"));
                victoryLength = Integer.valueOf(prop.getProperty("victorylength"));
                allowOverlines = Boolean.valueOf(prop.getProperty("allowOverlines"));
                threeAndThree = Boolean.valueOf(prop.getProperty("threeandthree"));
                fourAndFour = Boolean.valueOf(prop.getProperty("fourandfour"));
                swap2 = Boolean.valueOf(prop.getProperty("swap2"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

    }
}
