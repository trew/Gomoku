package se.samuelandersson.gomoku.client;

public class Arguments
{
  public static <T> void assertNotNull(final String name, final T parameter)
  {
    if (parameter == null)
    {
      throw new IllegalArgumentException(name + " must not be null.");
    }
  }
}
