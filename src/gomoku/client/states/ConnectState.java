package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.net.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import TWLSlick.RootPane;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.ProgressBar;

import static org.trew.log.Log.*;

/**
 * The connecting state of the game. This state's responsibility is to fetch
 * which server the player wants to connect to and connect to that server.
 *
 * @author Samuel Andersson
 */
public class ConnectState extends GomokuGameState {

    private static enum CONNECTSTATE {
        IDLE, CONNECTING, CONNECTED, CONNECTIONFAILED
    }

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
    protected RootPane createRootPane() {
        RootPane rp = super.createRootPane();

        nameField = new EditField();
        nameField.setSize(300, 20);
        nameField.setPosition(250, 130);

        addressField = new EditField();
        addressField.setSize(300, 20);
        addressField.setPosition(250, 200);
        addressField.setText("127.0.0.1");

        connectionBar = new ProgressBar();
        connectionBar.setSize(300, 10);
        connectionBar.setPosition(250, 395);
        connectionBar.setVisible(false);

        rp.add(connectionBar);
        rp.add(nameField);
        rp.add(addressField);
        return rp;
    }

    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {

        port = 9123;

        Image cBtn = new Image("res/buttons/connectbutton.png");
        connectButton = new Button(cBtn, 250, 330) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    connect(game);
                }
            }
        };
        connectButton.setCenterX(container.getWidth() / 2);
        Image bBtn = new Image("res/buttons/backbutton.png");
        backButton = new Button(bBtn, 250, 500) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    enterState(MAINMENUSTATE);
                }
            }
        };
        backButton.setCenterX(container.getWidth() / 2);

        addListener(connectButton);
        addListener(backButton);

        game.client = new Client();
        game.client.start();

        RegisterPackets.register(game.client.getKryo());

        connectMessage = "";
        connectingState = CONNECTSTATE.IDLE;
    }

    public boolean parseAddress(String address) {
        address = address.trim();
        String[] parts = address.split(":", 2);
        if (parts.length == 1) {
            this.address = parts[0];
            return true;
        }

        try {
            port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
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
     *            The client that till connect to the server
     */
    public void connect(final GomokuClient game) {
        if (!parseAddress(addressField.getText())) {
            return;
        }
        if (nameField.getText().trim().equals("")) {
            return;
        }
        connectMessage = "Connecting...";

        // lock all settings
        connectButton.disable();
        nameField.setEnabled(false);
        game.setPlayerName(nameField.getText());

        connectingState = CONNECTSTATE.CONNECTING;
        Listener listener = new Listener() {
            @Override
            public void connected(Connection conn) {
                connectingState = CONNECTSTATE.CONNECTED;
                connectMessage = "Connected.";
                game.client.removeListener(this);
                enterState(CHOOSEGAMESTATE);
            }
        };
        game.client.addListener(listener);
        // start a new thread for the connection, so we don't hold up this
        // thread from receiving input or updating
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    game.client.connect(5000, address, port);
                    game.client.sendTCP(new InitialClientDataPacket(game
                            .getPlayerName()));
                } catch (UnknownHostException e) {
                    connectingState = CONNECTSTATE.CONNECTIONFAILED;
                    if (TRACE)
                        trace(e);
                    connectMessage = "Unkown host";
                    connectButton.enable();
                } catch (IOException e) {
                    connectingState = CONNECTSTATE.CONNECTIONFAILED;
                    if (TRACE)
                        trace(e);
                    connectMessage = e.getMessage();
                    connectButton.enable();
                }
            }
        }).start();

    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {

        if (container.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
            container.exit();
        }

        if (connectingState == CONNECTSTATE.CONNECTING) {
            connectionBar.setVisible(true);
            // display a little bar while waiting for server response
            // increase the length of it every 0.025 seconds
            barTimer += delta;
            if (barTimer > 20) {
                barTimer = 0;
                if (connectionBar.getValue() == 1.0f)
                    connectionBar.setValue(0);
                else
                    connectionBar.setValue(connectionBar.getValue() + 0.05f);
            }
        } else if (connectingState == CONNECTSTATE.CONNECTIONFAILED) {
            connectionBar.setVisible(false);
            if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
                container.exit();
            }
        }
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        g.setFont(container.getDefaultFont());

        drawCenteredString(connectMessage, 410, container, g);

        drawCenteredString("Enter your name", 95, container, g);

        drawCenteredString("Address", 175, container, g);

        connectButton.render(container, g);
        backButton.render(container, g);
    }

    @Override
    public int getID() {
        return CONNECTGAMESTATE;
    }

    @Override
    public void enter(GameContainer container, GomokuClient game)
            throws SlickException {
        if (connectingState == CONNECTSTATE.CONNECTED) {
            enterState(CHOOSEGAMESTATE);
        }
    }

    @Override
    public void leave(GameContainer container, GomokuClient game)
            throws SlickException {
    }

}
