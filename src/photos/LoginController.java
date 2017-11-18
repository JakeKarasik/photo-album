package photos;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;

/**
 * This class controls the login window
 * @author Benjamin Ker (bk375)
 */
public class LoginController{

    @FXML
    private TextField user_in;

    @FXML TextField pass_in;

    @FXML
    private AnchorPane fx_anchor;

    /**
     * Gets user input and attempts to login.
     */
    public void login(){
        // Get user input
        String user_input = user_in.getText();
        String pass_input = pass_in.getText();
        User temp = new User(user_input, pass_input);

        // Set next fxml and title - either Photo Library or Admin
        String next_fxml = "General.fxml";
        String next_title = "Photo Library";

        if(user_input.equals("admin") && pass_input.equals("admin")){
            next_fxml = "Admin.fxml";
            next_title = "Administrator Dashboard";
        }else if(!temp.authenticated()){ return; }

        // Get stage and switch
        Stage stage = (Stage)fx_anchor.getScene().getWindow();
        Photos.switchStage(stage, next_fxml, next_title);
    }
}
