package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;

/**
 * This class creates Users and defines functionality
 * @author Benjamin Ker (bk375)
 * @author Jake Karasik (jak451)
 */
@SuppressWarnings({"unchecked", "Duplicates"})
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
    public final String storeDir = "database/user";
    public final String storeFile = ".ser";

    private String username;
    private String password;

    public static transient ObservableList<Album> albums = FXCollections.observableArrayList();

    public static transient ObservableList<Photo> master_album = FXCollections.observableArrayList();

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

            ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + username + "-" + "MASTER-ALBUM" + storeFile));

            ArrayList<Photo> read_master = (ArrayList<Photo>)(ois.readObject());
            User.master_album = FXCollections.observableArrayList(read_master);
            return true;
        }catch(Exception e){
            albums = FXCollections.observableArrayList();
            if(getUser().equals("stock")){
                Album stock = new Album("stock");
                albums.add(stock);
                File path = new File("src/resources/stock");
                File[] dir = path.listFiles();
                if(dir != null){
                    for(File file : dir){
                        stock.addPhoto(file);
                    }
                }
            }
            saveUser();
            return false;
        }
    }

    /**
     * Adds a new album to list
     * @param new_album New album to be added
     */
    public void addAlbum(Album new_album){
        albums.add(new_album);
        new_album.savePhotos();
        saveUser();
    }

    public boolean deleteAlbum(int index){
        try{
            String rm_album = albums.get(index).getTitle();
            String rm_owner = getUser();
            File path = new File("database/user");
            File[] dir = path.listFiles();
            if(dir != null){
                for(File file : dir){
                    if(file.getPath().contains(rm_owner + "-" + rm_album)){
                        file.delete();
                    }
                }
            }
            albums.remove(index);
            saveUser();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * ALlows ListView to print out usernames
     * @return String
     */
    @Override
    public String toString(){
        return this.username;
    }

    /**
     * Allows contains() and indexOf() to search for users
     * @param s User to be compared to
     * @return True if match found, false otherwise
     */
    @Override
    public boolean equals(Object s){
        return ( s != null && s instanceof User) && ( this.username.equalsIgnoreCase(((User)s).username) );
    }
}
