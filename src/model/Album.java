package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;

/**
 * This class creates an album and defines its functionality
 * @author Benjamin Ker (bk375)
 * @author Jake Karasik (jak451)
 */
@SuppressWarnings({"unchecked", "Duplicates"})
public class Album implements Serializable {

    /**
	 * ID used for tracking serial version
	 */
	private static final long serialVersionUID = 1L;
	
	// Metadata
    private String album_title;
    public final String storeDir = "data/user";
    public final String storeFile = ".ser";

    /*
     * List of photos contained by album
     * Transient in order to prevent serialization by User
     */
    public transient ObservableList<Photo> photos;

    /**
     * Creates our album and assigns it a title, owner, and list of photos
     * @param title Title of album.
     */
    public Album(String title){
        this.album_title = title;
        this.photos = FXCollections.observableArrayList();
    }

    /**
     * Gets title of album
     * @return Returns album title
     */
    public String getTitle(){ return album_title; }

    /**
     * Sets title of album
     * @param new_title New title of album
     */
    public void setTitle(String new_title){ album_title = new_title; }

    /**
     * Adds photo object to list photos
     * @param photo_file File of our photo to add
     */
    public void addPhoto(File photo_file){
        // Create a Photo object with our File, add it to the list and save
        Photo new_photo = new Photo(photo_file);
        photos.add(new_photo);
        savePhotos();
    }
    
    /**
     * Adds photo object to list photos
     * @param p Photo to add
     */
    public void addPhoto(Photo p) {
    	photos.add(p);
        savePhotos();
    }

    /**
     * Renames album given a new title
     * @param new_title String to replace album title with
     * @return True if successful, false otherwise
     */
    public boolean renameAlbum(String new_title){
        try{
            // Get username of active user
            String username = Admin.users.get(Admin.user_id).getUser();

            // Create old file, new file and rename
            File of = new File( storeDir + File.separator + username + "-" + getTitle() + storeFile);
            File nf = new File(storeDir + File.separator + username + "-" + new_title + storeFile);
            of.renameTo(nf);

            // Set new title of album
            setTitle(new_title);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Serializes list of photos
     * @return True if successful, false otherwise
     */
    public boolean savePhotos() {
        try{
            // Get username of active user
            String username = Admin.users.get(Admin.user_id).getUser();

            // Copy ObservableList to ArrayList in order to bypass transient and write
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
     * Deserializes list of photos
     * @return True if successful, false otherwise
     */
    public boolean loadPhotos(){
        try{
            // Get username of active user
            String username = Admin.users.get(Admin.user_id).getUser();

            // Read album and convert it to an ObservableList
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + username + "-" + getTitle() +  storeFile));
            ArrayList<Photo> read = (ArrayList<Photo>)(ois.readObject());
            photos = FXCollections.observableArrayList(read);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Deletes photo from list photos and saves
     * @param index Index to delete
     */
    public void deletePhoto(int index){
        photos.remove(index);
        savePhotos();
    }
    
    @Override
    public String toString() {
		return album_title;
    	
    }
}
