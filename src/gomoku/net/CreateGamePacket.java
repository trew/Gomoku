package gomoku.net;

public class CreateGamePacket {
    public String name;
    public int width;
    public int height;
    public boolean ownerReceivesBlack;

    @SuppressWarnings("unused")
    private CreateGamePacket() {
    }

    public CreateGamePacket(String name, int width, int height, boolean receiveBlack) {
        this.name = name;
        this.width = width;
        this.height = height;
        ownerReceivesBlack = receiveBlack;
    }
}
