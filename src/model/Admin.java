package model;

import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.ArrayList;

/**
 * This class defines Admin functionality
 * @author Benjamin Ker (bk375)
 */
@SuppressWarnings({"unchecked", "Duplicates"})
public class Admin{

    // List of users managed by admin
    public static ObservableList<User> users = FXCollections.observableArrayList();

    // Data storage location
    public static final String storeDir = "database";
    public static String storeFile = "users.ser";

    // ID of active user
    public static int user_id  = -1;

    /**
     * Admin is non-instantiable
     */
    private Admin(){}

    /**
     * Serializes list of users and active user ID
     * @return True if successful, false otherwise
     */
    public static boolean saveUsers() {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + storeFile));
            oos.writeObject(new ArrayList<>(users));
            oos.close();

            oos = new ObjectOutputStream(new FileOutputStream(storeDir + File.separator + "user-id.ser"));
            oos.writeObject(user_id);
            oos.close();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Deserializes list of users and active user ID
     * @return True if successful, false otherwise
     */
    public static boolean loadUsers(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + storeFile));
            ArrayList<User> read = (ArrayList<User>)(ois.readObject());
            users = FXCollections.observableArrayList(read);

            ois = new ObjectInputStream(new FileInputStream(storeDir + File.separator + "user-id.ser"));
            user_id = (int)(ois.readObject());
            ois.close();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Given a User object, checks its credentials, sets active user ID on success
     * @param input User object created by login form
     * @return True if credentials match, false otherwise
     */
    public static boolean authenticated(User input){
        boolean exists = users.contains(input);

        if(input.getUser().isEmpty() || input.getPass().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return false;
        }
        else if(!exists){
            Alert alert = new Alert(Alert.AlertType.ERROR, "User does not exist");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return false;
        }

        User cur_user = Admin.users.get(Admin.users.indexOf(input));

        if(!cur_user.getPass().equals(input.getPass())){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Password does not match");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return false;
        }
        user_id = users.indexOf(input);
        return true;
    }

    /**
     * Creates a user object and adds it to list of users
     * @param username Username of new user
     * @param password Password of new user
     * @param verify Password of new user to match
     */
    public static void createUser(String username, String password, String verify){
        User new_user = new User(username, password);
        if(username.equals("admin")){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Not allowed to create an admin");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }else if(username.isEmpty() || password.isEmpty() || verify.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "All fields must be filled");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }else if(!password.equals(verify)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Passwords do not match");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }else if(users.contains(new_user)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Duplicate username");
            alert.setHeaderText("Invalid Input");
            alert.showAndWait();
            return;
        }
        users.add(new_user);
        saveUsers();
    }

    /**
     * Deletes user if selected in ListView
     * @param index Index of User object to be removed
     */
    // Remove User object from list, given index
    public static boolean deleteUser(int index){
        try{
            String rm_user = users.get(index).getUser();
            File path = new File("database/user");
            File[] dir = path.listFiles();
            if(dir != null){
                for(File file : dir){
                    if(file.getPath().contains(rm_user)){
                        file.delete();
                    }
                }
            }
            users.remove(index);
            saveUsers();
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
