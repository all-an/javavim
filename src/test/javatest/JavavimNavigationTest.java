import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class JavavimNavigationTest {

    @Test
    public void testCursorMovementAndEditing() {
        // Simulate input: inserting "Hello", moving cursor left twice, deleting a character
        String simulatedInput = "i\nHello\nhhx:q\n";
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printOutput = new PrintStream(output);

        Javavim javavim = new Javavim(input, printOutput);
        try {
            javavim.runEditor();
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }

        String result = javavim.getCurrentFileContent();
        assertEquals("Helo", result);  // Expect "Hello" -> "Hell" -> "Helo"
    }
}