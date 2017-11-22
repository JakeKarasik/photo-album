package photos;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.*;

/**
 * This class controls the tag editor
 * @author Jake Karasik (jak451)
 */
public class TagEditorController {

	/**
	 * New tag value and name pair to be added
	 */
	@FXML
	private TextField new_tag_value, new_tag_name;

    /**
     * List of current tags of selected photo
     */
	@FXML
	private ListView<Tag> current_tags;

    /**
     * Button to go back to General
     */
	@FXML
	private Button close;

    /**
     * Setup for photo library
     * {@inheritDoc}
     */
    public void initialize() {
		current_tags.setItems(FXCollections.observableList(GeneralController.photo.getTags()));
    }

    /**
     * Adds tag based on input
     */
	public void addTag() {
		Photo photo = GeneralController.photo;
		Album album = GeneralController.album;
		
		ArrayList<Tag> tags = photo.getTags();
		
		//Make sure tag not empty
		if (new_tag_name.getText().isEmpty() || new_tag_value.getText().isEmpty()) {
			return;
		}
		
		Tag new_tag = new Tag(new_tag_name.getText(), new_tag_value.getText());
		
		if (!tags.contains(new_tag)) {
			//Add to tags
			current_tags.getItems().add(new_tag);
			
			//Update all instances of this photo
			for (Album a : GeneralController.current_user.albums) {
				a.loadPhotos();
				for (Photo p : a.photos) {
					if (p.equals(photo)) {
						p.addTag(new_tag);
					}
				}
				a.savePhotos();
			}
			
			//Save data
			album.savePhotos();
			
			//Clear form fields
			new_tag_name.setText("");
			new_tag_value.setText("");
		}
		
	}

    /**
     * Deletes a selected tag in the ListView
     */
	public void deleteTag() {
		Tag selected_tag = (Tag)current_tags.getSelectionModel().getSelectedItem();
		Album album = GeneralController.album;
		Photo photo = GeneralController.photo;
		
		//If no item selected, do nothing
		if (selected_tag == null) {
			return;
		}
		
		//Remove
		current_tags.getItems().remove(current_tags.getSelectionModel().getSelectedIndex());
		
		//Update all instances of this photo
		for (Album a : GeneralController.current_user.albums) {
			a.loadPhotos();
			for (Photo p : a.photos) {
				if (p.equals(photo)) {
					p.removeTag(selected_tag);
				}
			}
			a.savePhotos();
		}
		
		//Save data
		album.savePhotos();
	}

    /**
     * Closes TagEditor
     */
	public void close() {
		Stage stage = (Stage)close.getScene().getWindow();
		stage.close();
	}
}
