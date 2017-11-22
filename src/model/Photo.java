package model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class creates a photo and defines functionality
 * @author Benjamin Ker (bk375)
 * @author Jake Karasik (jak451)
 */
public class Photo implements Serializable {

	
	/**
	 * ID used for tracking serial version
	 */
	private static final long serialVersionUID = 1L;
	
    // Metadata
    private String path;
    private String caption;
    private ArrayList<Tag> photo_tags;
    private Calendar last_modified;


    /**
     * Gets the absolute path of photo
     * @return String file path
     */
    public String getPath(){
        return path;
    }

    /**
     * Gets the name of our photo
     * @return String photo name
     */
    public String getName() {
        File temp = new File(path);
        return temp.getName();
    }

    /**
     * Gets caption of our photo
     * @return String photo caption
     */
    public String getCaption(){ return caption; }

    /**
     * Gets date of our photo in a readable format
     * @return String date of last modification
     */
    public String getDate(){return last_modified.getTime().toString(); }
    
    
    /**
     * Gets date of our photo as calendar object
     * @return Date last modified
     */
    public Calendar getLastModified() {return last_modified; }

    /**
     * Sets the caption of our photo
     * @param new_caption String to set photo caption to
     */
    public void setCaption(String new_caption) { caption = new_caption; }

    /**
     * Creates Photo object from given file
     * @param file File passed in from file chooser
     */
    public Photo(File file){
        this.path = file.getAbsolutePath();
        this.caption = "\0";
        this.photo_tags = new ArrayList<Tag>();
        this.last_modified = Calendar.getInstance();
        this.last_modified.setTimeInMillis(file.lastModified());
        this.last_modified.set(Calendar.MILLISECOND, 0);
    }
    
    /**
     * Adds given tag to photo's tags if doesn't already exist.
     * @param t Tag to add
     */
    public void addTag(Tag t) {
    	if (!photo_tags.contains(t)) {
    		photo_tags.add(t);
    	}
    }
    
    /**
     * Deletes given tag from photo's tags
     * @param t Tag to delete
     */
    public void removeTag(Tag t) {
    	if (photo_tags.contains(t)) {
    		photo_tags.remove(t);
    	}
    }
    
    /**
     * Gets all tags for photo
     * @return t All tags for photo
     */
    public ArrayList<Tag> getTags() {
    	return photo_tags;
    }
    
    @Override
    public boolean equals(Object o) {
    	return o != null && (o instanceof Photo) && ((Photo)o).getPath().equals(this.getPath());
    }
}
