# Running JavaVim with Gradle

This guide explains how to run JavaVim using Gradle, including troubleshooting common issues.

## Quick Start

### Running the Application

```bash
# Using Gradle wrapper (recommended)
./gradlew run

# Using local Gradle installation
gradle run

# Run without daemon (if cache issues occur)
gradle run --no-daemon
```

### Building the Project

```bash
# Clean and build everything
./gradlew clean build

# Compile C++ modules only
./gradlew compileCpp

# Run tests
./gradlew test

# Create distribution package
./gradlew jpackage
```

## Troubleshooting

### Issue: Gradle Cache Corruption
**Error:** `Could not read workspace metadata from...`

**Solution:**
```bash
# Stop all Gradle daemons
gradle --stop

# Clean cache and run without daemon
rm -rf ~/.gradle/caches/9.0.0
gradle run --no-daemon
```

### Issue: Application Builds but Window Doesn't Open
**Symptoms:** `BUILD SUCCESSFUL` but no JavaFX window appears

**Possible Causes & Solutions:**

1. **Window opened behind other applications**
   - Check taskbar for JavaVim window
   - Alt+Tab to cycle through open applications
   - Check all monitors if using multiple displays

2. **JavaFX display issues**
   ```bash
   # Run with verbose JavaFX output
   gradle run -Dprism.verbose=true
   ```

3. **Java 24 compatibility warnings**
   - The warnings about `java.lang.System::load` are normal
   - Application should still open despite warnings

### Issue: Java Version Mismatch
**Error:** `Unsupported class file major version 68`

**Solution:**
```bash
# Verify Java version
java -version
# Should show: openjdk version "24.0.2"

# Set JAVA_HOME if needed
export JAVA_HOME=/path/to/java24
```

### Issue: C++ Compilation Fails
**Error:** `'cl' is not recognized as an internal or external command`

**Solution:**
1. Install Visual Studio Build Tools 2022
2. Run from **x64 Native Tools Command Prompt for VS 2022**
3. Or skip C++ compilation temporarily:
   ```bash
   gradle run -x compileCpp
   ```

## Advanced Options

### Debug Mode
```bash
# Run with debug information
gradle run --debug

# Run with JavaFX debug output
gradle run -Dprism.verbose=true -Djavafx.verbose=true
```

### Custom JVM Arguments
```bash
# Run with additional JVM args
gradle run -Dexec.args="--enable-native-access=javafx.graphics"
```

### Profile Build Performance
```bash
# Generate build scan
gradle run --scan
```

## System Requirements

- **Java 24**: Amazon Corretto 24 recommended
- **Gradle 9+**: Required for Java 24 support  
- **JavaFX 23**: Included automatically via Gradle plugin
- **Visual Studio Build Tools**: For C++ modules (Windows only)

## Expected Output

When running successfully, you should see:

```
> Task :compileCpp
Compiling C++ modules with MSVC...
C++ module compiled successfully

> Task :compileJava UP-TO-DATE
> Task :processResources 
> Task :classes

> Task :run

BUILD SUCCESSFUL in 20s
```

**Note:** JavaFX warnings about native access are normal with Java 24 and don't prevent the application from running.

## Verifying the Application

Once running, JavaVim should display:
- **Dark themed window** (1024x768 pixels)
- **Text area** with welcome message
- **Monospace font** (Consolas/Monaco/Courier New)
- **Dark background** (#1e1e1e) with light text (#d4d4d4)

If the window doesn't appear, check:
1. Windows taskbar for JavaVim application
2. Task Manager for `java.exe` process running JavaFX
3. Console output for any error messages after the warnings

## Performance Notes

- First run may be slower due to Gradle dependency download
- C++ compilation adds ~5 seconds to build time
- Subsequent runs with `--no-daemon` avoid cache issues but are slower
- Using `./gradlew` ensures consistent Gradle version