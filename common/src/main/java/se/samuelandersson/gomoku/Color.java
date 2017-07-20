package se.samuelandersson.gomoku;

public enum Color
{
  NONE(0, "None"), BLACK(1, "Black"), WHITE(2, "White");

  private int id;
  private String name;

  private Color(final int id, final String name)
  {
    this.id = id;
    this.name = name;
  }

  public int getId()
  {
    return this.id;
  }

  public boolean isValidPlayerColor()
  {
    return this != NONE;
  }

  public String getName()
  {
    return this.name;
  }

  @Override
  public String toString()
  {
    return this.getName();
  }

  public static Color valueOf(final int value)
  {
    switch (value)
    {
      case 1:
        return Color.BLACK;
      case 2:
        return Color.WHITE;
      default:
        return Color.NONE;
    }
  }
}