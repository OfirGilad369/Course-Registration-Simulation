#ifndef SPL_NET_CONNECTIONREADER_H
#define SPL_NET_CONNECTIONREADER_H

#include <string>
#include <iostream>

using namespace std;

class ConnectionReader {

public:
    ConnectionReader(ConnectionHandler* connectionHandler,bool *toLogout, bool* toTerminate);

    void run();
    short bytesToShort(char* bytesArray);
private:
    ConnectionHandler* connectionHandler;
    bool* toLogout;
    bool* toTerminate;
};

#endif //SPL_NET_CONNECTIONREADER_H
