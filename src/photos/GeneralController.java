package photos;

import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import model.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class controls the photo library
 * @author Benjamin Ker (bk375)
 * @author Jake Karasik (jak451)
 */
public class GeneralController implements Initializable {

    /**
     * Current user of photo library
     */
    static User current_user;

    /**
     * Album being accessed while viewing photos
     */
    static Album album = null;

    /**
     * Label of selected album while viewing albums
     */
    Label active_album = null;

    /**
     * Label of selected photo while viewing photos
     */
    Label active_photo = null;

    /**
     * Flag marked as true if move was called on a photo
     */
    static boolean moved = false;

    /**
     * Selected photo that can be modified by other controllers
     */
    static Photo photo;

    /**
     * Container for the photo library
     */
    @FXML
    private AnchorPane fx_anchor;

    /**
     * Displays a photo when it is selected
     */
    @FXML
    private ImageView fx_imageviewer;

    /**
     * Allows user to interact with photo library e.g. create, delete, search, etc.
     */
    @FXML
    private Button fx_create, fx_rename, fx_delete_album, fx_search, fx_save_search, fx_back, fx_logout, fx_prev,
            fx_next, fx_edit_caption, fx_delete_photo, fx_move_copy, fx_edit_tags, fx_favorite;

    /**
     * Displays metadata of a photo when it is selected
     */
    @FXML
    private TextField fx_name, fx_caption, fx_date;

    /**
     * Displays tags of a photo when it is selected
     */
    @FXML
    private TextArea fx_tags;

    /**
     * Displays user's albums by default, loads an album's photos when it is opened
     */
    @FXML
    private TilePane fx_tilepane;

    /*
     * ALBUM MANAGEMENT METHODS
     * renameAlbum()
     * addNewAlbum()
     * addNewAlbumData()
     * deleteAlbum()
     * mouseHandler()
     * displayAlbum()
     * showSearchResults()
     * saveSearch()
     * invalidAlbumTitle()
     */

    /**
     * Renames a selected Album via TextInputDialog
     */
    public void renameAlbum(){
        // Create a dialog and pass input into addNewAlbumData
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Enter new name of selected album");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> addNewAlbumData(title));
    }

    /**
     * Adds a new album via TextInputDialog
     */
    private void addNewAlbum(){
        // If there is a selected album, deselect it
        if(active_album != null){
            active_album.setStyle("-fx-border-color:transparent");
            active_album = null;
            fx_rename.setDisable(true);
            fx_delete_album.setDisable(true);
        }

        // Create a dialog and pass input into addNewAlbumData
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Enter name of new album");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> addNewAlbumData(title));
    }

    /**
     * Helper method that saves album data and adds it to the TilePane
     * @param title Title of new album
     */
    private void addNewAlbumData(String title){
        // Check that the input does not conflict with our albums and is not null
        if(invalidAlbumTitle(title)){ return; }

        // If there is an album selected, this means we are renaming an existing album
        if(active_album != null){
            Album existing_album = current_user.albums.get(fx_tilepane.getChildren().indexOf(active_album)-1);
            existing_album.loadPhotos();

            Photo oldest = null;
            Photo newest = null;

            for(Photo p : existing_album.photos){
                if(oldest == null){
                    oldest = p;
                    newest = p;
                }
                if(p.isOlder(oldest)){
                    oldest = p;
                }else if(p.isNewer(newest)){
                    newest = p;
                }
            }
            existing_album.renameAlbum(title);
            if(oldest == null){
                active_album.setText(title + " (" + existing_album.photos.size() + ")\n" + " ");
            }else{
                active_album.setText(title + " (" + existing_album.photos.size() + ")\n" + oldest.getShortDate() +
                        " to " + newest.getShortDate());
            }
            current_user.saveUser();
            return;
        }

        // Otherwise, create a new album and add to list and TilePane
        Album new_album = new Album(title);
        current_user.addAlbum(new_album);
        Label add_text = new Label(title + " (0)\n" + " ");
        File resource_dir = new File(System.getProperty("user.dir") + "/data/resources/folder.png");
        addToTilePane(add_text, resource_dir.toURI().toString());
        add_text.setOnMouseClicked(e -> mouseHandler(e, add_text));
    }

