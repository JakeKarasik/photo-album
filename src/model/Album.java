package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates an album and defines its functionality
 * @author Benjamin Ker (bk375)
 */
public class Album {

    private String album_title;
    private List<User> owners;

    ObservableList<Photo> photos = FXCollections.observableArrayList();

    /**
     * Creates our album and assigns it a title and owner
     * @param title Title of album.
     * @param owner Initial owner of album;
     */
    public Album(String title, User owner){
        this.album_title = title;
        this.owners = new ArrayList<>();
        owners.add(owner);
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
     */
    public void addPhoto(String path){

    }

    /**
     * Deletes photo from list photos
     * @param index Index to delete
     */
    public void deletePhoto(int index){

    }

}
