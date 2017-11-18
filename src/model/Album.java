package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.Serializable;
import java.net.URL;

/**
 * This class creates an album and defines its functionality
 * @author Benjamin Ker (bk375)
 */
public class Album implements Serializable {

    private String album_title;
    private User owner;

    ObservableList<Photo> photos = FXCollections.observableArrayList();

    /**
     * Creates our album and assigns it a title and owner
     * @param title Title of album.
     * @param user Initial owner of album;
     */
    public Album(String title, User user){
        this.album_title = title;
        owner = user;
    }

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
     * @param path Path of our photo to add
     * @return False if path is invalid, True otherwise
     */
    public boolean addPhoto(String path){
        URL check = null;

        if(check != null){
            System.out.println("Invalid path given");
            return false;
        }
        Photo new_photo = new Photo(path);
        photos.add(new_photo);
        System.out.println("Photo import succeeded: " + path);
        return true;
    }

    public boolean addPhoto(File file){
        Photo new_photo = new Photo(file);
        photos.add(new_photo);
        System.out.println("Photo import succeeded: " + file.getAbsolutePath());
        return true;
    }

    /**
     * Deletes photo from list photos
     * @param index Index to delete
     */
    public void deletePhoto(int index){

    }

}
