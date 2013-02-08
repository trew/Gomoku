package gomoku.client.states;

import gomoku.client.GomokuClient;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class OptionsMenuState extends GomokuNetworkGameState {

    public OptionsMenuState() {
    }

    @Override
    public void init(GameContainer container, GomokuClient game)
            throws SlickException {
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
        Input input = container.getInput();
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            exitState();
        }
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {

    }

    @Override
    public void leave(GameContainer container, GomokuClient game) throws SlickException {
        // if someone set this to true, disable once we leave
        setForwarding(false);
        setStateToForwardTo(null);
    }
    @Override
    public int getID() {
        return OPTIONSMENUSTATE;
    }

}
