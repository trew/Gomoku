package se.samuelandersson.gomoku.client.net;

import com.esotericsoftware.kryonet.Connection;

import se.samuelandersson.gomoku.net.BoardPacket;
import se.samuelandersson.gomoku.net.GameActionPacket;
import se.samuelandersson.gomoku.net.GameListPacket;
import se.samuelandersson.gomoku.net.HandshakeClientPacket;
import se.samuelandersson.gomoku.net.HandshakeServerPacket;
import se.samuelandersson.gomoku.net.InitialServerDataPacket;
import se.samuelandersson.gomoku.net.NotifyTurnPacket;
import se.samuelandersson.gomoku.net.PlayerListPacket;
import se.samuelandersson.gomoku.net.Request;
import se.samuelandersson.gomoku.net.VictoryPacket;

public interface PacketHandler
{
  /**
   * Handles how a received BoardPacket should be treated.
   *
   * @param connection
   *          the connection that sent the BoardPacket
   * @param bp
   *          the BoardPacket
   */
  default void handleBoard(Connection connection, BoardPacket bp)
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
  default void handleGameList(Connection connection, GameListPacket glp)
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
  default void handleRequest(Connection connection, Request request)
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
  default void handleInitialServerData(Connection connection, InitialServerDataPacket isdp)
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
  default void handleNotifyTurn(Connection connection, NotifyTurnPacket ntp)
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
  default void handleGameAction(Connection connection, GameActionPacket ppp)
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
  default void handlePlayerList(Connection connection, PlayerListPacket plp)
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
  default void handleVictory(Connection connection, VictoryPacket vp)
  {
  }
  
  default void handleHandshakeClient(Connection connection, HandshakeClientPacket hcp)
  {
  }
  
  default void handleHandshakeServer(Connection connection, HandshakeServerPacket hcp)
  {
  }
}
