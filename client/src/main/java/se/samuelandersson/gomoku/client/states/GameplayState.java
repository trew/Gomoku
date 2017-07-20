package se.samuelandersson.gomoku.client.states;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;

import de.matthiasmann.twl.slick.RootPane;
import se.samuelandersson.gomoku.Color;
import se.samuelandersson.gomoku.GomokuBoard;
import se.samuelandersson.gomoku.GomokuConfig;
import se.samuelandersson.gomoku.GomokuGame;
import se.samuelandersson.gomoku.Player;
import se.samuelandersson.gomoku.action.BoardAction;
import se.samuelandersson.gomoku.action.GameAction;
import se.samuelandersson.gomoku.action.PlacePieceAction;
import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.client.Settings.Setting;
import se.samuelandersson.gomoku.client.gui.BoardWidget;
import se.samuelandersson.gomoku.client.gui.BoardWidget.Callback;
import se.samuelandersson.gomoku.client.gui.Button;
import se.samuelandersson.gomoku.client.gui.Fonts;
import se.samuelandersson.gomoku.client.gui.ScrollingPane;
import se.samuelandersson.gomoku.exception.IllegalActionException;
import se.samuelandersson.gomoku.impl.GomokuGameImpl;
import se.samuelandersson.gomoku.net.BoardPacket;
import se.samuelandersson.gomoku.net.GameActionPacket;
import se.samuelandersson.gomoku.net.NotifyTurnPacket;
import se.samuelandersson.gomoku.net.PlayerListPacket;
import se.samuelandersson.gomoku.net.Request;
import se.samuelandersson.gomoku.net.VictoryPacket;

/**
 * The playing state of the Gomoku game.
 *
 * @author Samuel Andersson
 */
public class GameplayState extends AbstractNetworkGameState
{
  private static final Logger log = LoggerFactory.getLogger(GameplayState.class);

  private static final int GAMEOVER_LOSS = 0;
  private static final int GAMEOVER_VICTORY = 1;
  private static final int GAMEOVER_DRAW = 2;

  /** Contains the game logic */
  public GomokuGame gomokuGame;

  /** The player */
  public Player me;

  /** The board displayed */
  private ScrollingPane scrollpane;
  private BoardWidget boardWidget;
  private Button confirmMoveButton;

  private Image versus;
  private Image nametag;
  private Image infobar;
  private Image messagebox;

  private boolean loading;
  private boolean gameOver;
  private int gameOverVictoryState;

  private List<String> playerList;

  private String errorMsg;
  private long errorTimer;

  private GameAction pendingAction;

  @Override
  protected RootPane createRootPane()
  {
    RootPane rp = super.createRootPane();

    boardWidget = new BoardWidget();
    scrollpane = new ScrollingPane(boardWidget, boardWidget);
    rp.add(scrollpane);

    return rp;
  }

  @Override
  protected void layoutRootPane()
  {
    scrollpane.setPosition(300 - scrollpane.getWidth() / 2, 300 - scrollpane.getHeight() / 2);
    scrollpane.updateScrollbarSizes();
    scrollpane.setScrollPositionX(scrollpane.getMaxScrollPosX() / 2);
    scrollpane.setScrollPositionY(scrollpane.getMaxScrollPosY() / 2);
  }

  public boolean initialLoading()
  {
    return loading;
  }

  public void setInitialData(GomokuBoard board,
                             GomokuConfig config,
                             int playerID,
                             int turnID,
                             String[] playerList,
                             Color playerOneColor,
                             Color playerTwoColor)
  {
    // create a new game
    this.playerList = Arrays.asList(playerList);
    gomokuGame = new GomokuGameImpl(board, config);

    if (turnID == Player.PLAYERONE)
    {
      gomokuGame.setCurrentTurnPlayer(gomokuGame.getPlayerOne());
    }
    else if (turnID == Player.PLAYERTWO)
    {
      gomokuGame.setCurrentTurnPlayer(gomokuGame.getPlayerTwo());
    }

    setupPlayers(playerID, playerOneColor, playerTwoColor);

    setBoard(board);

    loading = false;
    gameOver = false;
    pendingAction = null;
    gameOverVictoryState = GAMEOVER_LOSS;
    boardWidget.setEnabled(true);
  }