    /**
     * Deletes the selected album
     */
    public void deleteAlbum(){
        // Get index of our album in the TilePane and remove it
        int index = fx_tilepane.getChildren().indexOf(active_album);
        fx_tilepane.getChildren().remove(index);

        // Delete album from User and save
        current_user.deleteAlbum(index-1);
        current_user.saveUser();

        // Set buttons
        fx_delete_album.setDisable(true);
        fx_rename.setDisable(true);
    }

    /**
     * Handles clicking an album. Single click selects an album; double click opens it.
     * @param e MouseEvent to check for how many clicks
     * @param label Label that we are working on
     */
    private void mouseHandler(MouseEvent e, Label label){

        // On double-click, enter the album
        if(e.getClickCount() == 2){

            // Clear TilePane and begin loading photo thumbnails
            album = current_user.albums.get(fx_tilepane.getChildren().indexOf(label)-1);
            album.loadPhotos();
            fx_tilepane.getChildren().clear();

            // Create "Add New" label and add it to the TilePane
            Label add_text = new Label("Add New Photo");
            File resource_dir = new File(System.getProperty("user.dir") + "/data/resources/add.png");
            addToTilePane(add_text, resource_dir.toURI().toString());
            add_text.setOnMouseClicked(f -> addNewPhoto());

            // For each photo in album, add it to the TilePane and set action on click
            displayAlbum();

            // Set buttons
            fx_back.setDisable(false);
            fx_delete_album.setDisable(true);
            fx_rename.setDisable(true);
        }else{
            // Deselect previously selected node if needed, mark clicked album as selected
            if(active_album != null){
                active_album.setStyle("-fx-border-color:transparent");
            }
            active_album = label;
            active_album.setStyle("-fx-border-color: black");

            // Set buttons
            fx_rename.setDisable(false);
            fx_delete_album.setDisable(false);
        }
    }

    /**
     * Displays the currently active album in the tilepane
     */
    private void displayAlbum(){
        for(Photo j : album.photos){
            File temp = new File(j.getPath());
            Label thumb = new Label(j.getCaption() + (j.isFavorite() ? " *" : ""));
            addToTilePane(thumb, temp.toURI().toString());
            Image img = new Image(temp.toURI().toString());
            thumb.setOnMouseClicked(f -> setImageviewer(thumb, img));
        }
    }

    /**
     * Loads search results into main display
     */
    public void showSearchResults() {
    	// Clear TilePane and begin loading photo thumbnails
        fx_tilepane.getChildren().clear();

        // Create "Search Results" label and add it to the TilePane
        Label add_text = new Label("Search Results");
        File resource_dir = new File(System.getProperty("user.dir") + "/data/resources/search_results.png");
        addToTilePane(add_text, resource_dir.toURI().toString());

        // For each photo in album, add it to the TilePane and set action on click
        displayAlbum();

        // Set buttons
        fx_back.setDisable(false);
        fx_delete_album.setDisable(true);
        fx_rename.setDisable(true);
        fx_save_search.setDisable(false);
    }
    
