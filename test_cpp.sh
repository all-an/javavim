#!/bin/bash

# Set project directories
CPP_TEST_DIR="src/test/cpptest"
BUILD_DIR="build"
CPP_SRC_DIR="src/cpp"

# Check if the C++ test file exists
if [ -f "$CPP_TEST_DIR/JavavimTest.cpp" ]; then
    # Compile and link the C++ test file with the MessagePrinter.cpp source file, including debug symbols (-g)
    g++ -g -o $BUILD_DIR/JavavimCppTest $CPP_TEST_DIR/JavavimTest.cpp $CPP_SRC_DIR/MessagePrinter.cpp -I $CPP_SRC_DIR
    if [ -f "$BUILD_DIR/JavavimCppTest" ]; then
        $BUILD_DIR/JavavimCppTest
    else
        echo "Error: $BUILD_DIR/JavavimCppTest not found."
    fi
else
    echo "Error: $CPP_TEST_DIR/JavavimTest.cpp not found."
    exit 1
fi