  /**
   * Setup names and values for players. If this client receives black, makes
   * sure the white player receives the correct name.
   *
   * @param playerColor
   *          The player color being given to this client
   */
  public void setupPlayers(int playerID, Color playerOneColor, Color playerTwoColor)
  {
    final String playerName = getGame().getSettings().getProperty(Setting.PLAYER_NAME, "(none)");
    if (playerID == Player.PLAYERONE)
    {
      me = gomokuGame.getPlayerOne();
      playerList.set(0, playerName);
      gomokuGame.getPlayerTwo().setName(playerList.get(1));

    }
    else if (playerID == Player.PLAYERTWO)
    {
      me = gomokuGame.getPlayerTwo();
      playerList.set(1, playerName);
      gomokuGame.getPlayerOne().setName(playerList.get(0));
    }
    else
    {
      me = new Player("", Player.NOPLAYER);
      gomokuGame.getPlayerOne().setName(playerList.get(0));
      gomokuGame.getPlayerTwo().setName(playerList.get(1));
    }

    gomokuGame.getPlayerOne().setColor(playerOneColor);
    gomokuGame.getPlayerTwo().setColor(playerTwoColor);
    me.setName(playerName);
    log.debug("Player ID set to " + me.getID());
  }

  public List<String> getPlayerList()
  {
    return Collections.unmodifiableList(playerList);
  }

  public void setPlayerList(List<String> playerList)
  {
    this.playerList = playerList;

    // if I'm player 1, do not update my name from the server. I *should*
    // know my name better that the server.
    if (me.getID() == Player.PLAYERONE)
    {
      gomokuGame.getPlayerTwo().setName(playerList.get(1));
    }
    else if (me.getID() == Player.PLAYERTWO)
    {
      gomokuGame.getPlayerOne().setName(playerList.get(0));
    }
    else
    {
      gomokuGame.getPlayerOne().setName(playerList.get(0));
      gomokuGame.getPlayerTwo().setName(playerList.get(1));
    }
  }

  @Override
  public void init(GameContainer container, final GomokuClient gomokuClient) throws SlickException
  {
    loading = true; // will be set to false once we receive data from the
                   // server
    gameOver = false;
    pendingAction = null;

    errorMsg = "";

    // add the board
    boardWidget.addCallback(new Callback()
    {
      @Override
      public void callback(int x, int y)
      {
        if (me == null || !myTurn())
        {
          return;
        }

        placePiece(gomokuClient, x, y);
      }
    });

    Image ok = new Image("buttons/ok.png");
    confirmMoveButton = new Button(ok, 640, 482, 3)
    {
      @Override
      public void buttonClicked(int button, int x, int y)
      {
        if (button == 0 && pendingAction != null)
        {
          pendingAction.confirmAction(gomokuGame);
          confirmAndSendGameAction(gomokuClient, pendingAction);
          pendingAction = null;
        }
      }
    };

    versus = new Image("versus.png");
    nametag = new Image("nametag.png");
    infobar = new Image("infobar.png");
    messagebox = new Image("bottommessagebox.png");

    addListener(confirmMoveButton);
  }

  /**
   * Try to place a new piece on provided position. If successful client-side, send a packet to server trying to do the
   * same thing.
   *
   * @param gomokuClient The game which we place the piece in
   * @param x The x location for the new piece
   * @param y The y location for the new piece
   */
  public void placePiece(GomokuClient gomokuClient, int x, int y)
  {
    final boolean requireConfirm = Boolean.valueOf(this.getGame().getSettings().getProperty(Setting.CONFIRM));

    try
    {
      if (pendingAction != null)
      {
        // just assume it's a board action...
        BoardAction boardAction = (BoardAction) pendingAction;
        pendingAction.undoAction(gomokuGame);

        // Is it the current piece?
        if (boardAction.getX() == x && boardAction.getY() == y)
        {
          pendingAction = null;
          return; // don't place a new one
        }
      }

      pendingAction = new PlacePieceAction(me.getColor(), x, y);
      pendingAction.doAction(gomokuGame);

      if (!requireConfirm)
      {
        confirmAndSendGameAction(gomokuClient, pendingAction);
        pendingAction = null;
      }
    }
    catch (IllegalActionException e)
    {
      pendingAction = null;
      setErrorMsg(e.getMessage());

    }
  }

