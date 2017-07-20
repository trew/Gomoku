package se.samuelandersson.gomoku.client.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;

import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.ComboBox;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.ToggleButton;
import de.matthiasmann.twl.model.SimpleChangableListModel;
import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.net.CreateGamePacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;

public class CreateGameState extends AbstractNetworkGameState
{
  private static final Logger log = LoggerFactory.getLogger(CreateGameState.class);

  private EditField gameNameField;
  SimpleChangableListModel<Integer> widthBoxModel;
  SimpleChangableListModel<Integer> heightBoxModel;
  private ComboBox<Integer> widthBox;
  private ComboBox<Integer> heightBox;
  private ToggleButton allowOverlinesCB;
  private ToggleButton threeAndThreeCB;
  private ToggleButton fourAndFourCB;
  private Button confirmButton;

  private Button backButton;

  private GomokuClient gomokuClient;

  private String errorMsg;

  public CreateGameState()
  {
  }

  @Override
  public RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    gameNameField = new EditField();
    gameNameField.setPosition(250, 50);
    gameNameField.setSize(300, 30);
    rp.add(gameNameField);

    widthBoxModel = new SimpleChangableListModel<Integer>();
    heightBoxModel = new SimpleChangableListModel<Integer>();
    for (int i = 5; i <= 40; i++)
    {
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
    allowOverlinesCB.setTooltipContent("Allowing overlines means you\n" + "can win by having 6 or more\n" +
                                       "stones in a row.");

    threeAndThreeCB = new ToggleButton("Three And Three");
    threeAndThreeCB.setPosition(250, 235);
    threeAndThreeCB.setSize(130, 30);
    threeAndThreeCB.setTooltipContent("Three and three means you are not\n" + "allowed to place a stone with which\n" +
                                      "you create two open rows of 3.");

    fourAndFourCB = new ToggleButton("Four And Four");
    fourAndFourCB.setPosition(250, 270);
    fourAndFourCB.setSize(130, 30);
    fourAndFourCB.setTooltipContent("Four and four means you are not\n" + "allowed to place a stone with which\n" +
                                    "you create two rows of 4.");

    rp.add(allowOverlinesCB);
    rp.add(threeAndThreeCB);
    rp.add(fourAndFourCB);

    confirmButton = new Button("Create Game!");
    confirmButton.setPosition(250, 380);
    confirmButton.setSize(300, 60);
    rp.add(confirmButton);

    backButton = new Button("Back");
    backButton.setPosition(250, 500);
    backButton.setSize(300, 60);

    rp.add(backButton);

    return rp;
  }

  @Override
  public void init(GameContainer container, final GomokuClient game) throws SlickException
  {
    gomokuClient = game;

    confirmButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          createNewGame();
        }
        catch (IllegalArgumentException e)
        {
          log.error("Error creating game", e);
          errorMsg = e.getMessage();
        }
      }
    });

    backButton.addCallback(new Runnable()
    {
      @Override
      public void run()
      {
        enterState(CHOOSEGAMESTATE);
      }
    });
  }

  public GomokuConfig getCurrentConfig()
  {
    int w = widthBoxModel.getEntry(widthBox.getSelected());
    int h = heightBoxModel.getEntry(heightBox.getSelected());

    return new GomokuConfig(gameNameField.getText().trim(),
                            w,
                            h,
                            5,
                            allowOverlinesCB.isActive(),
                            threeAndThreeCB.isActive(),
                            fourAndFourCB.isActive());
  }

  public void createNewGame()
  {
    GomokuConfig config = getCurrentConfig();

    if (gameNameField.getText().trim().equals(""))
    {
      throw new IllegalArgumentException("You must provide a game name");
    }

    confirmButton.setEnabled(false);
    gomokuClient.getNetworkClient().sendTCP(new CreateGamePacket(config));
  }

  @Override
  protected void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
  {
    confirmButton.setEnabled(true);
    ((GameplayState) gomokuClient.getState(GAMEPLAYSTATE)).setInitialData(isdp.getBoard(),
                                                                          isdp.getConfig(),
                                                                          isdp.getID(),
                                                                          isdp.getTurn(),
                                                                          isdp.getPlayerList(),
                                                                          isdp.getPlayerOneColor(),
                                                                          isdp.getPlayerTwoColor());
    enterState(GAMEPLAYSTATE);
  }

  @Override
  public void update(GameContainer container, GomokuClient game, int delta) throws SlickException
  {
  }

  @Override
  public void render(GameContainer container, GomokuClient game, Graphics g) throws SlickException
  {
    drawCenteredString("Game Name", 20, container, g);
    drawCenteredString("Width / Height", 90, container, g);

    if (errorMsg != null && errorMsg != "")
    {
      drawCenteredString(errorMsg, 450, container, g);
    }
  }

  @Override
  public int getID()
  {
    return CREATEGAMESTATE;
  }

}
