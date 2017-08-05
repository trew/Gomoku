package se.samuelandersson.gomoku.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;

import se.samuelandersson.gomoku.client.net.NetworkClient;
import se.samuelandersson.gomoku.client.net.NetworkListener;
import se.samuelandersson.gomoku.client.net.PacketHandler;
import se.samuelandersson.gomoku.client.net.impl.NetworkClientImpl;
import se.samuelandersson.gomoku.client.states.GameState;
import se.samuelandersson.gomoku.client.states.MainMenuState;
import se.samuelandersson.gomoku.server.GomokuServer;

public class GomokuClient extends StateApplicationListener
{
  private static final Logger log = LoggerFactory.getLogger(GomokuClient.class);

  /** The width of the screen */
  public static final int WIDTH = 800;

  /** The height of the screen */
  public static final int HEIGHT = 600;

  private NetworkClient client;

  /** Internal server */
  private GomokuServer server;

  private static Thread renderThread;

  private static final Map<Class<? extends GameState>, GameState> states = new HashMap<>();

  @Override
  public void create()
  {
    renderThread = Thread.currentThread();

    client = new NetworkClientImpl();

    Settings.getInstance();

    super.create();

    setNextState(getState(MainMenuState.class));
  }

  @Override
  public void dispose()
  {
    super.dispose();

    for (GameState gs : states.values())
    {
      gs.dispose();
    }

    this.client.stop();
    if (this.server != null)
    {
      this.server.stop();
    }

    Settings.getInstance().storeProperties();
  }
  
  @Override
  public void update(float delta)
  {
    this.getClient().processExecutionQueue();
  }

  @Override
  protected void enterState(StateHolder state)
  {
    super.enterState(state);
    GameState gameState = state.state;

    if (gameState instanceof NetworkListener)
    {
      this.getClient().addListener((NetworkListener) gameState);
    }

    if (gameState instanceof PacketHandler)
    {
      this.getClient().addPacketHandler((PacketHandler) gameState);
    }
  }

  @Override
  protected void exitState(StateHolder state)
  {
    super.exitState(state);
    GameState gameState = state.state;

    if (gameState instanceof NetworkListener)
    {
      this.getClient().removeListener((NetworkListener) gameState);
    }

    if (gameState instanceof PacketHandler)
    {
      this.getClient().removePacketHandler((PacketHandler) gameState);
    }
  }

  public NetworkClient getClient()
  {
    return client;
  }
  
  public GomokuServer getServer()
  {
    return this.server;
  }

  public void startServer()
  {
    if (this.server == null)
    {
      this.server = new GomokuServer(9123, true);
      try
      {
        this.server.start();
      }
      catch (IOException e)
      {
        log.error("Error starting server on *:" + 9123, e);
        this.server.stop();
        this.server = null;
      }
    }
    else
    {
      this.stopServer();
      this.startServer();
    }
  }
  
  public void stopServer()
  {
    this.server.stop();
    this.server = null;
  }

  @Override
  public void setNextState(Class<? extends GameState> state)
  {
    this.setNextState(this.getState(state));
  }
  
  @SuppressWarnings("unchecked")
  public <T extends GameState> T getState(Class<T> clazz)
  {
    GameState state = states.get(clazz);
    if (state == null)
    {
      final Callable<T> c = new Callable<T>()
      {
        @Override
        public T call() throws Exception
        {
          try
          {
            Constructor<T> constructor = clazz.getConstructor(GomokuClient.class);
            T instance = constructor.newInstance(GomokuClient.this);
            instance.initialize();
            states.put(clazz, (GameState) instance);
            return instance;
          }
          catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                 | IllegalArgumentException | InvocationTargetException e)
          {
            throw new IllegalStateException(e);
          }
        }
      };

      if (isRenderThread())
      {
        try
        {
          return c.call();
        }
        catch (Exception e)
        {
          throw new RuntimeException(e);
        }
      }
      else
      {
        return invokeAndWait(c);
      }
    }

    return (T) state;
  }

  private static boolean isRenderThread()
  {
    return Thread.currentThread() == renderThread;
  }

  private static <T> T invokeAndWait(final Callable<T> callable)
  {
    final CountDownLatch latch = new CountDownLatch(1);
    class CallableRunnable implements Runnable
    {
      private T value;

      @Override
      public void run()
      {
        try
        {
          value = callable.call();
        }
        catch (Exception e)
        {
        }
        finally
        {
          latch.countDown();
        }
      }
    }
    CallableRunnable runnable = new CallableRunnable();
    Gdx.app.postRunnable(runnable);

    try
    {
      latch.await();
    }
    catch (InterruptedException e)
    {
      log.error("Interrupted", e);
    }

    return runnable.value;
  }

  /**
   * Similar to {@link java.awt.EventQueue#invokeAndWait(Runnable)} but works with the LibGDX render thread.
   */
  static void invokeAndWait(final Runnable runnable)
  {
    final CountDownLatch latch = new CountDownLatch(1);
    Gdx.app.postRunnable(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          runnable.run();
        }
        finally
        {
          latch.countDown();
        }
      }
    });

    try
    {
      latch.await();
    }
    catch (InterruptedException e)
    {
      log.error("Interrupted", e);
    }
  }
}
