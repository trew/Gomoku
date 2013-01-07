package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.net.*;

import java.io.IOException;

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

    /** Which state we're in */
    private CONNECTSTATE connectingState;

    /** The message to be displayed showing the connection state */
    private String connectMessage;

    @SuppressWarnings("unchecked")
    @Override
    public void init(GameContainer container, GomokuClient game)
            throws SlickException {

        // setup game default font
        UnicodeFont ucf = new UnicodeFont("res/fonts/Monda-Regular.ttf", 18,
                false, false);
        ucf.addAsciiGlyphs();
        ucf.getEffects().add(new ColorEffect());
        ucf.loadGlyphs();
        container.setDefaultFont(ucf);

        nameField = new TextField(container, container.getDefaultFont(), 250,
                30, 275, 30);
        nameField.setBorderColor(Color.white);
        nameField.setBackgroundColor(Color.darkGray);

        game.client = new Client();
        game.client.start();

        RegisterPackets.register(game.client.getKryo());

        connectMessage = "Press space to connect";
        connectingState = CONNECTSTATE.IDLE;
        barString = ".";
    }

    /**
     * Create a new thread to connect to the server. Calling this function will
     * lock all selected settings, such as player name.
     * 
     * @param game
     *            The client that till connect to the server
     */
    public void connect(final GomokuClient game) {
        // lock all settings
        nameField.deactivate();

        connectingState = CONNECTSTATE.CONNECTING;
        Listener listener = new Listener() {
            @Override
            public void connected(Connection conn) {
                connectingState = CONNECTSTATE.CONNECTED;
                connectMessage = "Connected.";
                game.client.removeListener(this);
            }
        };
        game.client.addListener(listener);
        // start a new thread for the connection, so we don't hold up this
        // thread from receiving input or updating
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    game.client.connect(5000, GomokuClient.ADDRESS,
                            GomokuClient.PORT);
                } catch (IOException e) {
                    connectingState = CONNECTSTATE.CONNECTIONFAILED;
                    if (TRACE)
                        trace("ConnectState", e);
                    connectMessage = e.getMessage();
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

        if (connectingState == CONNECTSTATE.IDLE
                && container.getInput().isKeyPressed(Input.KEY_SPACE)) {
            connectMessage = "Connecting...";
            connect(game);
        } else if (connectingState == CONNECTSTATE.CONNECTING) {
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
        } else if (connectingState == CONNECTSTATE.CONNECTED) {
            if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
                game.enterState(1); // gameplaystate
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
        g.drawString(connectMessage, center(0, container.getWidth(), w), 500);
        if (connectingState == CONNECTSTATE.CONNECTING) {
            g.drawString(barString, 350, 530);
        }

        nameField.render(container, g);
    }

    @Override
    public int getID() {
        return 0;
    }

}
