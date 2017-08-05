package se.samuelandersson.gomoku.client.states;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Connection;

import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.net.PacketHandler;
import se.samuelandersson.gomoku.net.CreateGamePacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;

public class CreateGameState extends MenuState implements PacketHandler
{
  private TextField gameNameField;
  private SelectBox<Integer> widthBox;
  private SelectBox<Integer> heightBox;
  private CheckBox allowOverlinesCB;
  private CheckBox threeAndThreeCB;
  private CheckBox fourAndFourCB;
  private Button confirmButton;

  private Button backButton;

  private Label gameNameLabel;
  private Label whLabel;

  public CreateGameState(GomokuClient app)
  {
    super(app);
  }

  @Override
  public void initialize()
  {
    super.initialize();

    Skin skin = Assets.getInstance().getSkin();

    gameNameField = new TextField("game", skin);

    List<Integer> widthItems = new ArrayList<>();
    List<Integer> heightItems = new ArrayList<>();

    for (int i = 5; i <= 40; i++)
    {
      widthItems.add(i);
      heightItems.add(i);
    }

    widthBox = new SelectBox<Integer>(skin);
    widthBox.setItems(widthItems.toArray(new Integer[widthItems.size()]));
    widthBox.setSelected(Integer.valueOf(15));

    heightBox = new SelectBox<Integer>(skin);
    heightBox.setItems(heightItems.toArray(new Integer[heightItems.size()]));
    heightBox.setSelected(Integer.valueOf(15));

    allowOverlinesCB = new CheckBox("Allow Overlines", skin);
    allowOverlinesCB.align(Align.left);
    allowOverlinesCB.getLabelCell().spaceLeft(10);
    allowOverlinesCB.addListener(new TextTooltip("Allowing overlines means you can win by having 6 or more stones in a row",
                                                 skin));

    threeAndThreeCB = new CheckBox("Three And Three", skin);
    threeAndThreeCB.align(Align.left);
    threeAndThreeCB.getLabelCell().spaceLeft(10);
    threeAndThreeCB.addListener(new TextTooltip("Three and three means you are not allowed to place a stone with which you create two open rows of 3.",
                                                skin));

    fourAndFourCB = new CheckBox("Four And Four", skin);
    fourAndFourCB.align(Align.left);
    fourAndFourCB.getLabelCell().spaceLeft(10);
    fourAndFourCB.addListener(new TextTooltip("Four and four means you are not allowed to place a stone with which you create two open rows of 4.",
                                              skin));

    confirmButton = new TextButton("Create Game!", skin);
    confirmButton.setColor(Color.GREEN);
    confirmButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        createNewGame();
      }
    });

    backButton = new TextButton("Back", skin);
    backButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        GomokuClient app = CreateGameState.this.getApplication();
        if (app.getServer() != null)
        {
          app.getClient().disconnect();
          app.stopServer();
          app.setNextState(JoinOrStartServerState.class);
        }
        else
        {
          app.setNextState(ChooseGameState.class);
        }
      }
    });

    gameNameLabel = new Label("Game Name", skin);
    whLabel = new Label("Width / Height", skin);

    Table widthHeightBoxContainer = new Table(skin);
    widthHeightBoxContainer.defaults().grow().spaceLeft(5).spaceRight(5);
    widthHeightBoxContainer.add(widthBox);
    widthHeightBoxContainer.add(heightBox);

    this.getTable().add(gameNameLabel);
    this.getTable().add(gameNameField);
    this.getTable().row();
    this.getTable().add(whLabel);
    this.getTable().add(widthHeightBoxContainer);
    this.getTable().row();
    this.getTable().add(allowOverlinesCB).colspan(2);
    this.getTable().row();
    this.getTable().add(threeAndThreeCB).colspan(2);
    this.getTable().row();
    this.getTable().add(fourAndFourCB).colspan(2);
    this.getTable().row();
    this.getTable().add(confirmButton);
    this.getTable().add(backButton);

    this.getStage().addActor(this.getTable());

  }

  public GomokuConfig getCurrentConfig()
  {
    int w = widthBox.getSelected().intValue();
    int h = widthBox.getSelected().intValue();

    return new GomokuConfig(gameNameField.getText().trim(),
                            w,
                            h,
                            5,
                            allowOverlinesCB.isChecked(),
                            threeAndThreeCB.isChecked(),
                            fourAndFourCB.isChecked());
  }

  public void createNewGame()
  {
    GomokuConfig config = getCurrentConfig();

    if (config.getName().equals(""))
    {
      throw new IllegalArgumentException("You must provide a game name");
    }

    confirmButton.setDisabled(true);
    this.getApplication().getClient().sendTCP(new CreateGamePacket(config));
  }

  @Override
  public void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
  {
    confirmButton.setDisabled(false);
    final GomokuClient app = CreateGameState.this.getApplication();
    GameplayState state = app.getState(GameplayState.class);
    state.setInitialData(isdp);
    app.setNextState(state);
  }
}
