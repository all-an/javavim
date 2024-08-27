#!/bin/bash
sudo rm -rf build

# Set project directories
JAVA_TEST_DIR="src/test/javatest"
BUILD_DIR="build"
LIB_DIR="lib"
JAVA_SRC_DIR="src/java"

# Compile the Java main class and all test classes, including JLine and JUnit libraries
javac -g -d $BUILD_DIR -cp $LIB_DIR/jline-3.21.0.jar:$LIB_DIR/junit-jupiter-api-5.10.0.jar:$LIB_DIR/junit-jupiter-engine-5.10.0.jar:$LIB_DIR/junit-platform-console-standalone-1.10.0.jar:$LIB_DIR/apiguardian-api-1.1.2.jar:$LIB_DIR/opentest4j-1.2.0.jar $JAVA_SRC_DIR/Javavim.java $JAVA_SRC_DIR/JavavimTextBuffer.java $JAVA_TEST_DIR/*.java

# Run the JUnit tests with the native library path specified
java -Djava.library.path=$BUILD_DIR -cp $BUILD_DIR:$LIB_DIR/jline-3.21.0.jar:$LIB_DIR/junit-platform-console-standalone-1.10.0.jar org.junit.platform.console.ConsoleLauncher execute --scan-classpath
