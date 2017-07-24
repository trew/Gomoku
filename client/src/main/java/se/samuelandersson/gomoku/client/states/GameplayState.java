package se.samuelandersson.gomoku.client.states;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Connection;

import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.GomokuGameListener;
import se.samuelandersson.gomoku.Player;
import se.samuelandersson.gomoku.action.BoardAction;
import se.samuelandersson.gomoku.action.GameAction;
import se.samuelandersson.gomoku.action.PlacePieceAction;
import se.samuelandersson.gomoku.client.Assets;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.Settings;
import se.samuelandersson.gomoku.client.Settings.Setting;
import se.samuelandersson.gomoku.client.entities.BoardEntity;
import se.samuelandersson.gomoku.client.entities.BoardEvent;
import se.samuelandersson.gomoku.client.net.PacketHandler;
import se.samuelandersson.gomoku.event.GameEvent;
import se.samuelandersson.gomoku.event.SetPlayerTurnEvent;
import se.samuelandersson.gomoku.exception.IllegalActionException;
import se.samuelandersson.gomoku.impl.GomokuGameImpl;
import se.samuelandersson.gomoku.net.BoardPacket;
import se.samuelandersson.gomoku.net.GameActionPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.NotifyTurnPacket;
import se.samuelandersson.gomoku.net.PlayerListPacket;
import se.samuelandersson.gomoku.net.Request;
import se.samuelandersson.gomoku.net.VictoryPacket;

/**
 * The playing state of the Gomoku game.
 *
 * @author Samuel Andersson
 */
public class GameplayState extends AbstractGameState implements PacketHandler, GomokuGameListener
{
  private static final Logger log = LoggerFactory.getLogger(GameplayState.class);

  /** Contains the game logic */
  public GomokuGame gomokuGame;

  /** The player */
  public Player me;

  /** The board displayed */
  private ScrollPane boardPane;
  private BoardEntity boardEntity;

  private Image versus;
  private Container<Label> nametag1;
  private Container<Label> nametag2;
  private Container<Container<Label>> statusBar;

  private Table infoTable;
  private Label gameNameInfoLabel;
  private Label boardInfoLabel;
  private com.badlogic.gdx.scenes.scene2d.ui.List<String> playerListInfoList;
  private Button confirmButton;

  private Container<Label> messageLabelContainer;
  private Label messageLabel;
  private Label nametag1Label;
  private Label nametag2Label;

  private boolean loading;

  private List<Player> playerList;

  private com.badlogic.gdx.graphics.Color previousMsgColor;
  private String previousMsg;
  private float msgTimer;

  private GameAction pendingAction;

  public GameplayState(GomokuClient app)
  {
    super(app);
  }

  public boolean initialLoading()
  {
    return loading;
  }

  public void setInitialData(InitialServerDataPacket isdp)
  {
    this.setInitialData(isdp.getBoard(),
                        isdp.getConfig(),
                        isdp.getPlayerColor(),
                        isdp.getPlayerColorCurrentTurn(),
                        isdp.getPlayerList());
  }
  
  public void setInitialData(GomokuBoard board,
                             GomokuConfig config,
                             Color playerColor,
                             Color playerColorCurrentTurn,
                             List<Player> playerList)
  {
    // create a new game
    gomokuGame = new GomokuGameImpl(board, config);
    gomokuGame.addListener(this);

    setupPlayers(playerList, playerColor);

    if (playerColorCurrentTurn == Color.BLACK)
    {
      gomokuGame.setCurrentTurnPlayer(gomokuGame.getPlayerOne());
    }
    else if (playerColorCurrentTurn == Color.WHITE)
    {
      gomokuGame.setCurrentTurnPlayer(gomokuGame.getPlayerTwo());
    }
    
    setBoard(board);
    
    this.gameNameInfoLabel.setText(config.getName());
    loading = false;
    pendingAction = null;
    boardEntity.setTouchable(Touchable.enabled);
    
    this.updateInfoTable();
  }

  /**
   * Setup names and values for players. If this client receives black, makes
   * sure the white player receives the correct name.
   *
   * @param playerColor The player color being given to this client
   */
  public void setupPlayers(List<Player> playerList, Color playerColor)
  {
    this.playerList = new ArrayList<>(playerList);
    final String playerName = Settings.getInstance().getProperty(Setting.PLAYER_NAME, "(none)");

    for (Player player : this.playerList)
    {
      if (player.getColor() == Color.BLACK)
      {
        gomokuGame.getPlayerOne().setFrom(player);
      }
      if (player.getColor() == Color.WHITE)
      {
        gomokuGame.getPlayerTwo().setFrom(player);
      }
    }

    if (playerColor == Color.BLACK)
    {
      me = gomokuGame.getPlayerOne();
    }
    else if (playerColor == Color.WHITE)
    {
      me = gomokuGame.getPlayerTwo();
    }
    else
    {
      me = new Player("", Color.NONE);
    }

    me.setName(playerName);

    updatePlayerListInfoList();
  }

