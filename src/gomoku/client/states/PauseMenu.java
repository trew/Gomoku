package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.net.GenericRequestPacket;
import gomoku.net.Request;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;

public class PauseMenu extends GomokuGameState {

    private Color dimmedBackground;

    private Image menuBackground;

    private Button continueButton;
    private Button optionsButton;
    private Button exitToMenuButton;
    private Button exitToOSButton;

    public PauseMenu() {
        setRenderingParent(true);
    }

    @Override
    public void init(final GameContainer container, final GomokuClient game)
            throws SlickException {
        dimmedBackground = new Color(0, 0, 0, 0.5f);
        // try {
        // menuBackground = new Image("res/popup.png");
        // } catch (SlickException e) {
        // e.printStackTrace();
        // }
        Image continueImage = new Image("res/buttons/optionsbutton.png");// TODO:
                                                                         // fix
                                                                         // images
        Image optionsImage = new Image("res/buttons/optionsbutton.png");
        Image exitToMenuImage = new Image("res/buttons/optionsbutton.png");
        Image exitToOSImage = new Image("res/buttons/optionsbutton.png");
        continueButton = new Button(continueImage, 250, 150) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    exitState();
                }
            }
        };
        optionsButton = new Button(optionsImage, 250, 220) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    GomokuNetworkGameState optionsState = ((GomokuNetworkGameState) game
                            .getState(OPTIONSMENUSTATE));
                    optionsState.setForwarding(true);
                    optionsState
                            .setStateToForwardTo((GomokuNetworkGameState) game
                                    .getState(GAMEPLAYSTATE));
                    enterState(OPTIONSMENUSTATE,
                            (GomokuGameState) game.getCurrentState());
                }
            }
        };
        exitToMenuButton = new Button(exitToMenuImage, 250, 290) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    getGame().client.sendTCP(new GenericRequestPacket(
                            Request.LeaveGame));
                    enterState(MAINMENUSTATE);
                }
            }
        };
        exitToOSButton = new Button(exitToOSImage, 250, 360) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    container.exit();
                }
            }
        };

        addListener(continueButton);
        addListener(optionsButton);
        addListener(exitToMenuButton);
        addListener(exitToOSButton);
    }

    @Override
    public void leave(GameContainer container, GomokuClient game) throws SlickException {
        GomokuNetworkGameState optionsState = ((GomokuNetworkGameState) game
                .getState(OPTIONSMENUSTATE));
        optionsState.setForwarding(false);
        optionsState.setStateToForwardTo(null);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
        continueButton.setLocation(250, 150);
        optionsButton.setLocation(250, 220);
        exitToMenuButton.setLocation(250, 290);
        exitToOSButton.setLocation(250, 360);
        Input input = container.getInput();
        if (input.isKeyPressed(Input.KEY_ESCAPE)) {
            exitState();
        }
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        Color oldColor = g.getColor();

        g.setColor(dimmedBackground);
        g.fillRect(0, 0, container.getWidth(), container.getHeight());

        if (menuBackground != null)
            g.drawImage(menuBackground, 400 - menuBackground.getWidth() / 2,
                    300 - menuBackground.getHeight() / 2);

        g.setColor(Color.white);
        continueButton.render(container, g);
        optionsButton.render(container, g);
        exitToMenuButton.render(container, g);
        exitToOSButton.render(container, g);

        g.setColor(oldColor);
    }

    @Override
    public int getID() {
        return PAUSEMENUSTATE;
    }

}
