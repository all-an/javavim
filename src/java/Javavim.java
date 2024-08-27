import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Javavim {

    private LineReader reader;
    private StringBuilder buffer = new StringBuilder();
    private int cursorPosition = 0;
    private String currentFile;
    private String currentFileContent;
    private String lastSavedContent;

    static {
        System.loadLibrary("javavim"); // Load native library at runtime (libjavavim.so or javavim.dll)
    }

    public Javavim() {
        try {
            Terminal terminal = TerminalBuilder.terminal();
            this.reader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Javavim(InputStream input, PrintStream output) {
        try {
            Terminal terminal = TerminalBuilder.builder().streams(input, output).build();
            this.reader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Constructor for injecting a custom LineReader (used in tests)
    public Javavim(LineReader reader) {
        this.reader = reader;
    }

    // Declare a native method
    public native void sayHello();

    // Method to run the editor and handle input
    public void runEditor() {
        String line;
        try {
            while ((line = reader.readLine("> ")) != null) {
                for (char c : line.toCharArray()) {
                    if (c == 'h') {
                        moveCursorLeft();
                    } else if (c == 'l') {
                        moveCursorRight();
                    } else if (c == 'i') {
                        insertMode();
                    } else if (c == 'x') {
                        deleteCharacter();
                    } else if (c == ':') {
                        // Command mode for opening/saving files
                        String command = reader.readLine();
                        if (command.startsWith("e ")) {
                            openFile(command.substring(2).trim());
                        } else if (command.equals("w")) {
                            saveFile(currentFile);
                        } else if (command.equals("q")) {
                            return; // Exit editor
                        }
                    }
                }
                System.out.println(buffer.toString());
            }
        } catch (org.jline.reader.EndOfFileException e) {
            System.out.println("End of input reached.");
        }
    }

    private void moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
        }
    }

    private void moveCursorRight() {
        if (cursorPosition < buffer.length()) {
            cursorPosition++;
        }
    }

    private void insertMode() {
        String insertText = reader.readLine("Insert: ");
        buffer.insert(cursorPosition, insertText);
        cursorPosition += insertText.length();
    }

    private void deleteCharacter() {
        if (cursorPosition < buffer.length()) {
            buffer.deleteCharAt(cursorPosition);
        }
    }

    /**
     * Opens a file and reads its content into the buffer.
     * @param filename the name of the file to open
     * @return true if the file was opened successfully, false otherwise
     */
    public boolean openFile(String filename) {
        try {
            currentFileContent = new String(Files.readAllBytes(Paths.get(filename)));
            buffer = new StringBuilder(currentFileContent);
            cursorPosition = 0;
            System.out.println(buffer.toString());
            return true;  // File opened successfully
        } catch (IOException e) {
            System.out.println("Error opening file: " + e.getMessage());
            return false;  // File opening failed
        }
    }

    /**
     * Saves the current buffer content to a file.
     * @param filename the name of the file to save
     * @return true if the file was saved successfully, false otherwise
     */
    public boolean saveFile(String filename) {
        try {
            lastSavedContent = buffer.toString();  // Store the content in a variable
            Files.write(Paths.get(filename), lastSavedContent.getBytes());
            System.out.println("File saved to " + filename);
            return true;  // File saved successfully
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
            return false;  // File saving failed
        }
    }

    // New method to get current file content (for testing purposes)
    public String getCurrentFileContent() {
        return buffer.toString();
    }

    /**
     * Returns the content that was last saved by the saveFile method.
     * @return the last saved content
     */
    public String getLastSavedContent() {
        return lastSavedContent;
    }

    public static void main(String[] args) {
        Javavim javavim = new Javavim();
        javavim.runEditor();
        if (args.length > 0) {
            String result = javavim.processCommandLine(args);
            System.out.println(result);
        } else {
            // Simulate user interaction in the terminal
            javavim.executeCommand(":echo Welcome to Javavim!");
        }
        new Javavim().sayHello(); // Invoke the native method

        JavavimTextBuffer buffer = new JavavimTextBuffer();
        
        // Simulate text editing
        buffer.appendText("Welcome to Javavim!\n");
        buffer.appendText("This is your text editor.\n");
        
        System.out.println(buffer.getText());

        // Simulate text editing with cursor navigation
        buffer.insertText("Hello");
        buffer.moveCursorTo(5);
        buffer.insertText(", Javavim!");
        
        System.out.println(buffer.getText()); // Output the final text

        JavavimTextBuffer buffer2 = new JavavimTextBuffer();
        
        // Simulate text editing with insertion, navigation, and deletion
        buffer2.insertText("Hello, Javavim!");
        buffer2.moveCursorTo(7); // Move cursor after "Hello, "
        buffer2.deleteBeforeCursor(); // Delete ','
        buffer2.deleteAfterCursor(); // Delete 'K'
        
        System.out.println(buffer2.getText()); // Output the final text
    }

    // Method to reverse a string
    public String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    // Method to process command line arguments
    public String processCommandLine(String[] args) {
        if (args.length == 2 && "--reverse".equals(args[0])) {
            return reverseString(args[1]);
        }
        return "";
    }

    // Method to process editor commands
    public String processCommand(String command) {
        if (command.startsWith(":echo ")) {
            return command.substring(6); // Return the text after ":echo "
        }
        return "";
    }

    // Method to execute a command and interact with the terminal
    public void executeCommand(String command) {
        String result = processCommand(command);
        if (!result.isEmpty()) {
            System.out.println(result); // Output the result to the terminal
        }
    }
}
