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
 */
public class GeneralController implements Initializable {

    // Current user
    User current_user;

    // Previously selected album/photo
    //int cur_index = -1;

    Label active_album = null;

    Label active_photo = null;

    Album album = null;

    @FXML
    private AnchorPane fx_anchor;

    @FXML
    private ScrollPane fx_scrollpane;

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

    public void renameAlbum(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Rename Album");
        dialog.setHeaderText("Enter new name of selected album");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(title -> addNewAlbumData(title));
    }
    /**
     * Adds a new album given input from TextInputDialog
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
     * Helper method. Saves album data and adds it to the TilePane
     * @param title Title of new album
     */
    private void addNewAlbumData(String title){
        int size = current_user.albums.size();
        for(int i = 0; i < size; i++){
            if(title.equals(current_user.albums.get(i).getTitle()) || title.equals("")){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicate or empty title was entered");
                alert.setHeaderText("Invalid Input");
                alert.showAndWait();
                return;
            }
        }
        if(active_album != null){
            Album existing_album = current_user.albums.get(fx_tilepane.getChildren().indexOf(active_album)-1);
            existing_album.renameAlbum(title);
            active_album.setText(title);
            current_user.saveUser();
            return;
        }
        Album new_album = new Album(title, current_user);
        current_user.addAlbum(new_album);
        Label add_text = new Label(title);
        addToTilePane(add_text,  "resources/folder.png");
        add_text.setOnMouseClicked(e -> mouseHandler(e, add_text));
    }

    /**
     * Handles clicking an album. Single click selects an album; double click opens it.
     * @param e MouseEvent to check for how many clicks
     * @param label Label that we are working on
     */
    private void mouseHandler(MouseEvent e, Label label){
        if(e.getClickCount() == 2){
            // Clear TilePane and begin loading photo thumbnails
            album = current_user.albums.get(fx_tilepane.getChildren().indexOf(label)-1);
            album.loadPhotos();
            fx_tilepane.getChildren().clear();

            // Create "Add New" label and add it to the TilePane
            Label add_text = new Label("Add New");
            addToTilePane(add_text, "resources/add.png");
            add_text.setOnMouseClicked(f -> addNewPhoto());

            int size = album.photos.size();

            for(int j = 0; j < size; j++){
                File temp = new File(album.photos.get(j).getPath());
                int index = j;

                Label thumb = new Label(temp.getName());
                addToTilePane(thumb, temp.toURI().toString());
                Image img = new Image(temp.toURI().toString());
                thumb.setOnMouseClicked(f -> setImageviewer(thumb, img));
            }

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
            fx_rename.setDisable(false);
            fx_delete_album.setDisable(false);
        }
    }

    public void deleteAlbum(){
        int index = fx_tilepane.getChildren().indexOf(active_album);
        fx_tilepane.getChildren().remove(index);
        current_user.deleteAlbum(index-1);
        current_user.saveUser();
        fx_delete_album.setDisable(true);
        fx_rename.setDisable(true);
    }

    public void deletePhoto(){
        int index = fx_tilepane.getChildren().indexOf(active_photo);
        fx_tilepane.getChildren().remove(index);
        album.photos.remove(index-1);
        album.savePhotos();
        fx_imageviewer.setImage(null);
        fx_caption.setText("\0");
        fx_date.setText("\0");
        fx_name.setText("\0");
        fx_tags.setText("\0");
        active_photo = null;
        fx_edit_caption.setDisable(true);
        fx_delete_photo.setDisable(true);
        fx_prev.setDisable(true);
        fx_next.setDisable(true);
    }


