package se.samuelandersson.gomoku.client.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;

import se.samuelandersson.gomoku.client.GomokuClient;
import se.samuelandersson.gomoku.net.*;

/**
 * A NetworkGameState adds a BounceListener to the client which enables the state to have its own callback functions
 * from the network. This workaround is required because kryonet doesn't expose its listener as an interface.
 *
 * @author Samuel Andersson
 */
public abstract class AbstractNetworkGameState extends AbstractGameState
{
  private DelegateListener listener;

  private boolean forwarding;
  private AbstractNetworkGameState stateForForwarding;

  /**
   * Initialize the Network state by adding creating a {@link DelegateListener}
   */
  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException
  {
    super.init(container, game);
    listener = new DelegateListener(this);
  }

  /**
   * Adds the {@link DelegateListener} to the {@link Client} that exists in
   * {@link GomokuClient}. Proceeds by calling
   * {@link AbstractGameState#enter(GameContainer, GomokuClient)}
   *
   * @throws IllegalArgumentException
   *           if the StateBasedGame isn't a {@link GomokuClient}
   */
  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException
  {
    super.enter(container, game);

    if (!(game instanceof GomokuClient))
    {
      throw new IllegalArgumentException("game must be a GomokuClient");
    }

    GomokuClient gomokuClient = (GomokuClient) game;
    if (gomokuClient.getNetworkClient() != null)
    {
      gomokuClient.getNetworkClient().addListener(listener);
    }
  }

  /**
   * Removes the {@link DelegateListener} to the {@link Client} that exists in
   * {@link GomokuClient}. Proceeds by calling
   * {@link AbstractGameState#leave(GameContainer, GomokuClient)}
   */
  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException
  {
    super.leave(container, game);

    if (!(game instanceof GomokuClient))
    {
      throw new IllegalArgumentException("game must be a GomokuClient");
    }

    GomokuClient gomokuClient = (GomokuClient) game;
    if (gomokuClient.getNetworkClient() != null)
    {
      gomokuClient.getNetworkClient().removeListener(listener);
    }

    leave(container, gomokuClient);
  }

  public boolean isForwarding()
  {
    return forwarding;
  }

  public void setForwarding(boolean forward)
  {
    this.forwarding = forward;
  }

  public void setStateToForwardTo(AbstractNetworkGameState state)
  {
  }

  private AbstractNetworkGameState getStateToForwardTo()
  {
    if (stateForForwarding == null)
    {
      throw new NullPointerException("state to forward to");
    }

    return stateForForwarding;
  }

  /**
   * Called when the client is connected to the remote end.
   *
   * @param connection
   */
  public void connected(Connection connection)
  {
    if (forwarding)
    {
      AbstractNetworkGameState state = getStateToForwardTo();

      if (state != null)
      {
        state.connected(connection);
      }
    }
  }

  /**
   * Called when the client is disconnected from the remote end.
   *
   * @param connection
   */
  public void disconnected(Connection connection)
  {
    if (forwarding)
    {
      AbstractNetworkGameState state = getStateToForwardTo();
      if (state != null)
      {
        state.disconnected(connection);
      }
    }

    ((ConnectState) getGame().getState(CONNECTGAMESTATE)).disconnected();
  }

  /**
   * This function is called from {@link DelegateListener}s. It will distribute
   * the call out to specific functions depending on the packet type. The
   * function name is "handle&lt;PacketType&gt;". Packets can be found in
   * {@link se.samuelandersson.gomoku.net}.
   *
   * @param connection
   *          the connection that sent the packet
   * @param object
   *          the object
   */
  public void received(Connection connection, Object object)
  {
    if (forwarding)
    {
      AbstractNetworkGameState state = getStateToForwardTo();
      if (state != null)
      {
        state.received(connection, object);
      }

      return;
    }

    if (object instanceof BoardPacket)
    {
      handleBoard(connection, (BoardPacket) object);
    }
    else if (object instanceof GameListPacket)
    {
      handleGameList(connection, (GameListPacket) object);
    }
    else if (object instanceof Request)
    {
      handleRequest(connection, (Request) object);
    }
    else if (object instanceof InitialServerDataPacket)
    {
      handleInitialServerData(connection, (InitialServerDataPacket) object);
    }
    else if (object instanceof NotifyTurnPacket)
    {
      handleNotifyTurn(connection, (NotifyTurnPacket) object);
    }
    else if (object instanceof GameActionPacket)
    {
      handleGameAction(connection, (GameActionPacket) object);
    }
    else if (object instanceof PlayerListPacket)
    {
      handlePlayerList(connection, (PlayerListPacket) object);
    }
    else if (object instanceof VictoryPacket)
    {
      handleVictory(connection, (VictoryPacket) object);
    }
  }

  /**
   * Handles how a received BoardPacket should be treated.
   *
   * @param connection
   *          the connection that sent the BoardPacket
   * @param bp
   *          the BoardPacket
   */
  protected void handleBoard(Connection connection, BoardPacket bp)
  {
  }

  /**
   * Handles how a received GameListPacket should be treated.
   *
   * @param connection
   *          the connection that sent the GameListPacket
   * @param glp
   *          the GameListPacket
   */
  protected void handleGameList(Connection connection, GameListPacket glp)
  {
  }

  /**
   * Handles how a received Request should be treated.
   *
   * @param connection
   *          the connection that sent the Request
   * @param grp
   *          the Request
   */
  protected void handleRequest(Connection connection, Request request)
  {
  }

  /**
   * Handles how a received InitialServerDataPacket should be treated.
   *
   * @param connection
   *          the connection that sent the InitialServerDataPacket
   * @param isdp
   *          the InitialServerDataPacket
   */
  protected void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
  {
  }

  /**
   * Handles how a received NotifyTurnPacket should be treated.
   *
   * @param connection
   *          the connection that sent the NotifyTurnPacket
   * @param ntp
   *          the NotifyTurnPacket
   */
  protected void handleNotifyTurn(Connection connection, NotifyTurnPacket ntp)
  {
  }

  /**
   * Handles how a received PlacePiecePacket should be treated.
   *
   * @param connection
   *          the connection that sent the PlacePiecePacket
   * @param ppp
   *          the PlacePiecePacket
   */
  protected void handleGameAction(Connection connection, GameActionPacket ppp)
  {
  }

  /**
   * Handles how a received PlayerListPacket should be treated.
   *
   * @param connection
   *          the connection that sent the PlayerListPacket
   * @param plp
   *          the PlayerListPacket
   */
  protected void handlePlayerList(Connection connection, PlayerListPacket plp)
  {
  }

  /**
   * Handles how a received VictoryPacket should be treated.
   *
   * @param connection
   *          the connection that sent the VictoryPacket
   * @param vp
   *          the VictoryPacket
   */
  protected void handleVictory(Connection connection, VictoryPacket vp)
  {
  }

  public void idle(Connection connection)
  {
  }
}