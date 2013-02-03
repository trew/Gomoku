package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class MainMenuState extends GomokuGameState {

    private Image gomokuTitle;

    private Button multiPlayerButton;
    private Button optionsButton;
    private Button exitButton;

    public MainMenuState() {
    }

    @Override
    public void init(final GameContainer container, final GomokuClient game)
            throws SlickException {
        final GameContainer gamecontainer = container;
        game.setBackground(new Image("res/background.png"));

        gomokuTitle = new Image("res/gomoku.png");
        Image mp = new Image("res/buttons/playmultiplayerbutton.png");
        multiPlayerButton = new Button(mp, 250, 200) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    enterState(CONNECTGAMESTATE);
                }
            }
        };
        Image op = new Image("res/buttons/optionsbutton.png");
        optionsButton = new Button(op, 250, 280) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    //TODO
//                    enterState(OPTIONSSTATE);
                }
            }
        };
        Image ex = new Image("res/buttons/exitgamebutton.png");
        exitButton = new Button(ex, 250, 360) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    gamecontainer.exit();
                }
            }
        };

        addListener(multiPlayerButton);
        addListener(optionsButton);
        addListener(exitButton);
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        g.drawImage(game.getBackground(), 0, 0);
        g.drawImage(gomokuTitle, 16, 30);

        multiPlayerButton.render(container, g);
        optionsButton.render(container, g);
        exitButton.render(container, g);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
    }

    @Override
    public void enter(GameContainer container, GomokuClient game)
            throws SlickException {
    }

    @Override
    public void leave(GameContainer container, GomokuClient game)
            throws SlickException {
    }

    @Override
    public int getID() {
        return MAINMENUSTATE;
    }

}
