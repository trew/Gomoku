package gomoku.net;

public class PlayerPacket {
    public String name;

    @SuppressWarnings("unused")
    private PlayerPacket() {
    }

    public PlayerPacket(String name) {
        this.name = name;
    }
}
