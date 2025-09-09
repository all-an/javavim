# JavaVim

A modern vim-style editor built with JavaFX, designed for Java code editing with vim-like controls and potential C++ optimizations through Java 24 FFM (Foreign Function & Memory) API.

<p align="center">
        <img align="center" width="344" height="342" src="/javavim-logo.png" />
</p>

## Features

- **Dark Theme UI**: Modern dark theme interface built with JavaFX
- **Vim-style Controls**: Complete vim keybindings and modal editing (Normal, Insert, Visual, Command modes)
- **Java 24 Ready**: Built to leverage Java 24's latest features including FFM API
- **JavaFX + Swing Hybrid**: Primary JavaFX interface with Swing components for legacy compatibility
- **High Performance**: C++ optimization modules for text processing via FFM when needed
- **Modern Build System**: Gradle-based build system with Java 24 toolchain support

## Requirements

- **Java 24**: Latest OpenJDK 24 (required)
- **JavaFX 23**: Modern JavaFX for GUI components  
- **Gradle 9+**: Build system with Java 24 support
- **C++ Compiler**: (Optional) For native optimization modules

## Quick Start

### Running the Application

```bash
# Using Gradle wrapper
./gradlew run

# Or with local Gradle installation  
gradle run
```

### Building

```bash
# Clean and build
./gradlew clean build

# Run tests
./gradlew test

# Create distribution
./gradlew jpackage
```

## Architecture

### Core Components

- **JavaFX GUI**: Primary user interface with dark theme
- **Vim Engine**: Modal editing logic ported from terminal version
- **Text Processing**: High-performance text manipulation with optional C++ modules
- **File Management**: Advanced file operations and project management
- **Language Integration**: Eclipse JDT integration for Java development

### Technology Stack

- **Frontend**: JavaFX 23 with custom dark theme CSS
- **Backend**: Pure Java 24 with preview features enabled
- **Native Extensions**: C++ modules via Java 24 FFM API
- **Build System**: Gradle 9 with JavaFX plugin
- **Testing**: JUnit 5 + TestFX for UI testing

## Development

### Project Structure

```
javavim/
├── src/main/java/com/javavim/
│   ├── JavavimApplication.java      # Main JavaFX application
│   ├── gui/                         # JavaFX UI components
│   ├── vim/                         # Vim logic and keybindings
│   ├── native/                      # FFM integration
│   └── legacy/                      # Original terminal components
├── src/main/resources/
│   ├── dark-theme.css              # Dark theme styling
│   └── fxml/                       # FXML layouts
├── src/main/cpp/                   # Native C++ modules
└── src/test/                       # Tests
```

### Key Features

1. **Modal Editing**: Full vim modes (Normal, Insert, Visual, Command)
2. **Dark Theme**: Professional dark color scheme optimized for long coding sessions
3. **Performance**: Native C++ modules for intensive text operations
4. **Modern Java**: Leverages Java 24 features and FFM API
5. **Cross-Platform**: JavaFX ensures consistent experience across Windows, macOS, and Linux

### Building Native Modules (C++)

JavaVim uses C++ modules for performance-critical text operations via Java 24 FFM API.

#### Prerequisites
- **Visual Studio Community 2022** or **Build Tools for Visual Studio 2022** (free)
- Includes MSVC compiler (`cl.exe`) and x64 Native Tools Command Prompt

#### Building on Windows

**IMPORTANT:** Use the **x64 Native Tools Command Prompt** (not regular command prompt):

1. Search for "**x64 Native Tools Command Prompt for VS**" in Start menu
2. Navigate to your project directory
3. Build C++ modules:

```cmd
# Automatic compilation via Gradle
.\gradlew compileCpp

# Manual compilation for text processing module
cd src\main\cpp
cl /LD textutils.cpp /Fe:textutils.dll
```

#### Building on Linux/macOS
```bash
cd src/main/cpp
g++ -shared -fPIC -o libtextutils.so textutils.cpp
```

The build system automatically detects your platform and compiles the appropriate native library format (.dll on Windows, .so on Linux/macOS).

## Configuration

The application supports various JVM arguments for optimal performance:

```bash
# Enable Java 24 preview features and native access
java --enable-preview --enable-native-access=ALL-UNNAMED --enable-native-access=javafx.graphics
```

## Vim Bindings

JavaVim implements core vim functionality:

- **Normal Mode**: `h`, `j`, `k`, `l` navigation, `i` for insert, `:` for commands
- **Insert Mode**: Regular typing, `ESC` to return to normal mode
- **Visual Mode**: Text selection and manipulation
- **Command Mode**: File operations, search, and editor commands

## Contributing

JavaVim follows clean code principles with comprehensive testing:

- Single responsibility per method
- Comprehensive unit test coverage
- Modern Java idioms and patterns
- Cross-platform compatibility

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Roadmap

- [ ] Complete vim keybinding implementation
- [ ] Syntax highlighting for Java
- [ ] Language Server Protocol integration
- [ ] Plugin system architecture  
- [ ] Git integration
- [ ] File tree explorer
- [ ] Multiple buffer/tab support
- [ ] Configuration system