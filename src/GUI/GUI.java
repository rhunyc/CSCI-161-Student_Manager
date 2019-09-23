/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Classes.Administrator;
import Classes.Course;
import Classes.Student;
import Factories.CourseFactory;
import Factories.StudentFactory;
import Interfaces.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author brian.jackson.1
 */
public class GUI extends javax.swing.JFrame {

    GUIhelper gh = new GUIhelper();                                         // This is a helper class, haven't used it a lot, but wanted to try it, may move more variables over to it
    DefaultListModel model = new DefaultListModel();                        // Adminpanel main list of users / courses model (models are what show us what's in the Jlist)
    DefaultListModel aStudentCourseModel = new DefaultListModel();          // Adminpanel model for showing us the currently selected students courses
    DefaultListModel aStudentAllCourseModel = new DefaultListModel();       // Adminpanel model for showing us all of the courses available to the currently selected student
    DefaultListModel aCourseStudentsEnrolledModel = new DefaultListModel();
    DefaultListModel sModelCoursesTaken = new DefaultListModel();
    DefaultListModel sModelAvailalbeCourses = new DefaultListModel();
    DefaultListModel sModelProspectiveCourses = new DefaultListModel<>();
    DefaultListModel sModelRegisteredCourses = new DefaultListModel();
    DefaultListModel fModelCourses = new DefaultListModel();
    DefaultListModel fModelWaitlist = new DefaultListModel();
    DefaultListModel fModelStudentsEnrolled = new DefaultListModel();
    DefaultListModel[] models = new DefaultListModel[]{model, aStudentCourseModel, aStudentAllCourseModel};
    boolean aStudentSelected = false;                                       // These are checks to see what we're selecting, the reason why there is one for both, is it's possible to not have either selected
    boolean aCourseSelected = false;                                        // These are for the admin panel only
    boolean sBooleanAvailableCourseSelected = false;
    boolean sBooleanProspectCourseSelected = false;
    boolean sBooleanRegisteredCourseSelected = false;
    boolean[] adminBooleans = new boolean[]{aStudentSelected, aCourseSelected};
    Student currentStudent = null;                                          // This will hold our currently selected student
    Student aCurrentCourseStudent = null;                                    // This is for removing students in our adminPanel course edit screen
    Course currentCourse = null;                                            // This holds our currently selected course
    public static int loginCounter = 0;
    int selectedIndex = -1;                                                 // This is our selected index... Not sure if it's possible to use it for all of our lists, right now it's for our admin panel main list
    String searchString = "";                                               // This is what we'll store any typed in search values
    static File saveFile = new File("output.data");                         // Our save file

    // This is what runs our gui
    public GUI() {
        loadFile(saveFile);
        initComponents();
        updateAdminPanel();                                             // To update all of the fields
    }

    // This is the method to use inside of this class when mainpulating the list of students, change whatever is inside of here
    public ArrayList<Student> accessStudentList() {
        return Administrator.accessStudent();
    }

    // This is the method for manipualting the list of courses
    public ArrayList<Course> accessCourseList() {
        return Administrator.accessCourse();
    }

    // This is something that will run after any action you do, to refresh / update /save any changes in the admin panel
    public void updateAdminPanel() {
        saveFile(saveFile);
        clearAdminMainJList();
        populateAdminMainJList();
        Collections.sort(accessStudentList());
        Collections.sort(accessCourseList());
        populateStudentEditFields();
        populateCourseEditFields();
        GUIAdminHelper.updateCounts(aStudentCountLabel, aCourseCountLabel);
    }

    private void launchFacultyPanel() {
        fModelCourses.removeAllElements();
        for (int i = 0; i < accessCourseList().size(); i++) {
            fModelCourses.add(i, accessCourseList().get(i));
        }
        fJListCourses.setModel(fModelCourses);
        fJListCourses.setModel(fModelCourses);
    }

