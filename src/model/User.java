package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;

/**
 * This class creates Users and defines functionality
 * @author Benjamin Ker (bk375)
 */
@SuppressWarnings({"unchecked", "Duplicates"})
public class User implements Serializable {

    public final String storeDir = "database/user";
    public final String storeFile = this.username + ".ser";

    private String username;
    private String password;
    private boolean active;

    private transient ObservableList<Album> albums = FXCollections.observableArrayList();

    public User(String user, String pass){
        this.username = user;
        this.password = pass;
        this.active = false;
    }

    public String getUser(){
        return username;
    }
    public String getPass(){
        return password;
    }
    public boolean active() { return active; }


    /**
     * Serializes list of albums
     * @return True if successful, false otherwise
     */
    public boolean saveUser() {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
            oos.writeObject(new ArrayList<>(albums));
            oos.close();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Deserializes list of albums
     * @return True if successful, false otherwise
     */
    public boolean loadUser(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
            ArrayList<Album> read = (ArrayList<Album>)(ois.readObject());
            albums = FXCollections.observableArrayList(read);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    public void createAlbum(){

    }


    public boolean authenticated(){
        Admin temp_admin = new Admin();
        boolean exists = temp_admin.users.contains(this);

        if(this.username.isEmpty() || this.password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return false;
        }
        else if(!exists){
            Alert alert = new Alert(Alert.AlertType.ERROR, "User does not exist");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return false;
        }

        User cur_user = temp_admin.users.get(temp_admin.users.indexOf(this));

        if(!cur_user.password.equals(this.password)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Password does not match");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return false;
        }
        this.active = true;

        return true;
    }

    @Override
    public String toString(){
        return this.username;
    }

    @Override
    public boolean equals(Object s){
        return ( s != null && s instanceof User) && ( this.username.equalsIgnoreCase(((User)s).username) );
    }
}
