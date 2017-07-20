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
    PLAYER_NAME("playername", "Player"),
    SERVER_IP("server", "127.0.0.1"),
    CONFIRM("confirm", "true");
    
    private String name;
    private String defaultValue;
    
    private Setting(final String name, final String defaultValue)
    {
      this.name = name;
      this.defaultValue = defaultValue;
    }
  }
  private static final Logger log = LoggerFactory.getLogger(Settings.class);
  
  protected Properties properties;
  private String propertiesFilename = "settings.properties";

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
