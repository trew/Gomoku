package gomoku.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JTextArea;

/**
 * Represents a console viewable through a <code>JTextArea</code>.
 *
 * <p>
 *  Implementation:
 *  <code>
 *      System.setOut(new PrintStream(new Console( ... )));
 *  </code>
 *  </p>
 *
 * @author Derive McNeill
 *
 */
public class Console extends OutputStream {

    /**
     * Represents the data written to the stream.
     */
    ArrayList <Byte> data = new ArrayList <Byte> ();

    /**
     * Represents the text area that will be showing the written data.
     */
    private JTextArea output;

    /**
     * Creates a console context.
     * @param output
     *      The text area to output the consoles text.
     */
    public Console(JTextArea output) {
        this.output = output;
        this.output.setAutoscrolls(true);
    }

    /**
     * Called when data has been written to the console.
     */
    private void fireDataWritten() {

        // First we loop through our written data counting the lines.
        int lines = 0;
        for (int i = 0; i < data.size(); i++) {
            byte b = data.get(i);

            // Specifically we look for 10 which represents "\n".
            if (b == 10) {
                lines++;
            }

            // If the line count exceeds 250 we remove older lines.
            if (lines >= 250) {
                data = (ArrayList<Byte>) data.subList(i, data.size());
            }
        }

        // We then create a string builder to append our text data.
        StringBuilder bldr = new StringBuilder();

        // We loop through the text data appending it to the string builder.
        for (byte b : data) {
            bldr.append((char) b);
        }

        // Finally we set the outputs text to our built string.
        output.setText(bldr.toString());
    }

    @Override
    public void write(int i) throws IOException {

        // Append the piece of data to our array of data.
        data.add((byte) i);

        // Indicate that data has just been written.
        fireDataWritten();
    }

}