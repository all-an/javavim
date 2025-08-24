# javavim

Terminal based vim like editor written in pure Java with Language Server integration

<p align="center">
        <a href="[https://www.linkedin.com/in/all-an/](https://www.linkedin.com/in/allan-pereira-abrahao/)">
            <img align="center" width="344" height="342"  src="/javavim-logo.png" />
        </a>
</p>

Requirements

    JDK 11+ (Java Development Kit): Required for compiling and running Java code.
    JUnit and Hamcrest: Included in the lib/ directory for Java testing.
    Lanterna and JLine: For terminal-based UI management.
    Eclipse JDT Language Server: For Java language server integration.

Notes

    The project uses JUnit for comprehensive Java testing with clean code principles.
    All methods follow single responsibility principle with no more than one conditional per method.
    The project is structured to support easy integration of additional features like a terminal-based interface, keybindings, and language server integration.

Project Goals:

    Implementing a terminal-based interface for the code editor.
    Adding Vim-like keybindings.
    Integrating Git functionality similar to Visual Studio Code's built-in features.
    Expanding testing frameworks for Java

Project extra Goals:

    Master clean code principles with single responsibility methods.
    Improve Java knowledge and best practices.
    Learn how to create a Java code editor in the terminal using Eclipse JDT.
    Implement comprehensive unit testing for all components.

Project Structure:

    Pure Java Implementation: Clean, maintainable Java code following SOLID principles.
    Comprehensive Unit Testing: JUnit-based testing for all components with high coverage.
    Minimalistic and Terminal-Based Approach: The project focuses on building a minimalistic code editor that runs in the terminal.
    Vim-like Keybindings: The project will include Vim-like keybindings by default.
    Integrated Terminal: The editor will have an integrated terminal within it.
    Git Integration: The project will include Git integration similar to Visual Studio Code's built-in Git features.
    File Explorer: Similar to Vim's NerdTree, the project will include a file explorer feature for easy navigation and file management within the editor.
    Build Tools: The project will avoid using Maven, Gradle, or similar build tools; all build processes will be managed manually or with custom scripts.
    Testing: JUnit for comprehensive Java testing with clean code principles.
    Debugging: JDB for debugging Java parts, with IDE integration support.
    Java Libraries:
        Picocli: For command-line interface (CLI) functionality.
        JLine or Lanterna: For terminal-based UI management.

Setup and Usage

1. Compile and Run the Project

To compile and run the project, use the run.sh script:

```
bash run.sh

```

2. Run the Java Tests

To compile and run the Java tests, use the test_java.sh script:

```
bash test_java.sh

```


## Javavim first project structure

## Overview

Javavim is a minimalistic terminal-based code editor project implemented using Java. It serves as a learning project to:

- Improve Java knowledge
- Learn how to create a Java code editor in the terminal using Eclipse JDT

This `README.md` provides a comprehensive overview of the **Javavim** project, including its structure, goals, and how to compile, run, and test the code. Let me know if you need any further updates or if there's anything else you'd like to add!