#ifndef KOGEI_TEST_H
#define KOGEI_TEST_H

#include <iostream>

void assertTrue(bool condition) {
    if (!condition) {
        std::cerr << "Test failed" << std::endl;
    } else {
        std::cout << "Test passed" << std::endl;
    }
}

void runTest(const std::string& testName, void (*testFunc)()) {
    std::cout << "Running " << testName << "..." << std::endl;
    testFunc();
    std::cout << testName << " completed" << std::endl;
}

#endif // KOGEI_TEST_H
