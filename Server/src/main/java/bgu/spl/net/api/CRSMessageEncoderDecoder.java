package bgu.spl.net.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class CRSMessageEncoderDecoder implements MessageEncoderDecoder <Message> {
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private short opCode = -1;
    private short courseNum = -1;
    private String username = null;
    private String password = null;
    private int len = 0;
    private int byteIndex = -1;
    private boolean isEndOfMessage = false;

    @Override
    public Message decodeNextByte(byte nextByte) {
        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        pushByte(nextByte);
        byteIndex++;
        if (byteIndex == 1) {
            opCode = bytesToShort();
            popString();
        }
        if (opCode != -1) {
            if (opCode == 1 || opCode == 2 || opCode == 3) {
                if (username == null && nextByte == '\0') {
                    username = popString();
                }
                else if (password == null && nextByte == '\0') {
                    password = popString();
                    isEndOfMessage = true;
                }
            }
            if (opCode == 4 || opCode == 11) {
                isEndOfMessage = true;
            }
            if (opCode == 5 || opCode == 6 || opCode == 7 || opCode == 9 || opCode == 10) {
                if (byteIndex == 3) {
                    courseNum = bytesToShort();
                    isEndOfMessage = true;
                    popString();
                }
            }
            if (opCode == 8) {
                if (username == null && nextByte == '\0') {
                    username = popString();
                    isEndOfMessage = true;
                }
            }
        }
        if (isEndOfMessage) {
            //Get all data to runtime allocated variables
            int opCodeToSend = opCode;
            int courseNumToSend = courseNum;
            String usernameToSend = username;
            String passwordToSend = password;

            //Reset all data
            opCode = -1;
            courseNum = -1;
            username = null;
            password = null;
            byteIndex = -1;
            isEndOfMessage = false;

            switch (opCodeToSend) {
                case 1:
                    return new AdminRegister(usernameToSend, passwordToSend);
                case 2:
                    return new StudentRegister(usernameToSend, passwordToSend);
                case 3:
                    return new LoginRequest(usernameToSend, passwordToSend);
                case 4:
                    return new LogoutRequest();
                case 5:
                    return new RegisterToCourse(courseNumToSend);
                case 6:
                    return new CheckKdamCourse(courseNumToSend);
                case 7:
                    return new PrintCourseStatus(courseNumToSend);
                case 8:
                    return new PrintStudentStatus(usernameToSend);
                case 9:
                    return new CheckIfRegistered(courseNumToSend);
                case 10:
                    return new UnregisterToCourse(courseNumToSend);
                case 11:
                    return new CheckMyCurrentCourses();
            }
        }
        return null; //not a line yet
    }

    @Override
    public byte[] encode(Message message) {
        short opCodeToConvert = (short) message.getOpCode();
        short messageOpCodeToConvert;
        ByteArrayOutputStream messageToReturn = new ByteArrayOutputStream();
        //ACK
        if (opCodeToConvert == 12) {
            try {
                messageOpCodeToConvert = (short) ((Acknowledgement)message).getMessageOpCode();
                messageToReturn.write(shortToBytes(opCodeToConvert));
                messageToReturn.write(shortToBytes(messageOpCodeToConvert));
                switch (messageOpCodeToConvert) {
                    case 6:
                        messageToReturn.write((((Acknowledgement)message).getKdamCoursesList() + '\0').getBytes());
                        break;
                    case 7:
                        messageToReturn.write((((Acknowledgement)message).getCourseNumberAndName() + '\n').getBytes());
                        messageToReturn.write((((Acknowledgement)message).getSeatsAvailable() + '\n').getBytes());
                        //Trim the zeros
                        messageToReturn.write(bytesTrim((((Acknowledgement)message).getStudentsRegistered().getBytes())));
                        break;
                    case 8:
                        byte[] userNameBytes = (((Acknowledgement)message).getStudentStatsName()).getBytes();
                        if(userNameBytes[userNameBytes.length - 1] == '\0') {
                            userNameBytes[userNameBytes.length - 1] = '\n';
                            messageToReturn.write(userNameBytes);
                        }
                        else {
                            messageToReturn.write((((Acknowledgement) message).getStudentStatsName() + '\n').getBytes());
                        }
                        messageToReturn.write((((Acknowledgement)message).getStudentStats() + '\0').getBytes());
                        break;
                    case 11:
                        messageToReturn.write((((Acknowledgement)message).getStudentStats() + '\0').getBytes());
                        break;
                    case 9:
                        messageToReturn.write((((Acknowledgement)message).getIsRegistered() + '\0').getBytes());
                        break;
                }
            }
            catch (IOException ignored) { }
        }
        //ERROR
        else {
            try {
                messageOpCodeToConvert = (short) ((Error)message).getMessageOpCode();
                messageToReturn.write(shortToBytes(opCodeToConvert));
                messageToReturn.write(shortToBytes(messageOpCodeToConvert));
            }
            catch (IOException ignored) { }
        }
        return messageToReturn.toByteArray(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    private byte[] shortToBytes(short opCode) {
        byte[] opCodeBytes = new byte[2];
        opCodeBytes[0] = (byte) ((opCode >> 8) & 0xFF);
        opCodeBytes[1] = (byte) (opCode & 0xFF);
        return opCodeBytes;
    }

    //Convert the first 2 bytes to short
    private short bytesToShort(){
        byte[] byteArray = new byte[2];
        byteArray[0] = bytes[0];
        byteArray[1] = bytes[1];
        short convertedData = (short) ((byteArray[0] & 0xff) << 8);
        convertedData += (short) (byteArray[1] & 0xff);
        return convertedData;
    }

    private byte[] bytesTrim(byte[] byteArray) {
        LinkedList<Byte> byteList = new LinkedList<>();
        for (byte b : byteArray) {
            if (b != 0) {
                byteList.addLast(b);
            }
        }
        byte[] trimmedBytes = new byte[byteList.size() + 1];
        ListIterator<Byte> listIterator = byteList.listIterator();
        for (int i = 0; i < trimmedBytes.length - 1; i++) {
            trimmedBytes[i] = listIterator.next();
        }
        trimmedBytes[trimmedBytes.length - 1] = '\0';
        return trimmedBytes;
    }
}