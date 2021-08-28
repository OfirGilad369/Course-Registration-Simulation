package bgu.spl.net.srv;

import java.util.TreeMap;

public class Course {
    private final int courseNum;
    private final String courseName;
    private Integer[] KdamCoursesList;
    private final int numOfMaxStudent;
    private int currentNumberOfStudents;
    private final TreeMap<String, Student> registeredStudents;
    private final Integer courseId;
    private boolean isSorted;

    public Course (int courseNum, String courseName, Integer[] KdamCoursesList, int numOfMaxStudent, int courseId) {
        this.courseNum = courseNum;
        this.courseName = courseName;
        this.KdamCoursesList = KdamCoursesList;
        this.numOfMaxStudent = numOfMaxStudent;
        registeredStudents = new TreeMap<>();
        currentNumberOfStudents = 0;
        this.courseId = courseId;
        isSorted = false;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public boolean isFull() {
        return currentNumberOfStudents >= numOfMaxStudent;
    }

    public boolean isEligible(TreeMap<Integer, Course> courses) {
        for (int courseNum : KdamCoursesList) {
            if (courses.get(courseNum) == null) {
                return false;
            }
        }
        return true;
    }

    public synchronized boolean registerStudent(Student student) {
        if (!isFull() && isEligible(student.getCoursesByKeyCourseNum())) {
            currentNumberOfStudents ++;
            registeredStudents.put(student.getUsername(), student);
            return true;
        }
        else {
            return false;
        }
    }

    public synchronized boolean unregisterStudent(Student student) {
        if (registeredStudents.containsValue(student)) {
            currentNumberOfStudents --;
            registeredStudents.remove(student.getUsername(), student);
            return true;
        }
        else {
            return false;
        }
    }

    public void setSortedKdamCoursesList (Integer[] KdamCoursesList) {
        this.KdamCoursesList = KdamCoursesList;
        isSorted = true;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public Integer[] getKdamCoursesList() {
        return KdamCoursesList;
    }

    public synchronized int getNumberSeatsAvailable() {
        return numOfMaxStudent - currentNumberOfStudents;
    }

    public int getNumOfMaxStudent() {
        return numOfMaxStudent;
    }

    public synchronized TreeMap<String, Student> getRegisteredStudents() {
        return registeredStudents;
    }

    public boolean getIsSorted() {
        return isSorted;
    }
}
