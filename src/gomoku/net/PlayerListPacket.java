package gomoku.net;

public class PlayerListPacket {
    public String[] players;

    @SuppressWarnings("unused")
    private PlayerListPacket() {
    }

    public PlayerListPacket(String[] players) {
        this.players = players;
    }
}
