package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.net.CreateGamePacket;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

public class CreateGameState extends GomokuGameState {

    private TextField gameNameField;
    private TextField widthField;
    private TextField heightField;
    private Button confirmButton;

    private GomokuClient gomokuClient;

    private BounceListener listener;

    private String errorMsg;

    public CreateGameState() {
    }

    @Override
    public void init(GameContainer container, GomokuClient game)
            throws SlickException {
        gomokuClient = game;
        listener = new BounceListener(this);

        gameNameField = new TextField(container, container.getDefaultFont(),
                20, 50, 300, 25);
        gameNameField.setBorderColor(Color.white);
        gameNameField.setBackgroundColor(Color.darkGray);

        widthField = new TextField(container, container.getDefaultFont(), 20,
                120, 40, 25);
        widthField.setBorderColor(Color.white);
        widthField.setBackgroundColor(Color.darkGray);
        heightField = new TextField(container, container.getDefaultFont(), 20,
                180, 40, 25);
        heightField.setBorderColor(Color.white);
        heightField.setBackgroundColor(Color.darkGray);

        confirmButton = new Button(container, "Create Game", 20, 250) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                try {
                    createNewGame();
                } catch (IllegalArgumentException e) {
                    errorMsg = e.getMessage();
                }
            }
        };
    }

    public void createNewGame() {
        int w, h;
        try {
            w = Integer.parseInt(widthField.getText());
            if (w < 3 || w > 127) {
                throw new IllegalArgumentException("Width must be between 3-127");
            }
            h = Integer.parseInt(heightField.getText());
            if (h < 3 || h > 127) {
                throw new IllegalArgumentException("Height must be between 3-127");
            }
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Width or height must be valid numbers");
        }
        if (gameNameField.getText() == "") {
            throw new IllegalArgumentException("You must provide a game name");
        }

        confirmButton.disable();
        gomokuClient.client.sendTCP(new CreateGamePacket(gameNameField.getText(), w, h, true));
    }

    @Override
    public void enter(GameContainer container, GomokuClient game) {
        game.client.addListener(listener);
    }

    @Override
    public void leave(GameContainer container, GomokuClient game) {
        game.client.removeListener(listener);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        g.drawString("Game Name", 20, 20);
        gameNameField.render(container, g);
        g.drawString("Board Width", 20, 90);
        widthField.render(container, g);
        g.drawString("Board Height", 20, 150);
        heightField.render(container, g);

        if (errorMsg != null && errorMsg != "") {
            g.drawString(errorMsg, 20, 500);
        }
        confirmButton.render(container, g);
    }

    @Override
    public int getID() {
        return CREATEGAMESTATE;
    }

}
