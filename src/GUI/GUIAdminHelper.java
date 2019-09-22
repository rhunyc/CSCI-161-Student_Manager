/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Classes.Administrator;
import Classes.Course;
import Classes.Student;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Brian
 */
public class GUIAdminHelper {

   protected static ArrayList<Student> accessStudentList(){
       return Administrator.accessStudent();
   }
   
   protected static ArrayList<Course> accessCourseList(){
       return Administrator.accessCourse();
   }
    /*
    This method will update the user / student on the main admin panel, so we know how many current students there are
    */
protected static void updateCounts(JLabel studentCount, JLabel courseCount){
    studentCount.setText("" + accessStudentList().size());
    courseCount.setText("" + accessCourseList().size());
}    
    
public static void clearStudentList(){
    Administrator.CLEARSTUDENT();
}

public static void clearCourseList(){
    Administrator.CLEARCOURSE();
}

protected static void populateStudentEditFields(Student currentStudent, DefaultListModel[] models, boolean[] booleans,
        JTextField nameField, JTextField gpaField, JTextField majorField, 
        JTextField creditsNeeded, JTextField totalCredits, JList currentCoursesJList, JList allCoursesJList, JPanel editStudentPanel,
        JPanel editCoursePanel, JPanel editNothingPanel
        ){
    if (currentStudent != null){
        DefaultListModel currentCourses = models[1];
        DefaultListModel allCourses = models[2];
        int k = 0;
        int l = 0;
        currentCourses.removeAllElements();
        allCourses.removeAllElements();
        booleans[0] = true;
        booleans[1] = false;
        nameField.setText(currentStudent.getName());
        gpaField.setText("" + currentStudent.getGpa());
        majorField.setText("" + currentStudent.getMajor());
        creditsNeeded.setText("" + currentStudent.getCreditsNeeded());
        totalCredits.setText("" + currentStudent.getTotalCredits());
            for (int i = 0; i < currentStudent.getCourses().size(); i++) {
                currentCourses.add(k++, currentStudent.getCourses().get(i));
            }
            for (int i = 0; i < accessCourseList().size(); i++) {
                if (!currentStudent.isEnrolled(accessCourseList().get(i))) {
                    allCourses.add(l++, accessCourseList().get(i));
                }
            }
            currentCoursesJList.setModel(currentCourses);
            allCoursesJList.setModel(allCourses);
            editStudentPanel.setVisible(true);
            editCoursePanel.setVisible(false);
            editNothingPanel.setVisible(false);
    }
    
}
    
}
