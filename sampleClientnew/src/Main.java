import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	private static Stage primaryStage;
	protected static Scene previousScene;
	protected static Scene mainPageScene;
	public static ClientConsole clientConsole;
	public static String serverIP = null;

	@Override
	public void start(Stage stage) throws Exception {
		primaryStage = stage;

		// Load IP input page only (no main page preload!)
		switchScene("ippage.fxml");
		//Thread.sleep(100);
	}

	public static void switchScene(String fxmlFile) throws Exception {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
		Parent root = loader.load();
		
		previousScene = primaryStage.getScene(); // optional

		Scene scene = new Scene(root);
		


		// Apply CSS styles
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

	public static void goBackToPreviousScene() {
		if (previousScene != null) {
			primaryStage.setScene(previousScene);
			primaryStage.setTitle("AutoParking reservation");
			primaryStage.show();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void stop() throws Exception {
		if (clientConsole != null && clientConsole.client != null) {
			clientConsole.accept("disconnect");
			Thread.sleep(100); // optional small delay
			clientConsole.client.closeConnection();
		}
	}
}
