#include "../include/connectionHandler.h"
#include "../include/KeyboardReader.h"
#include "../include/ConnectionReader.h"
#include <thread>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

using namespace std;

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    bool *toTerminate = new bool;
    bool *toLogout = new bool;

    KeyboardReader keyboardReader(&connectionHandler, toLogout, toTerminate);
    thread keyboardThread(&KeyboardReader::run, &keyboardReader);
    ConnectionReader connectionReader(&connectionHandler, toLogout, toTerminate);
    connectionReader.run();
    keyboardThread.join();

    delete toTerminate;
    delete toLogout;
    return 0;
}