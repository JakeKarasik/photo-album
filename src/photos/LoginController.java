package photos;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;



public class LoginController implements Initializable {

    @FXML
    private ImageView imageview;

    @FXML
    private TextField user_in;

    @FXML TextField pass_in;

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

    }

    public void login(){
        Stage stage2 = (Stage)fx_anchor.getScene().getWindow();


        //Stage stage = new Stage();
        //stage.initModality(Modality.WINDOW_MODAL);
        //stage.initOwner(stage2);


        String user_input = user_in.getText();
        String pass_input = pass_in.getText();
        String next_fxml = "General.fxml";
        String next_title = "Photo Library";

        if(user_input.equals("admin") && pass_input.equals("admin")){
            next_fxml = "Admin.fxml";
            next_title = "Administrator Dashboard";
        }

        Photos.newStage(stage2, next_fxml, next_title);
        /*

        try {
            Parent root = FXMLLoader.load(getClass().getResource(next_fxml));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("resources/photos.css");

            stage.setTitle(next_title);
            stage.getIcons().add(new Image("resources/icon.png"));
            stage.setScene(scene);
            stage.setResizable(false);

            stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
        */
    }
}
