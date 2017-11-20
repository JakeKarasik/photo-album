package photos;

import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import model.*;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This class controls the photo library
 * @author Benjamin Ker (bk375)
 */
public class GeneralController implements Initializable {

    // Stock photos
    Album stock_photos;

    // Current user
    User current_user;

    // Previously selected album/photo
    int cur_index = -1;



    @FXML
    private AnchorPane fx_anchor;
    @FXML
    private ScrollPane fx_scrollpane;

    @FXML
    private ImageView fx_imageviewer;

    @FXML
    private Button fx_create, fx_rename, fx_delete_album, fx_search, fx_save_search, fx_back, fx_logout, fx_prev,
            fx_next, fx_edit, fx_delete_photo, fx_move_copy;

    @FXML
    private Label fx_name, fx_caption, fx_date;

    @FXML
    private TextArea fx_tags;

    @FXML
    private TilePane fx_tilepane;

    // ALBUM MANAGEMENT METHODS //

    /**
     * Adds a new album given input from TextInputDialog
     */
    private void addNewAlbum(){
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
        Album new_album = new Album(title, current_user);
        current_user.albums.add(new_album);
        current_user.saveUser();
        Label add_text = new Label(title);
        addToTilePane(add_text,  "resources/folder.png", Integer.toString(current_user.albums.size()-1));
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
            fx_tilepane.getChildren().clear();
            cur_index = -1;
            Album cur_album = current_user.albums.get(Integer.parseInt(label.getId()));
            cur_album.loadPhotos();

            // Create "Add New" label and add it to the TilePane
            Label add_text = new Label("Add New");
            addToTilePane(add_text, "resources/add.png", "0");
            add_text.setOnMouseClicked(f -> addNewPhoto());

            int size = cur_album.photos.size();
            for(int j = 0; j < size; j++){
                System.out.println(cur_album.photos.get(j).getPath());
                File temp = new File(cur_album.photos.get(j).getPath());
                Image image = new Image(temp.toURI().toString(), 120, 120, false, false);
                ImageView imv = new ImageView(image);
                Image img = new Image(temp.toURI().toString());
                Label thumb = new Label(cur_album.photos.get(j).getPath());
                thumb.setGraphic(imv);
                thumb.setContentDisplay(ContentDisplay.TOP);
                thumb.setTextAlignment(TextAlignment.CENTER);
                thumb.setPrefWidth(120.0);
                thumb.setWrapText(true);
                fx_tilepane.getChildren().addAll(thumb);
                thumb.setOnMouseClicked(f -> setImageviewer(img));
            }
        }else{
            // Deselect previously selected node if needed, mark clicked album as selected
            if(cur_index >= 0){
                Label prev = (Label)(fx_tilepane.getChildren().get(cur_index+1));
                prev.setStyle("-fx-border-color:transparent");
            }
            label.setStyle("-fx-border-color: black");
            cur_index = Integer.parseInt(label.getId());
        }
    }

    // PHOTO MANAGEMENT METHODS //

    /**
     * Helper method to mouseHandler
     * @param img Image to set ImageViewer to
     */
    private void setImageviewer(Image img){ fx_imageviewer.setImage(img); }


    private void addNewPhoto(){

    }

    public void getPath(){
        Stage fc_stage = (Stage)fx_anchor.getScene().getWindow();
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose Image");
        File file = fc.showOpenDialog(fc_stage);
        /*
        String path = fc.showOpenDialog(fc_stage).getAbsolutePath();
        System.out.println(path);
        Image img = new Image("file:"+path);
        fx_imageviewer.setImage(img);
        */
        //stock_photos.addPhoto(file);

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
     * @param id Sets ID of label so it is accessible later
     */
    private void addToTilePane(Label label, String path, String id){
        Image img = new Image(path, 120, 120, false, false);
        ImageView add_imv = new ImageView(img);
        label.setGraphic(add_imv);
        label.setContentDisplay(ContentDisplay.TOP);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPrefWidth(120.0);
        label.setWrapText(true);
        label.setId(id);
        fx_tilepane.getChildren().add(label);
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
        addToTilePane(add_text, "resources/add.png", "0");
        add_text.setOnMouseClicked(e -> addNewAlbum());

        // Display the albums of User
        int size = current_user.albums.size();
        for(int i = 0; i < size; i++){

            // Set up album icon
            Image image = new Image("resources/folder.png", 120, 120, false, false);
            ImageView imv = new ImageView(image);

            // Display albums as labels with icon and text
            Label icon = new Label(current_user.albums.get(i).getTitle());
            addToTilePane(icon, "resources/folder.png", Integer.toString(i));

            // Set behavior when clicked
            icon.setOnMouseClicked(e -> mouseHandler(e, icon));

        }
    }
}
