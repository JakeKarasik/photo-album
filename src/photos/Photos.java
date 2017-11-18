package photos;
	
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

@SuppressWarnings("WeakerAccess")
public class Photos extends Application {
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
            scene.getStylesheets().add("resources/photos.css");

            swap_stage.setTitle(title);
            swap_stage.getIcons().add(new Image("resources/icon.png"));
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
    public static void newStage(Stage old_stage, String next_fxml, String title){
	    Stage new_stage = new Stage();
        new_stage.initModality(Modality.WINDOW_MODAL);
        new_stage.initOwner(old_stage);
        switchStage(new_stage, next_fxml, title);
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
