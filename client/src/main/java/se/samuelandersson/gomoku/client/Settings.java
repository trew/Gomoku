package se.samuelandersson.gomoku.client;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings
{
  public static enum Setting
  {
    PLAYER_NAME("playername", "Player", String.class), SERVER_IP("server", "127.0.0.1",
        String.class), CONFIRM("confirm", "true", Boolean.class), DEBUG("debug", "false", Boolean.class);

    private String name;
    private String defaultValue;
    private Class<?> type;

    private Setting(final String name, final String defaultValue, final Class<?> type)
    {
      this.name = name;
      this.defaultValue = defaultValue;
      this.type = type;
    }
  }

  private static final Logger log = LoggerFactory.getLogger(Settings.class);

  private static Settings instance;

  private Settings()
  {
  }

  public static Settings getInstance()
  {
    if (instance == null)
    {
      instance = new Settings();
      instance.loadProperties();
    }

    return instance;
  }

  protected Properties properties;
  private String propertiesFilename = "settings.properties";

  public boolean getBoolean(Setting setting)
  {
    if (setting.type != Boolean.class)
    {
      throw new IllegalArgumentException(setting.name + " is not a boolean type");
    }

    return Boolean.valueOf(this.getProperty(setting));
  }

  public String getProperty(Setting setting)
  {
    return this.properties.getProperty(setting.name, setting.defaultValue);
  }

  public String getProperty(Setting setting, String defaultValue)
  {
    return this.properties.getProperty(setting.name, defaultValue);
  }

  public void setProperty(final Setting setting, String value)
  {
    this.properties.setProperty(setting.name, value);
  }

  protected void loadProperties()
  {
    log.info("Reading configuration file");
    properties = new Properties();
    final File propertiesFile = new File(propertiesFilename);
    if (!propertiesFile.exists())
    {
      log.info("Unable to find settings file, creating new.");
      storeProperties();
    }

    try (FileReader reader = new FileReader(propertiesFile))
    {
      properties.load(reader);
    }
    catch (IOException e)
    {
      log.error("Error loading settings", e);
    }
  }

  public void storeProperties()
  {
    final File propertiesFile = new File(propertiesFilename);
    try
    {
      properties.store(new FileWriter(propertiesFile), "Gomoku Settings");
    }
    catch (IOException e)
    {
      log.error("Unable to save configuration file: " + propertiesFilename, e);
    }
  }
}
