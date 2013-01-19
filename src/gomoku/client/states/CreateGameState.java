package gomoku.client.states;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.client.gui.CheckBox;
import gomoku.logic.GomokuConfig;
import gomoku.net.CreateGamePacket;
import gomoku.net.InitialServerDataPacket;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.TextField;

import com.esotericsoftware.kryonet.Connection;

public class CreateGameState extends GomokuGameState {

    private TextField gameNameField;
    private TextField widthField;
    private TextField heightField;
    private CheckBox allowOverlinesCB;
    private CheckBox threeAndThreeCB;
    private CheckBox fourAndFourCB;
    private CheckBox swap2CB;
    private Button confirmButton;

    // presets
    private Button gomokuPresetButton;

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
                110, 40, 25);
        widthField.setBorderColor(Color.white);
        widthField.setBackgroundColor(Color.darkGray);
        widthField.setText("15");
        widthField.setCursorPos(2);
        heightField = new TextField(container, container.getDefaultFont(), 70,
                110, 40, 25);
        heightField.setBorderColor(Color.white);
        heightField.setBackgroundColor(Color.darkGray);
        heightField.setText("15");
        heightField.setCursorPos(2);

        allowOverlinesCB = new CheckBox(container, 20, 150, 25, 25);
        threeAndThreeCB = new CheckBox(container, 20, 180, 25, 25);
        fourAndFourCB = new CheckBox(container, 20, 210, 25, 25);
        swap2CB = new CheckBox(container, 20, 240, 25, 25);

        gomokuPresetButton = new Button(container, "Gomoku", 600, 40) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                applyPreset(GomokuConfig.GomokuPreset());
            }
        };

        confirmButton = new Button(container, "Create Game", 20, 350) {
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

    private void applyPreset(GomokuConfig config) {
        if (gameNameField.getText().equals("")) {
            gameNameField.setText(config.getName());
        }
        widthField.setText(String.valueOf(config.getWidth()));
        heightField.setText(String.valueOf(config.getHeight()));

        allowOverlinesCB.setChecked(config.getAllowOverlines());
        threeAndThreeCB.setChecked(config.useThreeAndThree());
        fourAndFourCB.setChecked(config.useFourAndFour());
        swap2CB.setChecked(config.useSwap2());
    }

    public void createNewGame() {
        int w, h;
        try {
            w = Integer.parseInt(widthField.getText());
            if (w < 3 || w > 127) {
                throw new IllegalArgumentException(
                        "Width must be between 3-127");
            }
            h = Integer.parseInt(heightField.getText());
            if (h < 3 || h > 127) {
                throw new IllegalArgumentException(
                        "Height must be between 3-127");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Width or height must be valid numbers");
        }

        GomokuConfig config = new GomokuConfig(gameNameField.getText(), w, h,
                5, allowOverlinesCB.isChecked(), threeAndThreeCB.isChecked(),
                fourAndFourCB.isChecked(), swap2CB.isChecked());

        if (gameNameField.getText() == "") {
            throw new IllegalArgumentException("You must provide a game name");
        }

        confirmButton.disable();
        gomokuClient.client.sendTCP(new CreateGamePacket(config));
    }

    @Override
    protected void handleInitialServerData(Connection connection,
            InitialServerDataPacket isdp) {
        ((GameplayState) gomokuClient.getState(GAMEPLAYSTATE)).setInitialData(
                isdp.getBoard(), isdp.getConfig(), isdp.getColor(),
                isdp.getTurn(), isdp.getPlayerList());
        gomokuClient.enterState(GAMEPLAYSTATE);
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
        g.drawString("Width/Height", 20, 90);
        widthField.render(container, g);
        heightField.render(container, g);

        allowOverlinesCB.render(container, g);
        g.drawString("Allow Overlines", 50, 153);
        threeAndThreeCB.render(container, g);
        g.drawString("Three And Three", 50, 183);
        fourAndFourCB.render(container, g);
        g.drawString("Four And Four", 50, 213);
        swap2CB.render(container, g);
        g.drawString("Swap 2 opening", 50, 243);

        // presets to the right
        g.drawString("PRESETS", 600, 20);
        gomokuPresetButton.render(container, g);

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
