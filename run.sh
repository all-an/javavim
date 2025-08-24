#!/bin/bash

# Build and run Javavim
echo "Building JavaVim..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "Starting JavaVim..."
    java -jar target/javavim.jar
else
    echo "Build failed!"
    exit 1
fi