  private void updatePlayerListInfoList()
  {
    this.playerListInfoList.clearItems();
    String[] items = new String[this.playerList.size()];

    this.nametag1Label.setText("(none)");
    this.nametag2Label.setText("(none)");

    int i = 0;
    for (Player player : this.playerList)
    {
      items[i++] = player.getName();
      if (player.getColor() == Color.BLACK)
      {
        this.nametag1Label.setText(player.getName());
      }
      if (player.getColor() == Color.WHITE)
      {
        this.nametag2Label.setText(player.getName());
      }
    }
    this.playerListInfoList.setItems(items);
    this.playerListInfoList.setSelectedIndex(-1);
  }

  public void setPlayerList(List<Player> playerList)
  {
    this.playerList = new ArrayList<>(playerList);
    updatePlayerListInfoList();

    // if I'm player 1, do not update my name from the server. I *should*
    // know my name better that the server.
    for (Player player : playerList)
    {
      if (player.getColor() == Color.BLACK)
      {
        this.nametag1Label.setText(player.getName());
        gomokuGame.getPlayerOne().setFrom(player);
      }
      if (player.getColor() == Color.WHITE)
      {
        this.nametag2Label.setText(player.getName());
        gomokuGame.getPlayerTwo().setFrom(player);
      }
    }
  }

  @Override
  public void initialize()
  {
    super.initialize();

    loading = true; // will be set to false once we receive data from the server
    pendingAction = null;

    this.boardEntity = new BoardEntity(this.getApplication().getShapeRenderer());
    this.boardEntity.setSize(450, 450);

    Skin skin = Assets.getInstance().getSkin();

    this.boardPane = new ScrollPane(this.boardEntity, skin);
    this.boardPane.setPosition(55, 68);
    this.boardPane.setSize(455, 455);
    this.boardPane.setFadeScrollBars(false);

    this.getStage().addActor(this.boardPane);

    this.boardEntity.addListener(new EventListener()
    {
      @Override
      public boolean handle(Event event)
      {
        if (event instanceof BoardEvent)
        {
          BoardEvent be = (BoardEvent) event;
          if (me == null || !myTurn())
          {
            return false;
          }

          placePiece(be.getX(), be.getY());
          return true;
        }

        return false;
      }
    });

    versus = new Image(Assets.getInstance().getDrawable("versus"));
    versus.setPosition(400 - 117 / 2, GomokuClient.HEIGHT - versus.getHeight());
    infoTable = new Table(skin);
    infoTable.setBackground(Assets.getInstance().getDrawable("infobar"));
    infoTable.setSize(225, 454);
    infoTable.setPosition(GomokuClient.WIDTH - 225, 454 / 2 - 160);

    this.gameNameInfoLabel = new Label("(none)", skin);
    this.boardInfoLabel = new Label("0x0", skin);
    this.playerListInfoList = new com.badlogic.gdx.scenes.scene2d.ui.List<>(skin);
    this.playerListInfoList.setTouchable(Touchable.disabled);

    confirmButton = new TextButton("Confirm move?", skin);
    confirmButton.setColor(0, 1, 0, 1);
    confirmButton.addListener(new ChangeListener()
    {
      @Override
      public void changed(ChangeEvent event, Actor actor)
      {
        if (GameplayState.this.pendingAction != null)
        {
          GameplayState.this.pendingAction.confirmAction(gomokuGame);
          confirmAndSendGameAction(GameplayState.this.pendingAction);
          GameplayState.this.setPendingAction(null);
        }
      }
    });
    confirmButton.setVisible(false);

    messageLabel = new Label("message", skin);
    messageLabel.setColor(0, 0, 0, 1);
    this.messageLabelContainer = new Container<Label>(messageLabel);
    this.messageLabelContainer.setTransform(true);

    statusBar = new Container<Container<Label>>(this.messageLabelContainer);
    statusBar.setBackground(Assets.getInstance().getDrawable("bottommessagebox"));
    statusBar.setSize(800, 63);
    statusBar.setPosition(0, 0);

    nametag1Label = new Label("(none)", skin);
    nametag1Label.setColor(0, 0, 0, 1);

    nametag2Label = new Label("(none)", skin);
    nametag2Label.setColor(0, 0, 0, 1);

    nametag1 = new Container<Label>(nametag1Label);
    nametag1.setBackground(Assets.getInstance().getDrawable("nametag"));
    nametag1.setSize(290, 60);
    nametag1.setPosition(20, GomokuClient.HEIGHT - nametag1.getHeight() - 10);
    nametag2 = new Container<Label>(nametag2Label);
    nametag2.setBackground(Assets.getInstance().getDrawable("nametag"));
    nametag2.setSize(290, 60);
    nametag2.setPosition(GomokuClient.WIDTH - 20 - nametag2.getWidth(),
                         GomokuClient.HEIGHT - nametag2.getHeight() - 10);

    this.getStage().addActor(versus);
    this.getStage().addActor(nametag1);
    this.getStage().addActor(nametag2);
    this.getStage().addActor(infoTable);
    this.getStage().addActor(statusBar);
  }