  protected void confirmAndSendGameAction(GomokuClient client, GameAction action)
  {
    if (!action.isConfirmed())
    {
      if (log.isDebugEnabled())
      {
        log.debug("Confirming GameAction: {}", action);
      }

      action.confirmAction(gomokuGame);
    }

    client.getNetworkClient().sendTCP(new GameActionPacket(action));
  }

  /**
   * Sets the error message to be displayed with a timer of 5 seconds.
   *
   * @param msg
   */
  protected void setErrorMsg(String msg)
  {
    log.debug(msg);
    errorMsg = msg;
    errorTimer = 5000;
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
    boardWidget.setBoard(board);
    boardWidget.adjustSize();

    int scrollpaneWidth = Math.min(375, board.getWidth() * 25);
    int scrollpaneHeight = Math.min(375, board.getHeight() * 25);
    scrollpane.setInnerSize(scrollpaneWidth, scrollpaneHeight);
    scrollpane.setPosition(300 - scrollpane.getWidth() / 2, 300 - scrollpane.getHeight() / 2);
  }

  @Override
  public void update(GameContainer container, GomokuClient gomokuClient, int delta) throws SlickException
  {
    if (errorTimer > 0)
    {
      errorTimer -= delta;
    }

    Input input = container.getInput();

    if (input.isKeyPressed(Input.KEY_ESCAPE))
    {
      if (pendingAction != null)
      {
        pendingAction.undoAction(gomokuGame);
        pendingAction = null;
      }
      else
      {
        enterState(PAUSEMENUSTATE, this);
      }
    }

    /* *** NETWORK RELATED INPUT *** */
    if (gomokuClient.getNetworkClient().isConnected())
    {
      if (input.isKeyPressed(Input.KEY_F5))
      {
        gomokuClient.getNetworkClient().sendTCP(Request.UPDATE_BOARD);
      }
    }
    /* *** END NETWORK RELATED INPUT *** */
  }

  private static int rowYPos = 85;

  private void drawRow(String str, int x, Graphics g)
  {
    int textHeight = g.getFont().getHeight(str);
    g.drawString(str, x, rowYPos);
    rowYPos += textHeight + 1;
  }

