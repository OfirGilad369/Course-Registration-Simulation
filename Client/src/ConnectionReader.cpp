#include "../include/connectionHandler.h"
#include "../include/ConnectionReader.h"

using namespace std;

ConnectionReader::ConnectionReader(ConnectionHandler* connectionHandler, bool* toLogout, bool* toTerminate): connectionHandler(connectionHandler), toLogout(toLogout), toTerminate(toTerminate)  {}

void ConnectionReader::run() {
    *toTerminate = false;
    *toLogout = false;
    while (!(*toTerminate)) {
        char* opCodeArray = new char[2];
        connectionHandler->getBytes(opCodeArray, 2);
        short opCode = bytesToShort(opCodeArray);
        string outPut;

        if (opCode==12) {
            outPut = "ACK";
            connectionHandler->getBytes(opCodeArray, 2);
            short msgOpCode = bytesToShort(opCodeArray);
            outPut = outPut + " " + to_string(msgOpCode);
            string msgData;

            if(msgOpCode == 6 || msgOpCode == 9 || msgOpCode == 11) {
                connectionHandler->getLine(msgData);
                outPut= outPut + '\n' + msgData;
                msgData = "";
            }

            if (msgOpCode == 7) {
                connectionHandler->getLine(msgData);
                outPut = outPut + '\n' + msgData;
                msgData = "";
            }

            if (msgOpCode == 8) {
                connectionHandler->getLine(msgData);
                outPut = outPut + '\n' + msgData;
                msgData = "";
            }

            if(msgOpCode == 4) {
                *toTerminate = true;
            }
        }

        if (opCode==13) {
            outPut="ERROR";
            connectionHandler->getBytes(opCodeArray, 2);
            short errorCode = bytesToShort(opCodeArray);
            outPut = outPut + " " + to_string(errorCode);
            if (errorCode == 4) {
                *toLogout = false;
            }
        }
        if (outPut != "")
            cout << outPut << endl;

        delete[] opCodeArray;
    }
}

//This class is used to convert data from Bytes to Short
short ConnectionReader::bytesToShort(char* bytesArray) {
    short result = (short)((bytesArray[0] & 0xff) << 8);
    result += (short)(bytesArray[1] & 0xff);
    return result;
}