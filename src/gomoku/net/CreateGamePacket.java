package gomoku.net;

public class CreateGamePacket {
    public int width;
    public int height;
    public boolean ownerReceivesBlack;

    @SuppressWarnings("unused")
    private CreateGamePacket() {
    }

    public CreateGamePacket(int width, int height, boolean receiveBlack) {
        this.width = width;
        this.height = height;
        ownerReceivesBlack = receiveBlack;
    }
}
