package photos;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import model.*;
/**
 * This class controls the search window
 * @author Jake Karasik (jak451)
 */
public class SearchController {

    /**
     * Close and search
     */
	@FXML
	private Button close, regular_search, favorite_search;
    /**
     * Allows user to choose time period
     */
	@FXML
	private DatePicker from, to;

    /**
     * Tags to search on
     */
	@FXML
	private TextArea search_tags;
	
	 /**
     * Calls search based on given criteria
     */
	public void regularSearch() {
		search(false);
	}
	
	 /**
     * Calls search based on given criteria, only showing favorited photos
     */
	public void favoriteSearch() {
		search(true);
	}

    /**
     * Searches user's albums based on given  criteria
     * @param onlyFavorites Flag to see if only favorited photos should be searched
     */
	public void search(boolean onlyFavorites) {

		//Create temp album for storing search results
		Album temp = new Album("~search_results");
		
		//Track added to prevent duplicates
		ArrayList<Photo> added_photos = new ArrayList<Photo>();
		
		//By default search by tags and by date
		boolean search_by_tags = true, search_by_date = true;
		
		//Split tags by commas
		String[] tags = search_tags.getText().trim().split(",");
		
		//Create calendar instances for comparing date range
		String pattern = "yyyy-MM-dd";
		Calendar cfd = Calendar.getInstance();
		Calendar ctd = Calendar.getInstance();
		Calendar current_last_modified;
		
		//Attempt to format dates correctly
		try {
			Date from_d = new SimpleDateFormat(pattern).parse(from.getValue().toString());
			Date to_d = new SimpleDateFormat(pattern).parse(to.getValue().toString());
			cfd.setTime(from_d);
			ctd.setTime(to_d);
			cfd.set(Calendar.MILLISECOND,0);
			ctd.set(Calendar.MILLISECOND,0);
		} catch (Exception e) {
			search_by_date = false;
		}
		
		if (tags == null || (tags.length == 1 && tags[0].isEmpty())) {
			search_by_tags = false;
		}
		
		if (from == null || to == null) {
			search_by_date = false;
		}
		
		//If given photo has all tags
		boolean matches_all = true;
		
		for (Album a : GeneralController.current_user.albums) {
			a.loadPhotos();
			for (Photo p : a.photos) {
				matches_all = true;
				current_last_modified = p.getLastModified();
				current_last_modified.set(Calendar.MILLISECOND,0);
				//If search by date and not by tags, need to check if within range
				if (search_by_date && !search_by_tags && 
						current_last_modified.compareTo(ctd) <= 0 && 
						current_last_modified.compareTo(cfd) >= 0 &&
						!added_photos.contains(p) && //don't add duplicates
						((onlyFavorites && p.isFavorite()) || (!onlyFavorites))) { 
					temp.photos.add(p);
					temp.savePhotos();
					added_photos.add(p);
					continue;
				}
				
				//If search by tags... and possibly by date
				if (search_by_tags) {
					ArrayList<Tag> p_tags = p.getTags();
					//If search by date and tags, need to check if within range
					if (!added_photos.contains(p) && search_by_date && current_last_modified.compareTo(ctd) <= 0 && current_last_modified.compareTo(cfd) >= 0 && ((onlyFavorites && p.isFavorite()) || (!onlyFavorites))) {
						//Compare vs all tags to make sure all match 
						for (String test_tags : tags) {
							String[] test_tag = test_tags.split("=");
							if (!p_tags.contains(new Tag(test_tag[0].trim(), test_tag[1].trim()))) {
								matches_all = false;
								break;
							}
						}
						if (matches_all) {
							temp.photos.add(p);
							temp.savePhotos();
							added_photos.add(p);
						}
					} else if (!added_photos.contains(p) && !search_by_date && ((onlyFavorites && p.isFavorite()) || (!onlyFavorites))) {
						//Compare vs all tags to make sure all match 
						for (String test_tags : tags) {
							String[] test_tag = test_tags.split("=");
							if (!p_tags.contains(new Tag(test_tag[0].trim(), test_tag[1].trim()))) {
								matches_all = false;
								break;
							}
						}
						if (matches_all) {
							temp.photos.add(p);
							temp.savePhotos();
							added_photos.add(p);
						}
					}	
				}
			}
		}
		GeneralController.album = temp;
		close();
	}

    /**
     * Closes Search window
     */
	public void close() {
		Stage stage = (Stage)close.getScene().getWindow();
		stage.close();
	}
}