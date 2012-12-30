package tictactoe;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public abstract class TTTGameState extends BasicGameState {

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		init(container, (Tictactoe) game);
	}

	public abstract void init(GameContainer container, Tictactoe game)
			throws SlickException;

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		render(container, (Tictactoe) game, g);
	}

	public abstract void render(GameContainer container, Tictactoe game,
			Graphics g) throws SlickException;

	public float center(float x1, float x2, float width) {
		return x1 + (x2 - x1) / 2 - width / 2;
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		update(container, (Tictactoe) game, delta);

	}

	public abstract void update(GameContainer container, Tictactoe game,
			int delta) throws SlickException;

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		enter(container, (Tictactoe) game);
	}
	public void enter(GameContainer container, Tictactoe game) throws SlickException {}

}