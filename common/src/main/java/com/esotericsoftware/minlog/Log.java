
package com.esotericsoftware.minlog;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;

/**
 * Replacement that delegates to SLF4J.
 * 
 * @author Samuel Andersson
 */
public class Log
{
  public static final int LEVEL_NONE = 6;
  public static final int LEVEL_ERROR = 5;
  public static final int LEVEL_WARN = 4;
  public static final int LEVEL_INFO = 3;
  public static final int LEVEL_DEBUG = 2;
  public static final int LEVEL_TRACE = 1;

  public static boolean ERROR = true;
  public static boolean WARN = true;
  public static boolean INFO = true;
  public static boolean DEBUG = true;
  public static boolean TRACE = true;

  private static final Logger logger = new Logger();

  public static void set(int level)
  {
  }

  public static void NONE()
  {
  }

  public static void ERROR()
  {
  }

  public static void WARN()
  {
  }

  public static void INFO()
  {
  }

  public static void DEBUG()
  {
  }

  public static void TRACE()
  {
  }

  /**
   * Sets the logger that will write the log messages.
   */
  public static void setLogger(Logger logger)
  {
  }

  public static void error(String message, Throwable ex)
  {
    logger.log(LEVEL_ERROR, null, message, ex);
  }

  public static void error(String category, String message, Throwable ex)
  {
    logger.log(LEVEL_ERROR, category, message, ex);
  }

  public static void error(String message)
  {
    logger.log(LEVEL_ERROR, null, message, null);
  }

  public static void error(String category, String message)
  {
    logger.log(LEVEL_ERROR, category, message, null);
  }

  public static void warn(String message, Throwable ex)
  {
    logger.log(LEVEL_WARN, null, message, ex);
  }

  public static void warn(String category, String message, Throwable ex)
  {
    logger.log(LEVEL_WARN, category, message, ex);
  }

  public static void warn(String message)
  {
    logger.log(LEVEL_WARN, null, message, null);
  }

  public static void warn(String category, String message)
  {
    logger.log(LEVEL_WARN, category, message, null);
  }

  public static void info(String message, Throwable ex)
  {
    logger.log(LEVEL_INFO, null, message, ex);
  }

  public static void info(String category, String message, Throwable ex)
  {
    logger.log(LEVEL_INFO, category, message, ex);
  }

  public static void info(String message)
  {
    logger.log(LEVEL_INFO, null, message, null);
  }

  public static void info(String category, String message)
  {
    logger.log(LEVEL_INFO, category, message, null);
  }

  public static void debug(String message, Throwable ex)
  {
    logger.log(LEVEL_DEBUG, null, message, ex);
  }

  public static void debug(String category, String message, Throwable ex)
  {
    logger.log(LEVEL_DEBUG, category, message, ex);
  }

  public static void debug(String message)
  {
    logger.log(LEVEL_DEBUG, null, message, null);
  }

  public static void debug(String category, String message)
  {
    logger.log(LEVEL_DEBUG, category, message, null);
  }

  public static void trace(String message, Throwable ex)
  {
    logger.log(LEVEL_TRACE, null, message, ex);
  }

  public static void trace(String category, String message, Throwable ex)
  {
    logger.log(LEVEL_TRACE, category, message, ex);
  }

  public static void trace(String message)
  {
    logger.log(LEVEL_TRACE, null, message, null);
  }

  public static void trace(String category, String message)
  {
    logger.log(LEVEL_TRACE, category, message, null);
  }

  private Log()
  {
  }

  public static class Logger
  {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger("com.esotericsoftware.minlog");
    private static final Map<String, org.slf4j.Logger> loggers = new HashMap<>();

    public void log(int level, String category, String message, Throwable ex)
    {
      switch (level)
      {
        case Log.LEVEL_ERROR:
        {
          if (ex != null)
          {
            getLogger(category).error(message, ex);
          }
          else
          {
            getLogger(category).error(message);
          }

          break;
        }
        case Log.LEVEL_WARN:
        {
          if (ex != null)
          {
            getLogger(category).warn(message, ex);
          }
          else
          {
            getLogger(category).warn(message);
          }

          break;
        }
        case Log.LEVEL_INFO:
        {
          if (ex != null)
          {
            getLogger(category).info(message, ex);
          }
          else
          {
            getLogger(category).info(message);
          }

          break;
        }
        case Log.LEVEL_DEBUG:
        {
          if (ex != null)
          {
            getLogger(category).debug(message, ex);
          }
          else
          {
            getLogger(category).debug(message);
          }

          break;
        }
        case Log.LEVEL_TRACE:
        {
          if (ex != null)
          {
            getLogger(category).trace(message, ex);
          }
          else
          {
            getLogger(category).trace(message);
          }

          break;
        }
        default:
        {
          break;
        }
      }
    }

    private static org.slf4j.Logger getLogger(String category)
    {
      if (category == null)
      {
        return log;
      }

      org.slf4j.Logger logger = loggers.get(category);
      if (logger == null)
      {
        logger = LoggerFactory.getLogger("com.esotericsoftware." + category);
        loggers.put(category, logger);
      }

      return logger;
    }
  }
}