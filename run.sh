#!/bin/bash

# Set project directories
BUILD_DIR="build"
LIB_DIR="lib"
JAVA_SRC_DIR="src/java"

# Compile the Java classes
javac -g -d $BUILD_DIR -cp $LIB_DIR/jline-3.21.0.jar $JAVA_SRC_DIR/Javavim.java $JAVA_SRC_DIR/JavavimTextBuffer.java

# Compile the JNI C++ code (if applicable)
g++ -shared -fPIC -o $BUILD_DIR/libjavavim.so -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" src/cpp/Javavim.cpp src/cpp/MessagePrinter.cpp

# Run the Java application
java -Djava.library.path=$BUILD_DIR -cp $BUILD_DIR:$LIB_DIR/jline-3.21.0.jar Javavim
