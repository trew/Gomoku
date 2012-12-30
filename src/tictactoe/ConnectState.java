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

	@Override
	public void init(GameContainer container, Tictactoe game)
			throws SlickException {
		game.client = new Client();
		game.client.start();

		Kryo kryo = game.client.getKryo();
		kryo.register(PlacePiecePacket.class);
		kryo.register(MovePiecePacket.class);
		kryo.register(BoardPacket.class);
		kryo.register(int[].class);
	}

	@Override
	public void update(GameContainer container, Tictactoe game, int delta)
			throws SlickException {
		if (connectingState == 0) {
			try {
				game.client.connect(5000, "127.0.0.1", 9123);
				connectingState = 1;

			} catch (IOException e) {
				connectingState = 2;
				e.printStackTrace();
			}
		} else if (connectingState == 1) {
			if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
				game.enterState(1); //gameplaystate
			}
		} else if (connectingState == 2){
			if (container.getInput().isKeyPressed(Input.KEY_SPACE)) {
				container.exit();
			}
		}
	}

	@Override
	public void render(GameContainer container, Tictactoe game, Graphics g)
			throws SlickException {
		String s = "";
		if (connectingState == 0)
			s = "Connecting...";
		else if (connectingState == 1)
			s = "Connected.";
		else if (connectingState == 2)
			s = "Connection failed";
		float w = container.getDefaultFont().getWidth(s);
		g.drawString(s, center(0, container.getWidth(), w), 30);
	}

	@Override
	public int getID() {
		return 0;
	}

}
