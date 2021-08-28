#ifndef SPL_NET_KEYBOARDREADER_H
#define SPL_NET_KEYBOARDREADER_H

#include <string>
#include <iostream>

using namespace std;

class KeyboardReader {

public:
    KeyboardReader(ConnectionHandler* connectionHandler, bool *toLogout, bool* toTerminate);

    void run();
    void shortToBytes(short num, char* bytesArray);
private:
    ConnectionHandler* connectionHandler;
    bool* toLogout;
    bool* toTerminate;
};

#endif //SPL_NET_KEYBOARDREADER_H
