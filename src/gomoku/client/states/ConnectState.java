package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.net.*;

import java.io.IOException;
import java.net.UnknownHostException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.gui.TextField;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static com.esotericsoftware.minlog.Log.*;

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
    private String barString;
    private int barTimer;

    private TextField nameField;
    private TextField addressField;

    private String address;
    private int port;

    private Button connectButton;

    /** Which state we're in */
    private CONNECTSTATE connectingState;

    /** The message to be displayed showing the connection state */
    private String connectMessage;

    @SuppressWarnings("unchecked")
    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {

        // setup game default font
        UnicodeFont ucf = new UnicodeFont("res/fonts/Monda-Regular.ttf", 18,
                false, false);
        ucf.addAsciiGlyphs();
        ucf.getEffects().add(new ColorEffect());
        ucf.loadGlyphs();
        container.setDefaultFont(ucf);

        nameField = new TextField(container, container.getDefaultFont(), 300,
                50, 300, 30);
        nameField.setBorderColor(Color.white);
        nameField.setBackgroundColor(Color.darkGray);

        addressField = new TextField(container, container.getDefaultFont(),
                300, 100, 300, 30);
        addressField.setBorderColor(Color.white);
        addressField.setBackgroundColor(Color.darkGray);
        addressField.setText("127.0.0.1");
        port = 9123;

        connectButton = new Button(container, "Connect", 340, 350, 120, 35) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                connect(game);
            }
        };

        game.client = new Client();
        game.client.start();

        RegisterPackets.register(game.client.getKryo());

        connectMessage = "";
        connectingState = CONNECTSTATE.IDLE;
        barString = ".";
    }

    public boolean parseAddress(String address) {
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
        if (nameField.getText() == "") {
            return;
        }
        connectMessage = "Connecting...";

        // lock all settings
        connectButton.disable();
        nameField.deactivate();
        game.setPlayerName(nameField.getText());

        connectingState = CONNECTSTATE.CONNECTING;
        Listener listener = new Listener() {
            @Override
            public void connected(Connection conn) {
                connectingState = CONNECTSTATE.CONNECTED;
                connectMessage = "Connected.";
                game.client.removeListener(this);
                game.enterState(1); // choosegamestate
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
                } catch (UnknownHostException e) {
                    connectingState = CONNECTSTATE.CONNECTIONFAILED;
                    if (TRACE)
                        trace("ConnectState", e);
                    connectMessage = "Unkown host";
                    connectButton.enable();
                } catch (IOException e) {
                    connectingState = CONNECTSTATE.CONNECTIONFAILED;
                    if (TRACE)
                        trace("ConnectState", e);
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
            // display a little bar while waiting for server response
            // increase the length of it every 0.1 seconds
            barTimer += delta;
            if (barTimer > 100) {
                barTimer = 0;
                barString += ".";
                if (barString.length() > 10) {
                    barString = ".";
                }
            }
        } else if (connectingState == CONNECTSTATE.CONNECTIONFAILED) {
            if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
                container.exit();
            }
        }
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        g.setFont(container.getDefaultFont());

        int w = container.getDefaultFont().getWidth(connectMessage);
        g.drawString(connectMessage, center(0, container.getWidth(), w), 400);
        if (connectingState == CONNECTSTATE.CONNECTING) {
            g.drawString(barString, 350, 430);
        }

        g.drawString("Enter your name", 300, 25);
        nameField.render(container, g);

        g.drawString("Address", 300, 75);
        addressField.render(container, g);

        connectButton.render(container, g);
    }

    @Override
    public int getID() {
        return 0;
    }

}
