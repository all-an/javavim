import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Javavim {
    private StringBuilder buffer = new StringBuilder();
    private int cursorPosition = 0;
    private String lastSavedContent = "";

    public void displayBuffer() {
        System.out.println(buffer.toString());
    }

    public void insertText(String text) {
        buffer.insert(cursorPosition, text);
        cursorPosition += text.length();
    }

    public boolean openFile(String filePath) {
        try {
            buffer = new StringBuilder(new String(Files.readAllBytes(Paths.get(filePath))));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCurrentFileContent() {
        return buffer.toString();
    }

    public boolean saveFile(String filePath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(buffer.toString());
            lastSavedContent = buffer.toString();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getLastSavedContent() {
        return lastSavedContent;
    }

    public void runEditor(String filePath) {
        openFile(filePath);
        displayBuffer();
        insertText("Hello, World!");
    }

    public static void main(String[] args) {
        Javavim javavim = new Javavim();
        javavim.runEditor("testfile.txt");
    }
}