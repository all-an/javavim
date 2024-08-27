import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class JavavimInputTest {

    @Test
    public void testRunEditorHandlesQuitCommand() {
        // Simulate user input ":q"
        String simulatedInput = ":q\n";
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printOutput = new PrintStream(output);

        try {
            Terminal terminal = TerminalBuilder.builder().streams(input, printOutput).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            Javavim javavim = new Javavim(reader);
            javavim.runEditor();

            String result = output.toString();
            assertTrue(result.contains("> "));
            assertFalse(result.contains("You typed"));
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void testRunEditorHandlesTextInput() {
        // Simulate user input "Hello\n:q\n"
        String simulatedInput = "Hello\n:q\n";
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printOutput = new PrintStream(output);

        try {
            Terminal terminal = TerminalBuilder.builder().streams(input, printOutput).build();
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            Javavim javavim = new Javavim(reader);
            javavim.runEditor();

            String result = output.toString();
            System.out.println(result);
            
            // Check if the result starts with the expected output
            assertTrue(result.contains("Hello"));
            assertTrue(result.contains(":q"));
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }
}
