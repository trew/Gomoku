package tictactoe.states;

import java.io.IOException;

import net.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import tictactoe.Tictactoe;
import tictactoe.logic.Board;
import tictactoe.logic.Player;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ConnectState extends TTTGameState {

	private int connectingState;
	private String connectMessage;

	@Override
	public void init(GameContainer container, Tictactoe game)
			throws SlickException {

		game.client = new Client();
		game.client.start();

		Kryo kryo = game.client.getKryo();
		kryo.register(PlacePiecePacket.class);
		kryo.register(BoardPacket.class);
		kryo.register(Board.class);
		kryo.register(Player.class);
		kryo.register(GenericRequestPacket.class);
		kryo.register(SetColorPacket.class);
		kryo.register(NotifyTurnPacket.class);
		kryo.register(int[].class);

		connectMessage = "Press space to connect";
	}

	@Override
	public void update(GameContainer container, final Tictactoe game, int delta)
			throws SlickException {

		if (connectingState == 0 && container.getInput().isKeyPressed(Input.KEY_SPACE)) {
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
				game.client.connect(5000, Tictactoe.ADDRESS, Tictactoe.PORT);
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
	public void render(GameContainer container, Tictactoe game, Graphics g)
			throws SlickException {
		int w = container.getDefaultFont().getWidth(connectMessage);
		g.drawString(connectMessage, center(0, container.getWidth(), w), 30);
	}

	@Override
	public int getID() {
		return 0;
	}

}
