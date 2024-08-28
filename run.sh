#!/bin/bash

# Create the cpp directory if it doesn't exist
mkdir -p src/cpp

# Compile the Java code to generate the header file
javac -h src/cpp src/java/Javavim.java

# Compile the C++ code with JNI
g++ -I"$JAVA_HOME/include" -I"$JAVA_HOME/include/linux" -shared -fPIC -o src/cpp/libjavavim.so src/cpp/main.cpp

# Compile the Java code
javac -d . src/java/Javavim.java

# Run the Java code
java -Djava.library.path=src/cpp Javavim