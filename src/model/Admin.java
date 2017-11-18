package model;

import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;

/**
 * This class defines Admin functionality
 * @author Benjamin Ker (bk375)
 */
@SuppressWarnings({"unchecked", "Duplicates"})
public class Admin{
    public static ObservableList<User> users = FXCollections.observableArrayList();

    public static final String storeDir = "database";
    public static final String storeFile = "users.ser";

    /**
     * Admin is non-instantiable
     */
    private Admin(){}

    /**
     * Serializes list of users
     * @return True if successful, false otherwise
     */
    public static boolean saveUser() {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
            oos.writeObject(new ArrayList<>(users));
            oos.close();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Deserializes list of users
     * @return True if successful, false otherwise
     */
    public static boolean loadUser(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
            ArrayList<User> read = (ArrayList<User>)(ois.readObject());
            users = FXCollections.observableArrayList(read);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Creates a user object and adds it to list of users
     * @param username Username of new user
     * @param password Password of new user
     */
    public static void createUser(String username, String password, String verify){
        User new_user = new User(username, password);
        if(username.equals("admin")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Not allowed to create an admin");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }else if(username.isEmpty() || password.isEmpty() || verify.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }else if(!password.equals(verify)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Passwords do not match");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }else if(users.contains(new_user)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicate username");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }
        users.add(new_user);
        saveUser();
    }

    /**
     * Deletes user if selected in ListView
     * @param index Index of User object to be removed
     */
    // Remove User object from list, given index
    public static boolean deleteUser(int index){
        try{
            users.remove(index);
            saveUser();
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
