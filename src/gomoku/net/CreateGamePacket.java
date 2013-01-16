package gomoku.net;

import gomoku.logic.GomokuConfig;

public class CreateGamePacket {
    public String name;
    public GomokuConfig config;

    @SuppressWarnings("unused")
    private CreateGamePacket() {
    }

    public CreateGamePacket(String name, GomokuConfig config) {
        this.name = name;
        this.config = config;
    }
}
