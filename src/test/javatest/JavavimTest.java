import static org.junit.Assert.*;
import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class JavavimTest {

    @Test
    public void testRunEditor() throws IOException {
        Javavim editor = new Javavim();
        String filePath = "example.txt";
        editor.runEditor(filePath);
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        assertEquals("Hello, World!", content.trim());
    }

    @Test
    public void testSaveFile() throws IOException {
        Javavim editor = new Javavim();
        String filePath = "testfile.txt";
        editor.insertText("Test Content");
        editor.saveFile(filePath);
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        assertEquals("Test Content", content.trim());
    }
}