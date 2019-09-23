package Classes;

import Interfaces.User;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

public class Student implements User, Comparable<Student>, Serializable {

    private String name;
    private int id;
    private double gpa;
    private ArrayList<Course> waitListCourses = new ArrayList<>();
    private ArrayList<Course> courses = new ArrayList<>();
    private ArrayList<Course> prospectiveCourses = new ArrayList<>();
    private ArrayList<Course> compCourses = new ArrayList<>();
    private int totalCredits;
    private int creditsNeeded;
    private String major;
    private ArrayList<Integer> IDS = new ArrayList<>();

    Random rand = new Random();
    private static int lookupHelper;

    //Constructor for a student with a minor
    public Student(String name, double gpa, int totalCredits, int creditsNeeded, String major) {
        this.name = name;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.creditsNeeded = creditsNeeded;
        this.major = major;
        this.id = generateID();
        Administrator.userMaster.add(this);
        Administrator.studentMaster.add(this);
    }

    public Student(String name, double gpa, int totalCredits, int creditsNeeded, String major, ArrayList<Course> compCourses) {
        this.name = name;
        this.gpa = gpa;
        this.totalCredits = totalCredits;
        this.creditsNeeded = creditsNeeded;
        this.major = major;
        this.compCourses = compCourses;
        this.id = generateID();
        Administrator.userMaster.add(this);
        Administrator.studentMaster.add(this);
    }

    public int generateID() {
        boolean isNew = false;
        int newID = rand.nextInt((999_999 - 100_000) + 1) + 100_000;
        while (isNew == false) {
            if (!IDS.contains(newID)) {
                IDS.add(newID);
                isNew = true;
            }
            newID = rand.nextInt((999_999 - 100_000) + 1) + 100_000;
        }
        return newID;
    }

