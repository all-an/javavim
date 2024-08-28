#include <jni.h>
#include <iostream>
#include "Javavim.h"

JNIEXPORT void JNICALL Java_Javavim_sayHello(JNIEnv *env, jobject obj) {
    std::cout << "Hello from C++!" << std::endl;
}