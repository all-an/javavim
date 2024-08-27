import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class JavavimFileTest {
    @Test
    public void testOpenFile() throws IOException {
        Javavim javavim = new Javavim();
        File tempFile = File.createTempFile("testfile", ".txt");
        boolean fileOpened = javavim.openFile(tempFile.getAbsolutePath());
        assertTrue(fileOpened);
    }

    @Test
    public void testGetCurrentFileContent() throws IOException {
        Javavim javavim = new Javavim();
        File tempFile = File.createTempFile("testfile", ".txt");
        javavim.openFile(tempFile.getAbsolutePath());
        String result = javavim.getCurrentFileContent();
        assertNotNull(result);
    }

    @Test
    public void testSaveFile() throws IOException {
        Javavim javavim = new Javavim();
        File tempFile = File.createTempFile("testfile", ".txt");
        javavim.openFile(tempFile.getAbsolutePath());
        javavim.insertText("Hello, World!");
        boolean fileSaved = javavim.saveFile(tempFile.getAbsolutePath());
        assertTrue(fileSaved);
        String lastSavedContent = javavim.getLastSavedContent();
        assertEquals("Hello, World!", lastSavedContent);
    }
}