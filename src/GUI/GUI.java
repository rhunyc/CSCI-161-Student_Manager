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
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
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
    DefaultListModel[] models = new DefaultListModel[] {model, aStudentCourseModel, aStudentAllCourseModel};
    boolean aStudentSelected = false;                                       // These are checks to see what we're selecting, the reason why there is one for both, is it's possible to not have either selected
    boolean aCourseSelected = false;
    boolean[] adminBooleans = new boolean[]{aStudentSelected, aCourseSelected};
    Student currentStudent = null;                                          // This will hold our currently selected student
    Student aCurrentCourseStudent = null;                                    // This is for removing students in our adminPanel course edit screen
    Course currentCourse = null;                                            // This holds our currently selected course

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
    
  


    // This is the method to delete whatever is selected
    public void delete() {
        if (selectedIndex != -1) {                                                  // If we don't have anything selected, don't do anything
            if (aStudentSelected) {                                                  
                Administrator.deleteStudent((Student) model.get(selectedIndex));
                aEditStudentPanel.setVisible(false);                                // When deleting, it resets our selected index, without including these the panel will still
                aEditNothingPanel.setVisible(true);                                 // be visible, showing the previously deleted students info
                currentStudent = null;                                              // Resetting out current courses and students to null
                currentCourse = null;
            }
            if (aCourseSelected) {
                Administrator.deleteCourse((Course) model.get(selectedIndex));
                aEditCoursePanel.setVisible(false);
                aEditNothingPanel.setVisible(true);
                currentCourse = null;
                currentStudent = null;
            }
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not save file!");
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not load file!");
        }
    }

    // This will clear what is visible in the admin main panel JList
    private void clearAdminMainJList() {
        model.removeAllElements();
    }
    
    
    // This is the method that will populate our student fields
    private void populateStudentEditFields() {
        GUIAdminHelper.populateStudentEditFields(currentStudent, models, adminBooleans, aStudentNameTextField, 
                aStudentGPATextField, aStudentMajorTextField, aStudentCreditsNeededField, aStudentTotalCreditsField, aStudentCoursesJList,
                aAvailableCoursesList, aEditStudentPanel, aEditCoursePanel, aEditNothingPanel);  
    }

    private void populateCourseEditFields() {
        if (currentCourse != null){
        aCourseStudentsEnrolledModel.removeAllElements();
        int k = 0;
        aStudentSelected = false;
        aCourseSelected = true;
        for (int i = 0; i < currentCourse.getEnrolled().size(); i++){
            aCourseStudentsEnrolledModel.add(i, currentCourse.getEnrolled().get(i));
        }
        aCourseCapacityTextField.setText(currentCourse.getCapacity() + "");
        aCourseCreditsTextField.setText(currentCourse.getCredits() + "");
        aCourseInstructorTextField.setText(currentCourse.getTeacher());
        aCourseNameTextField.setText(currentCourse.getName());
        aCourseStudentsEnrolledJList.setModel(aCourseStudentsEnrolledModel);
        aEditStudentPanel.setVisible(false);
        aEditCoursePanel.setVisible(true);
        aEditNothingPanel.setVisible(false);
        }
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
            }}
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
        adminPanel = new javax.swing.JPanel();
        aEditSpacePanel = new javax.swing.JPanel();
        aEditNothingPanel = new javax.swing.JPanel();
        aEditStudentPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        aStudentCoursesJList = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        aAvailableCoursesList = new javax.swing.JList<>();
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
        aEditCoursePanel = new javax.swing.JPanel();
        aCourseNameTextField = new javax.swing.JTextField();
        aCourseInstructorTextField = new javax.swing.JTextField();
        aCourseCapacityTextField = new javax.swing.JTextField();
        aCourseCreditsTextField = new javax.swing.JTextField();
        aCourseSaveButton = new javax.swing.JButton();
        aCourseRemoveStudentButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        aCourseStudentsEnrolledJList = new javax.swing.JList<>();
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
        facultyPanel = new javax.swing.JPanel();
        studentPanel = new javax.swing.JPanel();
        sMainPanel = new javax.swing.JPanel();
        sNameLabel = new javax.swing.JLabel();
        sStudentIDLabel = new javax.swing.JLabel();
        sMajorLabel = new javax.swing.JLabel();
        sGPALabel = new javax.swing.JLabel();
        sTotalCreditsLabel = new javax.swing.JLabel();
        sCreditsNeededLabel = new javax.swing.JLabel();
        sRegisterButton = new javax.swing.JButton();
        sAddButton = new javax.swing.JButton();
        sClearButton = new javax.swing.JButton();
        sRemoveButton = new javax.swing.JButton();
        sCoursesScrollPane = new javax.swing.JScrollPane();
        sCoursesList = new javax.swing.JList<>();
        sAvailableCoursesScrollPane = new javax.swing.JScrollPane();
        sAvailableCoursesList = new javax.swing.JList<>();
        sProspectiveCoursesScrollPane = new javax.swing.JScrollPane();
        sProspectiveCoursesList = new javax.swing.JList<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel19 = new javax.swing.JLabel();
        sDropButton = new javax.swing.JButton();
        sLoginPanel = new javax.swing.JPanel();
        sLoginTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        sLoginSubmitButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
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

        lLoginSubmitButton.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        lLoginSubmitButton.setText("Submit");
        lLoginSubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lLoginSubmitButtonActionPerformed(evt);
            }
        });

        loginWelcomeLabel.setFont(new java.awt.Font("Lucida Console", 1, 36)); // NOI18N
        loginWelcomeLabel.setText("Welcome!");

        loginInstructionLabel.setFont(new java.awt.Font("Lucida Console", 0, 18)); // NOI18N
        loginInstructionLabel.setText("To get started, select the appropriate user type and hit submit.");

        loginTitleLabel.setFont(new java.awt.Font("Lucida Console", 0, 14)); // NOI18N
        loginTitleLabel.setText("Student Management Program v 1.0");

        loginButtonGroup.add(lStudentRadioButton);
        lStudentRadioButton.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        lStudentRadioButton.setText("Student");
        lStudentRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lStudentRadioButtonActionPerformed(evt);
            }
        });

        loginButtonGroup.add(lFacultyRadioButton);
        lFacultyRadioButton.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        lFacultyRadioButton.setText("Faculty");
        lFacultyRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lFacultyRadioButtonActionPerformed(evt);
            }
        });

        loginButtonGroup.add(lAdminRadioButton);
        lAdminRadioButton.setFont(new java.awt.Font("Lucida Console", 1, 24)); // NOI18N
        lAdminRadioButton.setText("Admin");
        lAdminRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lAdminRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout loginPanelLayout = new javax.swing.GroupLayout(loginPanel);
        loginPanel.setLayout(loginPanelLayout);
        loginPanelLayout.setHorizontalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loginPanelLayout.createSequentialGroup()
                .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(208, 208, 208)
                        .addComponent(loginTitleLabel))
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(244, 244, 244)
                        .addComponent(loginWelcomeLabel))
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(274, 274, 274)
                        .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lAdminRadioButton)
                            .addGroup(loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(lFacultyRadioButton)
                                .addComponent(lStudentRadioButton))))
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(lLoginSubmitButton))
                    .addGroup(loginPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(loginInstructionLabel)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        loginPanelLayout.setVerticalGroup(
            loginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loginPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loginTitleLabel)
                .addGap(18, 18, 18)
                .addComponent(loginWelcomeLabel)
                .addGap(18, 18, 18)
                .addComponent(loginInstructionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lStudentRadioButton)
                .addGap(18, 18, 18)
                .addComponent(lFacultyRadioButton)
                .addGap(18, 18, 18)
                .addComponent(lAdminRadioButton)
                .addGap(46, 46, 46)
                .addComponent(lLoginSubmitButton)
                .addGap(82, 82, 82))
        );

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
            .addGap(0, 417, Short.MAX_VALUE)
        );
        aEditNothingPanelLayout.setVerticalGroup(
            aEditNothingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 532, Short.MAX_VALUE)
        );

        aEditSpacePanel.add(aEditNothingPanel, "card2");

        aEditStudentPanel.setMaximumSize(new java.awt.Dimension(391, 387));
        aEditStudentPanel.setMinimumSize(new java.awt.Dimension(391, 387));

        aStudentCoursesJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        aStudentCoursesJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aStudentCoursesJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(aStudentCoursesJList);

        aAvailableCoursesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        aAvailableCoursesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aAvailableCoursesListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(aAvailableCoursesList);

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

        jLabel23.setText("Student's Courses:");

        jLabel15.setText("Name:");

        jLabel24.setText("Available Courses:");

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
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel23)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel21)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aEditStudentPanelLayout.createSequentialGroup()
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel18))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(aStudentGPATextField)
                                    .addComponent(aStudentTotalCreditsField)
                                    .addComponent(aStudentCreditsNeededField, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, aEditStudentPanelLayout.createSequentialGroup()
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel20))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(aStudentMajorTextField)
                                    .addComponent(aStudentNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))))
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(aEditStudentPanelLayout.createSequentialGroup()
                                        .addComponent(jLabel24)
                                        .addGap(0, 103, Short.MAX_VALUE))
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
                        .addGap(9, 9, 9)
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(aStudentGPATextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(aStudentTotalCreditsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(aEditStudentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aStudentCreditsNeededField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22))
                .addGap(42, 42, 42)
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
                .addContainerGap(153, Short.MAX_VALUE))
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

        aCourseStudentsEnrolledJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                aCourseStudentsEnrolledJListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(aCourseStudentsEnrolledJList);

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
                .addContainerGap(52, Short.MAX_VALUE))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(aEditSpacePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
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

        javax.swing.GroupLayout facultyPanelLayout = new javax.swing.GroupLayout(facultyPanel);
        facultyPanel.setLayout(facultyPanelLayout);
        facultyPanelLayout.setHorizontalGroup(
            facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1564, Short.MAX_VALUE)
        );
        facultyPanelLayout.setVerticalGroup(
            facultyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );

        mainUIPanel.add(facultyPanel, "card2");

        studentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Student"));
        studentPanel.setMaximumSize(new java.awt.Dimension(742, 512));
        studentPanel.setMinimumSize(new java.awt.Dimension(742, 512));
        studentPanel.setLayout(new java.awt.CardLayout());

        sMainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Main"));

        sNameLabel.setText("(Student name)");

        sStudentIDLabel.setText("(ID)");

        sMajorLabel.setText("(Major)");

        sGPALabel.setText("(GPA)");

        sTotalCreditsLabel.setText("(Total Credits)");

        sCreditsNeededLabel.setText("(Credits Needed)");

        sRegisterButton.setText("Register");

        sAddButton.setText("Add");

        sClearButton.setText("Clear");

        sRemoveButton.setText("Remove");

        sCoursesScrollPane.setViewportView(sCoursesList);

        sAvailableCoursesScrollPane.setViewportView(sAvailableCoursesList);

        sProspectiveCoursesScrollPane.setViewportView(sProspectiveCoursesList);

        jLabel9.setText("Prospective Courses:");

        jLabel13.setText("Available Courses:");

        jLabel14.setText("ID:");

        jLabel7.setText("Courses taken:");

        jLabel6.setText("Student:");

        jLabel10.setText("GPA:");

        jLabel8.setText("Major:");

        jLabel12.setText("Credits Needed:");

        jLabel11.setText("Total Credits:");

        jScrollPane2.setViewportView(jList1);

        jLabel19.setText("Registered Courses:");

        sDropButton.setText("Drop");

        javax.swing.GroupLayout sMainPanelLayout = new javax.swing.GroupLayout(sMainPanel);
        sMainPanel.setLayout(sMainPanelLayout);
        sMainPanelLayout.setHorizontalGroup(
            sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(sMainPanelLayout.createSequentialGroup()
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addGap(25, 25, 25)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sCreditsNeededLabel)
                            .addComponent(sTotalCreditsLabel)
                            .addComponent(sGPALabel)
                            .addComponent(sMajorLabel)
                            .addComponent(sNameLabel)
                            .addComponent(sStudentIDLabel)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sMainPanelLayout.createSequentialGroup()
                        .addComponent(sRegisterButton)
                        .addGap(18, 18, 18)
                        .addComponent(sRemoveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sClearButton))
                    .addComponent(sProspectiveCoursesScrollPane)
                    .addComponent(jLabel14)
                    .addComponent(jScrollPane2)
                    .addGroup(sMainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(sDropButton))
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel13)
                                .addComponent(sAvailableCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(sAddButton, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addComponent(jLabel7))
                    .addComponent(sCoursesScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        sMainPanelLayout.setVerticalGroup(
            sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sMainPanelLayout.createSequentialGroup()
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sMainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(sNameLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(sStudentIDLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(sMajorLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(sGPALabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(sTotalCreditsLabel)))
                    .addGroup(sMainPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(sCreditsNeededLabel))
                .addGap(20, 20, 20)
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(sMainPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(sMainPanelLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel9))
                            .addGroup(sMainPanelLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(sDropButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sProspectiveCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(sAvailableCoursesScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sMainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sClearButton)
                    .addComponent(sRegisterButton)
                    .addComponent(sAddButton)
                    .addComponent(sRemoveButton))
                .addContainerGap())
        );

        studentPanel.add(sMainPanel, "card2");

        sLoginPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Login"));

        sLoginTextField.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        sLoginTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sLoginTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sLoginTextFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Enter Student ID");

        sLoginSubmitButton.setText("Submit");
        sLoginSubmitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sLoginSubmitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sLoginPanelLayout = new javax.swing.GroupLayout(sLoginPanel);
        sLoginPanel.setLayout(sLoginPanelLayout);
        sLoginPanelLayout.setHorizontalGroup(
            sLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sLoginPanelLayout.createSequentialGroup()
                .addGroup(sLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sLoginPanelLayout.createSequentialGroup()
                        .addGap(277, 277, 277)
                        .addGroup(sLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sLoginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(sLoginPanelLayout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel1))))
                    .addGroup(sLoginPanelLayout.createSequentialGroup()
                        .addGap(318, 318, 318)
                        .addComponent(sLoginSubmitButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        sLoginPanelLayout.setVerticalGroup(
            sLoginPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sLoginPanelLayout.createSequentialGroup()
                .addGap(175, 175, 175)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(sLoginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sLoginSubmitButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        studentPanel.add(sLoginPanel, "card2");

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
        } else if (gh.isLoginFaculty()) {
            adminPanel.setVisible(false);
            studentPanel.setVisible(false);
            loginPanel.setVisible(false);
            facultyPanel.setVisible(true);
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
        if (currentCourse != null)
            currentStudent.addCourse(currentCourse);
        currentCourse = null;    
        updateAdminPanel();
        System.out.println("boo");
    }//GEN-LAST:event_aUserAddCourseButtonActionPerformed

    private void aAvailableCoursesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aAvailableCoursesListValueChanged
        if (!evt.getValueIsAdjusting()){
            if (aAvailableCoursesList.getSelectedIndex() != -1){
                System.out.println(currentCourse);    
                System.out.println(currentStudent);
                System.out.println("\n");
                currentCourse = (Course) model.get(aAvailableCoursesList.getSelectedIndex());
                System.out.println(currentCourse);
                System.out.println(currentStudent);
                System.out.println("\n");
            }
        }
        
    }//GEN-LAST:event_aAvailableCoursesListValueChanged

    private void aUserRemoveCourseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aUserRemoveCourseButtonActionPerformed
        if (currentCourse != null)
            currentStudent.removeCourse(currentCourse);
        currentCourse = null;
        updateAdminPanel();
    }//GEN-LAST:event_aUserRemoveCourseButtonActionPerformed

    private void aStudentCoursesJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aStudentCoursesJListValueChanged
        if (!evt.getValueIsAdjusting()){
            if (aStudentCoursesJList.getSelectedIndex() != -1)
                    currentCourse = (Course) model.get(aStudentCoursesJList.getSelectedIndex());
        }
    }//GEN-LAST:event_aStudentCoursesJListValueChanged

    private void sLoginSubmitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sLoginSubmitButtonActionPerformed
        searchString = sLoginTextField.getText().toLowerCase();
        Student THESTUDENTWITHTHISID = null;
        try{
            int studentID = Integer.parseInt(searchString);
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
            } else {
                //print somewhere that the student was not found
                JOptionPane.showMessageDialog(this, "No such user ID exists.");
            }
                
        }
        catch(NumberFormatException nfe){
            //Code to return not a number, Could have code that makes the search feild only take a number. There should be something like that in the shopping cart project on blackboard
            JOptionPane.showMessageDialog(this, "No such user ID exists.");
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
        if (aCurrentCourseStudent != null){
            currentCourse.removeStudent(aCurrentCourseStudent);
        }
        aCurrentCourseStudent = null;
        updateAdminPanel();
    }//GEN-LAST:event_aCourseRemoveStudentButtonActionPerformed

    private void aCourseStudentsEnrolledJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_aCourseStudentsEnrolledJListValueChanged
        if (!evt.getValueIsAdjusting()) {
            if  (aCourseStudentsEnrolledJList.getSelectedIndex() != -1)
            aCurrentCourseStudent = (Student) aCourseStudentsEnrolledModel.get(aCourseStudentsEnrolledJList.getSelectedIndex());
        }
    }//GEN-LAST:event_aCourseStudentsEnrolledJListValueChanged

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
    private javax.swing.JList<String> aAvailableCoursesList;
    private javax.swing.JTextField aCourseCapacityTextField;
    private javax.swing.JLabel aCourseCountLabel;
    private javax.swing.JTextField aCourseCreditsTextField;
    private javax.swing.JTextField aCourseInstructorTextField;
    private javax.swing.JTextField aCourseNameTextField;
    private javax.swing.JButton aCourseRemoveStudentButton;
    private javax.swing.JButton aCourseSaveButton;
    private javax.swing.JList<String> aCourseStudentsEnrolledJList;
    private javax.swing.JButton aDeleteSelectedButton;
    private javax.swing.JPanel aEditCoursePanel;
    private javax.swing.JPanel aEditNothingPanel;
    private javax.swing.JPanel aEditSpacePanel;
    private javax.swing.JPanel aEditStudentPanel;
    private javax.swing.JButton aGenerateCoursesButton;
    private javax.swing.JButton aGenerateStudentsButton;
    private javax.swing.JList<String> aMainJList;
    private javax.swing.JButton aRemoveAllCoursesButton;
    private javax.swing.JButton aRemoveAllStudentsButton;
    private javax.swing.JButton aSearchClearButton;
    private javax.swing.JButton aSearchSubmitButton;
    private javax.swing.JTextField aSearchTextField;
    private javax.swing.JCheckBox aShowCoursesCheckBox;
    private javax.swing.JCheckBox aShowStudentsCheckBox;
    private javax.swing.JLabel aStudentCountLabel;
    private javax.swing.JList<String> aStudentCoursesJList;
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
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
    private javax.swing.JButton sAddButton;
    private javax.swing.JList<String> sAvailableCoursesList;
    private javax.swing.JScrollPane sAvailableCoursesScrollPane;
    private javax.swing.JButton sClearButton;
    private javax.swing.JList<String> sCoursesList;
    private javax.swing.JScrollPane sCoursesScrollPane;
    private javax.swing.JLabel sCreditsNeededLabel;
    private javax.swing.JButton sDropButton;
    private javax.swing.JLabel sGPALabel;
    private javax.swing.JPanel sLoginPanel;
    private javax.swing.JButton sLoginSubmitButton;
    private javax.swing.JTextField sLoginTextField;
    private javax.swing.JPanel sMainPanel;
    private javax.swing.JLabel sMajorLabel;
    private javax.swing.JLabel sNameLabel;
    private javax.swing.JList<String> sProspectiveCoursesList;
    private javax.swing.JScrollPane sProspectiveCoursesScrollPane;
    private javax.swing.JButton sRegisterButton;
    private javax.swing.JButton sRemoveButton;
    private javax.swing.JLabel sStudentIDLabel;
    private javax.swing.JLabel sTotalCreditsLabel;
    private javax.swing.JPanel studentPanel;
    // End of variables declaration//GEN-END:variables
}