  @Override
  public void render(GameContainer container, GomokuClient gomokuClient, Graphics g) throws SlickException
  {
    g.setFont(container.getDefaultFont());

    // draw the board
    if (loading)
    {
      g.drawString("Loading...", 200, 200);
      return;
    }

    g.drawImage(versus, 400 - 117 / 2, 0);
    g.drawImage(nametag, 20, 10);
    g.drawImage(nametag, 490, 10);
    g.drawImage(infobar, 800 - 225, (600 - 454) / 2 + 8);
    g.drawImage(messagebox, 0, 600 - 63);

    // draw game info
    g.setFont(Fonts.getDefaultFont());
    int xPos = 590;
    rowYPos = 85;
    drawRow("Your name: " + me.getName(), xPos, g);
    if (me.getColor().isValidPlayerColor())
    {
      drawRow("Your color: " + me.getColor().getName(), xPos, g);
      drawRow("Turn: " + (myTurn() ? "You" : "Opponent"), xPos, g);
    }

    drawRow("Board size: " + gomokuGame.getBoard().getWidth() + "x" + gomokuGame.getBoard().getHeight(), xPos, g);

    drawRow("Connected players", xPos, g);
    drawRow("-----------------", xPos, g);
    for (String p : this.playerList)
    {
      if (p.equals("(none)"))
      {
        break;
      }
      drawRow(p, xPos, g);
    }

    if (pendingAction != null)
    {
      g.setFont(Fonts.getAngelCodeFont("fonts/messagebox"));
      g.drawString("Confirm move?", 600, 440);
      confirmMoveButton.render(container, g);
    }

    // top
    g.setColor(org.newdawn.slick.Color.white);
    g.setFont(Fonts.getAngelCodeFont("fonts/nametag"));
    String name = gomokuGame.getPlayerOne().getName();
    int stringWidth = g.getFont().getWidth(name);
    g.drawString(name, center(20, 20 + 290, stringWidth), 13);

    name = gomokuGame.getPlayerTwo().getName();
    stringWidth = g.getFont().getWidth(name);
    g.drawString(name, center(490, 490 + 290, stringWidth), 13);

    // error msg
    g.setColor(org.newdawn.slick.Color.black);
    g.setFont(Fonts.getAngelCodeFont("fonts/messagebox"));
    if (errorTimer > 0)
    {
      g.setColor(org.newdawn.slick.Color.red);
      stringWidth = g.getFont().getWidth(errorMsg);
      g.drawString(errorMsg, center(0, 800, stringWidth), 550);
    }
    else if (me.getID() == gomokuGame.getPlayerOne().getID() || me.getID() == gomokuGame.getPlayerTwo().getID())
    {
      String status = "";
      if (gameOver)
      {
        g.setColor(org.newdawn.slick.Color.white);
        status += "Game Over!";
        if (gameOverVictoryState == GAMEOVER_LOSS)
        {
          status += " You lost.";
        }
        else if (gameOverVictoryState == GAMEOVER_VICTORY)
        {
          status += " You won!";
        }
        else if (gameOverVictoryState == GAMEOVER_DRAW)
        {
          status += " Draw!";
        }
      }
      else if (!myTurn())
      {
        g.setColor(org.newdawn.slick.Color.red);
        status += "waiting for opponent";
      }
      else
      {
        g.setColor(org.newdawn.slick.Color.green);
        status += "Your turn!";
      }

      stringWidth = g.getFont().getWidth(status);
      g.drawString(status, center(0, 800, stringWidth), 550);
    }

    g.setColor(org.newdawn.slick.Color.white);
  }

  @Override
  protected void handleGameAction(Connection conn, GameActionPacket ppp)
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
      this.getGame().getNetworkClient().sendTCP(conn, Request.UPDATE_BOARD);
    }
  }

  @Override
  protected void handleBoard(Connection conn, BoardPacket bp)
  {
    // let's update our board with the board of the server
    gomokuGame.getBoard().setFrom(bp.getBoard());
  }

  @Override
  protected void handleRequest(Connection conn, Request request)
  {
  }

  @Override
  protected void handleNotifyTurn(Connection conn, NotifyTurnPacket ntp)
  {
    gomokuGame.setCurrentTurnPlayer(gomokuGame.getPlayer(ntp.getID()));
    log.info("Notified about turn: " + gomokuGame.getCurrentTurnPlayer().getName());
  }

  @Override
  protected void handlePlayerList(Connection conn, PlayerListPacket plp)
  {
    log.debug("Updated playerlist");
    setPlayerList(Arrays.asList(plp.players));
  }

  /**
   * Handles what to do when the server sends information about who won. (not
   * necessarily us)
   */
  @Override
  protected void handleVictory(Connection conn, VictoryPacket vp)
  {
    log.debug("Victorystatus received: " + vp);
    boardWidget.setEnabled(false);
    gameOver = true;

    if (vp.victoryColor == Color.NONE)
    {
      gameOverVictoryState = GAMEOVER_DRAW;
    }
    else if (vp.victoryColor == me.getColor())
    {
      gameOverVictoryState = GAMEOVER_VICTORY;
    }
    else
    {
      gameOverVictoryState = GAMEOVER_LOSS;
    }
  }

  @Override
  public int getID()
  {
    return GAMEPLAYSTATE;
  }
}
