#include <jni.h>
#include <iostream>
#include "Javavim.h"
#include "MessagePrinter.h"  // Include the header for the pure C++ function

// Implementation of the native method sayHello()
JNIEXPORT void JNICALL Java_Javavim_sayHello(JNIEnv *env, jobject thisObj) {
    std::cout << "Hello AGAIN from C++ in Javavim!" << std::endl;
    printMessage();  // Call the pure C++ function
}
