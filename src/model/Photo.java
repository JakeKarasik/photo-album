package model;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
	/**
	 * File path of photo
	 */
    private String path;
    /**
	 * Editable caption of photo
	 */
    private String caption;
    /**
	 * List of all tags on photo
	 */
    private ArrayList<Tag> photo_tags;
    /**
	 * Date last modified
	 */
    private Calendar last_modified;
    /**
	 * Flag if is favorited image
	 */
    private boolean favorite = false;


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
     * Gets date of our photo in a shorter readable format
     * @return String date of last modification in a shorter format
     */
    public String getShortDate() {
        SimpleDateFormat short_format = new SimpleDateFormat("MM/dd/yy");
        return short_format.format(last_modified.getTime());
    }

    /**
     * Returns true if photo is older than arg
     * @param p Photo to compare to
     * @return
     */
    public boolean isOlder(Photo p){
        return (last_modified.compareTo(p.last_modified) > 0);
    }

    /**
     * Returns true if photo is newer than arg
     * @param p Photo to compare to
     * @return
     */
    public boolean isNewer(Photo p){
        return (last_modified.compareTo(p.last_modified) < 0);
    }


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
    
    /**
     * Create a copy of a Photo instance given a photo
     * @return Photo instance with same data as original p
     */
	public Photo getCopy() {
	    //Create copy
		Photo copy = new Photo(new File(this.getPath()));
		//Copy tags
		for (Tag t : this.getTags()) {
			copy.addTag(t);
		}
		//Copy caption
		copy.setCaption(this.getCaption());
		
		return copy;
	}
	
	/**
	 * Check if photo is marked as a favorite.
	 * @return true if is favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * Change isFavorite status.
	 * @param b new favorite status
	 */
	public void setIsFavorite(boolean b) {
		favorite = b;
	}

    /**
     * Compares Photo objects by their paths
     * @param o Photo object to compare to
     * @return True if they are equivalent, false otherwise
     */
    @Override
    public boolean equals(Object o) {
    	return o != null && (o instanceof Photo) && ((Photo)o).getPath().equals(this.getPath());
    }
}
