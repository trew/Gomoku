package gomoku.logic;

import static org.trew.log.Log.error;
import gomoku.logic.Board.BoardAction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ActionRecorder {

    protected ArrayList<BoardAction> actions;

    public ActionRecorder() {
        actions = new ArrayList<BoardAction>();
    }

    public void add(BoardAction action) {
        actions.add(action);
    }

    public void store(String fileName) {
        try {
            FileWriter fw = new FileWriter(fileName);
            StringBuilder sb = new StringBuilder();
            for (BoardAction action : actions) {
                sb.append(action.getPlayer()).append(":").append(action.getX())
                        .append(":").append(action.getY()).append("\n");
            }
            fw.write(sb.toString());
            fw.close();
        } catch (IOException e) {
            error(e);
        }
    }

    public void load(String fileName) {
        // TODO
    }
}
