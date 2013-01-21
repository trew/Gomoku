package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.ResourceLoader;

public class MainMenuState extends GomokuGameState {

    private Image background;
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
        background = new Image("res/background.png");
        gomokuTitle = new Image("res/gomoku.png");
        Image mp = new Image("res/playmultiplayerbutton.png");
        Image mpHover = new Image("res/playmultiplayerbuttonhover.png");
        Image mpClick = new Image("res/playmultiplayerbuttonclick.png");
        multiPlayerButton = new Button(container, mp, mpHover, mpClick, null, 250, 200) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    game.enterState(CONNECTGAMESTATE);
                }
            }
        };
        Image op = new Image("res/optionsbutton.png");
        Image opHover = new Image("res/optionsbuttonhover.png");
        Image opClick = new Image("res/optionsbuttonclick.png");
        optionsButton = new Button(gamecontainer, op, opHover, opClick, null, 250, 280) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
//                    game.enterState(OPTIONSSTATE);
                }
            }
        };
        Image ex = new Image("res/exitgamebutton.png");
        Image exHover = new Image("res/exitgamebuttonhover.png");
        Image exClick = new Image("res/exitgamebuttonclick.png");
        exitButton = new Button(container, ex, exHover, exClick, null, 250, 360) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    gamecontainer.exit();
                }
            }
        };
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        g.drawImage(background, 0, 0);
        g.drawImage(gomokuTitle, 16, 30);

        multiPlayerButton.render(container, g);
        optionsButton.render(container, g);
        exitButton.render(container, g);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
        // TODO Auto-generated method stub

    }

    @Override
    public void enter(GameContainer container, GomokuClient game)
            throws SlickException {
        // TODO Auto-generated method stub

    }

    @Override
    public void leave(GameContainer container, GomokuClient game)
            throws SlickException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getID() {
        return MAINMENUSTATE;
    }

}