  @Override
  public void render()
  {
    super.render();

    //    this.boardEntity.drawLines();
  }

  private void setPendingAction(GameAction action)
  {
    this.pendingAction = action;
    this.confirmButton.setVisible(this.pendingAction != null);
  }

  /**
   * Try to place a new piece on provided position. If successful client-side, send a packet to server trying to do the
   * same thing.
   *
   * @param gomokuClient The game which we place the piece in
   * @param x The x location for the new piece
   * @param y The y location for the new piece
   */
  public void placePiece(int x, int y)
  {
    final boolean requireConfirm = Settings.getInstance().getBoolean(Setting.CONFIRM);

    try
    {
      if (this.pendingAction != null)
      {
        this.pendingAction.undoAction(gomokuGame);

        // just assume it's a board action...
        BoardAction boardAction = (BoardAction) pendingAction;
        // Is it the current piece?
        if (boardAction.getX() == x && boardAction.getY() == y)
        {
          this.setPendingAction(null);
          return; // don't place a new one
        }
      }

      GameAction pendingAction = new PlacePieceAction(me.getColor(), x, y);
      pendingAction.doAction(gomokuGame);

      if (!requireConfirm)
      {
        confirmAndSendGameAction(pendingAction);
      }
      else
      {
        this.setPendingAction(pendingAction);
        this.confirmButton.setVisible(true);
      }
    }
    catch (IllegalActionException e)
    {
      this.setPendingAction(null);
      this.setMessage(e.getMessage(), com.badlogic.gdx.graphics.Color.RED, 5);
    }
  }

  protected void confirmAndSendGameAction(GameAction action)
  {
    if (!action.isConfirmed())
    {
      if (log.isDebugEnabled())
      {
        log.debug("Confirming GameAction: {}", action);
      }

      action.confirmAction(gomokuGame);
    }

    this.getApplication().getClient().sendTCP(new GameActionPacket(action));
  }

  /**
   * Whether it's our turn or not
   *
   * @return True if it's our turn
   */
  public boolean myTurn()
  {
    return me == gomokuGame.getCurrentTurnPlayer();
  }

  protected void setBoard(GomokuBoard board)
  {
    boardEntity.setBoard(board);
    this.boardInfoLabel.setText(String.format("%sx%s",
                                              this.gomokuGame.getBoard().getWidth(),
                                              this.gomokuGame.getBoard().getHeight()));
  }

  private void updateInfoTable()
  {
    this.infoTable.clearChildren();
    this.infoTable.align(Align.top);
    this.infoTable.pad(10);

    this.infoTable.columnDefaults(0).align(Align.right);
    this.infoTable.columnDefaults(1).align(Align.left).growX().padLeft(10);

    Skin skin = Assets.getInstance().getSkin();
    this.infoTable.add(new Label("Game Name:", skin));
    this.infoTable.add(this.gameNameInfoLabel);
    this.infoTable.row();
    this.infoTable.add(new Label("Your name:", skin));
    this.infoTable.add(new Label(me.getName(), skin));
    this.infoTable.row();
    this.infoTable.add(new Label("Your color:", skin));
    this.infoTable.add(new Label(me.getColor().getName(), skin));
    this.infoTable.row();
    this.infoTable.add(new Label("Board size:", skin));
    this.infoTable.add(this.boardInfoLabel);
    this.infoTable.row().padTop(30);
    this.infoTable.add(new Label("Connected players", skin)).center().colspan(2);
    this.infoTable.row();

    this.infoTable.add(this.playerListInfoList).center().colspan(2);

    this.infoTable.row();
    this.infoTable.add().colspan(2).growY();
    this.infoTable.row();

    this.infoTable.add(this.confirmButton).bottom().center().colspan(2);
  }

