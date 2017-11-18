package photos;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Admin;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * This class controls the admin dashboard
 * @author Benjamin Ker (bk375)
 */
@SuppressWarnings ("unchecked")
public class AdminController implements Initializable {
    @FXML
    private AnchorPane fx_anchor;

    @FXML
    private Button delete_button, logout, create;

    @FXML
    private TextField new_username;

    @FXML
    private PasswordField new_password, verify_password;

    @FXML
    private ListView user_list;

    /**
     * Deserializes our user list and displays usernames in ListView
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Admin.loadUsers();
        showUsers();
    }

    /**
     * Displays usernames in a ListView
     */
    public void showUsers(){ user_list.setItems(Admin.users); }

    /**
     * Gets text from relevant TextFields and passes it into Admin.createUser
     */
    public void newUser(){
        String user_in = new_username.getText();
        String pass_in = new_password.getText();
        String ver_pass = verify_password.getText();
        Admin.createUser(user_in, pass_in, ver_pass);
    }

    /**
     * Gets index of selected username and passes it to Admin.deleteUser
     */
    public void rmUser(){
        int index = user_list.getSelectionModel().getSelectedIndex();
        Admin.deleteUser(index);
    }

    /**
     * Calls Photos.switchStage to logout
     */
    public void logout(){
        Photos.switchStage((Stage)fx_anchor.getScene().getWindow(), "Login.fxml", "Enter Authentication");
    }
}
