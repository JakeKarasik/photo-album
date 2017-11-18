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
    public final String storeFile = ".ser";

    private String username;
    private String password;

    public transient ObservableList<Album> albums = FXCollections.observableArrayList();

    public User(String user, String pass){
        this.username = user;
        this.password = pass;
    }

    public String getUser(){
        return username;
    }
    public String getPass(){
        return password;
    }


    /**
     * Serializes list of albums
     * @return True if successful, false otherwise
     */
    public boolean saveUser() {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + getUser() + storeFile));
            ArrayList<Album> copy = new ArrayList<>(albums);
            oos.writeObject(copy);
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
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + getUser() + storeFile));
            ArrayList<Album> read = (ArrayList<Album>)(ois.readObject());
            albums = FXCollections.observableArrayList(read);
            return true;
        }catch(Exception e){
            Album stock = new Album("stock", this);
            albums = FXCollections.observableArrayList();
            albums.add(stock);
            for(int i = 1; i < 7; i++){
                stock.addPhoto("resources/stock/stock-" + i + ".jpg");
            }
            saveUser();
            return false;
        }
    }

    public void addAlbum(Album new_album){

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
