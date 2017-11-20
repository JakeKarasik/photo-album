package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class creates a photo and defines functionality
 * @author Benjamin Ker (bk375)
 */
public class Photo implements Serializable {

    // Metadata
    private String path;
    private String caption;
    private ArrayList<Tags> photo_tags;
    private Calendar last_modified;


    public String getPath(){
        return path;
    }


    public String getName() {
        String temp[] = path.split("/");
        return temp[temp.length-1];
    }

    /**
     * Creates Photo object from given file
     * @param file File passed in from file chooser
     */
    public Photo(File file){
        this.path = file.getPath();
        //this.path = file.getAbsolutePath();
        this.caption = "\0";
        this.photo_tags = new ArrayList<>();
        this.last_modified = Calendar.getInstance();
        this.last_modified.setTimeInMillis(file.lastModified());
        this.last_modified.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Constructor for stock album and debugging
     * @param path Relative path from src
     */
    public Photo(String path){
        this.path = path;
        this.caption = "\0";
        this.photo_tags = new ArrayList<>();
        this.last_modified = Calendar.getInstance();
        File file = new File(path);
        this.last_modified.setTimeInMillis(file.lastModified());
        this.last_modified.set(Calendar.MILLISECOND, 0);
    }
}