    //Helper method to be used in addCourses()
    public boolean compareCourseTimes(Course eligableCourse) {
        int x;
        int i;
        for (Course c : courses) {
            for (String s : c.getDays()) {
                for (String t : eligableCourse.getDays()) {
                    switch (s) {
                        case "M":
                            x = 1;
                            break;
                        case "Tu":
                            x = 2;
                            break;
                        case "W":
                            x = 3;
                            break;
                        case "Th":
                            x = 4;
                            break;
                        case "F":
                            x = 5;
                            break;
                        default:
                            x = 0;
                    }

                    switch (t) {
                        case "M":
                            i = 1;
                            break;
                        case "Tu":
                            i = 2;
                            break;
                        case "W":
                            i = 3;
                            break;
                        case "Th":
                            i = 4;
                            break;
                        case "F":
                            i = 5;
                            break;
                        default:
                            i = 0;
                    }

                    if (x == i && c.getTime() == c.getTime()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<Course> accessWaitList() {
        return waitListCourses;
    }

    public void addProspectiveCourse(Course c) {
        prospectiveCourses.add(c);
    }

    public void clearProspectiveCourses() {
        if (prospectiveCourses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Prospective courses already empty");
        }
        prospectiveCourses.clear();
    }

    /**
     * Will check for eligibility of courses in shoppingCart and add the Courses
     * to their list and print the classes not added
     *
     * @param shoppingCart list of Courses
     * @return boolean whether all courses were added
     */
    public void addCourses(ArrayList<Course> shoppingCart) {
        boolean allAdded = false;
        ArrayList<Course> notAdded = new ArrayList();

        for (int x = 0; x < shoppingCart.size(); x++) {
            if (compareCourseTimes(shoppingCart.get(x)) && !isEnrolled(shoppingCart.get(x))) {
                if (shoppingCart.get(x).addStudent(this)) {
                    courses.add(shoppingCart.get(x));
                } else {
                    waitListCourses.add(shoppingCart.get(x));
                }
            } else {
                notAdded.add(shoppingCart.get(x));
                allAdded = false;
            }
        }
        if (!notAdded.isEmpty()) {
            String notAddedList = "";
            for (Course c : notAdded) {
                notAddedList = (c.getName() + "\n");
            }
            JOptionPane.showMessageDialog(null, "Courses not added: " + notAddedList);
        }
    }

    /**
     * Will check if the list contains the course and remove the course, if a
     * course is removed the wait list will be checked and updated appropriately
     *
     * @param c any Course object
     */
    public void removeCourse(Course c) {
        if (courses.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Courses are Empty");
        }
        if (courses.contains(c)) {
            c.removeStudent(this);
            courses.remove(c);
        } else if (waitListCourses.contains(c)) {
            c.removeStudentWait(this);
            waitListCourses.remove(c);
        } else if (prospectiveCourses.contains(c)) {
            prospectiveCourses.remove(c);
            //doesn't need to remove the student from the course as the student has not been enrolled yet
        } else {
            JOptionPane.showMessageDialog(null, "Course does not exist in any Student list");
        }
    }

    /**
     * will clear all of the courses for the student and check the waitList for
     * each course
     */
    public void CLEARCOURSES() {
        for (Course c : courses) {
            c.removeStudent(this);
        }
        courses.clear();
    }

    public void adminClearCourses() {
        courses.clear();
    }

    public void addCourse(Course c) {
        if (compareCourseTimes(c) && !isEnrolled(c)) {
            if (c.addStudent(this)) {
                courses.add(c);
                prospectiveCourses.remove(c);
            } else {
                waitListCourses.add(c);
                prospectiveCourses.remove(c);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Ineligable for course. Delete: the following lines should all return true to add the course");
            System.out.println("Times :" + compareCourseTimes(c));
            System.out.println("isEnrolled: " + !isEnrolled(c));
        }
    }

    public void adminAddCourse(Course c) {
        Administrator.enrolledList(c).add(this);
        this.courses.add(c);
    }

    public void addCompCourseList(ArrayList<Course> c) {
        this.compCourses.addAll(c);
    }

    public void adminAddCompCourse(Course c) {
        this.compCourses.add(c);
    }

    public void adminRemoveCompCourse(Course c) {
        this.compCourses.remove(c);
    }

    /**
     * Will return the number of courses the student is taking
     *
     * @return
     */
    public int numCourses() {
        return courses.size();
    }

    public boolean isEnrolled(Course c) {
        for (Course e : courses) {
            if (c.equals(e)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getID() {
        return id;
    }

    @Override
    public int compareTo(Student s) {
        return ((this.getID() < s.getID()) ? -1 : (this.getID() == s.getID() ? 0 : 1));
    }

    @Override
    public String toString() {
        return "ID: " + id + " Name: " + name;
    }

    /**
     * adds course to completed courses, performs proper credits arithmetic, and
     * removes course from current courses
     *
     * @param c
     */
    public void courseComplete(Course c) {
        compCourses.add(c);
        int credits = c.getCredits();
        totalCredits += credits;
        creditsNeeded -= credits;
        removeCourse(c);
    }

    /**
     * verifies the student is in studentMaster
     *
     * @param id
     * @return
     */
    public static boolean lookup(int id) {
        System.out.println("ID to check: " + id);
        if (Administrator.studentMaster.isEmpty()) {
            return false;
        }

        for (int x = 0; x < Administrator.studentMaster.size(); x++) {
            if (id == Administrator.studentMaster.get(x).getID()) {
                lookupHelper = x;
                System.out.println("Found match with id: " + Administrator.studentMaster.get(x).getID());
                return true;
            }
        }
        return false;
    }

    public static Student iDFinder() {
        return Administrator.studentMaster.get(lookupHelper);
    }

    public ArrayList<String> printingWaitList() {
        ArrayList<String> wCourses = new ArrayList<>();

        for (Course c : waitListCourses) {
            wCourses.add("(!) " + c.toString());
        }
        return wCourses;
    }

    /*
    Start of Getter and Setter
     */
    public void setName(String name) {
        this.name = name;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public ArrayList<Course> getProspectiveCourses() {
        return prospectiveCourses;
    }

    public ArrayList<Course> getCompCourses() {
        return compCourses;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public int getCreditsNeeded() {
        return creditsNeeded;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }
}
