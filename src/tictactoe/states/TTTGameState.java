package tictactoe.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import tictactoe.Tictactoe;

public abstract class TTTGameState extends BasicGameState {

	/**
	 * @see BasicGameState#init(GameContainer, StateBasedGame)
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		init(container, (Tictactoe) game);
	}

	/**
	 * @see #init(GameContainer, StateBasedGame)
	 */
	public abstract void init(GameContainer container, Tictactoe game)
			throws SlickException;

	/**
	 * @see BasicGameState#render(GameContainer, StateBasedGame, Graphics)
	 */
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		render(container, (Tictactoe) game, g);
	}

	/**
	 * @see BasicGameState#render(GameContainer, StateBasedGame, Graphics)
	 */
	public abstract void render(GameContainer container, Tictactoe game,
			Graphics g) throws SlickException;

	/**
	 * Calculate the left X position for centering something within borders
	 *
	 * @param x1
	 *            The left position of the border
	 * @param x2
	 *            The right position of the border
	 * @param width
	 *            The width of the object being centered
	 * @return The left position
	 */
	public int center(float x1, float x2, float width) {
		return (int)(x1 + (x2 - x1) / 2 - width / 2);
	}

	/**
	 * @see BasicGameState#update(GameContainer, StateBasedGame, int)
	 */
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		update(container, (Tictactoe) game, delta);

	}

	/**
	 * @see BasicGameState#update(GameContainer, StateBasedGame, int)
	 */
	public abstract void update(GameContainer container, Tictactoe game,
			int delta) throws SlickException;

	/**
	 * @see BasicGameState#enter(GameContainer, StateBasedGame)
	 */
	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		enter(container, (Tictactoe) game);
	}

	/**
	 * Notification that we've entered this game state
	 *
	 * @param container
	 *            The container holding the game
	 * @param game
	 *            The Tictactoe game holding this state
	 * @throws SlickException
	 *             Indicates an internal error that will be reported through the
	 *             standard framework mechanism
	 * @see #enter(GameContainer, StateBasedGame)
	 */
	public void enter(GameContainer container, Tictactoe game)
			throws SlickException {
	}

}