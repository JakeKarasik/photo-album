package photos;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.*;

/**
 * This class controls the move/copy window
 * @author Jake Karasik (jak451)
 */
public class MoveCopyController {

	/**
	 * Button to close the window
	 */
	/**
	 * Button to move the photo to selected album
	 */
	/**
	 * Button to copy the photo to selected album
	 */
	@FXML
	private Button close, move, copy;
	
	/**
	 * Photo to be moved/copied
	 */
	@FXML
	private TextField selected_image;
	
	/**
	 * List of destination albums
	 */
	@FXML
	private ChoiceBox<Album> choices;
	
	
	private int photo_index;
	
	/**
	 * Load destination choices and show name of photo being moved/copied
	 */
	@FXML
	public void initialize() {
		choices.setItems(GeneralController.current_user.albums);
		selected_image.setText(GeneralController.photo.getName());
		//get index of photo within album
		photo_index = GeneralController.album.photos.indexOf(GeneralController.photo);
	}
	
	/**
	 * Create a copy of photo and place it in the selected album
	 */
	public void copy() {
		Album dest_album = choices.getValue();
		
		if (dest_album == null) {
			return;
		}
		
		dest_album.addPhoto(GeneralController.photo);
	}
	
	/**
	 * Move photo to selected album and delete from old album
	 */
	public void move() {
		copy();
		GeneralController.album.deletePhoto(photo_index);
	}
	
	
	/**
	 * Close window
	 */
	public void close() {
		Stage stage = (Stage)close.getScene().getWindow();
		stage.close();
	}
}