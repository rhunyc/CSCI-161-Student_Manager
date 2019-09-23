/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Factories;

import Classes.Administrator;
import Classes.Course;
import Classes.Student;
import java.util.ArrayList;
import java.util.Random;
import Factories.StudentFactory;
import java.io.Serializable;

/**
 *
 * @author zachary.d.anderson
 */
public class CourseFactory implements Serializable {

    private static Random rand = new Random();

    private static String[] majors = {"Gender Studies", "Art or whatever", "Business", "MATH", "PHY ed"};
    private static int[] times = {700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600, 1700, 1800};
    private static String[] prefixes = {"Mr.", "Mrs.", "Ms.", "Dr."};
    private static String[] fNames = {"Dorcas ", "Doreen ", "Shela ", "Emmanuel ", "David ", "Lory ", "Elma ", "Rayford ", "Micheal ", "Adrian "};
    private static String[] lNames = {"Stansberry", "Back", "Popham", "Gammage", "Liddell", "Bowman", "Bosque", "Evangelista", "Giunta", "Coil"};
    private static String[] days;
    private static int time;
    private static int capacity;
    private static int credits;
    public static ArrayList d = new ArrayList<>();

    public static void generateCourseList(int n) {
        for (int x = 0; x < n; x++) {
            generateCourse();
        }
    }

    public static Course generateCourse() {
        String[] names;

        String name;
        String major = majors[rand.nextInt(majors.length)];
        names = generateCourseNames(major);
        name = names[rand.nextInt(names.length)];
        schedule();
        time = times[rand.nextInt(times.length)];
        capacity = rand.nextInt((50 - 20) + 1) + 20;

        credits = rand.nextInt((5 - 2) + 1) + 2;
        String prefix = prefixes[rand.nextInt(prefixes.length)];
        String fName = fNames[rand.nextInt(fNames.length)];
        String lName = lNames[rand.nextInt(lNames.length)];
        String teach = prefix + fName + lName;
        Course c = new Course(name, days, time, major, capacity, teach, credits);
        return c;
    }

    private static String[] generateCourseNames(String major) {
        String[] names = new String[5];
        switch (major) {
            case "Gender Studies":
                names[0] = "Feminist Theory: An Introduction";
                names[1] = "Health, Gender and Ethnicity";
                names[2] = "Gender and Creative Labor";
                names[3] = "Ecofeminism";
                names[4] = "Gender and Social Justice";
                break;
            case "Art or whatever":
                names[0] = "Papercuts 2D to 3D";
                names[1] = "Cultural Perspectives";
                names[2] = "The Art of Sci Fi Production";
                names[3] = "Themes & Narrative";
                names[4] = "Watercolor Technique";
                break;
            case "Business":
                names[0] = "\"Accounting\" for Decision Making";
                names[1] = "Introductory Microeconomics";
                names[2] = "Business Law";
                names[3] = "Introductory Macroeconomics";
                names[4] = "Foundations of Marketing";
                break;
            case "MATH":
                names[0] = "General Calculus I";
                names[1] = "General Calculus II";
                names[2] = "Introduction to Statistics";
                names[3] = "Introductory Algebra";
                names[4] = "Precalculus";
                break;
            case "PHY ed":
                names[0] = "Take the Leap: Intro to Diving";
                names[1] = "Basic Sailing";
                names[2] = "Ballet III";
                names[3] = "Yoga Dance";
                names[4] = "Zumba";
                break;
        }
        return names;
    }

    public static void schedule() {
        String[] full = {"M", "Tu", "W", "Th", "F"};
        String[] three = {"M", "W", "F"};
        String[] two = {"Tu", "Th"};

        int pick = rand.nextInt(3);

        switch (pick) {
            case 1:
                days = full;
                break;
            case 2:
                days = three;
                break;
            case 0:
                days = two;
                break;
        }
    }
}
