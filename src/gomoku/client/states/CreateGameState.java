package gomoku.client.states;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gomoku.client.GomokuClient;
import gomoku.client.gui.Button;
import gomoku.logic.GomokuConfig;
import gomoku.net.CreateGamePacket;
import gomoku.net.InitialServerDataPacket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

import TWLSlick.RootPane;

import com.esotericsoftware.kryonet.Connection;

import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.model.SimpleChangableListModel;

import static org.trew.log.Log.*;

public class CreateGameState extends GomokuNetworkGameState {

    private EditField gameNameField;
    SimpleChangableListModel<Integer> widthBoxModel;
    SimpleChangableListModel<Integer> heightBoxModel;
    private ComboBox<Integer> widthBox;
    private ComboBox<Integer> heightBox;
    private ToggleButton allowOverlinesCB;
    private ToggleButton threeAndThreeCB;
    private ToggleButton fourAndFourCB;
    private ToggleButton swap2CB;
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

    private String errorMsg;

    public CreateGameState() {
    }

    @Override
    public RootPane createRootPane() {
        RootPane rp = super.createRootPane();

        gameNameField = new EditField();
        gameNameField.setPosition(250, 50);
        gameNameField.setSize(300, 20);
        rp.add(gameNameField);

        widthBoxModel = new SimpleChangableListModel<Integer>();
        heightBoxModel = new SimpleChangableListModel<Integer>();
        for (int i = 5; i <= 40; i++) {
            widthBoxModel.addElement(i);
            heightBoxModel.addElement(i);
        }

        widthBox = new ComboBox<Integer>(widthBoxModel);
        widthBox.setPosition(330, 110);
        widthBox.setSize(60, 20);
        widthBox.setSelected(widthBoxModel.findElement(Integer.valueOf(15)));

        heightBox = new ComboBox<Integer>(heightBoxModel);
        heightBox.setPosition(400, 110);
        heightBox.setSize(60, 20);
        heightBox.setSelected(widthBoxModel.findElement(Integer.valueOf(15)));

        rp.add(widthBox);
        rp.add(heightBox);

        allowOverlinesCB = new ToggleButton("Allow Overlines");
        allowOverlinesCB.setPosition(250, 200);
        allowOverlinesCB.setSize(130, 20);

        threeAndThreeCB = new ToggleButton("Three And Three");
        threeAndThreeCB.setPosition(250, 230);
        threeAndThreeCB.setSize(130, 20);

        fourAndFourCB = new ToggleButton("Four And Four");
        fourAndFourCB.setPosition(250, 260);
        fourAndFourCB.setSize(130, 20);

        swap2CB = new ToggleButton("Swap 2 Rule");
        swap2CB.setPosition(250, 290);
        swap2CB.setSize(130, 20);

        rp.add(allowOverlinesCB);
        rp.add(threeAndThreeCB);
        rp.add(fourAndFourCB);
        rp.add(swap2CB);

        return rp;
    }

    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {
        gomokuClient = game;

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
                    enterState(CHOOSEGAMESTATE);
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

        preset1Button = new Button("Preset 1", container.getDefaultFont(), 500,
                220) {
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
        preset2Button = new Button("Preset 2", container.getDefaultFont(), 500,
                250) {
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
        preset3Button = new Button("Preset 3", container.getDefaultFont(), 500,
                280) {
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
        widthBox.setSelected(widthBoxModel.findElement(new Integer(String
                .valueOf(config.getWidth()))));
        heightBox.setSelected(heightBoxModel.findElement(new Integer(String
                .valueOf(config.getHeight()))));

        allowOverlinesCB.setActive(config.getAllowOverlines());
        threeAndThreeCB.setActive(config.useThreeAndThree());
        fourAndFourCB.setActive(config.useFourAndFour());
        swap2CB.setActive(config.useSwap2());
    }

    public GomokuConfig getCurrentConfig() {
        int w = widthBoxModel.getEntry(widthBox.getSelected());
        int h = heightBoxModel.getEntry(heightBox.getSelected());
        return new GomokuConfig(gameNameField.getText().trim(), w, h, 5,
                allowOverlinesCB.isActive(), threeAndThreeCB.isActive(),
                fourAndFourCB.isActive(), swap2CB.isActive());
    }

    public void createNewGame() {
        GomokuConfig config = getCurrentConfig();

        if (gameNameField.getText().trim().equals("")) {
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
        enterState(GAMEPLAYSTATE);
    }

    @Override
    public void update(GameContainer container, GomokuClient game, int delta)
            throws SlickException {
    }

    @Override
    public void render(GameContainer container, GomokuClient game, Graphics g)
            throws SlickException {
        drawCenteredString("Game Name", 20, container, g);
        drawCenteredString("Width / Height", 90, container, g);

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
