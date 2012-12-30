package tictactoe;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;

public class Tictactoe extends StateBasedGame {

	private static final int WIDTH = 640;
	private static final int HEIGHT = 480;
	private static final boolean FULLSCREEN = false;
	private static final int TARGET_FPS = 60;

	public Client client;

	public Tictactoe() {
		super("Tic tac toe");
		client = new Client();
	}

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
		this.addState(new ConnectState());  //stateID: 0
		this.addState(new GameplayState()); //stateID: 1
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Tictactoe game = new Tictactoe();
			AppGameContainer container = new AppGameContainer(game);

			container.setDisplayMode(WIDTH, HEIGHT, FULLSCREEN);
			container.setTargetFrameRate(TARGET_FPS);

			container.start();
			game.client.stop();

		} catch (SlickException e) {
			e.printStackTrace();
		}

	}


}
