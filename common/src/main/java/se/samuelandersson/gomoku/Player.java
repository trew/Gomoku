package se.samuelandersson.gomoku;

/**
 * Represents a player in a Gomoku game
 *
 * @author Samuel Andersson
 */
public class Player
{
  public static final int NOPLAYER = 0;
  public static final int PLAYERONE = 1;
  public static final int PLAYERTWO = 2;

  /**
   * 0 = Spectator, or no player 1 = Player one 2 = Player two
   */
  private int playerID;

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
   *
   * @param name
   *          The player name
   * @param id
   *          The player ID. {@link Player#NOPLAYER},
   *          {@link Player#PLAYERONE} or {@link Player#PLAYERTWO}
   */
  public Player(String name, int id)
  {
    this.name = name;
    setID(id);
  }

  public int getID()
  {
    return playerID;
  }

  public void setID(int id)
  {
    if (id == 0 || id == 1 || id == 2)
    {
      playerID = id;
    }
    else
    {
      throw new IllegalArgumentException("PlayerID set to unknown value " + id);
    }
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
    if (!color.isValidPlayerColor())
    {
      throw new IllegalArgumentException("Player cannot be " + color);
    }

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
    return String.format("Player<%s(%s):%s>", this.name, this.playerID, this.color);
  }
}
