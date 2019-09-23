package Classes;

import Interfaces.LinkedQueue;
import Interfaces.Queue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Course implements  Comparable<Course>, Serializable {

    private int[] days;

    private int time;

    private String name;

    private String major;

    private ArrayList<Course> prereq = new ArrayList<>();

    private Queue<Student> waitList = new LinkedQueue<>();

    private int capacity;

    private String teacher; //do we want this?

    private ArrayList<Student> enrolled = new ArrayList<>();

    private int credits;

    private int courseID;

    private ArrayList<Integer> IDS = new ArrayList<>();

    public Course(String name, int[] days, int time, String major, ArrayList<Course> prereq, int capacity, String teacher, int credits) {
        this.name = name;
        this.days = days;
        this.time = time;
        this.major = major;
        this.prereq = prereq;
        this.capacity = capacity;
        this.teacher = teacher;
        this.credits = credits;
        this.courseID = generateCourseID();
        Administrator.courseMaster.add(this);
    }

    public int generateCourseID() {
        Random rand = new Random();
        boolean isNew = false;
        int newID = rand.nextInt((9999 - 1000) + 1) + 1000;
        while (isNew == false) {
            if (!IDS.contains(newID)) {
                IDS.add(newID);
                isNew = true;
                break;
            }
            newID = rand.nextInt((9999 - 1000) + 1) + 1000;
        }
        return newID;
    }

    public boolean isFull() {
        return enrolled.size() == capacity;

    }

    public void addStudent(Student s) {
        if (isFull()) {
            addStudentWait(s);
        }
        enrolled.add(s);
    }

    public void addStudentWait(Student s) {
        waitList.enqueue(s);
    }

    public void checkWaitList() {
        if (!isFull() && !waitList.isEmpty()) {
            enrolled.add(waitList.dequeue());
        }
    }

    public void removeStudent(Student s) {
        if (enrolled.contains(s)) {
            enrolled.remove(s);
            s.removeCourse(this);
            checkWaitList();
        }
    }

    @Override
    public String toString() {
        return name + ", Instructor: " + teacher;
    }
    
    public String toStringlong() {
        return "Course{" + "time=" + time + ", name=" + name + ", major=" + major + ", capacity=" + capacity + ", teacher=" + teacher + ", credits=" + credits + ", courseID=" + courseID + '}';
    }

    @Override
    public int compareTo(Course c) {
        return ((this.getCourseID() < c.getCourseID()) ? -1 : (this.getCourseID() == c.getCourseID() ? 0 : 1));
    }

    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Course other = (Course) obj;
        if (this.time != other.time) {
            return false;
        }
        if (this.capacity != other.capacity) {
            return false;
        }
        if (this.credits != other.credits) {
            return false;
        }
        if (this.courseID != other.courseID) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.major, other.major)) {
            return false;
        }
        if (!Objects.equals(this.teacher, other.teacher)) {
            return false;
        }
        if (!Arrays.equals(this.days, other.days)) {
            return false;
        }
        if (!Objects.equals(this.prereq, other.prereq)) {
            return false;
        }
        if (!Objects.equals(this.waitList, other.waitList)) {
            return false;
        }
        if (!Objects.equals(this.enrolled, other.enrolled)) {
            return false;
        }
        return true;
    }

    
    
    public int[] getDays() {
        return days;
    }

    public void setDays(int[] days) {
        this.days = days;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public ArrayList<Course> getPrereq() {
        return prereq;
    }

    public void setPrereq(ArrayList<Course> Prereq) {
        this.prereq = Prereq;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int Capacity) {
        this.capacity = Capacity;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public ArrayList<Student> getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(ArrayList<Student> enrolled) {
        this.enrolled = enrolled;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }
}
