# VIM Terminal Edition - A Terminal-Style VIM Editor

A vim-like text editor with a classic terminal aesthetic (black background, green text) built with Java Swing.

## Features

- **4 VIM Modes**: NORMAL, INSERT, VISUAL, COMMAND
- **NerdTree File Explorer**: Ctrl+N to toggle file tree (like NerdTree plugin)
- **Classic Terminal Styling**: Black background with green monospace text
- **Block Cursor**: Terminal-style blinking block cursor
- **Essential VIM Keybindings**: hjkl navigation, i/a/o for insert, v for visual, : for commands
- **File Operations**: Open, edit, and save files
- **Fullscreen Terminal Experience**: Borderless window with status bar

## Quick Start

### Running from JAR
```bash
# Open without a file
java -jar target\javafxterminaltoexe-1.0-SNAPSHOT.jar

# Open a specific file
java -jar target\javafxterminaltoexe-1.0-SNAPSHOT.jar MyFile.java
```

### Running from Native Executable
```bash
# Open without a file
.\SwingApp\SwingApp.exe

# Open a specific file
.\SwingApp\SwingApp.exe MyFile.java
```

## VIM Commands

### FILE EXPLORER (NerdTree)
- `Ctrl+N` - Toggle file explorer tree (works in NORMAL and INSERT modes)
- `Tab` - Switch focus between tree and editor
- **In tree view (vim-style navigation):**
  - `j` - Move down (or Down arrow)
  - `k` - Move up (or Up arrow)
  - `h` - Collapse folder (or Left arrow)
  - `l` - Expand folder (or Right arrow)
  - `Enter` - Open selected file
  - Double-click - Open file
  - `Tab` - Return focus to editor
  - `Ctrl+N` - Close the tree

### NORMAL MODE (default)
- `h, j, k, l` - Move cursor left, down, up, right
- `i` - Enter INSERT mode (insert before cursor)
- `a` - Append (insert after cursor)
- `A` - Append at end of line
- `o` - Open new line below and enter INSERT mode
- `v` - Enter VISUAL mode
- `x` - Delete character under cursor
- `:` - Enter COMMAND mode
- `Ctrl+N` - Toggle NerdTree file explorer
- `Tab` - Focus file tree (if visible)

### INSERT MODE
- `ESC` - Return to NORMAL mode
- `Ctrl+N` - Toggle NerdTree file explorer
- Type freely to edit text

### VISUAL MODE
- `h, j, k, l` - Extend selection in direction
- `d` - Delete selection
- `ESC` - Return to NORMAL mode

### COMMAND MODE
- `:w` - Save current file
- `:w filename` - Save as filename
- `:e filename` - Open file
- `:q` - Quit application
- `:wq` or `:x` - Save and quit
- `:help` - Show help screen
- `ESC` - Return to NORMAL mode

## Prerequisites

- Java Development Kit (JDK) 21 or later
- Maven
- jpackage (included with JDK 16+)

## Building the Application

### Step 1: Build the JAR file

```powershell
mvn clean package
```

This will create `javafxterminaltoexe-1.0-SNAPSHOT.jar` in the `target` directory.

### Step 2: Create Native Executable with jpackage

#### Option A: Create App-Image (Recommended - No installer needed)

```powershell
jpackage --input target --name SwingApp --main-jar javafxterminaltoexe-1.0-SNAPSHOT.jar --main-class org.example.Main --type app-image --win-console
```

This creates a `SwingApp` folder containing:
- `SwingApp.exe` - The executable
- `runtime` folder - Bundled Java runtime
- `app` folder - Your application JAR

**To run**: Simply double-click `SwingApp\SwingApp.exe` or distribute the entire `SwingApp` folder.

#### Option B: Create Windows Installer (Requires WiX Toolset)

To create a proper `.exe` installer, you need to install WiX Toolset first.

### Installing WiX Toolset

**Method 1: Using winget (Recommended)**
```powershell
winget install -e --id WiXToolset.WiX
```

