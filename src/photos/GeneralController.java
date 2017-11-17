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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class GeneralController implements Initializable {

    // Stock photos
    Album stock_photos;

    // Current user
    User current_user;


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

    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){

        TilePane tiles = new TilePane(10, 10); tiles.setPrefColumns(2);
        tiles.setPadding(new Insets(10,10,10,10));
        current_user = null;
        stock_photos = new Album("stock", current_user);
        /*
        for(int i = 1; i < 7; i++){
            stock_photos.addPhoto("resources/stock/stock-" + i + ".jpg");
        }
        */



        for(int i = 1; i < 7; i++){
            Image image = new Image("resources/stock/stock-" + i + ".jpg", 120, 120, false, false);
            ImageView imv = new ImageView(image);
            Image img = new Image("resources/stock/stock-" + i + ".jpg");
            Label label2 = new Label("stock-" + i + ".jpg");
            label2.setGraphic(imv);
            label2.setContentDisplay(ContentDisplay.TOP);
            label2.setTextAlignment(TextAlignment.CENTER);
            label2.setPrefWidth(120.0);
            label2.setWrapText(true); tiles.getChildren().addAll(label2);
            label2.setOnMouseClicked(new EventHandler<MouseEvent>(){
                @Override
                public void handle(MouseEvent arg0) {
                    fx_imageviewer.setImage(img);
                }
            });
        }
        fx_scrollpane.setContent(tiles);
        fx_scrollpane.setPannable(true);

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
        stock_photos.addPhoto(file);

    }
}
