package gomoku.client.states;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class BounceListener extends Listener {

    private GomokuGameState state;

    public BounceListener(GomokuGameState state) {
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
