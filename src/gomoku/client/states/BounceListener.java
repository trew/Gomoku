package gomoku.client.states;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

/**
 * A BounceListener will return all events from a regular Listener to the
 * corresponding functions in the GomokuGameState that was passed to the
 * listener on creation.
 *
 * @author Samuel Andersson
 *
 */
public class BounceListener extends Listener {

    private GomokuNetworkGameState state;

    public BounceListener(GomokuNetworkGameState state) {
        this.state = state;
    }

    @Override
    public void connected(Connection conn) {
        state.connected(conn);
    }

    @Override
    public void disconnected(Connection conn) {
        state.disconnected(conn);
    }

    @Override
    public void received(Connection conn, Object obj) {
        state.received(conn, obj);
    }

    @Override
    public void idle(Connection conn) {
        state.idle(conn);
    }
}
