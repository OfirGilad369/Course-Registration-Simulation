package bgu.spl.net.srv;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

	private final ConcurrentHashMap<String, UserType> users;
	private final ConcurrentHashMap<Integer, Course> courses;
	private final ConcurrentHashMap<String, UserType> loggedInUsers;
	private int numberOfCourses;

	private static class SingletonHolder {
		private static final Database getInstance = new Database();
	}

	//to prevent user from creating new Database
	private Database() {
		users = new ConcurrentHashMap<>();
		courses = new ConcurrentHashMap<>();
		loggedInUsers = new ConcurrentHashMap<>();
		numberOfCourses = 0;
		String coursesFilePath = "./Courses.txt";
		if(!initialize(coursesFilePath)) {
			throw new IllegalArgumentException("Courses.txt not found");
		}
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Database getInstance() {
		return SingletonHolder.getInstance;
	}

	/**
	 * loads the courses from the file path specified
	 * into the Database, returns true if successful.
	 */
	boolean initialize(String coursesFilePath) {
		try(BufferedReader br = new BufferedReader(new FileReader(coursesFilePath))) {
			for(String line; (line = br.readLine()) != null; ) {
				String[] parts = line.split("\\|");

				int courseNum = Integer.parseInt(parts[0]);
				String courseName = parts[1];
				Integer[] KdamCoursesList = get_kdam_list_from_string(parts[2]);
				int numOfMaxStudent = Integer.parseInt(parts[3]);
				courses.put(courseNum,new Course(courseNum,courseName,KdamCoursesList,numOfMaxStudent,numberOfCourses));
				numberOfCourses++;
			}
		}
		catch (IOException e) {
			return false;
		}
		return true;
	}

	private Integer[] get_kdam_list_from_string(String kdam_str)
	{
		Integer[] kdam_int_arr = new Integer[0];
		kdam_str = kdam_str.substring(1,kdam_str.length()-1);
		if(!kdam_str.equals("")) {
			String[] kdam_str_arr = kdam_str.split(",");
			kdam_int_arr = new Integer[kdam_str_arr.length];
			for (int i = 0; i < kdam_str_arr.length; i++) {
				kdam_int_arr[i] = Integer.parseInt(kdam_str_arr[i]);
			}
		}
		return kdam_int_arr;
	}

	public String userType(String username) {
		return users.get(username).getType();
	}

	public boolean checkIfCourseExists(int courseNumber) {
		return courses.containsKey(courseNumber);
	}

	public boolean checkIfStudentExists(String userName) {
		return users.containsKey(userName);
	}

	//Used for: ADMINREG, STUDENTREG
	public synchronized boolean registerUser(String username, String password, String userType) {
		if (users.containsKey(username)) {
			//"User is already registered"
			return false;
		}
		else {
			if (userType.equals("Admin"))
				users.put(username,new Admin(username, password));
			else {
				users.put(username,new Student(username, password));
			}
			//"User registered successfully"
			return true;
		}
	}

	//Used for: LOGIN
	public synchronized boolean login(String username, String password) {
		if (users.containsKey(username)) {
			if (users.get(username).getPassword().equals(password)) {
				//"true if user hasn't logged in yet, else false"
				if (!loggedInUsers.containsKey(username)) {
					loggedInUsers.put(username, users.get(username));
					return true;
				}
			}
			else {
				//"User password is incorrect"
				return false;
			}
		}
		//"User is not registered";
		return false;
	}

	//Used for: LOGOUT
	public synchronized void logout(String username) {
		if (loggedInUsers.containsKey(username)) {
			loggedInUsers.remove(username, users.get(username));
			//"User logged out successfully"
		}
	}

	//Used for: COURSEREG
	public boolean registerToCourse(String username, int courseNumber) {
		UserType user = users.get(username);
		String userType = user.getType();
		if (userType.equals("Student")) {
			if (courses.containsKey(courseNumber)) {
				Course course = courses.get(courseNumber);
				return ((Student) user).registerToCourse(course);
			}
			else {
				//"Course number is not valid"
				return false;
			}
		}
		else {
			//"User is Admin"
			return false;
		}
	}

	//Used for: KDAMCHECK
	public String KdamCheck(int courseNumber) {
		if (courses.containsKey(courseNumber)) {
			Course course = courses.get(courseNumber);
			//Sort KdamCoursesList for the selected course
			if (!course.getIsSorted()) {
				course.setSortedKdamCoursesList(sortCourseArray(course.getKdamCoursesList()));
			}
			Integer[] kdamCourses = course.getKdamCoursesList();
			StringBuilder kdamCoursesString = new StringBuilder("[");
			for (int i = 0; i < kdamCourses.length; i ++) {
				kdamCoursesString.append(kdamCourses[i]);
				if (i + 1 != kdamCourses.length) {
					kdamCoursesString.append(",");
				}
			}
			kdamCoursesString.append("]");
			return kdamCoursesString.toString();
		}
		else {
			return "false";
		}
	}

	//Sorting courseArray according to the order in the Courses.txt file
	private Integer[] sortCourseArray(Integer[] coursesArray) {
		Arrays.sort(coursesArray, (Comparator.comparing(courseNum -> courses.get(courseNum).getCourseId())));
		return coursesArray;
	}

	//Used for: COURSESTAT
	public String courseStatsCourseNumberAndName(int courseNumber) {
		if (courses.containsKey(courseNumber)) {
			Course course = courses.get(courseNumber);
			return "(" + courseNumber + ") " + course.getCourseName();
		}
		else {
			return null;
		}
	}

	//Used for: COURSESTAT
	public String courseStatsSeatsAvailable(int courseNumber) {
		if (courses.containsKey(courseNumber)) {
			Course course = courses.get(courseNumber);
			return course.getNumberSeatsAvailable() + "/" + course.getNumOfMaxStudent();
		}
		else {
			return null;
		}
	}

	//Used for: COURSESTAT
	public String courseStatsStudentsRegistered(int courseNumber) {
		if (courses.containsKey(courseNumber)) {
			Set courseSet = courses.get(courseNumber).getRegisteredStudents().entrySet();
			Iterator courseIterator = courseSet.iterator();
			StringBuilder studentRegistered = new StringBuilder("[");
			while (courseIterator.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) courseIterator.next();
				studentRegistered.append(mapEntry.getKey());
				if (courseIterator.hasNext()) {
					studentRegistered.append(",");
				}
			}
			studentRegistered.append("]");
			return studentRegistered.toString();
		}
		else {
			return null;
		}
	}

	//used for: STUDENTSTAT, MYCOURSES
	public String studentStats(String username) {
		if (users.containsKey(username)) {
			UserType user = users.get(username);
			String userType = user.getType();
			if (userType.equals("Student")) {
				//Export all the HashMap data to int array
				Set coursesSet = ((Student) user).getCoursesByKeyCourseId().entrySet();
				Iterator coursesIterator = coursesSet.iterator();
				StringBuilder coursesRegistered = new StringBuilder("[");
				while (coursesIterator.hasNext()) {
					Map.Entry mapEntry = (Map.Entry) coursesIterator.next();
					coursesRegistered.append(((Course) mapEntry.getValue()).getCourseNum());
					if (coursesIterator.hasNext()) {
						coursesRegistered.append(",");
					}
				}
				coursesRegistered.append("]");
				return coursesRegistered.toString();
			}
			else {
				//"User is Admin"
				return "false";
			}
		}
		else {
			//"User does not exist"
			return "false";
		}
	}

	//used for: ISREGISTERED
	public String isRegistered(String username, int courseNumber) {
		UserType user = users.get(username);
		String userType = user.getType();
		if (userType.equals("Student")) {
			if (((Student) user).isRegisteredToCourse(courses.get(courseNumber))) {
				//"Student is registered"
				return "REGISTERED";
			}
			else {
				//"Student is not registered"
				return "NOT REGISTERED";
			}
		}
		else {
			//"User is Admin"
			return "NOT REGISTERED";
		}
	}

	//used for: UNREGISTER
	public boolean unregisterToCourse(String username, int courseNumber) {
		UserType user = users.get(username);
		String userType = user.getType();
		if (userType.equals("Student")) {
			if (courses.containsKey(courseNumber)) {
				Course course = courses.get(courseNumber);
				return ((Student) user).unregisterToCourse(course);
			}
			else {
				//"Course number is not valid"
				return false;
			}
		}
		else {
			//"User is Admin"
			return false;
		}
	}
}