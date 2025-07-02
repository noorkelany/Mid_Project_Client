import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Entry point of the AutoParking reservation JavaFX application.
 * Manages scene switching and connection lifecycle with the server.
 */
public class Main extends Application {

    /** The primary window (stage) used by the application. */
    private static Stage primaryStage;

    /** Stores the previous scene before navigation (used for going back). */
    protected static Scene previousScene;

    /** Caches the main page scene if needed for reuse. */
    protected static Scene mainPageScene;

    /** Handles client-server communication. */
    public static ClientConsole clientConsole;

    /** Stores the server IP address entered by the user. */
    public static String serverIP = null;

    /**
     * JavaFX application entry point. Initializes and shows the first scene.
     *
     * @param stage the primary stage provided by the JavaFX runtime
     * @throws Exception if the initial scene fails to load
     */
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        switchScene("ippage.fxml"); // Load IP input page
    }

    /**
     * Switches the scene to a new FXML view.
     * Applies a specific stylesheet based on the target FXML file.
     *
     * @param fxmlFile the FXML file to load (must be in same package)
     * @throws Exception if loading the FXML fails
     */
    public static void switchScene(String fxmlFile) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
        Parent root = loader.load();

        previousScene = primaryStage.getScene(); // Save the current scene for "go back"

        Scene scene = new Scene(root);

        // Apply scene-specific CSS
        if (fxmlFile.equals("ShowOrder.fxml")) {
            scene.getStylesheets().add(Main.class.getResource("showorders.css").toExternalForm());
        } else if (fxmlFile.equals("UpdateOrder.fxml")) {
            scene.getStylesheets().add(Main.class.getResource("updateorder.css").toExternalForm());
        } else if (fxmlFile.equals("OrderIDPage.fxml")) {
            scene.getStylesheets().add(Main.class.getResource("orderid.css").toExternalForm());
        } else if (fxmlFile.equals("mainpage.css")) {
            scene.getStylesheets().add(Main.class.getResource("mainpage.css").toExternalForm());
        }

        primaryStage.setScene(scene);
        primaryStage.setTitle("AutoParking reservation");
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    /**
     * Restores the previous scene, if available.
     * Called when the user presses a "Back" button.
     */
    public static void goBackToPreviousScene() {
        if (previousScene != null) {
            primaryStage.setScene(previousScene);
            primaryStage.setTitle("AutoParking reservation");
            primaryStage.show();
        }
    }

    /**
     * Main method (required for launching JavaFX apps from IDEs without FX launcher support).
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called automatically when the application is closing.
     * Closes the client-server connection cleanly.
     *
     * @throws Exception if an error occurs while closing the connection
     */
    @Override
    public void stop() throws Exception {
        if (clientConsole != null && clientConsole.client != null) {
            clientConsole.accept("disconnect");
            Thread.sleep(100); // Optional delay to ensure message delivery
            clientConsole.client.closeConnection();
        }
    }
}