    private void updateFacultyPanel() {
        fModelWaitlist.removeAllElements();
        fModelStudentsEnrolled.removeAllElements();

        for (int i = 0; i < currentCourse.getEnrolled().size(); i++) {
            fModelStudentsEnrolled.add(i, currentCourse.getEnrolled().get(i));
        }
        Student[] temp = new Student[currentCourse.getWaitList().size()];
        int size = currentCourse.getWaitList().size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                temp[i] = currentCourse.getWaitList().dequeue();
                fModelWaitlist.add(i, temp[i]);
                currentCourse.getWaitList().enqueue(temp[i]);
            }
        }

        fJListEnrolled.setModel(fModelStudentsEnrolled);
        fJListWaitlist.setModel(fModelWaitlist);
    }

    public void updateStudentPanel() {
        updateCurrentLabels();
        int a = 0;
        int b = 0;
        saveFile(saveFile);
        sModelAvailalbeCourses.removeAllElements();
        sModelCoursesTaken.removeAllElements();
        sModelProspectiveCourses.removeAllElements();
        sModelRegisteredCourses.removeAllElements();
        for (int i = 0; i < accessCourseList().size(); i++) {
            if (!currentStudent.isEnrolled(accessCourseList().get(i))
                    && !currentStudent.getCompCourses().contains(accessCourseList().get(i))
                    && !currentStudent.getProspectiveCourses().contains(accessCourseList().get(i))
                    && !currentStudent.accessWaitList().contains(accessCourseList().get(i))) {
                sModelAvailalbeCourses.add(a++, accessCourseList().get(i));
            }
        }
        for (int i = 0; i < currentStudent.getCompCourses().size(); i++) {
            sModelCoursesTaken.add(i, currentStudent.getCompCourses().get(i));
        }
        for (int i = 0; i < currentStudent.getProspectiveCourses().size(); i++) {
            sModelProspectiveCourses.add(i, currentStudent.getProspectiveCourses().get(i));
        }
        for (int i = 0; i < currentStudent.getCourses().size(); i++) {
            sModelRegisteredCourses.add(b++, currentStudent.getCourses().get(i));
        }
        for (int i = 0; i < currentStudent.accessWaitList().size(); i++) {
            sModelRegisteredCourses.add(b++, currentStudent.printingWaitList().get(i));
        }
        sJListProspectiveCourses.setModel(sModelProspectiveCourses);
        sJListAvailableCourses.setModel(sModelAvailalbeCourses);
        sJListCoursesTaken.setModel(sModelCoursesTaken);
        sJListRegisteredCourses.setModel(sModelRegisteredCourses);
    }

    // This is the method to delete whatever is selected
    public void delete() {
        if (selectedIndex != -1) {                                                  // If we don't have anything selected, don't do anything
            if (aStudentSelected) {
                Administrator.deleteStudent((Student) model.get(selectedIndex));
                aEditStudentPanel.setVisible(false);                                // When deleting, it resets our selected index, without including these the panel will still
                aEditNothingPanel.setVisible(true);                                 // be visible, showing the previously deleted students info
                currentStudent = null;                                              // Resetting out current courses and students to null
                currentCourse = null;
                aStudentSelected = false;
            }
            if (aCourseSelected) {
                Administrator.deleteCourse((Course) model.get(selectedIndex));
                aEditCoursePanel.setVisible(false);
                aEditNothingPanel.setVisible(true);
                currentCourse = null;
                currentStudent = null;
                aCourseSelected = false;
            }
        }
    }

    public void updateCurrentLabels() {
        if (currentCourse != null) {
            aCurrentCourseJLabel.setText(currentCourse.getName());
            sLabelCurrentCourse.setText(currentCourse.getName());
            sLabelID.setText(currentCourse.getCourseID() + "");
            sLabelCredits.setText(currentCourse.getCredits() + "");
            sLabelDays.setText(currentCourse.printDays());
            sLabelInstructor.setText(currentCourse.getTeacher());
            sLabelMajor.setText(currentCourse.getMajor());
            sLabelTimes.setText(currentCourse.printTime());
        } else {
            aCurrentCourseJLabel.setText("null");
            sLabelCurrentCourse.setText("No course is selected.");
            sLabelID.setText("");
            sLabelCredits.setText("");
            sLabelDays.setText("");
            sLabelInstructor.setText("");
            sLabelMajor.setText("");
            sLabelTimes.setText("");

        }
        if (currentStudent != null) {
            aCurrentStudentJLabel.setText(currentStudent.getName());

        } else {
            aCurrentStudentJLabel.setText("null");
        }
    }

    public void clearStudentList() {
        Administrator.CLEARSTUDENT();
    }

    public void clearCourseList() {
        Administrator.CLEARCOURSE();
    }

    // The file is saved anytime the list is updated
    private void saveFile(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            Object[] output = {accessStudentList(), accessCourseList()};
            oos.writeObject(output);
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(this, "FileNotFoundException thrown!");
            System.out.println(fnfe.getMessage());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "IOException thrown!");
            System.out.println(ex.getMessage());
        }
    }

    // The file is loaded at the startup of the program
    private void loadFile(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object[] thingsToLoad = (Object[]) ois.readObject();
            Administrator.setStudent((ArrayList<Student>) thingsToLoad[0]);
            Administrator.setCourse((ArrayList<Course>) thingsToLoad[1]);
        } catch (FileNotFoundException fnfe) {
            JOptionPane.showMessageDialog(this, "Could not load file!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "IOException thrown!");
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // This will clear what is visible in the admin main panel JList
    private void clearAdminMainJList() {
        model.removeAllElements();
    }

    // This is the method that will populate our student fields
    private void populateStudentEditFields() {
        GUIAdminHelper.populateStudentEditFields(currentStudent, models, adminBooleans, aStudentNameTextField,
                aStudentGPATextField, aStudentMajorTextField, aStudentCreditsNeededField, aStudentTotalCreditsField, aIDNumberLabel, aJListStudentCoursesCurrent,
                aJListStudentAvailableCourses, aEditStudentPanel, aEditCoursePanel, aEditNothingPanel);
    }

    private void populateCourseEditFields() {

        if (currentCourse != null) {

            aCourseStudentsEnrolledModel.removeAllElements();
            int k = 0;
            //aStudentSelected = false;
            //aCourseSelected = true;
            for (int i = 0; i < currentCourse.getEnrolled().size(); i++) {
                aCourseStudentsEnrolledModel.add(i, currentCourse.getEnrolled().get(i));
            }
            aCourseCapacityTextField.setText(currentCourse.getCapacity() + "");
            aCourseCreditsTextField.setText(currentCourse.getCredits() + "");
            aCourseInstructorTextField.setText(currentCourse.getTeacher());
            aCourseNameTextField.setText(currentCourse.getName());
            aJListCourseStudentsEnrolled.setModel(aCourseStudentsEnrolledModel);
            aEditStudentPanel.setVisible(false);
            aEditCoursePanel.setVisible(true);
            aEditNothingPanel.setVisible(false);
        }
        updateCurrentLabels();
    }

    private void populateAdminMainJList() {
        int k = 0;
        if (!accessStudentList().isEmpty()) {

            if (aShowStudentsCheckBox.isSelected()) {
                for (int i = 0; i < accessStudentList().size(); i++) {
                    if (searchString == "") {
                        model.add(i, accessStudentList().get(i));
                    } else {
                        String currentStudentID = "" + accessStudentList().get(i).getID();
                        String currentStudentName = accessStudentList().get(i).getName().toLowerCase();
                        String currentStudentMajor = accessStudentList().get(i).getMajor().toLowerCase();
                        if (currentStudentID.contains(searchString) || currentStudentName.contains(searchString) || currentStudentMajor.contains(searchString)) {
                            model.add(k++, accessStudentList().get(i));
                        }
                    }
                }
            }
        }
        if (aShowCoursesCheckBox.isSelected()) {

            for (int j = 0; j < accessCourseList().size(); j++) {
                if (searchString == "") {
                    model.add(j, accessCourseList().get(j));
                } else {
                    String currentCourseName = accessCourseList().get(j).getName().toLowerCase();
                    String currentCourseTeacher = accessCourseList().get(j).getTeacher().toLowerCase();
                    if (currentCourseName.contains(searchString) || currentCourseTeacher.contains(searchString)) {
                        model.add(k++, accessCourseList().get(j));
                    }
                }
            }

        }
        aMainJList.setModel(model);
        updateCurrentLabels();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginButtonGroup = new javax.swing.ButtonGroup();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        exitButton = new javax.swing.JButton();
        homeButton = new javax.swing.JButton();
        mainUIPanel = new javax.swing.JPanel();
        loginPanel = new javax.swing.JPanel();
        lLoginSubmitButton = new javax.swing.JButton();
        loginWelcomeLabel = new javax.swing.JLabel();
        loginInstructionLabel = new javax.swing.JLabel();
        loginTitleLabel = new javax.swing.JLabel();
        lStudentRadioButton = new javax.swing.JRadioButton();
        lFacultyRadioButton = new javax.swing.JRadioButton();
        lAdminRadioButton = new javax.swing.JRadioButton();
        jLabel42 = new javax.swing.JLabel();
        adminPanel = new javax.swing.JPanel();
        aEditSpacePanel = new javax.swing.JPanel();
        aEditNothingPanel = new javax.swing.JPanel();
        aEditStudentPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        aJListStudentCoursesCurrent = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        aJListStudentAvailableCourses = new javax.swing.JList<>();
        aStudentNameTextField = new javax.swing.JTextField();
        aStudentGPATextField = new javax.swing.JTextField();
        aStudentMajorTextField = new javax.swing.JTextField();
        aStudentTotalCreditsField = new javax.swing.JTextField();
        aStudentCreditsNeededField = new javax.swing.JTextField();
        aUserAddCourseButton = new javax.swing.JButton();
        aUserRemoveCourseButton = new javax.swing.JButton();
        aUserSaveChangesButton = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        aIDNumberLabel = new javax.swing.JLabel();
        aEditCoursePanel = new javax.swing.JPanel();
        aCourseNameTextField = new javax.swing.JTextField();
        aCourseInstructorTextField = new javax.swing.JTextField();
        aCourseCapacityTextField = new javax.swing.JTextField();
        aCourseCreditsTextField = new javax.swing.JTextField();
        aCourseSaveButton = new javax.swing.JButton();
        aCourseRemoveStudentButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        aJListCourseStudentsEnrolled = new javax.swing.JList<>();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        aUserListScrollPane = new javax.swing.JScrollPane();
        aMainJList = new javax.swing.JList<>();
        aSearchSubmitButton = new javax.swing.JButton();
        aSearchClearButton = new javax.swing.JButton();
        aDeleteSelectedButton = new javax.swing.JButton();
        aGenerateStudentsButton = new javax.swing.JButton();
        aGenerateCoursesButton = new javax.swing.JButton();
        aRemoveAllStudentsButton = new javax.swing.JButton();
        aRemoveAllCoursesButton = new javax.swing.JButton();
        aShowStudentsCheckBox = new javax.swing.JCheckBox();
        aShowCoursesCheckBox = new javax.swing.JCheckBox();
        aSearchTextField = new javax.swing.JTextField();
        aStudentCountLabel = new javax.swing.JLabel();
        aCourseCountLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        aCurrentStudentJLabel = new javax.swing.JLabel();
        aCurrentCourseJLabel = new javax.swing.JLabel();
        facultyPanel = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        fJListEnrolled = new javax.swing.JList<>();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        fJListCourses = new javax.swing.JList<>();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        fJListWaitlist = new javax.swing.JList<>();
        studentPanel = new javax.swing.JPanel();
        sLoginPanel = new javax.swing.JPanel();
        sLoginTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        sLoginSubmitButton = new javax.swing.JButton();
        sMainPanel = new javax.swing.JPanel();
        sNameLabel = new javax.swing.JLabel();
        sStudentIDLabel = new javax.swing.JLabel();
        sMajorLabel = new javax.swing.JLabel();
        sGPALabel = new javax.swing.JLabel();
        sTotalCreditsLabel = new javax.swing.JLabel();
        sCreditsNeededLabel = new javax.swing.JLabel();
        sButtonRegisterClass = new javax.swing.JButton();
        sButtonAddAvailableCourse = new javax.swing.JButton();
        sButtonClearProspectiveCoursesList = new javax.swing.JButton();
        sButtonRemoveClass = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        sJListRegisteredCourses = new javax.swing.JList<>();
        sCoursesScrollPane = new javax.swing.JScrollPane();
        sJListCoursesTaken = new javax.swing.JList<>();
        sAvailableCoursesScrollPane = new javax.swing.JScrollPane();
        sJListAvailableCourses = new javax.swing.JList<>();
        sProspectiveCoursesScrollPane = new javax.swing.JScrollPane();
        sJListProspectiveCourses = new javax.swing.JList<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        sButtonDropRegisteredCourse = new javax.swing.JButton();
        sLabelCurrentCourse = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        sLabelMajor = new javax.swing.JLabel();
        sLabelInstructor = new javax.swing.JLabel();
        sLabelDays = new javax.swing.JLabel();
        sLabelTimes = new javax.swing.JLabel();
        sLabelCredits = new javax.swing.JLabel();
        sLabelID = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 600));

        exitButton.setText("Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        homeButton.setText("Home");
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        mainUIPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        mainUIPanel.setMaximumSize(new java.awt.Dimension(800, 600));
        mainUIPanel.setMinimumSize(new java.awt.Dimension(800, 600));
        mainUIPanel.setName(""); // NOI18N
        mainUIPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        mainUIPanel.setLayout(new java.awt.CardLayout());

        loginPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Login"));
        loginPanel.setMaximumSize(new java.awt.Dimension(742, 512));
        loginPanel.setMinimumSize(new java.awt.Dimension(742, 512));
        loginPanel.setLayout(null);

        lLoginSubmitButton.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lLoginSubmitButton.setText("Submit");
        lLoginSubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lLoginSubmitButtonActionPerformed(evt);
            }
        });
        loginPanel.add(lLoginSubmitButton);
        lLoginSubmitButton.setBounds(300, 460, 161, 53);

        loginWelcomeLabel.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        loginWelcomeLabel.setText("Welcome!");
        loginPanel.add(loginWelcomeLabel);
        loginWelcomeLabel.setBounds(290, 150, 192, 37);

        loginInstructionLabel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        loginInstructionLabel.setText("To get started, select the appropriate user type and hit submit.");
        loginPanel.add(loginInstructionLabel);
        loginInstructionLabel.setBounds(120, 220, 570, 19);

        loginTitleLabel.setFont(new java.awt.Font("Lucida Console", 1, 30)); // NOI18N
        loginTitleLabel.setText("Student Management Program v 1.0");
        loginPanel.add(loginTitleLabel);
        loginTitleLabel.setBounds(90, 50, 650, 70);

        loginButtonGroup.add(lStudentRadioButton);
        lStudentRadioButton.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lStudentRadioButton.setText("Student");
        lStudentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lStudentRadioButtonActionPerformed(evt);
            }
        });
        loginPanel.add(lStudentRadioButton);
        lStudentRadioButton.setBounds(320, 280, 119, 37);

        loginButtonGroup.add(lFacultyRadioButton);
        lFacultyRadioButton.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lFacultyRadioButton.setText("Faculty");
        lFacultyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lFacultyRadioButtonActionPerformed(evt);
            }
        });
        loginPanel.add(lFacultyRadioButton);
        lFacultyRadioButton.setBounds(320, 330, 113, 37);

        loginButtonGroup.add(lAdminRadioButton);
        lAdminRadioButton.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lAdminRadioButton.setText("Admin");
        lAdminRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lAdminRadioButtonActionPerformed(evt);
            }
        });
        loginPanel.add(lAdminRadioButton);
        lAdminRadioButton.setBounds(320, 380, 101, 37);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("Deleoped by Zachary Anderson, Mir Housainee, Brian Jackson, and William Roberts");
        loginPanel.add(jLabel42);
        jLabel42.setBounds(120, 560, 530, 14);

        mainUIPanel.add(loginPanel, "card2");

        adminPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Admin"));
        adminPanel.setMaximumSize(new java.awt.Dimension(742, 512));
        adminPanel.setMinimumSize(new java.awt.Dimension(742, 512));

        aEditSpacePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Edit"));
        aEditSpacePanel.setLayout(new java.awt.CardLayout());

        aEditNothingPanel.setMaximumSize(new java.awt.Dimension(391, 387));
        aEditNothingPanel.setMinimumSize(new java.awt.Dimension(391, 387));

        javax.swing.GroupLayout aEditNothingPanelLayout = new javax.swing.GroupLayout(aEditNothingPanel);
        aEditNothingPanel.setLayout(aEditNothingPanelLayout);
        aEditNothingPanelLayout.setHorizontalGroup(
            aEditNothingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 418, Short.MAX_VALUE)
        );
        aEditNothingPanelLayout.setVerticalGroup(
            aEditNothingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 532, Short.MAX_VALUE)
        );

        aEditSpacePanel.add(aEditNothingPanel, "card2");

        aEditStudentPanel.setMaximumSize(new java.awt.Dimension(391, 387));
        aEditStudentPanel.setMinimumSize(new java.awt.Dimension(391, 387));

        aJListStudentCoursesCurrent.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        aJListStudentCoursesCurrent.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aJListStudentCoursesCurrentValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(aJListStudentCoursesCurrent);

        aJListStudentAvailableCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        aJListStudentAvailableCourses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aJListStudentAvailableCoursesValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(aJListStudentAvailableCourses);

        aUserAddCourseButton.setText("Add");
        aUserAddCourseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aUserAddCourseButtonActionPerformed(evt);
            }
        });

        aUserRemoveCourseButton.setText("Remove");
        aUserRemoveCourseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aUserRemoveCourseButtonActionPerformed(evt);
            }
        });

        aUserSaveChangesButton.setText("Save");
        aUserSaveChangesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aUserSaveChangesButtonActionPerformed(evt);
            }
        });

        jLabel20.setText("Major:");

        jLabel18.setText("GPA:");

        jLabel21.setText("Total Credits:");

        jLabel22.setText("Credits Needed:");

        jLabel23.setText("Student's Completed Courses:");

        jLabel15.setText("Name:");

        jLabel24.setText("Available Courses:");

        jLabel30.setText("ID Number: ");

        aIDNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        aIDNumberLabel.setText("0");
        aIDNumberLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout aEditStudentPanelLayout = new javax.swing.GroupLayout(aEditStudentPanel);
        aEditStudentPanel.setLayout(aEditStudentPanelLayout);
        aEditStudentPanelLayout.setHorizontalGroup(
            aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aEditStudentPanelLayout.createSequentialGroup()
                        .addComponent(aUserRemoveCourseButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(aUserAddCourseButton))
                    .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, aEditStudentPanelLayout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(aIDNumberLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel20))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(aStudentMajorTextField)
                                    .addComponent(aStudentNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)))
                            .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel18)
                                    .addComponent(jLabel21))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(aStudentTotalCreditsField, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(aStudentGPATextField)
                                        .addComponent(aStudentCreditsNeededField, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)))))
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addGap(0, 104, Short.MAX_VALUE))
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aEditStudentPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(aUserSaveChangesButton)
                                .addGap(8, 8, 8)))))
                .addContainerGap())
        );
        aEditStudentPanelLayout.setVerticalGroup(
            aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(aUserSaveChangesButton))
                    .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(aStudentNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(aStudentMajorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18)
                            .addComponent(aStudentGPATextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21)
                            .addComponent(aStudentTotalCreditsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aStudentCreditsNeededField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(aIDNumberLabel))
                .addGap(22, 22, 22)
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(aUserAddCourseButton)
                    .addComponent(aUserRemoveCourseButton))
                .addContainerGap(154, Short.MAX_VALUE))
        );

        aEditSpacePanel.add(aEditStudentPanel, "card2");

        aEditCoursePanel.setMaximumSize(new java.awt.Dimension(391, 387));
        aEditCoursePanel.setMinimumSize(new java.awt.Dimension(391, 387));
        aEditCoursePanel.setPreferredSize(new java.awt.Dimension(391, 387));

        aCourseCapacityTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aCourseCapacityTextFieldActionPerformed(evt);
            }
        });

        aCourseCreditsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aCourseCreditsTextFieldActionPerformed(evt);
            }
        });

        aCourseSaveButton.setText("Save");
        aCourseSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aCourseSaveButtonActionPerformed(evt);
            }
        });

        aCourseRemoveStudentButton.setText("Remove");
        aCourseRemoveStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aCourseRemoveStudentButtonActionPerformed(evt);
            }
        });

        aJListCourseStudentsEnrolled.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aJListCourseStudentsEnrolledValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(aJListCourseStudentsEnrolled);

        jLabel27.setText("Capacity:");

        jLabel28.setText("Credits:");

        jLabel26.setText("Course Instructor:");

        jLabel25.setText("Course Name:");

        jLabel29.setText("Students Enrolled Currently:");

        javax.swing.GroupLayout aEditCoursePanelLayout = new javax.swing.GroupLayout(aEditCoursePanel);
        aEditCoursePanel.setLayout(aEditCoursePanelLayout);
        aEditCoursePanelLayout.setHorizontalGroup(
            aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aEditCoursePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 355, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aEditCoursePanelLayout.createSequentialGroup()
                        .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26)
                            .addComponent(jLabel25)
                            .addComponent(jLabel27)
                            .addComponent(jLabel28))
                        .addGap(18, 18, 18)
                        .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(aCourseNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                            .addComponent(aCourseInstructorTextField)
                            .addGroup(aEditCoursePanelLayout.createSequentialGroup()
                                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(aCourseCreditsTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                                    .addComponent(aCourseCapacityTextField, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(aCourseSaveButton))))
                    .addGroup(aEditCoursePanelLayout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(aCourseRemoveStudentButton)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        aEditCoursePanelLayout.setVerticalGroup(
            aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(aEditCoursePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(aCourseNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(aCourseInstructorTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addComponent(aCourseCapacityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aCourseSaveButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(aCourseCreditsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(aEditCoursePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(aCourseRemoveStudentButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addContainerGap())
        );

        aEditSpacePanel.add(aEditCoursePanel, "card2");

        aMainJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        aMainJList.setRequestFocusEnabled(false);
        aMainJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aMainJListValueChanged(evt);
            }
        });
        aUserListScrollPane.setViewportView(aMainJList);

        aSearchSubmitButton.setText("Submit");
        aSearchSubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aSearchSubmitButtonActionPerformed(evt);
            }
        });

        aSearchClearButton.setText("Clear");
        aSearchClearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aSearchClearButtonActionPerformed(evt);
            }
        });

        aDeleteSelectedButton.setText("Delete Selected");
        aDeleteSelectedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aDeleteSelectedButtonActionPerformed(evt);
            }
        });

        aGenerateStudentsButton.setText("Generate Students");
        aGenerateStudentsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aGenerateStudentsButtonActionPerformed(evt);
            }
        });

        aGenerateCoursesButton.setText("Generate Courses");
        aGenerateCoursesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aGenerateCoursesButtonActionPerformed(evt);
            }
        });

        aRemoveAllStudentsButton.setText("Remove All Students");
        aRemoveAllStudentsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aRemoveAllStudentsButtonActionPerformed(evt);
            }
        });

        aRemoveAllCoursesButton.setText("Remove All Courses");
        aRemoveAllCoursesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aRemoveAllCoursesButtonActionPerformed(evt);
            }
        });

        aShowStudentsCheckBox.setSelected(true);
        aShowStudentsCheckBox.setText("Students");
        aShowStudentsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aShowStudentsCheckBoxActionPerformed(evt);
            }
        });

        aShowCoursesCheckBox.setSelected(true);
        aShowCoursesCheckBox.setText("Courses");
        aShowCoursesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aShowCoursesCheckBoxActionPerformed(evt);
            }
        });

        aStudentCountLabel.setText("0");

        aCourseCountLabel.setText("0");

        jLabel4.setText("Search:");

        jLabel5.setText("Number of Courses:");

        jLabel16.setText("Show:");

        jLabel3.setText("Number of Students:");

        jLabel17.setText("currentCourse:");

        jLabel31.setText("currentStudent:");

        aCurrentStudentJLabel.setText("text");

        aCurrentCourseJLabel.setText("text");

        javax.swing.GroupLayout adminPanelLayout = new javax.swing.GroupLayout(adminPanel);
        adminPanel.setLayout(adminPanelLayout);
        adminPanelLayout.setHorizontalGroup(
            adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(adminPanelLayout.createSequentialGroup()
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(adminPanelLayout.createSequentialGroup()
                                .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3)
                                    .addGroup(adminPanelLayout.createSequentialGroup()
                                        .addComponent(aSearchClearButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(aSearchSubmitButton)
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel5)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(aStudentCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(aCourseCountLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(aUserListScrollPane)
                            .addGroup(adminPanelLayout.createSequentialGroup()
                                .addComponent(aSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                                .addComponent(aDeleteSelectedButton)
                                .addGap(30, 30, 30))
                            .addGroup(adminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(aShowStudentsCheckBox)
                                .addGap(10, 10, 10)
                                .addComponent(aShowCoursesCheckBox)
                                .addGap(41, 41, 41)))
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, adminPanelLayout.createSequentialGroup()
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aGenerateCoursesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(aGenerateStudentsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aRemoveAllStudentsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(aRemoveAllCoursesButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(adminPanelLayout.createSequentialGroup()
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(adminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(aCurrentCourseJLabel))
                            .addGroup(adminPanelLayout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aCurrentStudentJLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(aEditSpacePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        adminPanelLayout.setVerticalGroup(
            adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(adminPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(aEditSpacePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(adminPanelLayout.createSequentialGroup()
                        .addComponent(aUserListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(aShowStudentsCheckBox)
                                .addComponent(aShowCoursesCheckBox)
                                .addComponent(jLabel16))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(aSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(aDeleteSelectedButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(aSearchClearButton)
                            .addComponent(aSearchSubmitButton)
                            .addComponent(jLabel5)
                            .addComponent(aCourseCountLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(aStudentCountLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(aCurrentStudentJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(aCurrentCourseJLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(aRemoveAllCoursesButton)
                            .addComponent(aGenerateCoursesButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(adminPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(aGenerateStudentsButton)
                            .addComponent(aRemoveAllStudentsButton))))
                .addContainerGap())
        );

        mainUIPanel.add(adminPanel, "card2");

        facultyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Faculty"));
        facultyPanel.setMaximumSize(new java.awt.Dimension(742, 512));
        facultyPanel.setMinimumSize(new java.awt.Dimension(742, 512));

        fJListEnrolled.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fJListEnrolled.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fJListEnrolledValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(fJListEnrolled);

        jLabel32.setText("Courses:");

        fJListCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fJListCourses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fJListCoursesValueChanged(evt);
            }
        });
        jScrollPane6.setViewportView(fJListCourses);

        jLabel33.setText("Students Enrolled:");

        jLabel34.setText("Students on Waitlist:");

        fJListWaitlist.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        fJListWaitlist.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                fJListWaitlistValueChanged(evt);
            }
        });
        jScrollPane7.setViewportView(fJListWaitlist);

        javax.swing.GroupLayout facultyPanelLayout = new javax.swing.GroupLayout(facultyPanel);
        facultyPanel.setLayout(facultyPanelLayout);
        facultyPanelLayout.setHorizontalGroup(
            facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, facultyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel34)
                    .addComponent(jLabel33)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addGap(31, 31, 31))
        );
        facultyPanelLayout.setVerticalGroup(
            facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(facultyPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(facultyPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane7))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        mainUIPanel.add(facultyPanel, "card2");

        studentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Student"));
        studentPanel.setMaximumSize(new java.awt.Dimension(742, 512));
        studentPanel.setMinimumSize(new java.awt.Dimension(742, 512));
        studentPanel.setPreferredSize(new java.awt.Dimension(742, 512));
        studentPanel.setLayout(new java.awt.CardLayout());

        sLoginPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Login"));
        sLoginPanel.setLayout(null);

        sLoginTextField.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        sLoginTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sLoginTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sLoginTextFieldActionPerformed(evt);
            }
        });
        sLoginPanel.add(sLoginTextField);
        sLoginTextField.setBounds(280, 270, 180, 60);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Enter Student ID");
        sLoginPanel.add(jLabel1);
        jLabel1.setBounds(220, 130, 330, 70);

        sLoginSubmitButton.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        sLoginSubmitButton.setText("Submit");
        sLoginSubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sLoginSubmitButtonActionPerformed(evt);
            }
        });
        sLoginPanel.add(sLoginSubmitButton);
        sLoginSubmitButton.setBounds(270, 390, 220, 53);

        studentPanel.add(sLoginPanel, "card2");

        sMainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Main"));
        sMainPanel.setMaximumSize(new java.awt.Dimension(788, 577));
        sMainPanel.setMinimumSize(new java.awt.Dimension(788, 577));
        sMainPanel.setLayout(null);

        sNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sNameLabel.setText("(Student name)");
        sMainPanel.add(sNameLabel);
        sNameLabel.setBounds(68, 27, 140, 14);

        sStudentIDLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sStudentIDLabel.setText("(ID)");
        sMainPanel.add(sStudentIDLabel);
        sStudentIDLabel.setBounds(110, 50, 100, 14);

        sMajorLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sMajorLabel.setText("(Major)");
        sMainPanel.add(sMajorLabel);
        sMajorLabel.setBounds(70, 70, 140, 14);

        sGPALabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sGPALabel.setText("(GPA)");
        sMainPanel.add(sGPALabel);
        sGPALabel.setBounds(90, 90, 120, 14);

        sTotalCreditsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sTotalCreditsLabel.setText("(TC)");
        sMainPanel.add(sTotalCreditsLabel);
        sTotalCreditsLabel.setBounds(100, 110, 110, 14);

        sCreditsNeededLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sCreditsNeededLabel.setText("(CN)");
        sMainPanel.add(sCreditsNeededLabel);
        sCreditsNeededLabel.setBounds(110, 130, 100, 14);

        sButtonRegisterClass.setText("Register");
        sButtonRegisterClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sButtonRegisterClassActionPerformed(evt);
            }
        });
        sMainPanel.add(sButtonRegisterClass);
        sButtonRegisterClass.setBounds(20, 510, 110, 23);

        sButtonAddAvailableCourse.setText("Add");
        sButtonAddAvailableCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sButtonAddAvailableCourseActionPerformed(evt);
            }
        });
        sMainPanel.add(sButtonAddAvailableCourse);
        sButtonAddAvailableCourse.setBounds(670, 510, 51, 23);

        sButtonClearProspectiveCoursesList.setText("Remove All");
        sButtonClearProspectiveCoursesList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sButtonClearProspectiveCoursesListActionPerformed(evt);
            }
        });
        sMainPanel.add(sButtonClearProspectiveCoursesList);
        sButtonClearProspectiveCoursesList.setBounds(290, 510, 100, 23);

        sButtonRemoveClass.setText("Remove");
        sButtonRemoveClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sButtonRemoveClassActionPerformed(evt);
            }
        });
        sMainPanel.add(sButtonRemoveClass);
        sButtonRemoveClass.setBounds(160, 510, 100, 23);

        sJListRegisteredCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sJListRegisteredCourses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                sJListRegisteredCoursesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(sJListRegisteredCourses);

        sMainPanel.add(jScrollPane2);
        jScrollPane2.setBounds(15, 200, 380, 142);

        sJListCoursesTaken.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sJListCoursesTaken.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                sJListCoursesTakenValueChanged(evt);
            }
        });
        sCoursesScrollPane.setViewportView(sJListCoursesTaken);

        sMainPanel.add(sCoursesScrollPane);
        sCoursesScrollPane.setBounds(410, 50, 316, 232);

        sJListAvailableCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sJListAvailableCourses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                sJListAvailableCoursesValueChanged(evt);
            }
        });
        sAvailableCoursesScrollPane.setViewportView(sJListAvailableCourses);

        sMainPanel.add(sAvailableCoursesScrollPane);
        sAvailableCoursesScrollPane.setBounds(410, 310, 315, 190);

        sJListProspectiveCourses.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sJListProspectiveCourses.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                sJListProspectiveCoursesValueChanged(evt);
            }
        });
        sProspectiveCoursesScrollPane.setViewportView(sJListProspectiveCourses);

        sMainPanel.add(sProspectiveCoursesScrollPane);
        sProspectiveCoursesScrollPane.setBounds(15, 370, 380, 130);

        jLabel9.setText("Prospective Courses:");
        sMainPanel.add(jLabel9);
        jLabel9.setBounds(20, 350, 230, 14);

        jLabel13.setText("Available Courses:");
        sMainPanel.add(jLabel13);
        jLabel13.setBounds(410, 290, 230, 14);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel14.setText("ID:");
        sMainPanel.add(jLabel14);
        jLabel14.setBounds(16, 47, 50, 14);

        jLabel7.setText("Courses taken:");
        sMainPanel.add(jLabel7);
        jLabel7.setBounds(410, 30, 250, 14);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Student:");
        sMainPanel.add(jLabel6);
        jLabel6.setBounds(16, 27, 80, 14);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("GPA:");
        sMainPanel.add(jLabel10);
        jLabel10.setBounds(16, 87, 50, 14);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Major:");
        sMainPanel.add(jLabel8);
        jLabel8.setBounds(16, 67, 70, 14);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel12.setText("Credits Needed:");
        sMainPanel.add(jLabel12);
        jLabel12.setBounds(16, 127, 120, 14);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Total Credits:");
        sMainPanel.add(jLabel11);
        jLabel11.setBounds(16, 107, 110, 14);

        jLabel19.setText("Registered Courses:");
        sMainPanel.add(jLabel19);
        jLabel19.setBounds(20, 180, 210, 14);

        sButtonDropRegisteredCourse.setText("Drop");
        sButtonDropRegisteredCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sButtonDropRegisteredCourseActionPerformed(evt);
            }
        });
        sMainPanel.add(sButtonDropRegisteredCourse);
        sButtonDropRegisteredCourse.setBounds(288, 166, 55, 23);

        sLabelCurrentCourse.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        sLabelCurrentCourse.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelCurrentCourse.setText("(currentcourse)");
        sMainPanel.add(sLabelCurrentCourse);
        sLabelCurrentCourse.setBounds(250, 30, 150, 13);

        jLabel35.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel35.setText("ID:");
        sMainPanel.add(jLabel35);
        jLabel35.setBounds(220, 50, 16, 13);

        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel36.setText("Major:");
        sMainPanel.add(jLabel36);
        jLabel36.setBounds(220, 70, 33, 13);

        jLabel37.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel37.setText("Instructor:");
        sMainPanel.add(jLabel37);
        jLabel37.setBounds(220, 90, 70, 13);

        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel38.setText("Days:");
        sMainPanel.add(jLabel38);
        jLabel38.setBounds(220, 110, 28, 13);

        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel39.setText("Times:");
        sMainPanel.add(jLabel39);
        jLabel39.setBounds(220, 130, 33, 13);

        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 10)); // NOI18N
        jLabel40.setText("Credits:");
        sMainPanel.add(jLabel40);
        jLabel40.setBounds(220, 150, 39, 13);

        sLabelMajor.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sLabelMajor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelMajor.setText("(major)");
        sMainPanel.add(sLabelMajor);
        sLabelMajor.setBounds(310, 70, 90, 13);

        sLabelInstructor.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sLabelInstructor.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelInstructor.setText("(instructor)");
        sMainPanel.add(sLabelInstructor);
        sLabelInstructor.setBounds(270, 90, 130, 13);

        sLabelDays.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sLabelDays.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelDays.setText("(days)");
        sMainPanel.add(sLabelDays);
        sLabelDays.setBounds(310, 110, 90, 13);

        sLabelTimes.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sLabelTimes.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelTimes.setText("(times)");
        sMainPanel.add(sLabelTimes);
        sLabelTimes.setBounds(310, 130, 90, 13);

        sLabelCredits.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sLabelCredits.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelCredits.setText("(credits)");
        sMainPanel.add(sLabelCredits);
        sLabelCredits.setBounds(290, 150, 110, 13);

        sLabelID.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        sLabelID.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sLabelID.setText("(ID)");
        sMainPanel.add(sLabelID);
        sLabelID.setBounds(310, 50, 90, 13);

        studentPanel.add(sMainPanel, "card2");

        mainUIPanel.add(studentPanel, "card2");

        jLabel2.setText("Select User:");
        mainUIPanel.add(jLabel2, "card6");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainUIPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(homeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(mainUIPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(homeButton)
                    .addComponent(exitButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed
        adminPanel.setVisible(false);
        studentPanel.setVisible(false);
        loginPanel.setVisible(true);
    }//GEN-LAST:event_homeButtonActionPerformed

    private void lLoginSubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lLoginSubmitButtonActionPerformed
        if (Administrator.accessStudent().isEmpty()) {
            StudentFactory.studentPrimer();
            updateAdminPanel();
        }
        loginCounter++;
        if (gh.isLoginAdmin()) {
            adminPanel.setVisible(true);
            studentPanel.setVisible(false);
            loginPanel.setVisible(false);
            facultyPanel.setVisible(false);
        } else if (gh.isLoginStudent()) {
            adminPanel.setVisible(false);
            studentPanel.setVisible(true);
            loginPanel.setVisible(false);
            facultyPanel.setVisible(false);
            sLoginPanel.setVisible(true);
            sMainPanel.setVisible(false);
            sBooleanAvailableCourseSelected = false;
            sBooleanProspectCourseSelected = false;
            sBooleanRegisteredCourseSelected = false;
        } else if (gh.isLoginFaculty()) {
            adminPanel.setVisible(false);
            studentPanel.setVisible(false);
            loginPanel.setVisible(false);
            facultyPanel.setVisible(true);
            launchFacultyPanel();
        } else {
            adminPanel.setVisible(false);
            studentPanel.setVisible(false);
            loginPanel.setVisible(true);
            facultyPanel.setVisible(false);
        }

    }//GEN-LAST:event_lLoginSubmitButtonActionPerformed

    private void lFacultyRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lFacultyRadioButtonActionPerformed
        gh.loginCheckedFaculty(); // Helper method that sets booleans inside of it
    }//GEN-LAST:event_lFacultyRadioButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void lStudentRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lStudentRadioButtonActionPerformed
        gh.loginCheckedStudent(); // Helper method that sets booleans inside of it
    }//GEN-LAST:event_lStudentRadioButtonActionPerformed

    private void lAdminRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lAdminRadioButtonActionPerformed
        gh.loginCheckedAdmin(); // Helper method that sets booleans inside of it
    }//GEN-LAST:event_lAdminRadioButtonActionPerformed

    private void sLoginTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sLoginTextFieldActionPerformed

    }//GEN-LAST:event_sLoginTextFieldActionPerformed

    private void aDeleteSelectedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aDeleteSelectedButtonActionPerformed
        delete();
        updateAdminPanel();
    }//GEN-LAST:event_aDeleteSelectedButtonActionPerformed

    private void aShowCoursesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aShowCoursesCheckBoxActionPerformed
        updateAdminPanel();
    }//GEN-LAST:event_aShowCoursesCheckBoxActionPerformed

    private void aShowStudentsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aShowStudentsCheckBoxActionPerformed
        updateAdminPanel();
    }//GEN-LAST:event_aShowStudentsCheckBoxActionPerformed

    private void aGenerateCoursesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aGenerateCoursesButtonActionPerformed
        CourseFactory.generateCourseList(5);
        updateAdminPanel();
    }//GEN-LAST:event_aGenerateCoursesButtonActionPerformed

    private void aRemoveAllStudentsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aRemoveAllStudentsButtonActionPerformed
        clearStudentList();
        updateAdminPanel();
    }//GEN-LAST:event_aRemoveAllStudentsButtonActionPerformed

    private void aGenerateStudentsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aGenerateStudentsButtonActionPerformed
        StudentFactory.generateArrayListStudents(20);
        updateAdminPanel();
    }//GEN-LAST:event_aGenerateStudentsButtonActionPerformed

    private void aSearchSubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aSearchSubmitButtonActionPerformed
        searchString = aSearchTextField.getText().toLowerCase();
        updateAdminPanel();
    }//GEN-LAST:event_aSearchSubmitButtonActionPerformed

    private void aMainJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aMainJListValueChanged
        if (!evt.getValueIsAdjusting()) {
            selectedIndex = aMainJList.getSelectedIndex();
            if (selectedIndex != -1) {
                Object thing = model.get(selectedIndex);
                if (thing instanceof Student) {
                    currentStudent = (Student) thing;
                    System.out.println(currentStudent);
                    aStudentSelected = true;
                    aCourseSelected = false;
                    populateStudentEditFields();
                } else {
                    currentCourse = (Course) thing;
                    System.out.println(currentCourse.getName());
                    aStudentSelected = false;
                    aCourseSelected = true;
                    populateCourseEditFields();
                }
            } else {
                aStudentSelected = false;
                aCourseSelected = false;
                aEditNothingPanel.setVisible(true);
                aEditStudentPanel.setVisible(false);
                aEditCoursePanel.setVisible(false);
            }
        }
        updateCurrentLabels();
    }//GEN-LAST:event_aMainJListValueChanged

    private void aRemoveAllCoursesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aRemoveAllCoursesButtonActionPerformed
        clearCourseList();
        updateAdminPanel();
    }//GEN-LAST:event_aRemoveAllCoursesButtonActionPerformed

    private void aSearchClearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aSearchClearButtonActionPerformed
        searchString = "";
        aSearchTextField.setText(searchString);
        updateAdminPanel();
    }//GEN-LAST:event_aSearchClearButtonActionPerformed

    private void aUserAddCourseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aUserAddCourseButtonActionPerformed
        if (currentCourse != null && currentStudent != null) {
            currentStudent.adminAddCompCourse(currentCourse);
            populateStudentEditFields();
            currentCourse = null;
            updateAdminPanel();
        } else {
            System.out.println("Current Student is null");
        }

    }//GEN-LAST:event_aUserAddCourseButtonActionPerformed

    private void aJListStudentAvailableCoursesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aJListStudentAvailableCoursesValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (aJListStudentAvailableCourses.getSelectedIndex() != -1) {
                currentCourse = (Course) aStudentAllCourseModel.get(aJListStudentAvailableCourses.getSelectedIndex());
                aJListStudentCoursesCurrent.clearSelection();
            }
            updateCurrentLabels();
        }

    }//GEN-LAST:event_aJListStudentAvailableCoursesValueChanged

    private void aUserRemoveCourseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aUserRemoveCourseButtonActionPerformed
        if (currentCourse != null) {
            currentStudent.adminRemoveCompCourse(currentCourse);
        }
        currentCourse = null;
        updateAdminPanel();
    }//GEN-LAST:event_aUserRemoveCourseButtonActionPerformed

    private void aJListStudentCoursesCurrentValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aJListStudentCoursesCurrentValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (aJListStudentCoursesCurrent.getSelectedIndex() != -1) {
                currentCourse = (Course) aStudentCourseModel.get(aJListStudentCoursesCurrent.getSelectedIndex());
                aJListStudentAvailableCourses.clearSelection();
            }
            updateCurrentLabels();
        }
    }//GEN-LAST:event_aJListStudentCoursesCurrentValueChanged

    private void sLoginSubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sLoginSubmitButtonActionPerformed
        String loginSearch = sLoginTextField.getText().toLowerCase();
        Student THESTUDENTWITHTHISID = null;
        try {
            int studentID = Integer.parseInt(loginSearch);
            boolean contains = Student.lookup(studentID);
            if (contains) {
                THESTUDENTWITHTHISID = Student.iDFinder();
                sNameLabel.setText(THESTUDENTWITHTHISID.getName());
                sMajorLabel.setText(THESTUDENTWITHTHISID.getMajor());
                sGPALabel.setText(THESTUDENTWITHTHISID.getGpa() + "");
                sCreditsNeededLabel.setText(THESTUDENTWITHTHISID.getCreditsNeeded() + "");
                sStudentIDLabel.setText(THESTUDENTWITHTHISID.getID() + "");
                sTotalCreditsLabel.setText(THESTUDENTWITHTHISID.getTotalCredits() + "");
                sLoginPanel.setVisible(false);
                sMainPanel.setVisible(true);
                currentStudent = THESTUDENTWITHTHISID;
                updateStudentPanel();
            } else {
                //print somewhere that the student was not found

                JOptionPane.showMessageDialog(this, "No such user ID exists. entered else");
            }

        } catch (NumberFormatException nfe) {
            //Code to return not a number, Could have code that makes the search feild only take a number. There should be something like that in the shopping cart project on blackboard
            JOptionPane.showMessageDialog(this, "No such user ID exists. caught exception");
        }


    }//GEN-LAST:event_sLoginSubmitButtonActionPerformed

    private void aCourseCapacityTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aCourseCapacityTextFieldActionPerformed
        // Run Course.checkWaitList() on the course;
    }//GEN-LAST:event_aCourseCapacityTextFieldActionPerformed

    private void aCourseCreditsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aCourseCreditsTextFieldActionPerformed
        // 
    }//GEN-LAST:event_aCourseCreditsTextFieldActionPerformed

    private void aUserSaveChangesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aUserSaveChangesButtonActionPerformed
        currentStudent.setName(aStudentNameTextField.getText());
        currentStudent.setMajor(aStudentMajorTextField.getText());
        currentStudent.setGpa(Double.parseDouble(aStudentGPATextField.getText()));
        //currentStudent.setTotalCredits(Integer.parseInt(aStudentTotalCreditsField.getText()));
        updateAdminPanel();
    }//GEN-LAST:event_aUserSaveChangesButtonActionPerformed

    private void aCourseSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aCourseSaveButtonActionPerformed
        currentCourse.setCapacity(Integer.parseInt(aCourseCapacityTextField.getText()));
        currentCourse.setName(aCourseNameTextField.getText());
        currentCourse.setCredits(Integer.parseInt(aCourseCreditsTextField.getText()));
        currentCourse.setTeacher(aCourseInstructorTextField.getText());
        updateAdminPanel();
    }//GEN-LAST:event_aCourseSaveButtonActionPerformed

    private void aCourseRemoveStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aCourseRemoveStudentButtonActionPerformed
        if (aCurrentCourseStudent != null) {
            currentCourse.removeStudent(aCurrentCourseStudent);
        }
        aCurrentCourseStudent = null;
        updateAdminPanel();
    }//GEN-LAST:event_aCourseRemoveStudentButtonActionPerformed

    private void aJListCourseStudentsEnrolledValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aJListCourseStudentsEnrolledValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (aJListCourseStudentsEnrolled.getSelectedIndex() != -1) {
                aCurrentCourseStudent = (Student) aCourseStudentsEnrolledModel.get(aJListCourseStudentsEnrolled.getSelectedIndex());
            }
        }
    }//GEN-LAST:event_aJListCourseStudentsEnrolledValueChanged

    private void sButtonDropRegisteredCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sButtonDropRegisteredCourseActionPerformed
        if (sBooleanRegisteredCourseSelected) {
            currentStudent.removeCourse(currentCourse);
            updateStudentPanel();
            updateCurrentLabels();
        }
    }//GEN-LAST:event_sButtonDropRegisteredCourseActionPerformed

    private void sJListCoursesTakenValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_sJListCoursesTakenValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (sJListCoursesTaken.getSelectedIndex() != -1) {
                currentCourse = (Course) sModelCoursesTaken.get(sJListCoursesTaken.getSelectedIndex());
                clearStudentSelections(sJListCoursesTaken);
            }
            updateCurrentLabels();
        }
    }//GEN-LAST:event_sJListCoursesTakenValueChanged

    private void sJListAvailableCoursesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_sJListAvailableCoursesValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (sJListAvailableCourses.getSelectedIndex() != -1) {
                currentCourse = (Course) sModelAvailalbeCourses.get(sJListAvailableCourses.getSelectedIndex());
                clearStudentSelections(sJListAvailableCourses);
            }
            updateCurrentLabels();
        }
    }//GEN-LAST:event_sJListAvailableCoursesValueChanged

    private void clearStudentSelections(JList current) {
        if (current == sJListAvailableCourses) {
            sJListCoursesTaken.clearSelection();
            sJListProspectiveCourses.clearSelection();
            sJListRegisteredCourses.clearSelection();
            sBooleanAvailableCourseSelected = true;
            sBooleanProspectCourseSelected = false;
            sBooleanRegisteredCourseSelected = false;
        } else if (current == sJListCoursesTaken) {
            sJListAvailableCourses.clearSelection();
            sJListProspectiveCourses.clearSelection();
            sJListRegisteredCourses.clearSelection();
            sBooleanAvailableCourseSelected = false;
            sBooleanProspectCourseSelected = false;
            sBooleanRegisteredCourseSelected = false;
        } else if (current == sJListProspectiveCourses) {
            sJListCoursesTaken.clearSelection();
            sJListAvailableCourses.clearSelection();
            sJListRegisteredCourses.clearSelection();
            sBooleanAvailableCourseSelected = false;
            sBooleanProspectCourseSelected = true;
            sBooleanRegisteredCourseSelected = false;
        } else if (current == sJListRegisteredCourses) {
            sJListCoursesTaken.clearSelection();
            sJListAvailableCourses.clearSelection();
            sJListProspectiveCourses.clearSelection();
            sBooleanAvailableCourseSelected = false;
            sBooleanProspectCourseSelected = false;
            sBooleanRegisteredCourseSelected = true;
        }
    }

    private void sJListProspectiveCoursesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_sJListProspectiveCoursesValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (sJListProspectiveCourses.getSelectedIndex() != -1) {
                currentCourse = (Course) sModelProspectiveCourses.get(sJListProspectiveCourses.getSelectedIndex());
                clearStudentSelections(sJListProspectiveCourses);
            }
            updateCurrentLabels();
        }
    }//GEN-LAST:event_sJListProspectiveCoursesValueChanged

    private void sJListRegisteredCoursesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_sJListRegisteredCoursesValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (sJListRegisteredCourses.getSelectedIndex() != -1) {
                try {
                    currentCourse = (Course) sModelRegisteredCourses.get(sJListRegisteredCourses.getSelectedIndex());
                    clearStudentSelections(sJListRegisteredCourses);
//                
                } catch (ClassCastException cce) {
                    String courseString = (String) sModelRegisteredCourses.get(sJListRegisteredCourses.getSelectedIndex());
                    String sub = courseString.substring(4, 8);
                    int s = Integer.parseInt(sub);

                    boolean contains = Course.lookup(s);
                    if (contains) {
                        Course c = Course.iDFinder();
                        currentCourse = c;

                    }
                }
                if (currentStudent.accessWaitList().contains(currentCourse)) {
                    JOptionPane.showMessageDialog(this, "You are on a waitlist for this course.");
                }
                updateCurrentLabels();
            }
        }
    }//GEN-LAST:event_sJListRegisteredCoursesValueChanged

    private void fJListCoursesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fJListCoursesValueChanged
        if (!evt.getValueIsAdjusting()) {
            if (fJListCourses.getSelectedIndex() != -1) {
                currentCourse = (Course) fModelCourses.get(fJListCourses.getSelectedIndex());

                updateFacultyPanel();
            }
        }
    }//GEN-LAST:event_fJListCoursesValueChanged

    private void fJListEnrolledValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fJListEnrolledValueChanged

    }//GEN-LAST:event_fJListEnrolledValueChanged

    private void fJListWaitlistValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_fJListWaitlistValueChanged

    }//GEN-LAST:event_fJListWaitlistValueChanged

    private void sButtonAddAvailableCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sButtonAddAvailableCourseActionPerformed
        if (sBooleanAvailableCourseSelected) {
            currentStudent.addProspectiveCourse(currentCourse);
            updateCurrentLabels();
            updateStudentPanel();
            currentCourse = null;
        }
    }//GEN-LAST:event_sButtonAddAvailableCourseActionPerformed

    private void sButtonRemoveClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sButtonRemoveClassActionPerformed
        if (sBooleanProspectCourseSelected) {
            currentStudent.removeCourse(currentCourse);
            updateCurrentLabels();
            updateStudentPanel();
        }
    }//GEN-LAST:event_sButtonRemoveClassActionPerformed

    private void sButtonClearProspectiveCoursesListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sButtonClearProspectiveCoursesListActionPerformed
        if (sBooleanProspectCourseSelected) {
            currentStudent.clearProspectiveCourses();
            currentCourse = null;
            updateCurrentLabels();
            updateStudentPanel();
        }
    }//GEN-LAST:event_sButtonClearProspectiveCoursesListActionPerformed

    private void sButtonRegisterClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sButtonRegisterClassActionPerformed
        if (sBooleanProspectCourseSelected) {
            if (currentCourse != null) {
                currentStudent.addCourse(currentCourse);
                /*
            Do we want to let them add individually? or just all at the same time? 
            We could have another button to do that as long as there is something that stores everything in the list
                 */
                currentCourse = null;
                updateStudentPanel();
                updateCurrentLabels();
            }
        }
    }//GEN-LAST:event_sButtonRegisterClassActionPerformed

    /*
    private void sButtonRegisterAllClassActionPerformed(java.awt.event.ActionEvent evt) {                                                     
        currentStudent.addCourses(sProspectiveCourseList);
    }                                                    
     */
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField aCourseCapacityTextField;
    private javax.swing.JLabel aCourseCountLabel;
    private javax.swing.JTextField aCourseCreditsTextField;
    private javax.swing.JTextField aCourseInstructorTextField;
    private javax.swing.JTextField aCourseNameTextField;
    private javax.swing.JButton aCourseRemoveStudentButton;
    private javax.swing.JButton aCourseSaveButton;
    private javax.swing.JLabel aCurrentCourseJLabel;
    private javax.swing.JLabel aCurrentStudentJLabel;
    private javax.swing.JButton aDeleteSelectedButton;
    private javax.swing.JPanel aEditCoursePanel;
    private javax.swing.JPanel aEditNothingPanel;
    private javax.swing.JPanel aEditSpacePanel;
    private javax.swing.JPanel aEditStudentPanel;
    private javax.swing.JButton aGenerateCoursesButton;
    private javax.swing.JButton aGenerateStudentsButton;
    private javax.swing.JLabel aIDNumberLabel;
    private javax.swing.JList<String> aJListCourseStudentsEnrolled;
    private javax.swing.JList<String> aJListStudentAvailableCourses;
    private javax.swing.JList<String> aJListStudentCoursesCurrent;
    private javax.swing.JList<String> aMainJList;
    private javax.swing.JButton aRemoveAllCoursesButton;
    private javax.swing.JButton aRemoveAllStudentsButton;
    private javax.swing.JButton aSearchClearButton;
    private javax.swing.JButton aSearchSubmitButton;
    private javax.swing.JTextField aSearchTextField;
    private javax.swing.JCheckBox aShowCoursesCheckBox;
    private javax.swing.JCheckBox aShowStudentsCheckBox;
    private javax.swing.JLabel aStudentCountLabel;
    private javax.swing.JTextField aStudentCreditsNeededField;
    private javax.swing.JTextField aStudentGPATextField;
    private javax.swing.JTextField aStudentMajorTextField;
    private javax.swing.JTextField aStudentNameTextField;
    private javax.swing.JTextField aStudentTotalCreditsField;
    private javax.swing.JButton aUserAddCourseButton;
    private javax.swing.JScrollPane aUserListScrollPane;
    private javax.swing.JButton aUserRemoveCourseButton;
    private javax.swing.JButton aUserSaveChangesButton;
    private javax.swing.JPanel adminPanel;
    private javax.swing.JButton exitButton;
    private javax.swing.JList<String> fJListCourses;
    private javax.swing.JList<String> fJListEnrolled;
    private javax.swing.JList<String> fJListWaitlist;
    private javax.swing.JPanel facultyPanel;
    private javax.swing.JButton homeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JRadioButton lAdminRadioButton;
    private javax.swing.JRadioButton lFacultyRadioButton;
    private javax.swing.JButton lLoginSubmitButton;
    private javax.swing.JRadioButton lStudentRadioButton;
    private javax.swing.ButtonGroup loginButtonGroup;
    private javax.swing.JLabel loginInstructionLabel;
    private javax.swing.JPanel loginPanel;
    private javax.swing.JLabel loginTitleLabel;
    private javax.swing.JLabel loginWelcomeLabel;
    private javax.swing.JPanel mainUIPanel;
    private javax.swing.JScrollPane sAvailableCoursesScrollPane;
    private javax.swing.JButton sButtonAddAvailableCourse;
    private javax.swing.JButton sButtonClearProspectiveCoursesList;
    private javax.swing.JButton sButtonDropRegisteredCourse;
    private javax.swing.JButton sButtonRegisterClass;
    private javax.swing.JButton sButtonRemoveClass;
    private javax.swing.JScrollPane sCoursesScrollPane;
    private javax.swing.JLabel sCreditsNeededLabel;
    private javax.swing.JLabel sGPALabel;
    private javax.swing.JList<String> sJListAvailableCourses;
    private javax.swing.JList<String> sJListCoursesTaken;
    private javax.swing.JList<String> sJListProspectiveCourses;
    private javax.swing.JList<String> sJListRegisteredCourses;
    private javax.swing.JLabel sLabelCredits;
    private javax.swing.JLabel sLabelCurrentCourse;
    private javax.swing.JLabel sLabelDays;
    private javax.swing.JLabel sLabelID;
    private javax.swing.JLabel sLabelInstructor;
    private javax.swing.JLabel sLabelMajor;
    private javax.swing.JLabel sLabelTimes;
    private javax.swing.JPanel sLoginPanel;
    private javax.swing.JButton sLoginSubmitButton;
    private javax.swing.JTextField sLoginTextField;
    private javax.swing.JPanel sMainPanel;
    private javax.swing.JLabel sMajorLabel;
    private javax.swing.JLabel sNameLabel;
    private javax.swing.JScrollPane sProspectiveCoursesScrollPane;
    private javax.swing.JLabel sStudentIDLabel;
    private javax.swing.JLabel sTotalCreditsLabel;
    private javax.swing.JPanel studentPanel;
    // End of variables declaration//GEN-END:variables
}
