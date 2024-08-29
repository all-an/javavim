# Compile the Java code and place the classes in the build directory
javac -d build -cp lib/junit-jupiter-api-5.8.2.jar:lib/junit-jupiter-engine-5.8.2.jar:lib/junit-platform-launcher-1.8.2.jar:lib/apiguardian-api-1.1.0.jar src/java/Javavim.java src/java/JavavimTest.java

# Run the Java code
java -Djava.library.path=src/cpp -cp build:lib/junit-jupiter-api-5.8.2.jar:lib/junit-jupiter-engine-5.8.2.jar:lib/junit-platform-launcher-1.8.2.jar:lib/apiguardian-api-1.1.0.jar:lib/junit-platform-console-standalone-1.8.2.jar org.junit.platform.console.ConsoleLauncher --class-path build --scan-class-path