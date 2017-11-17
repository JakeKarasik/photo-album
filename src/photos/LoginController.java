package photos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class LoginController implements Initializable {

    @FXML
    private ImageView imageview;

    @FXML
    private Label label;

    @FXML
    private Button login;

    @FXML
    private AnchorPane fx_anchor;


    /**
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        /*
        Image image = new Image("resources/placeholder.png");
        imageview1.setImage(image);
        */

    }

    public void login(){
        Stage stage = (Stage)fx_anchor.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("General.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("resources/photos.css");

            stage.setTitle("Photo Library");
            stage.getIcons().add(new Image("resources/icon.png"));
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
