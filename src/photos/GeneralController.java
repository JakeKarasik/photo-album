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

    // Current user
    static User current_user;

    // Album being accessed - viewing Photos
    static Album album = null;

    // Label of selected album - viewing Albums
    Label active_album = null;

    // Label of selected photo - viewing Photos
    Label active_photo = null;

    static boolean moved = false;
    
    static Photo photo;

    @FXML
    private AnchorPane fx_anchor;

    @FXML
    private ImageView fx_imageviewer;

    @FXML
    private Button fx_create, fx_rename, fx_delete_album, fx_search, fx_save_search, fx_back, fx_logout, fx_prev,
            fx_next, fx_edit_caption, fx_delete_photo, fx_move_copy, fx_edit_tags;

    @FXML
    private TextField fx_name, fx_caption, fx_date;

    @FXML
    private TextArea fx_tags;

    @FXML
    private TilePane fx_tilepane;

    // ALBUM MANAGEMENT METHODS //

    /**
     * Renames a selected Album via TextInputDialog
     */
    public void renameAlbum(){
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
        if(active_album != null){
            active_album.setStyle("-fx-border-color:transparent");
            active_album = null;
            fx_rename.setDisable(true);
            fx_delete_album.setDisable(true);
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Enter name of new album");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> addNewAlbumData(title));
    }

    /**
     * Helper method, saves album data and adds it to the TilePane
     * @param title Title of new album
     */
    private void addNewAlbumData(String title){
        // Get size of the list of albums owned by current user
        int size = current_user.albums.size();

        // Check that the input does not conflict with our albums and is not null
        for(int i = 0; i < size; i++){
            if(title.equals(current_user.albums.get(i).getTitle()) || title.equals("")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicate or empty title was entered");
                alert.setHeaderText("Invalid Input");
                alert.showAndWait();
                return;
            }
        }

        // If there is an album selected, this means we are renaming an existing album
        if(active_album != null){
            Album existing_album = current_user.albums.get(fx_tilepane.getChildren().indexOf(active_album)-1);
            existing_album.renameAlbum(title);
            active_album.setText(title);
            current_user.saveUser();
            return;
        }

        // Otherwise, create a new album and add to list and TilePane
        Album new_album = new Album(title);
        current_user.addAlbum(new_album);
        Label add_text = new Label(title);
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
            int size = album.photos.size();
            for(int j = 0; j < size; j++){
                File temp = new File(album.photos.get(j).getPath());
                //Label thumb = new Label(temp.getName());
                Label thumb = new Label(album.photos.get(j).getCaption());
                addToTilePane(thumb, temp.toURI().toString());
                Image img = new Image(temp.toURI().toString());
                thumb.setOnMouseClicked(f -> setImageviewer(thumb, img));
            }

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
     * Loads search results into main display
     */
    public void showSearchResults() {
    	// Clear TilePane and begin loading photo thumbnails
        fx_tilepane.getChildren().clear();

        // Create "Add New" label and add it to the TilePane
        Label add_text = new Label("Search Results");
        File resource_dir = new File(System.getProperty("user.dir") + "/data/resources/search_results.png");
        addToTilePane(add_text, resource_dir.toURI().toString());
        //add_text.setOnMouseClicked(f -> addNewPhoto());

        // For each photo in album, add it to the TilePane and set action on click
        int size = album.photos.size();
        for(int j = 0; j < size; j++){
            File temp = new File(album.photos.get(j).getPath());
            Label thumb = new Label(temp.getName());
            addToTilePane(thumb, temp.toURI().toString());
            Image img = new Image(temp.toURI().toString());
            thumb.setOnMouseClicked(f -> setImageviewer(thumb, img));
        }

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
            // Get size of the list of albums owned by current user
            int size = current_user.albums.size();

            // Check that the input does not conflict with our albums and is not null
            for(int i = 0; i < size; i++){
                if(album_name.equals(current_user.albums.get(i).getTitle()) || album_name.equals("")){
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicate or empty title was entered");
                    alert.setHeaderText("Invalid Input");
                    alert.showAndWait();
                    return;
                }
            }
            album.setTitle(album_name);
            current_user.addAlbum(album);
            fx_save_search.setDisable(true);
        }
    }
    

    // PHOTO MANAGEMENT METHODS //

    /**
     * Helper method for MouseEvents
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

        // Set label as active photo
        active_photo = thumb;

        // Set buttons
        fx_delete_photo.setDisable(false);
        fx_edit_caption.setDisable(false);
        fx_edit_tags.setDisable(false);
        fx_prev.setDisable(false);
        fx_next.setDisable(false);
        fx_move_copy.setDisable(false);
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
            if(!album.photos.contains(new_photo)){
                boolean found = false;
                for (Album a : current_user.albums) {
                    if(a != null){
                        a.loadPhotos();
                        for (Photo p : a.photos) {
                            if(p != null){
                                if (p.equals(new_photo)) {
                                    System.out.println(p.getCaption());
                                    new_photo.setCaption(p.getCaption());
                                    for (Tag t : p.getTags()) {
                                        new_photo.addTag(t);
                                    }
                                    break;
                                }
                            }
                        }
                        if(found){ break; }
                    }
                }
                album.addPhoto(new_photo);
                Label add_text = new Label(new_photo.getCaption());
                addToTilePane(add_text, file.toURI().toString());
                Image img = new Image(file.toURI().toString());
                add_text.setOnMouseClicked(f -> setImageviewer(add_text, img));
            }else{
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

            // Get index, set caption of photo and save
            int index = fx_tilepane.getChildren().indexOf(active_photo);
            //fx_tilepane.getChildren().get(index).setAccessibleText(fx_caption.getText());
            album.photos.get(index-1).setCaption(fx_caption.getText());
            Label cur_label = (Label)(fx_tilepane.getChildren().get(index));
            cur_label.setText(fx_caption.getText());

            Photo cur_photo = album.photos.get(index-1);


            album.savePhotos();

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
     * Launch edit tags window
     */
    public void editTags() {
    	//Get index of current photo
    	int index = fx_tilepane.getChildren().indexOf(active_photo) - 1;
    	photo = album.photos.get(index);
    	Stage edit_tags_stage = Photos.newStage((Stage)fx_anchor.getScene().getWindow(), "TagEditor.fxml", "Edit Tags");
    	//Update tags when editor window closes
    	edit_tags_stage.setOnHidden(e -> fx_tags.setText(photo.getTags().toString()));
    }
    
    /**
     * Launch window to search for images
     */
    public void search() {
    	Stage search_stage = Photos.newStage((Stage)fx_anchor.getScene().getWindow(), "Search.fxml", "Search");
    	search_stage.setOnHidden(e -> {
    		if (album != null && album.getTitle().equals("~search_results")) {
    			showSearchResults();
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
    	Stage move_copy_stage = Photos.newStage((Stage)fx_anchor.getScene().getWindow(), "MoveCopy.fxml", "Move or Copy");
    	move_copy_stage.setOnHidden(e -> moveCopyHelper());
    }

    public void moveCopyHelper(){
        if(moved == true){
            int index = fx_tilepane.getChildren().indexOf(active_photo);
            fx_tilepane.getChildren().remove(index);
            moved = false;
        }
    }

    // GENERAL METHODS //

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
        int size = current_user.albums.size();
        for(int i = 0; i < size; i++){

            // Display albums as labels with icon and text
            Label icon = new Label(current_user.albums.get(i).getTitle());
            File folder_resource_dir = new File(System.getProperty("user.dir") + "/data/resources/folder.png");
            addToTilePane(icon, folder_resource_dir.toURI().toString());

            // Set behavior when clicked
            icon.setOnMouseClicked(e -> mouseHandler(e, icon));
        }
    }
}
