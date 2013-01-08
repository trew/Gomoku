package gomoku.net;

import java.util.HashMap;

import gomoku.server.GomokuNetworkGame;

public class GameListPacket {
    public int[] gameID;
    public String[] gameName;

    @SuppressWarnings("unused")
    private GameListPacket() {
    }

    public GameListPacket(HashMap<Integer, GomokuNetworkGame> games) {
        if (games == null) {
            gameID = new int[0];
            gameName = new String[0];
        } else {
            gameID = new int[games.size()];
            gameName = new String[games.size()];
            int i = 0;
            for (GomokuNetworkGame g : games.values()) {
                gameID[i] = g.getID();
                gameName[i] = g.getName();
                i++;
            }
        }
    }
}