**Method 2: Manual Download**
1. Visit https://wixtoolset.org/
2. Download WiX Toolset v3.x or later
3. Install and add to your PATH environment variable

**Verify Installation:**
```powershell
wix --version
# or for WiX v3
light.exe -?
```

After installing WiX, **restart your terminal** and run:

```powershell
jpackage --input target --name SwingApp --main-jar javafxterminaltoexe-1.0-SNAPSHOT.jar --main-class org.example.Main --type exe --win-console
```

This will create `SwingApp-1.0.exe` installer file.

### Creating MSI Installer (Alternative)

```powershell
jpackage --input target --name SwingApp --main-jar javafxterminaltoexe-1.0-SNAPSHOT.jar --main-class org.example.Main --type msi --win-console
```

## Running the Application

### From JAR:
```powershell
# No file (shows welcome screen)
java -jar target\javafxterminaltoexe-1.0-SNAPSHOT.jar

# Open a specific file
java -jar target\javafxterminaltoexe-1.0-SNAPSHOT.jar example.java
```

### From Native Executable:
```powershell
# No file (shows welcome screen)
.\SwingApp\SwingApp.exe

# Open a specific file
.\SwingApp\SwingApp.exe example.java
```

### From Installer:
Run the installer and it will install the application to your system.

## Tips for Using the VIM Editor

1. **Browse files**: Press `Ctrl+N` to open the file explorer, navigate with arrow keys, press `Enter` to open a file
2. **Start editing**: Press `i` to enter INSERT mode, type your text, press `ESC` to return to NORMAL mode
3. **Save your work**: Press `:`, type `w`, press `Enter`
4. **Exit**: Press `:`, type `q`, press `Enter`
5. **Save and exit**: Press `:`, type `wq`, press `Enter`
6. **Get help anytime**: Press `:`, type `help`, press `Enter`

## Distribution

- **App-Image**: Distribute the entire `SwingApp` folder (contains bundled runtime)
- **Installer**: Distribute the `.exe` or `.msi` installer file

## Additional jpackage Options

### Custom Icon
```powershell
jpackage --input target --name SwingApp --main-jar javafxterminaltoexe-1.0-SNAPSHOT.jar --main-class org.example.Main --type app-image --win-console --icon path\to\icon.ico
```

### Set Version
```powershell
jpackage --input target --name SwingApp --main-jar javafxterminaltoexe-1.0-SNAPSHOT.jar --main-class org.example.Main --type app-image --win-console --app-version 1.0.0
```

### Add Description
```powershell
jpackage --input target --name SwingApp --main-jar javafxterminaltoexe-1.0-SNAPSHOT.jar --main-class org.example.Main --type app-image --win-console --description "Simple Swing Application"
```

## Troubleshooting

### jpackage not found
- Make sure you're using JDK 16 or later
- Check that `JAVA_HOME` is set correctly
- Verify jpackage is in your PATH: `jpackage --version`

### WiX Toolset not found
- Install WiX Toolset using the instructions above
- Restart your terminal/PowerShell after installation
- Verify WiX is in PATH: `wix --version` or `light.exe -?`

### Application doesn't start
- Try running from command line to see error messages
- Ensure all dependencies are included in the JAR
- Check that the main class is correct: `org.example.Main`

## Project Structure

```
javafxterminaltoexe/
├── src/
│   └── main/
│       ├── java/
│       │   └── org/
│       │       └── example/
│       │           └── Main.java
│       └── resources/
├── target/
│   └── javafxterminaltoexe-1.0-SNAPSHOT.jar
├── SwingApp/                    (created by jpackage)
│   ├── SwingApp.exe
│   ├── runtime/
│   └── app/
├── pom.xml
└── README.md
```

## Notes

- The `--win-console` flag keeps the console window open (useful for debugging)
- Remove `--win-console` if you want a GUI-only application without console
- The app-image approach is recommended as it doesn't require WiX installation
- The bundled runtime makes the application self-contained (no JRE needed on target machine)
