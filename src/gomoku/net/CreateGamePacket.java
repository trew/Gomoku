package gomoku.net;

import gomoku.logic.GomokuConfig;

public class CreateGamePacket {
    public GomokuConfig config;

    @SuppressWarnings("unused")
    private CreateGamePacket() {
    }

    public CreateGamePacket(GomokuConfig config) {
        this.config = config;
    }
}
