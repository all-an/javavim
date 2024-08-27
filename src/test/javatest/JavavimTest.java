import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class JavavimTest {

    @Test
    public void testSayHello() {
        Javavim javavim = new Javavim();
        javavim.sayHello(); // Just ensure the JNI call doesn't throw any exceptions
        assertTrue(true);
    }

    @Test
    public void testReverseString() {
        Javavim javavim = new Javavim();
        String original = "Javavim";
        String reversed = javavim.reverseString(original);
        assertEquals("mivavaJ", reversed);
    }

    @Test
    public void testReverseCommand() {
        Javavim javavim = new Javavim();
        String[] args = {"--reverse", "Javavim"};
        String result = javavim.processCommandLine(args);
        assertEquals("mivavaJ", result);
    }

    @Test
    public void testEchoCommand() {
        Javavim javavim = new Javavim();
        String command = ":echo Hello, Javavim!";
        String result = javavim.processCommand(command);
        assertEquals("Hello, Javavim!", result);
    }

    @Test
    public void testEchoCommandOutput() {
        Javavim javavim = new Javavim();
        String command = ":echo Hello, Terminal!";
        javavim.executeCommand(command);
        // We cannot directly assert terminal output, but this test ensures the method is called correctly
    }

    @Test
    public void testTextBuffer() {
        JavavimTextBuffer buffer = new JavavimTextBuffer();
        buffer.appendText("Hello, Javavim!");
        assertEquals("Hello, Javavim!", buffer.getText());
    }

    @Test
    public void testTextInsertionAndNavigation() {
        JavavimTextBuffer buffer = new JavavimTextBuffer();
        buffer.insertText("Hello");
        buffer.moveCursorTo(5);
        buffer.insertText(", Javavim!");
        assertEquals("Hello, Javavim!", buffer.getText());
        assertEquals(15, buffer.getCursorPosition());
    }

    @Test
    public void testTextDeletion() {
        JavavimTextBuffer buffer = new JavavimTextBuffer();
        buffer.insertText("Hello, Javavim!");
        buffer.moveCursorTo(6); // Move cursor after "Hello, "
        
        buffer.deleteBeforeCursor(); // Delete ','
        assertEquals("Hello Javavim!", buffer.getText());
        assertEquals(5, buffer.getCursorPosition());

        buffer.deleteAfterCursor(); // Delete 'K'
        assertEquals("HelloJavavim!", buffer.getText());
        assertEquals(5, buffer.getCursorPosition());
    }
}
