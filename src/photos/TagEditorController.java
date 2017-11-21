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

	@FXML
	private TextField new_tag_value, new_tag_name;
	
	@FXML
	private ListView<Tag> current_tags;
	
	@FXML
	private Button close;
	
	@FXML
    public void initialize() {
		current_tags.setItems(FXCollections.observableList(GeneralController.photo.getTags()));
    }
	
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
			//Save data
			album.savePhotos();
			
			//Clear form fields
			new_tag_name.setText("");
			new_tag_value.setText("");
		}
		
	}
	
	public void deleteTag() {
		Tag selected_tag = (Tag)current_tags.getSelectionModel().getSelectedItem();
		Album album = GeneralController.album;
		
		//If no item selected, do nothing
		if (selected_tag == null) {
			return;
		}
		
		//Remove
		current_tags.getItems().remove(current_tags.getSelectionModel().getSelectedIndex());
		//Save data
		album.savePhotos();
	}
	
	public void close() {
		Stage stage = (Stage)close.getScene().getWindow();
		stage.close();
	}
}
