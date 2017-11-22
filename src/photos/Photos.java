package photos;
	
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.File;
import java.io.Serializable;

/**
 * This class initializes the program and implements scene switching
 * @author Benjamin Ker (bk375)
 */
@SuppressWarnings("WeakerAccess")
public class Photos extends Application implements Serializable {
	
	
	/**
	 * ID used for tracking serial version
	 */
	private static final long serialVersionUID = 1L;
	
    /**
     * Calls switchStage to start the program
     * @param primaryStage Stage argument to call switchStage on
     */
	@Override
	public void start(Stage primaryStage) {
	    String start_fxml = "Login.fxml";
	    String start_title = "Enter Authentication";

	    switchStage(primaryStage, start_fxml, start_title);
	}

    /**
     * Called by other Controllers to switch a stage to a new one
     * @param swap_stage Stage to have scene swapped
     * @param next_fxml Name of FXML to load
     * @param title Title of next scene
     */
	public static void switchStage(Stage swap_stage, String next_fxml, String title){
        try {
            Parent root = FXMLLoader.load(Photos.class.getResource(next_fxml));
            Scene scene = new Scene(root);
            File ico = new File(System.getProperty("user.dir") + "/data/resources/");
            String path = ico.toURI().toString();
            swap_stage.setTitle(title);
            swap_stage.getIcons().add(new Image(path + "icon.png"));
            swap_stage.setScene(scene);
            swap_stage.setResizable(false);
            swap_stage.show();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Called by other Controllers to create a new stage. Used for temporary dialogues such as
     * MoveCopy, Tag Editor, Search, etc.
     * @param old_stage Current stage to be disabled until new Stage is closed
     * @param next_fxml Name of FXML to load
     * @param title Title of next scene
     */
    public static Stage newStage(Stage old_stage, String next_fxml, String title){
	    Stage new_stage = new Stage();
        new_stage.initModality(Modality.WINDOW_MODAL);
        new_stage.initOwner(old_stage);
        switchStage(new_stage, next_fxml, title);
        return new_stage;
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
