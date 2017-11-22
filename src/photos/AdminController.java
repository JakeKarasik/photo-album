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
 * @author Jake Karasik (jak451)
 */
@SuppressWarnings ("unchecked")
public class AdminController implements Initializable {
    /**
     * Container for the Admin dashboard
     */
    @FXML
    private AnchorPane fx_anchor;

    /**
     * New username to be added
     */
    @FXML
    private TextField new_username;

    /**
     * Password to be attached to new user
     */
    @FXML
    private PasswordField new_password, verify_password;

    /**
     * Shows list of users
     */
    @FXML
    private ListView user_list;

    /**
     * Load our list of Users into the ListView
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources){
        Admin.loadUsers();
        user_list.setItems(Admin.users);
    }

    /**
     * Gets text from relevant TextFields and creates a new user
     */
    public void newUser(){
        String user_in = new_username.getText();
        String pass_in = new_password.getText();
        String ver_pass = verify_password.getText();
        Admin.createUser(user_in, pass_in, ver_pass);
    }

    /**
     * Gets index of selected username and deletes it
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
