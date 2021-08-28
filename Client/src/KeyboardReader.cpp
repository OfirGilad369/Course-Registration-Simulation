#include "../include/connectionHandler.h"
#include "../include/KeyboardReader.h"
#include "boost/lexical_cast.hpp"

#include <boost/algorithm/string.hpp>

using namespace std;

KeyboardReader::KeyboardReader(ConnectionHandler* connectionHandler, bool* toLogout, bool* toTerminate): connectionHandler(connectionHandler), toLogout(toLogout), toTerminate(toTerminate)  {}

void KeyboardReader::run() {
    *toTerminate = false;
    *toLogout = false;
    while (!(*toTerminate)) {
        bool toClose = false;
        while (*toLogout) {
            if (*toTerminate) {
                toClose = true;
                break;
            }
        }
        if (toClose)
            break;

        const short bufsize = 1024;
        char buf[bufsize];
        cin.getline(buf, bufsize);
        string line(buf);
        vector <string> onScreenText;

        boost::split(onScreenText, line, boost::is_any_of(" "));

        char dataToBytes[2];

        if (onScreenText[0] == "ADMINREG") {
            shortToBytes(1, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            connectionHandler->sendLine(onScreenText[1]);
            connectionHandler->sendLine(onScreenText[2]);
        }

        if (onScreenText[0] == "STUDENTREG") {
            shortToBytes(2, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            connectionHandler->sendLine(onScreenText[1]);
            connectionHandler->sendLine(onScreenText[2]);
        }

        if (onScreenText[0] == "LOGIN") {
            shortToBytes(3, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            connectionHandler->sendLine(onScreenText[1]);
            connectionHandler->sendLine(onScreenText[2]);
        }

        if (onScreenText[0] == "LOGOUT") {
            shortToBytes(4, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            *toLogout = true;
        }

        if (onScreenText[0] == "COURSEREG") {
            shortToBytes(5, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            short courseNum;
            courseNum = boost::lexical_cast<short>(onScreenText[1]);
            shortToBytes(courseNum, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
        }

        if (onScreenText[0] == "KDAMCHECK") {
            shortToBytes(6, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            short courseNum;
            courseNum = boost::lexical_cast<short>(onScreenText[1]);
            shortToBytes(courseNum, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
        }

        if (onScreenText[0] == "COURSESTAT") {
            shortToBytes(7, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            short courseNum;
            courseNum = boost::lexical_cast<short>(onScreenText[1]);
            shortToBytes(courseNum, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
        }

        if (onScreenText[0] == "STUDENTSTAT") {
            shortToBytes(8, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            connectionHandler->sendLine(onScreenText[1]);
        }

        if (onScreenText[0] == "ISREGISTERED") {
            shortToBytes(9, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            short courseNum;
            courseNum = boost::lexical_cast<short>(onScreenText[1]);
            shortToBytes(courseNum, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
        }

        if (onScreenText[0] == "UNREGISTER") {
            shortToBytes(10, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
            short courseNum;
            courseNum = boost::lexical_cast<short>(onScreenText[1]);
            shortToBytes(courseNum, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
        }

        if (onScreenText[0] == "MYCOURSES") {
            shortToBytes(11, dataToBytes);
            connectionHandler->sendBytes(dataToBytes, 2);
        }
    }
}

//This class is used to convert data from Short to Bytes
void KeyboardReader::shortToBytes(short num, char* bytesArray) {
    bytesArray[0] = ((num >> 8) & 0xFF);
    bytesArray[1] = (num & 0xFF);
}