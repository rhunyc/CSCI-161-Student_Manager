/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Factories.CourseFactory;
import Factories.StudentFactory;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Sky_Net
 */
public class TESTING {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Administrator admin = new Administrator("James");
        System.out.println(admin.toString());

        //StudentFactory.generateArrayListStudents(5);

        Collections.sort(Administrator.accessStudent());
        
        System.out.println(admin.getClass() + admin.getName());

        /*
        Decomment for Student creation/reading
        */        
        
//      for (int x = 0; x < Administrator.accessStudent().size(); x++) {
//           System.out.println(Administrator.accessStudent().get(x).getID());
//        }
//
//        for (int x = 0; x < Administrator.accessStudent().size(); x++) {
//          System.out.println(Administrator.accessStudent().get(x).toString());
//       }
//
//        CourseFactory.generateCourseList(2);
//        
//        Collections.sort(Administrator.accessCourse());
//        
//       for(int i = 0; i < Administrator.accessCourse().size(); i++)
//        {
//            System.out.println(Administrator.accessCourse().get(i).getCourseID());
//        }
//        
//        for(int i = 0; i < Administrator.accessCourse().size(); i++)
//        {
//            System.out.println(Administrator.accessCourse().get(i).toString());
//       }
        StudentFactory.generateStudent();
        CourseFactory.generateCourse();
        System.out.println("Student at 0 of access student: " + Administrator.accessStudent().get(0));
        System.out.println("Current students created:");
        for (Student s : Administrator.accessStudent()){
            System.out.println(s);
        }
        System.out.println("Students Enrolled in course " + Administrator.accessCourse().get(0) + ":");
        Administrator.accessStudent().get(0).addCourse(Administrator.accessCourse().get(0));
        for (Student s : Administrator.accessCourse().get(0).getEnrolled()){
            System.out.println(s);
        }
        System.out.println("All available courses:");
        for (Course c :Administrator.accessCourse()){
            System.out.println(c);
        }
        

    }

}
