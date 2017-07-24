package se.samuelandersson.gomoku.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public final class Assets
{
  private static final String ATLAS_PATH = "atlas/gomoku.atlas";
  private static final String SKIN_PATH = "skin/uiskin.json";

  public AssetManager assetManager;

  private TextureAtlas atlas;
  private Skin skin;
  private BitmapFont defaultFont;

  private static Assets instance;

  public static Assets getInstance()
  {
    if (instance == null)
    {
      instance = new Assets();
    }

    return instance;
  }

  private Assets()
  {
    assetManager = new AssetManager();
  }

  public void load()
  {
    assetManager.load(ATLAS_PATH, TextureAtlas.class);
    assetManager.load(SKIN_PATH, Skin.class);
    assetManager.finishLoading();

    defaultFont = new BitmapFont();
    atlas = assetManager.get(ATLAS_PATH, TextureAtlas.class);
    skin = assetManager.get(SKIN_PATH, Skin.class);
  }

  public void dispose()
  {
    assetManager.dispose();
    defaultFont.dispose();
  }

  public <T> T get(String fileName, Class<T> type)
  {
    if (assetManager.isLoaded(fileName))
    {
      return assetManager.get(fileName, type);
    }

    assetManager.load(fileName, type);
    assetManager.finishLoading();

    return assetManager.get(fileName, type);
  }

  public Drawable getDrawable(String region)
  {
    return new TextureRegionDrawable(atlas.findRegion(region));
  }

  public Skin getSkin()
  {
    return skin;
  }

  public TextureAtlas getAtlas()
  {
    return atlas;
  }

  public BitmapFont getFont()
  {
    return defaultFont;
  }

}
