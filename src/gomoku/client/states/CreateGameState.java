package gomoku.client.states;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.client.gui.CheckBox;
import gomoku.client.gui.TextField;
import gomoku.logic.GomokuConfig;
import gomoku.net.CreateGamePacket;
import gomoku.net.InitialServerDataPacket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

import com.esotericsoftware.kryonet.Connection;

import static org.trew.log.Log.*;

public class CreateGameState extends GomokuNetworkGameState {

    private TextField gameNameField;
    private TextField widthField;
    private TextField heightField;
    private CheckBox allowOverlinesCB;
    private CheckBox threeAndThreeCB;
    private CheckBox fourAndFourCB;
    private CheckBox swap2CB;
    private Button confirmButton;

    private Button backButton;

    // presets
    private Button preset1Button;
    private GomokuConfig preset1;
    private Button preset2Button;
    private GomokuConfig preset2;
    private Button preset3Button;
    private GomokuConfig preset3;

    private GomokuClient gomokuClient;

    private BounceListener listener;

    private String errorMsg;

    public CreateGameState() {
    }

    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {
        gomokuClient = game;
        listener = new BounceListener(this);

        Image textfield = new Image("res/textfield.png");
        gameNameField = new TextField(container, textfield, container.getDefaultFont(),
                20, 50, 300);
        gameNameField.setCenterX(container.getWidth() / 2);

        widthField = new TextField(container, textfield, container.getDefaultFont(), 220,
                110, 60);
        widthField.setText("15");
        widthField.setCursorPos(2);
        heightField = new TextField(container, textfield, container.getDefaultFont(), 290,
                110, 60);
        heightField.setText("15");
        heightField.setCursorPos(2);
        widthField.setCenterX((container.getWidth() - heightField.getWidth() - 10) / 2);
        heightField.setCenterX((container.getWidth() + widthField.getWidth() + 10) / 2);

        allowOverlinesCB = new CheckBox(250, 200, 25, 25);
        threeAndThreeCB = new CheckBox(250, 230, 25, 25);
        fourAndFourCB = new CheckBox(250, 260, 25, 25);
        swap2CB = new CheckBox(250, 290, 25, 25);

        addListener(allowOverlinesCB);
        addListener(threeAndThreeCB);
        addListener(fourAndFourCB);
        addListener(swap2CB);
        addListener(gameNameField);
        addListener(widthField);
        addListener(heightField);

        initPresets(container);

        Image cgBtn = new Image("res/buttons/creategamebutton.png");
        confirmButton = new Button(cgBtn, 20, 380) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                try {
                    createNewGame();
                } catch (IllegalArgumentException e) {
                    errorMsg = e.getMessage();
                }
            }
        };
        confirmButton.setCenterX(container.getWidth() / 2);

        Image bBtn = new Image("res/buttons/backbutton.png");
        backButton = new Button(bBtn, 250, 500) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (button == 0) {
                    game.enterState(CHOOSEGAMESTATE);
                }
            }
        };
        backButton.setCenterX(container.getWidth() / 2);

        addListener(confirmButton);
        addListener(backButton);
        addListener(preset1Button);
        addListener(preset2Button);
        addListener(preset3Button);
    }

    private void initPresets(GUIContext container) {
        try {
            new FileInputStream("presets/preset1.txt");
            preset1 = new GomokuConfig();
            preset1.load("preset1.txt");
        } catch (FileNotFoundException e) {
        }
        try {
            new FileInputStream("presets/preset2.txt");
            preset2 = new GomokuConfig();
            preset2.load("preset2.txt");
        } catch (FileNotFoundException e) {
        }
        try {
            new FileInputStream("presets/preset3.txt");
            preset3 = new GomokuConfig();
            preset3.load("preset3.txt");
        } catch (FileNotFoundException e) {
        }

        preset1Button = new Button("Preset 1", container.getDefaultFont(), 500, 220) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (preset1 != null && button == 0)
                    applyPreset(preset1);
                else if (button == 1) {
                    try {
                        preset1 = getCurrentConfig();
                        preset1.store("preset1.txt");
                    } catch (IllegalArgumentException e) {
                        info(e.getMessage());
                    }
                }
            }
        };
        preset2Button = new Button("Preset 2", container.getDefaultFont(), 500, 250) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (preset2 != null && button == 0)
                    applyPreset(preset2);
                else if (button == 1) {
                    try {
                        preset2 = getCurrentConfig();
                        preset2.store("preset2.txt");
                    } catch (IllegalArgumentException e) {
                        info(e.getMessage());
                    }
                }
            }
        };
        preset3Button = new Button("Preset 3", container.getDefaultFont(), 500, 280) {
            @Override
            public void buttonClicked(int button, int x, int y) {
                if (preset3 != null && button == 0)
                    applyPreset(preset3);
                else if (button == 1) {
                    try {
                        preset3 = getCurrentConfig();
                        preset3.store("preset3.txt");
                    } catch (IllegalArgumentException e) {
                        info(e.getMessage());
                    }
                }
            }
        };
    }

    private void applyPreset(GomokuConfig config) {
        gameNameField.setText(config.getName());
        widthField.setText(String.valueOf(config.getWidth()));
        heightField.setText(String.valueOf(config.getHeight()));

        allowOverlinesCB.setChecked(config.getAllowOverlines());
        threeAndThreeCB.setChecked(config.useThreeAndThree());
        fourAndFourCB.setChecked(config.useFourAndFour());
        swap2CB.setChecked(config.useSwap2());
    }

    public GomokuConfig getCurrentConfig() {
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
        return new GomokuConfig(gameNameField.getText(), w, h,
                5, allowOverlinesCB.isChecked(), threeAndThreeCB.isChecked(),
                fourAndFourCB.isChecked(), swap2CB.isChecked());
    }

    public void createNewGame() {
        GomokuConfig config = getCurrentConfig();

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
        g.drawImage(game.getBackground(), 0, 0);

        drawCenteredString("Game Name", 20, container, g);
        gameNameField.render(container, g);
        drawCenteredString("Width / Height", 90, container, g);
        widthField.render(container, g);
        heightField.render(container, g);

        allowOverlinesCB.render(container, g);
        g.drawString("Allow Overlines", 285, 203);
        threeAndThreeCB.render(container, g);
        g.drawString("Three And Three", 285, 233);
        fourAndFourCB.render(container, g);
        g.drawString("Four And Four", 285, 263);
        swap2CB.render(container, g);
        g.drawString("Swap 2 opening", 285, 293);

        // presets to the right
        g.drawString("PRESETS", 500, 220);
        preset1Button.render(container, g);
        preset2Button.render(container, g);
        preset3Button.render(container, g);

        if (errorMsg != null && errorMsg != "") {
            drawCenteredString(errorMsg, 450, container, g);
        }
        confirmButton.render(container, g);
        backButton.render(container, g);
    }

    @Override
    public int getID() {
        return CREATEGAMESTATE;
    }

}
