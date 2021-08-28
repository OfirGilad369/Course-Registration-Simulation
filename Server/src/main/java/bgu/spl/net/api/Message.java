package bgu.spl.net.api;

public abstract class Message {
    private final int opCode;

    protected Message(int opCode) {
        this.opCode=opCode;
    }

    public int getOpCode() {
        return opCode;
    }
}

class AdminRegister extends Message {
    private final String username;
    private final String password;

    public AdminRegister(String username, String password) {
        super(1);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class StudentRegister extends Message {
    private final String username;
    private final String password;

    public StudentRegister(String username, String password) {
        super(2);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class LoginRequest extends Message {
    private final String username;
    private final String password;

    public LoginRequest(String username, String password) {
        super(3);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class LogoutRequest extends Message {

    public LogoutRequest() {
        super(4);
    }
}

class RegisterToCourse extends Message {
    private final int courseNumber;

    public RegisterToCourse(int courseNumber) {
        super(5);
        this.courseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return courseNumber;
    }
}

class CheckKdamCourse extends Message {
    private final int courseNumber;

    public CheckKdamCourse(int courseNumber) {
        super(6);
        this.courseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return courseNumber;
    }
}

class PrintCourseStatus extends Message {
    private final int courseNumber;

    public PrintCourseStatus(int courseNumber) {
        super(7);
        this.courseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return courseNumber;
    }
}

class PrintStudentStatus extends Message {
    private final String studentUsername;

    public PrintStudentStatus(String studentUsername) {
        super(8);
        this.studentUsername = studentUsername;
    }

    public String getStudentUsername() {
        return studentUsername;
    }
}

class CheckIfRegistered extends Message {
    private final int courseNumber;

    public CheckIfRegistered(int courseNumber) {
        super(9);
        this.courseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return courseNumber;
    }
}

class UnregisterToCourse extends Message {
    private final int  courseNumber;

    public UnregisterToCourse(int courseNumber) {
        super(10);
        this.courseNumber = courseNumber;
    }

    public int getCourseNumber() {
        return courseNumber;
    }
}

class CheckMyCurrentCourses extends Message {

    public CheckMyCurrentCourses() {
        super(11);
    }
}

class Acknowledgement extends Message {
    private final int messageOpCode;
    private String KdamCoursesList = null;
    private String courseNumberAndName = null;
    private String seatsAvailable = null;
    private String studentsRegistered = null;
    private String studentStatsName = null;
    private String studentStats = null;
    private String isRegistered = null;

    public Acknowledgement(int messageOpCode) {
        super(12);
        this.messageOpCode = messageOpCode;
    }

    public int getMessageOpCode() {
        return messageOpCode;
    }

    public void setKdamCoursesList (String KdamCoursesList) {
        this.KdamCoursesList = KdamCoursesList;
    }

    public void setCourseNumberAndName(String courseNumberAndName) {
        this.courseNumberAndName = courseNumberAndName;
    }

    public void setSeatsAvailable (String seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public void setStudentsRegistered (String studentsRegistered) {
        this.studentsRegistered = studentsRegistered;
    }

    public void setStudentStatsName (String studentStatsName) {
        this.studentStatsName = studentStatsName;
    }

    public void setStudentStats (String studentStats) {
        this.studentStats = studentStats;
    }

    public void setIsRegistered (String isRegistered) {
        this.isRegistered = isRegistered;
    }

    public String getKdamCoursesList() {
        return KdamCoursesList;
    }

    public String getCourseNumberAndName() {
        return courseNumberAndName;
    }

    public String getSeatsAvailable() {
        return seatsAvailable;
    }

    public String getStudentsRegistered() {
        return studentsRegistered;
    }

    public String getStudentStatsName() {
        return studentStatsName;
    }

    public String getStudentStats() {
        return studentStats;
    }

    public String getIsRegistered() {
        return isRegistered;
    }
}

class Error extends Message {
    private final int messageOpCode;

    public Error(int messageOpCode) {
        super(13);
        this.messageOpCode = messageOpCode;
    }

    public int getMessageOpCode() {
        return messageOpCode;
    }
}