# vim like editor built with Java Swing

A vim-like text editor with terminal aesthetic (black background, green text) built with Java Swing.

## Features

- 4 VIM Modes: NORMAL, INSERT, VISUAL, COMMAND
- NerdTree-style File Explorer with full filesystem navigation
- Integrated Terminal (PowerShell/Bash)
- `Ctrl+E` to compile all Java files in current file folder and run current file class
- Line numbers
- Terminal styling with block cursor

## Build & Run

### Prerequisites
- JDK 21+
- Maven

### Build JAR
```bash
mvn clean package -DskipTests
```

### Run Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ConfigTest
mvn test -Dtest=MainTest
```

### Run
```bash
# Without file
java -jar target/javavim-1.0-SNAPSHOT.jar

# With file
java -jar target/javavim-1.0-SNAPSHOT.jar MyFile.java
```

### Create Native Executable (Optional)
```bash
jpackage --input target --name SwingApp --main-jar javavim-1.0-SNAPSHOT.jar --main-class org.javavim.Main --type app-image --win-console
```

Run with: `./SwingApp/SwingApp.exe`

## Keybindings

### Integrated Terminal (Ctrl+' to toggle)
| Key | Action |
|-----|--------|
| `Ctrl+'` | Toggle terminal |
| `Ctrl+1` | Focus editor |
| `Ctrl+C` | Interrupt command |
| `Ctrl+E` | Compile+run current Java file folder |
| `Enter` | Execute command |

### Ctrl+E Java Run
- Open/select a `.java` file, then press `Ctrl+E`.
- Javavim compiles all `.java` files in the same folder (recursive) with `javac`.
- It runs the currently opened Java class with `java`.
- Output appears in the integrated terminal and is saved to `ctrl-e-run.log` in that folder.

### File Explorer (Ctrl+N to toggle)
| Key | Action |
|-----|--------|
| `j` / `k` | Navigate down / up |
| `l` | Expand folder or enter directory |
| `h` | Collapse folder or go to parent |
| `Enter` | Open file or enter directory |
| `..` | Navigate to parent folder |
| `Backspace` | Go to parent directory |
| `-` | Go to parent directory |
| `Tab` | Switch focus to editor |

### Normal Mode
| Key | Action |
|-----|--------|
| `h` `j` `k` `l` | Move left, down, up, right |
| `i` | Insert mode |
| `a` | Append after cursor |
| `A` | Append at end of line |
| `o` | Open line below |
| `v` | Visual mode |
| `x` | Delete character |
| `:` | Command mode |
| `Ctrl+N` | Toggle file explorer |
| `Ctrl+E` | Compile+run current Java file folder |
| `Ctrl+'` | Toggle terminal |
| `Tab` | Focus file tree |

### Insert Mode
| Key | Action |
|-----|--------|
| `ESC` | Return to Normal mode |
| `Ctrl+N` | Toggle file explorer |
| `Ctrl+E` | Compile+run current Java file folder |
| `Ctrl+'` | Toggle terminal |

### Visual Mode
| Key | Action |
|-----|--------|
| `h` `j` `k` `l` | Extend selection |
| `d` | Delete selection |
| `Ctrl+E` | Compile+run current Java file folder |
| `ESC` | Return to Normal mode |

### Command Mode
| Command | Action |
|---------|--------|
| `:w` | Save |
| `:w filename` | Save as |
| `:e filename` | Open file |
| `:q` | Quit |
| `:wq` / `:x` | Save and quit |
| `:help` | Show help |

## Project Structure

```
javavim/
├── src/main/java/org/javavim/Main.java
├── target/javavim-1.0-SNAPSHOT.jar
├── pom.xml
└── README.md
```
