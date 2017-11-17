package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates an album and defines its functionality
 * @author Benjamin Ker (bk375)
 */
public class Album  {

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
     */
    public void addPhoto(String path){
        URL check = getClass().getResource("../" + path);

        if(check == null){
            System.out.println("Invalid path");
        }else{
            Photo new_photo = new Photo(path);
            photos.add(new_photo);
            System.out.println("Photo success");
        }
    }

    /**
     * Deletes photo from list photos
     * @param index Index to delete
     */
    public void deletePhoto(int index){

    }

}
