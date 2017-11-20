package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;

/**
 * This class creates an album and defines its functionality
 * @author Benjamin Ker (bk375)
 */
@SuppressWarnings({"unchecked", "Duplicates"})
public class Album implements Serializable {

    // Metadata
    private String album_title;
    private User owner;
    public final String storeDir = "database/user";
    public final String storeFile = ".ser";

    // List of photos contained by album
    public transient ObservableList<Photo> photos;

    /**
     * Creates our album and assigns it a title and owner
     * @param title Title of album.
     * @param user Initial owner of album;
     */
    public Album(String title, User user){
        this.album_title = title;
        this.photos = FXCollections.observableArrayList();
        owner = user;
    }

    /**
     * Gets owner of album selected
     * @return Returns User owner
     */
    public User getOwner(){ return owner; }

    /**
     * Gets title of album selected
     * @return Returns album title
     */
    public String getTitle(){ return album_title; }

    /**
     * Sets title of album selected
     * @param new_title New title of album
     */
    public void setTitle(String new_title){ this.album_title = new_title; }

    /**
     * Adds photo object to list photos
     * @param photo_file File of our photo to add
     */
    public void addPhoto(File photo_file){
        Photo new_photo = new Photo(photo_file);
        photos.add(new_photo);
        savePhotos();
    }

    /**
     * Serializes list of photos
     * @return True if successful, false otherwise
     */
    public boolean savePhotos() {
        try{
            String username = Admin.users.get(Admin.user_id).getUser();
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + username + "-" + getTitle() + storeFile));
            ArrayList<Photo> copy = new ArrayList<>(photos);
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
    public boolean loadPhotos(){
        try{
            String username = Admin.users.get(Admin.user_id).getUser();
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + username + "-" + getTitle() +  storeFile));
            ArrayList<Photo> read = (ArrayList<Photo>)(ois.readObject());
            photos = FXCollections.observableArrayList(read);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Deletes photo from list photos
     * @param index Index to delete
     */
    public void deletePhoto(int index){
        photos.remove(index);
        savePhotos();
    }

}
