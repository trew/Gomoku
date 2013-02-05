package gomoku.logic;

import static org.trew.log.Log.error;
import gomoku.logic.Board.BoardAction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
            BufferedWriter writer = new BufferedWriter(fw);

            StringBuilder sb = new StringBuilder();
            for (BoardAction action : actions) {
                sb.append(action.getClass().getName()).append(":")
                        .append(action.getPlayer()).append(":")
                        .append(action.getX()).append(":")
                        .append(action.getY());
                writer.write(sb.toString());
                writer.newLine();
            }
            fw.close();
        } catch (IOException e) {
            error(e);
        }
    }

    public void load(String fileName) {
        FileReader fr;
        try {
            fr = new FileReader(fileName);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            do {
                try {
                    line = reader.readLine();
                    String[] items = line.split(":");
                    Class<?> clazz = Class.forName(items[0]);
                    int player = Integer.parseInt(items[1]);
                    int x = Integer.parseInt(items[2]);
                    int y = Integer.parseInt(items[3]);

                    BoardAction action = (BoardAction) clazz.getConstructor()
                            .newInstance(player, x, y);
                    actions.add(action);
                } catch (IOException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException
                        | SecurityException | ClassNotFoundException e) {
                    error(e);
                    actions.clear();
                    reader.close();
                    return;
                }
            } while (line != null);
            reader.close();
        } catch (IOException e) {
            error(e);
        }
    }
}
