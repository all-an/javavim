#include "../cpp/MessagePrinter.h"
#include "JavavimTest.h"

// Define a simple test for printMessage
void testPrintMessage() {
    printMessage(); // Expected output: "Message from a pure C++ function in Javavim!"
    assertTrue(true); // Placeholder, replace with actual checks if needed
}

int main() {
    runTest("testPrintMessage", testPrintMessage);
    return 0;
}