  @Override
  public boolean keyUp(int keycode)
  {
    if (keycode == Input.Keys.ESCAPE)
    {
      if (pendingAction != null)
      {
        pendingAction.undoAction(gomokuGame);
        this.setPendingAction(null);
      }
      else
      {
        this.getApplication().setNextState(this.getApplication().getState(PauseMenu.class), true, true);
      }

      return true;
    }
    else if (keycode == Input.Keys.F5)
    {
      if (this.getApplication().getClient().isConnected())
      {
        this.getApplication().getClient().sendTCP(Request.UPDATE_BOARD);
        return true;
      }
    }

    return false;
  }

  @Override
  public void update(float delta)
  {
    super.update(delta);

    if (msgTimer > 0)
    {
      msgTimer -= delta;

      if (msgTimer < 0)
      {
        this.messageLabel.setColor(this.previousMsgColor);
        this.messageLabel.setText(this.previousMsg);

        this.previousMsg = null;
        this.previousMsgColor = null;
      }
    }
  }

  @Override
  public void handleGameAction(Connection conn, GameActionPacket ppp)
  {
    try
    {
      log.debug("Received GameAction of type " + ppp.getAction().getClass().getSimpleName());
      ppp.getAction().doAction(gomokuGame);
      ppp.getAction().confirmAction(gomokuGame);
    }
    catch (IllegalActionException e)
    {
      log.warn("Illegal Action from server: ", e);
      this.getApplication().getClient().sendTCP(conn, Request.UPDATE_BOARD);
    }
  }

  @Override
  public void handleBoard(Connection conn, BoardPacket bp)
  {
    // let's update our board with the board of the server
    gomokuGame.getBoard().setFrom(bp.getBoard());
  }

  @Override
  public void handleNotifyTurn(Connection conn, NotifyTurnPacket ntp)
  {
    gomokuGame.setCurrentTurnPlayer(gomokuGame.getPlayer(ntp.getID()));
    log.info("Notified about turn: " + gomokuGame.getCurrentTurnPlayer().getName());
  }

  @Override
  public void handlePlayerList(Connection conn, PlayerListPacket plp)
  {
    log.debug("Updated playerlist");
    setPlayerList(plp.getPlayerList());
  }

  private void setMessage(String text, com.badlogic.gdx.graphics.Color color, float timeout)
  {
    if (timeout >= 0)
    {
      if (this.previousMsg == null)
      {
        this.previousMsgColor = this.messageLabel.getColor().cpy();
        this.previousMsg = this.messageLabel.getText().toString();
      }

      this.msgTimer = timeout;
      this.messageLabelContainer.addAction(Actions.sequence(Actions.rotateBy(3, 0.05f),
                                                            Actions.rotateBy(-6, 0.05f),
                                                            Actions.rotateBy(6, 0.05f),
                                                            Actions.rotateBy(-3, 0.05f)));
    }
    else
    {
      this.previousMsg = null;
      this.previousMsgColor = null;
      this.msgTimer = -1;
    }

    if (color == null)
    {
      this.messageLabel.setColor(0, 0, 0, 1);
    }
    else
    {
      this.messageLabel.setColor(color);
    }

    this.messageLabel.setText(text);
    this.messageLabelContainer.setOrigin(this.messageLabelContainer.getPrefWidth() / 2f,
                                         this.messageLabelContainer.getPrefHeight() / 2f);
  }

  /**
   * Handles what to do when the server sends information about who won. (not
   * necessarily us)
   */
  @Override
  public void handleVictory(Connection conn, VictoryPacket vp)
  {
    log.debug("Victorystatus received: " + vp);
    boardEntity.setTouchable(Touchable.disabled);

    StringBuilder sb = new StringBuilder();
    sb.append("Game over!");
    if (vp.victoryColor == Color.NONE)
    {
      sb.append(" Draw!");
    }
    else if (vp.victoryColor == me.getColor())
    {
      sb.append(" You won!");
    }
    else
    {
      sb.append(" You lost!");
    }

    this.setMessage(sb.toString(), null, -1);
  }

  @Override
  public void preEvent(GameEvent event)
  {
  }

  @Override
  public void onEvent(GameEvent event)
  {
    if (event instanceof SetPlayerTurnEvent)
    {
      SetPlayerTurnEvent spte = (SetPlayerTurnEvent) event;
      if (spte.getColor() == me.getColor())
      {
        this.setMessage("Your turn!", com.badlogic.gdx.graphics.Color.GREEN, -1);
      }
      else if (me.getColor().isValidPlayerColor())
      {
        this.setMessage("Waiting for opponent", com.badlogic.gdx.graphics.Color.RED, -1);
      }
      else
      {
        this.setMessage("You are spectating", com.badlogic.gdx.graphics.Color.BLACK, -1);
      }
    }
  }
}
