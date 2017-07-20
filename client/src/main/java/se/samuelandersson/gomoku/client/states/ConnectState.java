package se.samuelandersson.gomoku.client.states;

import java.io.IOException;
import java.net.UnknownHostException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Event;
import de.matthiasmann.twl.EditField.Callback;
import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.NetworkClient;
import se.samuelandersson.gomoku.client.Settings.Setting;
import se.samuelandersson.gomoku.net.*;
import de.matthiasmann.twl.ProgressBar;

/**
 * The connecting state of the game. This state's responsibility is to fetch
 * which server the player wants to connect to and connect to that server.
 *
 * @author Samuel Andersson
 */
public class ConnectState extends AbstractGameState
{
  private static final Logger log = LoggerFactory.getLogger(ConnectState.class);

  /** The possible connection stages */
  private ProgressBar connectionBar;
  private int barTimer;

  private EditField nameField;
  private EditField addressField;

  private String address;
  private int port;

  private Button connectButton;
  private Button backButton;

  /** Which state we're in */
  private CONNECTSTATE connectingState;

  /** The message to be displayed showing the connection state */
  private String connectMessage;

  @Override
  protected RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    nameField = new EditField();
    nameField.setSize(300, 30);
    nameField.setPosition(250, 130);
    nameField.setText(getGame().getSettings().getProperty(Setting.PLAYER_NAME));

    addressField = new EditField();
    addressField.setSize(300, 30);
    addressField.setPosition(250, 200);
    addressField.setText(getGame().getSettings().getProperty(Setting.SERVER_IP));

    connectionBar = new ProgressBar();
    connectionBar.setSize(300, 15);
    connectionBar.setPosition(250, 395);
    connectionBar.setVisible(false);

    connectButton = new Button("Connect!");
    connectButton.setSize(300, 60);
    connectButton.setPosition(250, 330);

    backButton = new Button("Back");
    backButton.setSize(300, 60);
    backButton.setPosition(250, 450);

    rp.add(connectButton);
    rp.add(backButton);
    rp.add(connectionBar);
    rp.add(nameField);
    rp.add(addressField);

    return rp;
  }

  @Override
  public void init(GameContainer container, final GomokuClient game) throws SlickException
  {
    port = 9123;

    connectButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        connect(game);
      }
    });

    backButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        enterState(MAINMENUSTATE);
      }
    });

    Callback cb = new Callback()
    {
      @Override
      public void callback(int key)
      {
        if (key == Event.KEY_RETURN)
        {
          connect(game);
        }
      }
    };

    nameField.addCallback(cb);
    addressField.addCallback(cb);

    game.setNetworkClient(new NetworkClient());
    game.getNetworkClient().start();

    RegisterPackets.register(game.getNetworkClient().getKryo());

    connectMessage = "";
    connectingState = CONNECTSTATE.IDLE;
  }

  public boolean parseAddress(String address)
  {
    address = address.trim();
    String[] parts = address.split(":", 2);

    if (parts.length == 1)
    {
      this.address = parts[0];
      return true;
    }

    try
    {
      port = Integer.parseInt(parts[1]);
    }
    catch (NumberFormatException e)
    {
      return false;
    }

    this.address = parts[0];

    return true;
  }

  /**
   * Create a new thread to connect to the server. Calling this function will
   * lock all selected settings, such as player name.
   *
   * @param game
   *          The client that till connect to the server
   */
  public void connect(final GomokuClient game)
  {
    if (!parseAddress(addressField.getText()))
    {
      return;
    }

    if (nameField.getText().trim().equals(""))
    {
      return;
    }

    connectMessage = "Connecting...";

    // lock all settings, and save them
    connectButton.setEnabled(false);
    nameField.setEnabled(false);

    game.getSettings().setProperty(Setting.PLAYER_NAME, nameField.getText().trim());
    game.getSettings().setProperty(Setting.SERVER_IP, addressField.getText().trim());

    connectingState = CONNECTSTATE.CONNECTING;
    Listener listener = new Listener()
    {
      @Override
      public void connected(Connection conn)
      {
        log.info("Connected to " + conn.getRemoteAddressTCP().getAddress().getHostAddress());

        connectingState = CONNECTSTATE.CONNECTED;
        connectMessage = "Connected.";
        game.getNetworkClient().removeListener(this);
        enterState(CHOOSEGAMESTATE);
      }
    };

    game.getNetworkClient().addListener(listener);
    // start a new thread for the connection, so we don't hold up this
    // thread from receiving input or updating
    new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          if (log.isDebugEnabled())
          {
            log.debug("Connecting...");
          }

          game.getNetworkClient().connect(5000, address, port);

          final String playerName = game.getSettings().getProperty(Setting.PLAYER_NAME);
          game.getNetworkClient().sendTCP(new InitialClientDataPacket(playerName));
        }
        catch (UnknownHostException e)
        {
          connectingState = CONNECTSTATE.CONNECTIONFAILED;

          log.error("Error", e);

          connectMessage = "Unknown host";
          connectButton.setEnabled(true);
        }
        catch (IOException e)
        {
          connectingState = CONNECTSTATE.CONNECTIONFAILED;

          log.error("Error", e);

          connectMessage = e.getMessage();
          connectButton.setEnabled(true);
        }
      }
    }).start();
  }

  @Override
  public void update(GameContainer container, GomokuClient game, int delta) throws SlickException
  {

    if (connectingState == CONNECTSTATE.CONNECTING)
    {
      connectionBar.setVisible(true);
      // display a little bar while waiting for server response
      // increase the length of it every 0.025 seconds
      barTimer += delta;
      if (barTimer > 20)
      {
        barTimer = 0;
        if (connectionBar.getValue() == 1.0f)
        {
          connectionBar.setValue(0);
        }
        else
        {
          connectionBar.setValue(connectionBar.getValue() + 0.05f);
        }
      }
    }
    else if (connectingState == CONNECTSTATE.CONNECTIONFAILED)
    {
      connectionBar.setVisible(false);
      nameField.setEnabled(true);

      if (container.getInput().isKeyPressed(Input.KEY_SPACE))
      {
        container.exit();
      }
    }
  }

  @Override
  public void render(GameContainer container, GomokuClient game, Graphics g) throws SlickException
  {
    g.setFont(container.getDefaultFont());
    drawCenteredString(connectMessage, 410, container, g);
    drawCenteredString("Enter your name", 95, container, g);
    drawCenteredString("Address", 175, container, g);
  }

  @Override
  public int getID()
  {
    return CONNECTGAMESTATE;
  }

  @Override
  public void enter(GameContainer container, GomokuClient game) throws SlickException
  {
    if (connectingState == CONNECTSTATE.CONNECTED)
    {
      enterState(CHOOSEGAMESTATE);
    }
  }

  @Override
  public void leave(GameContainer container, GomokuClient game) throws SlickException
  {
  }

  public void disconnected()
  {
    connectingState = CONNECTSTATE.IDLE;
  }

  private static enum CONNECTSTATE
  {
    IDLE, CONNECTING, CONNECTED, CONNECTIONFAILED
  }
}
