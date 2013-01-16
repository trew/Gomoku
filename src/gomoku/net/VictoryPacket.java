package gomoku.net;

public class VictoryPacket {

    public short victory;

    @SuppressWarnings("unused")
    private VictoryPacket() {
    }

    /**
     *
     * @param victory the victory value. 0 = loss, 1 = win, 2 = draw.
     */
    public VictoryPacket(short victory) {
        this.victory = victory;
    }
}
