package model;


import photos.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class creates an admin and defines functionality
 * @author Benjamin Ker (bk375)
 */
public class Admin {

    private String user;
    private String pass;

    ObservableList<User> users = FXCollections.observableArrayList();

    /**
     * Creates an administrator when program initialized and no admin exists
     */
    public Admin(){
        this.user = "admin";
        this.pass = "admin";
    }

    /**
     * Gets user list
     * @return Returns list of users
     */
    public ObservableList listUsers(){
        return users;
    }


    /**
     * Creates a user object and adds it to list of users
     * @param username Username of new user
     * @param password Password of new user
     */
    public void createUser(String username, String password){
        User new_user = new User(username, password);
        users.add(new_user);
    }

    /**
     * Deletes user if selected in ListView
     * @param index Index of User object to be removed
     */
    // Remove User object from list, given index
    public void deleteUser(int index){

    }

    /**
     * Deletes user given a string input
     * @param username Username of User object to be removed
     */
    // Remove User object from list, given username
    public void deleteUser(String username){

    }

}
