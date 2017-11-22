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

    /**
     * ID used for tracking serial version
     */
	private static final long serialVersionUID = 1L;

	/**
     * Directory where user will be stored
	 */
    public final String storeDir = "data/user";

    /**
     * File extension of serialization
     */
    public final String storeFile = ".ser";

    /**
     * Username of user
     */
    private String username;

    /**
     * Password of user
     */
    private String password;

    /**
     * Stores list of albums owned by user
     */
    public transient ObservableList<Album> albums = FXCollections.observableArrayList();

    /**
     * Creates a User object
     * @param user Username of user
     * @param pass Password of user
     */
    public User(String user, String pass){
        this.username = user;
        this.password = pass;
    }

    /**
     * Getter for username
     * @return String username
     */
    public String getUser(){
        return username;
    }

    /**
     * Getter for password
     * @return String password
     */
    public String getPass(){
        return password;
    }


    /**
     * Serializes list of albums
     * @return true if successful, false otherwise
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
     * @return true if successful, false otherwise
     */
    public boolean loadUser(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + getUser() + storeFile));
            ArrayList<Album> read = (ArrayList<Album>)(ois.readObject());
            albums = FXCollections.observableArrayList(read);
            return true;
        }catch(Exception e){
            albums = FXCollections.observableArrayList();
            if(getUser().equals("stock")){
                Album stock = new Album("stock");
                albums.add(stock);
                File path = new File(System.getProperty("user.dir") + "/data/resources/stock");
                File[] dir = path.listFiles();
                if(dir != null){
                    for(File file : dir){
                        stock.addPhoto(file);
                    }
                }
                stock.savePhotos();
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

    /**
     * Deletes album
     * @param index Index of album to be deleted
     * @return true if successful, false otherwise
     */
    public boolean deleteAlbum(int index){
        try{
            String rm_album = albums.get(index).getTitle();
            String rm_owner = getUser();
            File path = new File(System.getProperty("user.dir") + "/data/user");
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
     * Allows ListView to print out usernames
     * @return String username
     */
    @Override
    public String toString(){
        return this.username;
    }

    /**
     * Allows contains() and indexOf() to search for users
     * @param s User to be compared to
     * @return true if match found, false otherwise
     */
    @Override
    public boolean equals(Object s){
        return ( s != null && s instanceof User) && ( this.username.equalsIgnoreCase(((User)s).username) );
    }
}
