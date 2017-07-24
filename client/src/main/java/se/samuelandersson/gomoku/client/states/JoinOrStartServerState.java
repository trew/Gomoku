package se.samuelandersson.gomoku.client.states;

import java.io.IOException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.esotericsoftware.kryonet.Connection;

import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.Settings;
import se.samuelandersson.gomoku.client.Settings.Setting;
import se.samuelandersson.gomoku.client.net.NetworkListener;
import se.samuelandersson.gomoku.client.net.PacketHandler;
import se.samuelandersson.gomoku.net.HandshakeClientPacket;
import se.samuelandersson.gomoku.net.HandshakeServerPacket;
import se.samuelandersson.gomoku.net.InitialClientDataPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.Request;

/**
 * The connecting state of the game. This state's responsibility is to fetch
 * which server the player wants to connect to and connect to that server.
 *
 * @author Samuel Andersson
 */
public class JoinOrStartServerState extends MenuState
{
  private static final Logger log = LoggerFactory.getLogger(JoinOrStartServerState.class);

  /** The possible connection stages */
  private ProgressBar connectionBar;
  private int connectionBarDirection = 1;

  private TextField nameField;
  private TextField addressField;

  private Button joinGameButton;
  private Button startGameButton;
  private Button backButton;

  /** Which state we're in */
  private CONNECTSTATE connectingState;

  private Label connectMessageLabel;
  private Label enterNameLabel;
  private Label addressLabel;

  public JoinOrStartServerState(GomokuClient application)
  {
    super(application);
  }

