import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;

public class JavavimFileTest {
    

    @Test
    public void testOpenFileCommand() throws Exception {
        // Create a temporary file with some content
        File tempFile = File.createTempFile("testFile", ".txt");
        Files.write(tempFile.toPath(), "Hello, Javavim!".getBytes());

        // Simulate the `:e` command to open the file
        String simulatedInput = ":e " + tempFile.getAbsolutePath() + "\n:q\n";
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printOutput = new PrintStream(output);

        Javavim javavim = new Javavim(input, printOutput);
        boolean fileOpened = javavim.openFile(tempFile.getAbsolutePath());

        // Check that the file was opened successfully
        assertTrue(fileOpened, "The file should have been opened successfully.");

        // Verify the content read from the file
        String result = javavim.getCurrentFileContent();
        assertEquals("Hello, Javavim!", result);

        tempFile.delete();  // Clean up
    }

    @Test
    public void testSaveFileCommand() throws Exception {
        // Step 1: Create a temporary file to save content
        File tempFile = File.createTempFile("testSaveFile", ".txt");

        // Step 2: Input text into the buffer
        String simulatedInput = "i\nThis is a test\n";
        InputStream input = new ByteArrayInputStream(simulatedInput.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printOutput = new PrintStream(output);

        Javavim javavim = new Javavim(input, printOutput);
        javavim.runEditor();  // Input the text

        // Verify the content in the buffer before saving
        String bufferContent = javavim.getCurrentFileContent();
        assertEquals("This is a test", bufferContent, "The buffer content should match the input text.");

        // Step 3: Save the buffer content to the file
        boolean fileSaved = javavim.saveFile(tempFile.getAbsolutePath());
        assertTrue(fileSaved, "The file should have been saved successfully.");

        // Step 4: Verify the content was correctly saved
        String lastSavedContent = javavim.getLastSavedContent();
        assertEquals("This is a test", lastSavedContent, "The last saved content should match the buffer content.");

        // Step 5: Verify the actual file content
        String fileContent = new String(Files.readAllBytes(tempFile.toPath()));
        assertEquals("This is a test", fileContent.trim(), "The file content should match the saved content.");

        tempFile.delete();  // Clean up
    }



} 
