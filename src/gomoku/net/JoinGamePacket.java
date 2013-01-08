package gomoku.net;

public class JoinGamePacket {
    public int gameID;

    @SuppressWarnings("unused")
    private JoinGamePacket() {
    }

    public JoinGamePacket(int gameID) {
        this.gameID = gameID;
    }
}