    public void nextPhoto(){
        int index = fx_tilepane.getChildren().indexOf(active_photo);
        int max = fx_tilepane.getChildren().size();
        if(index == max-1){ index = 1; } else { index += 1; }

        File temp = new File(album.photos.get(index-1).getPath());
        Image img = new Image(temp.toURI().toString());
        Label next_image = (Label)(fx_tilepane.getChildren().get(index));
        setImageviewer(next_image, img);
    }
    public void prevPhoto(){
        int index = fx_tilepane.getChildren().indexOf(active_photo);
        int max = fx_tilepane.getChildren().size();
        if(index == 1){ index = max-1; } else { index -= 1; }

        File temp = new File(album.photos.get(index-1).getPath());
        Image img = new Image(temp.toURI().toString());
        Label next_image = (Label)(fx_tilepane.getChildren().get(index));
        setImageviewer(next_image, img);
    }

    // PHOTO MANAGEMENT METHODS //

    /**
     * Helper method to mouseHandler
     * @param img File to set ImageViewer to
     */
    private void setImageviewer(Label thumb, Image img){
        Photo selected = album.photos.get(fx_tilepane.getChildren().indexOf(thumb)-1);
        fx_caption.setText(selected.getCaption());
        fx_name.setText(selected.getName());
        fx_date.setText(selected.getDate());
        fx_imageviewer.setImage(img);
        active_photo = thumb;
        fx_delete_photo.setDisable(false);
        fx_edit_caption.setDisable(false);
        fx_prev.setDisable(false);
        fx_next.setDisable(false);
    }

    /**
     * Adds a new album given input from TextInputDialog
     */
    private void addNewPhoto(){
        Stage fc_stage = (Stage)fx_anchor.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image");

        File file = fc.showOpenDialog(fc_stage);
        if(file != null){
            album.addPhoto(file);
            Label add_text = new Label(file.getName());
            addToTilePane(add_text, file.toURI().toString());
            Image img = new Image(file.toURI().toString());
            add_text.setOnMouseClicked(f -> setImageviewer(add_text, img));
        }
    }

    public void editCaption(){
        fx_edit_caption.setDisable(false);
        fx_caption.setDisable(false);
        if(fx_edit_caption.getText().equals("save edit")){
            int index = fx_tilepane.getChildren().indexOf(active_photo);
            album.photos.get(index-1).setCaption(fx_caption.getText());
            album.savePhotos();
            fx_edit_caption.setText("edit caption");
            fx_caption.setDisable(true);
            return;
        }
        fx_edit_caption.setText("save edit");
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
        Image img = new Image(path, 120, 120, false, false);
        ImageView add_imv = new ImageView(img);
        label.setGraphic(add_imv);
        label.setContentDisplay(ContentDisplay.TOP);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPrefWidth(120.0);
        label.setWrapText(true);
        fx_tilepane.getChildren().add(label);
    }

    public void back(){
        // Get stage and switch
        Stage stage = (Stage)fx_anchor.getScene().getWindow();
        Photos.switchStage(stage, "General.fxml", "Photo Library // Current User: " +
                Admin.users.get(Admin.user_id).getUser());
    }
    public void logout(){
        Admin.user_id = -1;
        Stage stage = (Stage)fx_anchor.getScene().getWindow();
        Photos.switchStage(stage, "Login.fxml", "Enter Authentication");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        // Set up TilePane, padding and two columns
        //TilePane tiles = new TilePane(10, 10); tiles.setPrefColumns(2);
        fx_tilepane.setPadding(new Insets(10,10,10,10));

        // Grab the active User and load its data
        getUser();
        current_user.loadUser();

        // Create "Add New" label and add it to the TilePane
        Label add_text = new Label("Add New");
        addToTilePane(add_text, "resources/add.png");
        add_text.setOnMouseClicked(e -> addNewAlbum());

        // Display the albums of User
        int size = current_user.albums.size();
        for(int i = 0; i < size; i++){

            // Display albums as labels with icon and text
            Label icon = new Label(current_user.albums.get(i).getTitle());
            addToTilePane(icon, "resources/folder.png");

            // Set behavior when clicked
            icon.setOnMouseClicked(e -> mouseHandler(e, icon));

        }
    }
}
