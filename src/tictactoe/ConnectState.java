package tictactoe;

import java.io.IOException;

import net.*;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;

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
		kryo.register(MovePiecePacket.class);
		kryo.register(BoardPacket.class);
		kryo.register(GenericRequestPacket.class);
		kryo.register(int[].class);

		connectMessage = "Connecting...";
	}

	@Override
	public void update(GameContainer container, Tictactoe game, int delta)
			throws SlickException {

		if (connectingState == 0) {
			try {
				game.client.connect(5000, Tictactoe.ADDRESS, Tictactoe.PORT);
				connectingState = 1;
				connectMessage = "Connected.";
			} catch (IOException e) {
				connectingState = 2;
				connectMessage = e.getMessage();
			}
		} else if (connectingState == 1) {
			if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
				game.enterState(1); // gameplaystate
			}
		} else if (connectingState == 2) {
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
