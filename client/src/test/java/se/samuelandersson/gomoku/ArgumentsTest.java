package se.samuelandersson.gomoku;

import org.testng.annotations.Test;

import se.samuelandersson.gomoku.client.Arguments;

public class ArgumentsTest
{
  @Test
  public void testAssertNotNullWhenNotNull()
  {
    Arguments.assertNotNull("notNullArg", new Object());
  }

  @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "nullArg must not be null.")
  public void testAssertNotNullWhenNull()
  {
    Arguments.assertNotNull("nullArg", null);
  }
}
