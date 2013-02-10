package gomoku.client.states;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import gomoku.client.GomokuClient;
import gomoku.logic.GomokuConfig;
import gomoku.net.CreateGamePacket;
import gomoku.net.InitialServerDataPacket;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;

import TWLSlick.RootPane;

import com.esotericsoftware.kryonet.Connection;

import de.matthiasmann.twl.Button;
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
    private Button preset1Save;
    private GomokuConfig preset1;
    private Button preset2Button;
    private Button preset2Save;
    private GomokuConfig preset2;
    private Button preset3Button;
    private Button preset3Save;
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
        gameNameField.setSize(300, 30);
        rp.add(gameNameField);

        widthBoxModel = new SimpleChangableListModel<Integer>();
        heightBoxModel = new SimpleChangableListModel<Integer>();
        for (int i = 5; i <= 40; i++) {
            widthBoxModel.addElement(i);
            heightBoxModel.addElement(i);
        }

        widthBox = new ComboBox<Integer>(widthBoxModel);
        widthBox.setPosition(330, 110);
        widthBox.setSize(60, 30);
        widthBox.setSelected(widthBoxModel.findElement(Integer.valueOf(15)));

        heightBox = new ComboBox<Integer>(heightBoxModel);
        heightBox.setPosition(410, 110);
        heightBox.setSize(60, 30);
        heightBox.setSelected(widthBoxModel.findElement(Integer.valueOf(15)));

        rp.add(widthBox);
        rp.add(heightBox);

        allowOverlinesCB = new ToggleButton("Allow Overlines");
        allowOverlinesCB.setPosition(250, 200);
        allowOverlinesCB.setSize(130, 30);
        allowOverlinesCB.setTooltipContent("Allowing overlines means you\n"
                + "can win by having 6 or more\n" + "stones in a row.");

        threeAndThreeCB = new ToggleButton("Three And Three");
        threeAndThreeCB.setPosition(250, 235);
        threeAndThreeCB.setSize(130, 30);
        threeAndThreeCB.setTooltipContent("Three and three means you are not\n"
                + "allowed to place a stone with which\n"
                + "you create two open rows of 3.");

        fourAndFourCB = new ToggleButton("Four And Four");
        fourAndFourCB.setPosition(250, 270);
        fourAndFourCB.setSize(130, 30);
        fourAndFourCB.setTooltipContent("Four and four means you are not\n"
                + "allowed to place a stone with which\n"
                + "you create two rows of 4.");

        swap2CB = new ToggleButton("Swap 2 Rule");
        swap2CB.setPosition(250, 305);
        swap2CB.setSize(130, 30);
        swap2CB.setTooltipContent("In a swap 2 opening, the first player places\n"
                + "3 stones. The second player can now choose to\n"
                + "pick color, or place 2 more stones. If he places\n"
                + "two more stones, the first player will choose color.");

        rp.add(allowOverlinesCB);
        rp.add(threeAndThreeCB);
        rp.add(fourAndFourCB);
        rp.add(swap2CB);

        preset1Button = new Button("Preset 1");
        preset1Button.setPosition(420, 220);
        preset1Button.setSize(55, 30);
        preset2Button = new Button("Preset 2");
        preset2Button.setPosition(420, 260);
        preset2Button.setSize(55, 30);
        preset3Button = new Button("Preset 3");
        preset3Button.setPosition(420, 300);
        preset3Button.setSize(55, 30);
        preset1Save = new Button("Save");
        preset1Save.setPosition(500, 220);
        preset1Save.setSize(32, 30);
        preset1Save.setTooltipContent("Saves the current configuration");
        preset2Save = new Button("Save");
        preset2Save.setPosition(500, 260);
        preset2Save.setSize(32, 30);
        preset2Save.setTooltipContent("Saves the current configuration");
        preset3Save = new Button("Save");
        preset3Save.setPosition(500, 300);
        preset3Save.setSize(32, 30);
        preset3Save.setTooltipContent("Saves the current configuration");
        rp.add(preset1Button);
        rp.add(preset2Button);
        rp.add(preset3Button);
        rp.add(preset1Save);
        rp.add(preset2Save);
        rp.add(preset3Save);

        confirmButton = new Button("Create Game!");
        confirmButton.setPosition(250,  380);
        confirmButton.setSize(300, 60);
        rp.add(confirmButton);

        backButton = new Button("Back");
        backButton.setPosition(250, 500);
        backButton.setSize(300, 60);

        rp.add(backButton);

        return rp;
    }

    @Override
    public void init(GameContainer container, final GomokuClient game)
            throws SlickException {
        gomokuClient = game;

        initPresets(container);

        confirmButton.addCallback(new Runnable() {
            @Override
            public void run() {
                try {
                    createNewGame();
                } catch (IllegalArgumentException e) {
                    errorMsg = e.getMessage();
                }
            }
        });

        backButton.addCallback(new Runnable() {
            @Override
            public void run() {
                enterState(CHOOSEGAMESTATE);
            }
        });
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

        preset1Button.addCallback(new Runnable() {
            @Override
            public void run() {
                if (preset1 != null)
                    applyPreset(preset1);
            }
        });
        preset2Button.addCallback(new Runnable() {
            @Override
            public void run() {
                if (preset2 != null)
                    applyPreset(preset2);
            }
        });
        preset3Button.addCallback(new Runnable() {
            @Override
            public void run() {
                if (preset3 != null)
                    applyPreset(preset3);
            }
        });
        preset1Save.addCallback(new Runnable() {
            @Override
            public void run() {
                try {
                    preset1 = getCurrentConfig();
                    preset1.store("preset1.txt");
                } catch (IllegalArgumentException e) {
                    info(e.getMessage());
                }
            }
        });
        preset2Save.addCallback(new Runnable() {
            @Override
            public void run() {
                try {
                    preset2 = getCurrentConfig();
                    preset2.store("preset2.txt");
                } catch (IllegalArgumentException e) {
                    info(e.getMessage());
                }
            }
        });
        preset3Save.addCallback(new Runnable() {
            @Override
            public void run() {
                try {
                    preset3 = getCurrentConfig();
                    preset3.store("preset3.txt");
                } catch (IllegalArgumentException e) {
                    info(e.getMessage());
                }
            }
        });
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

        confirmButton.setEnabled(false);
        gomokuClient.client.sendTCP(new CreateGamePacket(config));
    }

    @Override
    protected void handleInitialServerData(Connection connection,
            InitialServerDataPacket isdp) {
        confirmButton.setEnabled(true);
        ((GameplayState) gomokuClient.getState(GAMEPLAYSTATE)).setInitialData(
                isdp.getBoard(), isdp.getConfig(), isdp.getSwap2State(),
                isdp.getID(), isdp.getTurn(), isdp.getPlayerList(),
                isdp.getPlayerOneColor(), isdp.getPlayerTwoColor());
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

        if (errorMsg != null && errorMsg != "") {
            drawCenteredString(errorMsg, 450, container, g);
        }
    }

    @Override
    public int getID() {
        return CREATEGAMESTATE;
    }

}
