package bgu.spl.net.api;

import bgu.spl.net.srv.Database;

public class CRSMessagingProtocol implements MessagingProtocol<Message> {

    private final Database database;
    private boolean shouldTerminate = false;
    private boolean isLoggedIn = false;
    private int opCode;
    private String LoggedInUserUsername;
    private String username;
    private String password;
    private String userType;
    private int courseNum;
    private String studentUsername;

    public CRSMessagingProtocol (Database database) {
        this.database = database;
    }

    public Message process(Message msg) {
        opCode = msg.getOpCode();
        switch (opCode) {
            case 1:
                username = ((AdminRegister) msg).getUsername();
                password = ((AdminRegister) msg).getPassword();
                break;
            case 2:
                username = ((StudentRegister) msg).getUsername();
                password = ((StudentRegister) msg).getPassword();
                break;
            case 3:
                username = ((LoginRequest) msg).getUsername();
                password = ((LoginRequest) msg).getPassword();
                break;
            case 4:
                //LogoutRequest
                break;
            case 5:
                courseNum = ((RegisterToCourse) msg).getCourseNumber();
                break;
            case 6:
                courseNum = ((CheckKdamCourse) msg).getCourseNumber();
                break;
            case 7:
                courseNum = ((PrintCourseStatus) msg).getCourseNumber();
                break;
            case 8:
                studentUsername = ((PrintStudentStatus) msg).getStudentUsername();
                break;
            case 9:
                courseNum = ((CheckIfRegistered) msg).getCourseNumber();
                break;
            case 10:
                courseNum = ((UnregisterToCourse) msg).getCourseNumber();
                break;
            case 11:
                //CheckMyCurrentCourses
                break;
        }
        return runCommand();
    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    private Message runCommand () {
        Message outPutMessage = null;
        switch (opCode) {
            //ADMINREG
            case 1:
                if (!isLoggedIn && database.registerUser(username, password, "Admin")) {
                    outPutMessage = new Acknowledgement(opCode);
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //STUDENTREG
            case 2:
                if (!isLoggedIn && database.registerUser(username, password, "User")) {
                    outPutMessage = new Acknowledgement(opCode);
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //LOGIN
            case 3:
                if (!isLoggedIn && database.login(username, password)) {
                    isLoggedIn = true;
                    LoggedInUserUsername = username;
                    userType = database.userType(username);
                    outPutMessage = new Acknowledgement(opCode);
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //LOGOUT
            case 4:
                if (isLoggedIn) {
                    isLoggedIn = false;
                    database.logout(LoggedInUserUsername);
                    LoggedInUserUsername = null;
                    outPutMessage = new Acknowledgement(opCode);
                    shouldTerminate = true;
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //COURSEREG
            case 5:
                if (isLoggedIn && userType.equals("Student") && database.registerToCourse(LoggedInUserUsername, courseNum)) {
                    outPutMessage = new Acknowledgement(opCode);
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //KDAMCHECK
            case 6:
                if (isLoggedIn && userType.equals("Student")) {
                    String KdamCheckResult = database.KdamCheck(courseNum);
                    if (!KdamCheckResult.equals("false")) {
                        outPutMessage = new Acknowledgement(opCode);
                        ((Acknowledgement) outPutMessage).setKdamCoursesList(KdamCheckResult);
                    }
                    else {
                        outPutMessage = new Error(opCode);
                    }
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //COURSESTAT
            case 7:
                if (isLoggedIn && userType.equals("Admin") && database.checkIfCourseExists(courseNum)) {
                    outPutMessage = new Acknowledgement(opCode);
                    ((Acknowledgement) outPutMessage).setCourseNumberAndName("Course: " + database.courseStatsCourseNumberAndName(courseNum));
                    ((Acknowledgement) outPutMessage).setSeatsAvailable("Seats Available: " + database.courseStatsSeatsAvailable(courseNum));
                    ((Acknowledgement) outPutMessage).setStudentsRegistered("Students Registered: " + database.courseStatsStudentsRegistered(courseNum));
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //STUDENTSTAT
            case 8:
                if (isLoggedIn && userType.equals("Admin") && database.checkIfStudentExists(studentUsername)) {
                    outPutMessage = new Acknowledgement(opCode);
                    ((Acknowledgement) outPutMessage).setStudentStatsName("Student: " + studentUsername);
                    ((Acknowledgement) outPutMessage).setStudentStats("Courses: " + database.studentStats(studentUsername));
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //ISREGISTER
            case 9:
                if (isLoggedIn && userType.equals("Student") && database.checkIfCourseExists(courseNum)) {
                    outPutMessage = new Acknowledgement(opCode);
                    ((Acknowledgement) outPutMessage).setIsRegistered(database.isRegistered(LoggedInUserUsername, courseNum));
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //UNREGISTER
            case 10:
                if (isLoggedIn && userType.equals("Student") && database.checkIfCourseExists(courseNum) && database.unregisterToCourse(LoggedInUserUsername, courseNum)) {
                    outPutMessage = new Acknowledgement(opCode);
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
            //MYCOURSES
            case 11:
                if (isLoggedIn && userType.equals("Student")) {
                    outPutMessage = new Acknowledgement(opCode);
                    ((Acknowledgement) outPutMessage).setStudentStats(database.studentStats(LoggedInUserUsername));
                }
                else {
                    outPutMessage = new Error(opCode);
                }
                break;
        }
        return outPutMessage;
    }
}