package bgu.spl.net.srv;

import java.util.TreeMap;

public abstract class UserType {

    protected final String username;
    protected final String password;

    public UserType(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public abstract String getType();
}

class Student extends UserType {
    private final TreeMap<Integer, Course> coursesByKeyCourseId;
    private final TreeMap<Integer, Course> coursesByKeyCourseNum;

    public Student(String username, String password) {
        super(username, password);
        coursesByKeyCourseId = new TreeMap<>();
        coursesByKeyCourseNum = new TreeMap<>();
    }

    public String getType() {
        return "Student";
    }

    //All Student class methods are synchronized since every change in the student's courses HashMap effect all this functions output
    //Example: when Student requests: COURSEREG, and Admin requests: this STUDENTSTAT
    public synchronized TreeMap<Integer, Course> getCoursesByKeyCourseId() {
        return coursesByKeyCourseId;
    }

    public synchronized TreeMap<Integer, Course> getCoursesByKeyCourseNum() {
        return coursesByKeyCourseNum;
    }

    public synchronized boolean registerToCourse(Course course) {
        if (!coursesByKeyCourseId.containsValue(course)) {
            if (course.registerStudent(this)) {
                coursesByKeyCourseId.put(course.getCourseId(), course);
                coursesByKeyCourseNum.put(course.getCourseNum(), course);
                //"User registered successfully"
                return true;
            }
        }
        //"User isn't meet the requirement"
        return false;
    }

    public synchronized boolean unregisterToCourse(Course course) {
        if (coursesByKeyCourseId.containsValue(course)) {
            if (course.unregisterStudent(this)) {
                coursesByKeyCourseId.remove(course.getCourseId(), course);
                coursesByKeyCourseNum.remove(course.getCourseNum(), course);
                //"User unregistered successfully"
                return true;
            }
        }
        //"User is already unregistered"
        return false;
    }

    public synchronized boolean isRegisteredToCourse(Course course) {
        return coursesByKeyCourseId.containsValue(course);
    }
}

class Admin extends UserType {

    public Admin(String username, String password) {
        super(username, password);
    }

    public String getType() {
        return "Admin";
    }
}
