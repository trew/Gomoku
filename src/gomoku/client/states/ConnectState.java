package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.net.*;

import java.io.IOException;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;


import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * The connecting state of the game. This state's responsibility is to fetch
 * which server the player wants to connect to and connect to that server.
 *
 * @author Samuel Andersson
 */
public class ConnectState extends GomokuGameState {

	/** Which state we're in */
	private int connectingState;

	/** The message to be displayed showing the connection state */
	private String connectMessage;

	@Override
	public void init(GameContainer container, GomokuClient game)
			throws SlickException {

		game.client = new Client();
		game.client.start();

		RegisterPackets.register(game.client.getKryo());

		connectMessage = "Press space to connect";
	}

	@Override
	public void update(GameContainer container, final GomokuClient game, int delta)
			throws SlickException {

		if (connectingState == 0
				&& container.getInput().isKeyPressed(Input.KEY_SPACE)) {
			connectingState = 1;
			connectMessage = "Connecting...";
		} else if (connectingState == 1) {
			try {
				Listener listener = new Listener() {
					@Override
					public void connected(Connection conn) {
						connectingState = 3;
						connectMessage = "Connected.";
						game.client.removeListener(this);
					}
				};
				connectingState = 2;
				game.client.addListener(listener);
				game.client.connect(5000, GomokuClient.ADDRESS, GomokuClient.PORT);
			} catch (IOException e) {
				connectingState = 4;
				connectMessage = e.getMessage();
			}
		} else if (connectingState == 2) {
		} else if (connectingState == 3) {
			if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
				game.enterState(1); // gameplaystate
			}
		} else if (connectingState == 4) {
			if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
				container.exit();
			}
		}
	}

	@Override
	public void render(GameContainer container, GomokuClient game, Graphics g)
			throws SlickException {
		int w = container.getDefaultFont().getWidth(connectMessage);
		g.drawString(connectMessage, center(0, container.getWidth(), w), 30);
	}

	@Override
	public int getID() {
		return 0;
	}

}
