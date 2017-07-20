package se.samuelandersson.gomoku;

public class GomokuConfig
{
  private String name;

  private int boardWidth;
  private int boardHeight;

  private int victoryLength;

  private boolean allowOverlines;

  private boolean threeAndThree;

  private boolean fourAndFour;

  public GomokuConfig()
  {
    name = "Gomoku";
    boardWidth = 15;
    boardHeight = 15;
    victoryLength = 5;
    allowOverlines = false;
    threeAndThree = false;
    fourAndFour = false;
  }

  public GomokuConfig(String name, int width, int height, int victoryLength, boolean allowOverLines,
      boolean threeAndThree, boolean fourAndFour)
  {
    this.name = name;
    this.boardWidth = width;
    this.boardHeight = height;
    this.victoryLength = victoryLength;
    this.allowOverlines = allowOverLines;
    this.threeAndThree = threeAndThree;
    this.fourAndFour = fourAndFour;
  }

  public int getVictoryLength()
  {
    return victoryLength;
  }

  public boolean getAllowOverlines()
  {
    return allowOverlines;
  }

  public int getHeight()
  {
    return boardHeight;
  }

  public int getWidth()
  {
    return boardWidth;
  }

  public boolean useThreeAndThree()
  {
    return threeAndThree;
  }

  public boolean useFourAndFour()
  {
    return fourAndFour;
  }

  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return String.format("GomokuConfig<%s,%sx%s%s%s%s>",
                         name,
                         boardWidth,
                         boardHeight,
                         this.allowOverlines ? ", Allow overlines" : "",
                         this.threeAndThree ? ",3&3" : "",
                         this.fourAndFour ? ",4&4" : "");
  }
}