    /**
     * Called when save search button clicked. Saves result as new album
     */
    public void saveSearch() {
    	TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Search Results As Album");
        dialog.setHeaderText("What would you like to name the album?");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String album_name = result.get();

            // Check that the input does not conflict with our albums and is not null
            if(invalidAlbumTitle(album_name)){ return; }

            album.setTitle(album_name);
            current_user.addAlbum(album);
            fx_save_search.setDisable(true);
        }
    }

    /**
     * Checks if album title is a valid input
     * @param album_name Title given by user input
     * @return True if invalid, false otherwise
     */
    private boolean invalidAlbumTitle(String album_name) {
        for (Album a : current_user.albums) {
            if (album_name.equals(a.getTitle()) || album_name.equals("")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicate or empty title was entered");
                alert.setHeaderText("Invalid Input");
                alert.showAndWait();
                return true;
            }
        }
        return false;
    }


    /*
     * PHOTO MANAGEMENT METHODS
     * setImageviewer()
     * addNewPhoto()
     * deletePhoto()
     * nextPhoto()
     * prevPhoto()
     * editCaption()
     * editTags()
     * search()
     * moveCopy()
     * moveCopyHelper()
     */

    /**
     * Helper method for MouseEvents
     * @param thumb Label to get photo metadata
     * @param img File to set ImageViewer to
     */
    private void setImageviewer(Label thumb, Image img){

        // Get Photo associated with with selected label in TilePane
        Photo selected = album.photos.get(fx_tilepane.getChildren().indexOf(thumb)-1);

        // Set photo metadata fields -->
        fx_caption.setText(selected.getCaption());
        fx_name.setText(selected.getName());
        fx_date.setText(selected.getDate());
        fx_imageviewer.setImage(img);
        fx_tags.setText(selected.getTags().toString());
        fx_tags.setWrapText(true);
        fx_favorite.setText(selected.isFavorite() ? "unfavorite" : "favorite");

        // Set label as active photo
        active_photo = thumb;

        // Set buttons
        fx_delete_photo.setDisable(false);
        fx_edit_caption.setDisable(false);
        fx_edit_tags.setDisable(false);
        fx_prev.setDisable(false);
        fx_next.setDisable(false);
        fx_move_copy.setDisable(false);
        fx_favorite.setDisable(false);
    }

    /**
     * Adds a new photo via FileChooser
     */
    private void addNewPhoto(){
        // Open up a FileChooser to get path
        Stage fc_stage = (Stage)fx_anchor.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image");
        File file = fc.showOpenDialog(fc_stage);

        // If a file was selected, add it
        if(file != null){
            Photo new_photo = new Photo(file);
            // If album does not contain our photo
            if(!album.photos.contains(new_photo)){
                boolean found = false;
                // Check each album
                for (Album a : current_user.albums) {
                    if(a != null){
                        // Load photos and check each photo
                        a.loadPhotos();
                        for (Photo p : a.photos) {
                            if(p != null){
                                // If a photo matches, copy its caption and tags
                                if (p.equals(new_photo)) {
                                    new_photo.setCaption(p.getCaption());
                                    for (Tag t : p.getTags()) {
                                        new_photo.addTag(t);
                                    }
                                    found = true;
                                    break;
                                }
                            }
                        }
                        // If a photo match was found, no need to continue, break
                        if(found){ break; }
                    }
                }
                // Add our photo to album, tilepane
                album.addPhoto(new_photo);
                Label add_text = new Label(new_photo.getCaption());
                addToTilePane(add_text, file.toURI().toString());
                Image img = new Image(file.toURI().toString());
                add_text.setOnMouseClicked(f -> setImageviewer(add_text, img));
            }else{
                // If album already contains photo, error
                Alert alert = new Alert(Alert.AlertType.ERROR, "Photo already inside current album");
                alert.setHeaderText("Invalid Photo Selected");
                alert.showAndWait();
                return;
            }
        }
    }

    /**
     * Delete selected photo
     */
    public void deletePhoto(){
        // Get index of photo in TilePane and remove it
        int index = fx_tilepane.getChildren().indexOf(active_photo);
        fx_tilepane.getChildren().remove(index);

        // Delete photo from list
        album.deletePhoto(index-1);

        // Set active photo back to null
        active_photo = null;

        // Set components to empty
        fx_imageviewer.setImage(null);
        fx_caption.setText("\0");
        fx_date.setText("\0");
        fx_name.setText("\0");
        fx_tags.setText("\0");

        // Set buttons
        fx_edit_caption.setDisable(true);
        fx_delete_photo.setDisable(true);
        fx_prev.setDisable(true);
        fx_next.setDisable(true);
        fx_move_copy.setDisable(true);
        fx_edit_tags.setDisable(true);
        fx_favorite.setDisable(true);
    }

    /**
     * Display next photo
     */
    public void nextPhoto(){
        // Get index of currently selected photo
        int index = fx_tilepane.getChildren().indexOf(active_photo);

        // Get size of TilePane
        int max = fx_tilepane.getChildren().size();

        // If we are at the end of the TilePane, go back to start, otherwise go to next
        if(index == max-1){ index = 1; } else { index += 1; }

        // Display the image and its metadata
        File temp = new File(album.photos.get(index-1).getPath());
        Image img = new Image(temp.toURI().toString());
        Label next_image = (Label)(fx_tilepane.getChildren().get(index));
        setImageviewer(next_image, img);
    }

    /**
     * Display previous photo
     */
    public void prevPhoto(){
        // See nextPhoto()
        int index = fx_tilepane.getChildren().indexOf(active_photo);
        int max = fx_tilepane.getChildren().size();
        if(index == 1){ index = max-1; } else { index -= 1; }
        File temp = new File(album.photos.get(index-1).getPath());
        Image img = new Image(temp.toURI().toString());
        Label next_image = (Label)(fx_tilepane.getChildren().get(index));
        setImageviewer(next_image, img);
    }


    /**
     * Edit caption of selected photo
     */
    public void editCaption(){
        // Set components
        fx_edit_caption.setDisable(false);
        fx_caption.setDisable(false);

        // If it shows "save edit", implies edit caption was pressed earlier
        if(fx_edit_caption.getText().equals("save edit")){

            // Get index, set caption of photo and its label and save
            int index = fx_tilepane.getChildren().indexOf(active_photo);
            album.photos.get(index-1).setCaption(fx_caption.getText());
            Label cur_label = (Label)(fx_tilepane.getChildren().get(index));
            cur_label.setText(fx_caption.getText());
            album.savePhotos();

            // Check for identical photos in other albums and sync their captions
            Photo cur_photo = album.photos.get(index-1);
            for (Album a : current_user.albums) {
                if(a != null){
                    a.loadPhotos();
                    for (Photo p : a.photos) {
                        if(p != null){
                            if (p.equals(cur_photo)) {
                                p.setCaption(fx_caption.getText());
                            }
                        }
                    }
                    a.savePhotos();
                }
            }

            // Reset components
            fx_edit_caption.setText("edit caption");
            fx_caption.setDisable(true);
            return;
        }
        // Otherwise, change component to save edit and wait for it to be pressed again
        fx_edit_caption.setText("save edit");
    }
    
    /**
     * Mark/unmark photo as favorite
     */
    public void setIsFavorite() {
    	 Photo cur_photo = album.photos.get(fx_tilepane.getChildren().indexOf(active_photo)-1);
    	 boolean toSet = !cur_photo.isFavorite();
    	 
    	 //Update status
    	 cur_photo.setIsFavorite(toSet);
     	 fx_favorite.setText(toSet ? "unfavorite" : "favorite");
     	 active_photo.setText(cur_photo.getCaption() + (toSet ? " *" : "")); 
     	 album.savePhotos();
     	 
     	 //Update status for all same image
         for (Album a : current_user.albums) {
             a.loadPhotos();
             for (Photo p : a.photos) {
                 if (p.equals(cur_photo)) {
                     p.setIsFavorite(toSet);
                 }
             }
             a.savePhotos();
         }
    }
    
    /**
     * Launch edit tags window
     */
    public void editTags() {
    	//Get index of current photo
    	int index = fx_tilepane.getChildren().indexOf(active_photo) - 1;
    	photo = album.photos.get(index);

    	// Create the TagEditor stage
    	Stage edit_tags_stage = Photos.newStage((Stage)fx_anchor.getScene().getWindow(), "TagEditor.fxml", "Edit Tags");

    	// Update tags when editor window closes
    	edit_tags_stage.setOnHidden(e -> fx_tags.setText(photo.getTags().toString()));
    }
    
    /**
     * Launch window to search for images
     */
    public void search() {
        // Create the Search stage
    	Stage search_stage = Photos.newStage((Stage)fx_anchor.getScene().getWindow(), "Search.fxml", "Search");

    	// When closed, if an temporary album from search was created, display it
    	search_stage.setOnHidden(e -> {
    		if (album != null && album.getTitle().equals("~search_results")) {
    			showSearchResults();

                // Set components to empty
                fx_imageviewer.setImage(null);
                fx_caption.setText("\0");
                fx_date.setText("\0");
                fx_name.setText("\0");
                fx_tags.setText("\0");

                // Set buttons
                fx_edit_caption.setDisable(true);
                fx_delete_photo.setDisable(true);
                fx_prev.setDisable(true);
                fx_next.setDisable(true);
                fx_move_copy.setDisable(true);
                fx_edit_tags.setDisable(true);
                fx_favorite.setDisable(true);
    		}
    	});
    }
    
    /**
     * Launch window to move or copy image
     */
    public void moveCopy() {
    	//Get index of current photo
    	int index = fx_tilepane.getChildren().indexOf(active_photo) - 1;
    	photo = album.photos.get(index);

    	// Create MoveCopy stage
    	Stage move_copy_stage = Photos.newStage((Stage)fx_anchor.getScene().getWindow(), "MoveCopy.fxml", "Move or Copy");

    	// When closed, call helper function
    	move_copy_stage.setOnHidden(e -> moveCopyHelper());
    }

    /**
     * Removes photo from tilepane if it was moved to another photo
     */
    private void moveCopyHelper(){
        if(moved == true){
            int index = fx_tilepane.getChildren().indexOf(active_photo);
            fx_tilepane.getChildren().remove(index);
            moved = false;
        }
    }

    /* GENERAL METHODS
     * getUser()
     * addtToTilePane()
     * back()
     * logout()
     * initialize(0
     */

    /**
     * Gets active user to work on
     */
    private void getUser() { current_user = Admin.users.get(Admin.user_id); }

    /**
     * Adds an album/image thumbnail to the TilePane
     * @param label Label to add
     * @param path Path of image to display
     */
    private void addToTilePane(Label label, String path){
        // Get ImageView to add

        Image img = new Image(path, 120, 120, false, false);
        ImageView add_imv = new ImageView(img);

        // Set label characteristics and add to TilePane
        label.setGraphic(add_imv);
        label.setContentDisplay(ContentDisplay.TOP);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPrefWidth(120.0);
        label.setWrapText(true);
        fx_tilepane.getChildren().add(label);
    }

    /**
     * Goes back from displaying photos to albums
     */
    public void back(){
        // Get stage and switch
        Stage stage = (Stage)fx_anchor.getScene().getWindow();
        Photos.switchStage(stage, "General.fxml", "Photo Library // Current User: " +
                Admin.users.get(Admin.user_id).getUser());
        album = null;
    }

    /**
     * Logs out the user
     */
    public void logout(){
        // Set active user to -1 and switch stage back to Login.fxml
        Admin.user_id = -1;
        Stage stage = (Stage)fx_anchor.getScene().getWindow();
        Photos.switchStage(stage, "Login.fxml", "Enter Authentication");
    }

    /**
     * Setup for photo library
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        // Set up TilePane padding
        fx_tilepane.setPadding(new Insets(10,10,10,10));

        // Grab the active User and load its data
        getUser();
        current_user.loadUser();

        // Create "Add New" label and add it to the TilePane
        Label add_text = new Label("Add New Album");
        File resource_dir = new File(System.getProperty("user.dir") + "/data/resources/add.png");
        addToTilePane(add_text, resource_dir.toURI().toString());
        add_text.setOnMouseClicked(e -> addNewAlbum());

        // Display the albums of User
        for(Album a : current_user.albums){
            // Display albums as labels with icon and text

            a.loadPhotos();
            int size = a.photos.size();

            Photo oldest = null;
            Photo newest = null;

            for(Photo p : a.photos){
                if(oldest == null){
                    oldest = p;
                    newest = p;
                }
                if(p.isOlder(oldest)){
                    oldest = p;
                }else if(p.isNewer(newest)){
                    newest = p;
                }
            }
            File folder_resource_dir = new File(System.getProperty("user.dir") + "/data/resources/folder.png");
            Label icon;
            if (oldest == null){
                icon = new Label(a.getTitle() + " (" + size + ")\n" + " ");
            }else{
                icon = new Label(a.getTitle() + " (" + size + ")\n" + oldest.getShortDate() + " to " + newest.getShortDate());
            }

            addToTilePane(icon, folder_resource_dir.toURI().toString());
            // Set behavior when clicked
            icon.setOnMouseClicked(e -> mouseHandler(e, icon));
        }
    }
}
