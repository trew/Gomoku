package se.samuelandersson.gomoku;

/**
 * Represents a player in a Gomoku game
 *
 * @author Samuel Andersson
 */
public class Player
{
  public void setFrom(Player other)
  {
    this.color = other.color;
    this.name = other.name;
  }

  /**
   * The player color
   */
  private Color color;

  /** The player name */
  private String name;

  /** Empty constructor for Kryonet's pleasure */
  public Player()
  {
  }

  /**
   * Create a new player
   */
  public Player(String name, Color color)
  {
    this.name = name;
    this.color = color;
  }

  /**
   * Returns the player color
   *
   * @return the player color
   */
  public Color getColor()
  {
    return color;
  }

  /**
   * Sets the player color
   *
   * @param color the player color
   * @throws IllegalArgumentException if the color is not a valid player color. See {@link Color#isValidPlayerColor()}.
   */
  public void setColor(Color color) throws IllegalArgumentException
  {
    this.color = color;
  }

  /**
   * Get the player name
   *
   * @return The player name
   */
  public String getName()
  {
    return name;
  }

  /**
   * Set the player name
   *
   * @param name
   *          The new player name
   * @return The old name
   */
  public String setName(String name)
  {
    String old = this.name;
    this.name = name;

    return old;
  }

  @Override
  public String toString()
  {
    return String.format("Player<%s(%s)>", this.name, this.color);
  }
}
