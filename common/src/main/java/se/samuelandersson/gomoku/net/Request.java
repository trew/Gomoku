package se.samuelandersson.gomoku.net;

/**
 * Contains possible requests mapped to integers
 *
 * @see GenericRequestPacket
 * @author Samuel Andersson
 */
public enum Request
{
  /** Request the board to be updated */
  UPDATE_BOARD,

  /** Clear and reset the board */
  CLEAR_BOARD,

  /** Get whose turn it is */
  GET_TURN,

  /** Request the list of connected players */
  GET_PLAYER_LIST,

  /** Request a list of open games */
  GET_GAME_LIST,

  /** Request to start a new game */
  CREATE_GAME,

  /** Request to leave the game */
  LEAVE_GAME,
  
  /** Request to join a game in a single-game server */
  JOIN_SINGLE_GAME_SERVER;
}
