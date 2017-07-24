package se.samuelandersson.gomoku.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class HeadlessServer
{
  private static final Logger log = LoggerFactory.getLogger(HeadlessServer.class);

  /**
   * The port which this server is listening on. Can be set by providing
   * --port to the application command line
   *
   * @see #parseArgs(String[])
   */
  private static int PORT;

  /**
   * Parse command line arguments that was passed to the application upon
   * startup.
   *
   * @param args
   *          The arguments passed to the application
   */
  public static void parseArgs(String[] args)
  {
    JSAP jsap = new JSAP();
    FlaggedOption portOpt = new FlaggedOption("port").setStringParser(JSAP.INTEGER_PARSER)
                                                     .setDefault("9123")
                                                     .setLongFlag("port");

    try
    {
      jsap.registerParameter(portOpt);

      JSAPResult config = jsap.parse(args);
      PORT = config.getInt("port");
    }
    catch (JSAPException e)
    {
      if (log.isTraceEnabled())
      {
        log.trace("Error", e);
      }
      else
      {
        log.error("Error parsing arguments: " + e.getMessage());
      }

      System.exit(-1);
    }
  }

  /**
   * The main entry point of the server
   *
   * @param args
   *          Any arguments passed to the server
   */
  public static void main(String[] args)
  {
    parseArgs(args);

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        log.info("Shutting down...");
      }
    }));

    final GomokuServer gomokuserver = new GomokuServer(PORT, false);
    try
    {
      gomokuserver.start();
    }
    catch (IOException e)
    {
      log.error("Error starting server on *:" + PORT, e);
    }
  }
}