  @Override
  public void initialize()
  {
    super.initialize();

    Skin skin = Assets.getInstance().getSkin();

    this.startGameButton = new TextButton("Start Game", skin);
    this.startGameButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        startNewServer();
      }
    });

    nameField = new TextField("", skin);
    nameField.setText(Settings.getInstance().getProperty(Setting.PLAYER_NAME));

    addressField = new TextField("", skin);
    addressField.setText(Settings.getInstance().getProperty(Setting.SERVER_IP));

    connectionBar = new ProgressBar(0.f, 100.f, 1.f, false, skin);
    connectionBar.setVisible(false);

    joinGameButton = new TextButton("Join Game", skin);
    joinGameButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        connect();
      }
    });

    backButton = new TextButton("Back", skin);
    this.backButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        GomokuClient app = JoinOrStartServerState.this.getApplication();
        app.setNextState(app.getState(MainMenuState.class));
      }
    });

    this.connectMessageLabel = new Label("", skin);
    this.enterNameLabel = new Label("Enter your name", skin);
    this.addressLabel = new Label("Address", skin);

    this.getTable().add(this.startGameButton).colspan(2).padBottom(20);
    this.getTable().row();
    this.getTable().add(this.joinGameButton).colspan(2);
    this.getTable().row();
    this.getTable().add(this.enterNameLabel);
    this.getTable().add(this.nameField);
    this.getTable().row();
    this.getTable().add(this.addressLabel);
    this.getTable().add(this.addressField);
    this.getTable().row();
    this.getTable().add(this.connectionBar).colspan(2);
    this.getTable().row();
    this.getTable().add(this.backButton).colspan(2);
    this.getTable().row();
    this.getTable().add(this.connectMessageLabel).colspan(2);

    this.getStage().addActor(this.getTable());

    this.getApplication().getClient().start();

    connectingState = CONNECTSTATE.IDLE;
  }

  private String parseAddress(String address)
  {
    address = address.trim();
    String[] parts = address.split(":", 2);

    if (parts.length < 3)
    {
      return parts[0];
    }

    return null;
  }

  private int parsePort(String address, int defaultPort)
  {
    address = address.trim();
    String[] parts = address.split(":", 2);

    if (parts.length == 1)
    {
      return defaultPort;
    }
    else if (parts.length > 2)
    {
      return -1;
    }

    try
    {
      return Integer.parseInt(parts[1]);
    }
    catch (NumberFormatException e)
    {
      return -1;
    }
  }

  public void startNewServer()
  {
    this.getApplication().startServer();
    this.connect("127.0.0.1", 9123, true);
  }

  /**
   * Create a new thread to connect to the server. Calling this function will lock
   * all selected settings, such as player name.
   *
   * @param game
   *          The client that till connect to the server
   */
  public void connect()
  {
    final String address = parseAddress(addressField.getText());
    final int port = parsePort(addressField.getText(), 9123);
    if (address == null || port < 0)
    {
      return;
    }

    if (nameField.getText().trim().equals(""))
    {
      return;
    }

    connectMessageLabel.setText("Connecting...");

    // lock all settings, and save them
    setSettingsFieldsLocked(true);

    Settings.getInstance().setProperty(Setting.PLAYER_NAME, nameField.getText().trim());
    Settings.getInstance().setProperty(Setting.SERVER_IP, addressField.getText().trim());

    this.connect(address, port, false);
  }

  private void connect(String host, int port, final boolean local)
  {
    final GomokuClient app = this.getApplication();

    connectingState = CONNECTSTATE.CONNECTING;

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

          final NetworkListener listener = new NetworkListener()
          {
            @Override
            public void connected(Connection conn)
            {
              log.info("Connected to " + conn.getRemoteAddressTCP().getAddress().getHostAddress());

              connectingState = CONNECTSTATE.CONNECTED;
              connectMessageLabel.setText("Connected.");
              app.getClient().removeListener(this);
            }
          };

          app.getClient().addListener(listener);
          app.getClient().connect(5000, host, port);
        }
        catch (UnknownHostException e)
        {
          connectingState = CONNECTSTATE.CONNECTIONFAILED;

          log.error("Error", e);

          connectMessageLabel.setText("Unknown host");
          setSettingsFieldsLocked(false);
          return;
        }
        catch (IOException e)
        {
          connectingState = CONNECTSTATE.CONNECTIONFAILED;

          log.error("Error", e);

          connectMessageLabel.setText(e.getMessage());
          setSettingsFieldsLocked(false);
          return;
        }
        
        final String playerName = Settings.getInstance().getProperty(Setting.PLAYER_NAME);
        app.getClient().addPacketHandler(new PacketHandler()
        {
          @Override
          public void handleHandshakeServer(Connection connection, HandshakeServerPacket hcp)
          {
            if (!local && !hcp.isReady())
            {
              app.getClient().removePacketHandler(this);
              app.getClient().disconnect();
              connectingState = CONNECTSTATE.IDLE;
              connectMessageLabel.setText("Server is not ready with a started game, try again later");
              setSettingsFieldsLocked(false);
              return;
            }

            app.getClient().sendTCP(new InitialClientDataPacket(playerName));

            if (local)
            {
              app.setNextState(CreateGameState.class);
            }
            else if (!hcp.isSingleGameServer())
            {
              app.setNextState(ChooseGameState.class);
            }
            else
            {
              app.getClient().sendTCP(Request.JOIN_SINGLE_GAME_SERVER);
            }
          };

          @Override
          public void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
          {
            app.getState(GameplayState.class).setInitialData(isdp);
            app.setNextState(GameplayState.class);
            app.getClient().removePacketHandler(this);
          };
        });

        app.getClient().sendTCP(new HandshakeClientPacket(playerName));
      }
    }).start();
  }

  public void setSettingsFieldsLocked(boolean locked)
  {
    joinGameButton.setDisabled(locked);
    nameField.setDisabled(locked);
    addressField.setDisabled(locked);
    connectionBar.setVisible(locked);
    connectionBar.setValue(0);
  }

  @Override
  public void update(float delta)
  {
    if (connectingState == CONNECTSTATE.CONNECTING)
    {
      if (connectionBar.getValue() > 99)
      {
        connectionBar.setValue(100);
        connectionBarDirection = -1;
      }
      else if (connectionBar.getValue() < 1)
      {
        connectionBar.setValue(0);
        connectionBarDirection = 1;
      }

      connectionBar.setValue(connectionBar.getValue() + 100f * delta * connectionBarDirection);
    }
  }

  @Override
  public void show()
  {
    super.show();

    if (connectingState == CONNECTSTATE.CONNECTED)
    {
      // state is out of sync, correct it
      if (!this.getApplication().getClient().isConnected())
      {
        connectMessageLabel.setText("");
        connectingState = CONNECTSTATE.IDLE;
        setSettingsFieldsLocked(false);
      }
      else
      {
        this.getApplication().setNextState(this.getApplication().getState(ChooseGameState.class));
      }
    }
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
