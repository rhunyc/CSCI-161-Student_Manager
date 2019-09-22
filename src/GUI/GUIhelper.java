/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.Serializable;

/**
 *
 * @author Brian
 */
public class GUIhelper implements Serializable{
    boolean loginStudent = false;
    boolean loginFaculty = false;
    boolean loginAdmin = false;
    Object[] dataStore;
    
    public GUIhelper(){
        
    }
    
    public GUIhelper(Object[] temp){
        dataStore = temp;
    }

    public boolean isLoginStudent() {
        return loginStudent;
    }

    public void setLoginStudent(boolean loginStudent) {
        this.loginStudent = loginStudent;
    }

    public boolean isLoginFaculty() {
        return loginFaculty;
    }

    public void setLoginFaculty(boolean loginFaculty) {
        this.loginFaculty = loginFaculty;
    }

    public boolean isLoginAdmin() {
        return loginAdmin;
    }

    public void setLoginAdmin(boolean loginAdmin) {
        this.loginAdmin = loginAdmin;
    }
    
    
    public void loginCheckedStudent(){
        loginStudent = true;
        loginAdmin = false;
        loginFaculty = false;
    }
    
    public void loginCheckedFaculty(){
        loginStudent = false;
        loginAdmin = false;
        loginFaculty = true;
    }
    
    public void loginCheckedAdmin(){
        loginStudent = false;
        loginAdmin = true;
        loginFaculty = false;
    }
}
