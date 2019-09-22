/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import Interfaces.User;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author zachary.d.anderson
 */
public class Example {
    
    private static ArrayList<User> userList;
    
    
    public static void main(String[] args) throws Exception{
      //  userList = new ArrayList();
        
//        for(int x = 0; x < 50; x++)
//        {
//            userList.add(User());
//        }

//        userList = Administrator.userMaster;
        
    //    writeList();
    }
    
    public static void writeList(ArrayList<User> userArrayList) throws Exception{
        File f = new File("output.data");
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        
        oos.writeObject(userArrayList);
    }
    
    public static ArrayList<User> readList()throws Exception{
        File f = new File("output.data");
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fis);
        
        userList = (ArrayList<User>) ois.readObject();
        return userList;
    }
}