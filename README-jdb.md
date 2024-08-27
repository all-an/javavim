Using JDB to Debug Java Code

JDB (Java Debugger) is a command-line tool for debugging Java applications. Here's how to use it with the Javavim project.
Step 1: Compile the Java Code

You’ve already set up the project to compile the Java code. To ensure it's ready for debugging, just run the test_java.sh script.

```
bash test_java.sh
```

Step 2: Start JDB

JDB can be used to start your Java application and attach the debugger right from the beginning.

Command to start JDB debugging class Javavim:

```
jdb -classpath build -sourcepath src/java -Djava.library.path=build Javavim


```

Basic JDB Commands:

    Choose any class to debug, in this case Javavim (but can be JavavimTextBuffer.java)
    Set a breakpoint: stop at <class>:<line> (e.g., stop at Javavim:10)
    Run the program: run
    Step through the code: step (steps into functions) or next (steps over functions)
    Print the value of a variable: print <variable_name> (e.g., print myVariable)
    Continue execution: cont
    Exit JDB: exit
