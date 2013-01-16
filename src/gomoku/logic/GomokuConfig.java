package gomoku.logic;

public class GomokuConfig {

    private int boardWidth;
    private int boardHeight;

    private int victoryLength;

    private boolean allowOverlines;

    private boolean threeAndThree;

    private boolean fourAndFour;

    private boolean swap2;

    public GomokuConfig() {
        boardWidth = 15;
        boardHeight = 15;
        victoryLength = 5;
        allowOverlines = false;
        threeAndThree = false;
        fourAndFour = false;
        swap2 = false;
    }

    public GomokuConfig(int width, int height, int victoryLength,
            boolean allowOverLines, boolean threeAndThree, boolean fourAndFour,
            boolean swap2) {
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
        return new GomokuConfig(15, 15, 5, true, true, false, false);
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
}
