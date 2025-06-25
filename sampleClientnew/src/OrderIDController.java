
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OrderIDController {

	@FXML
	private Button btnExit = null;

	@FXML
	private Button btnSend = null;

	@FXML
	private Button btnBack = null;
	@FXML
	private TextField OrderIdtxt;
	@FXML
	private Label statusLabel;
	private Main mainApp;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	private String getID() {
		return OrderIdtxt.getText();
	}

	public void Send(ActionEvent event) throws Exception {
		String id = getID().trim();

		if (id.isEmpty()) {
			statusLabel.setText("You must enter an order ID first");
			statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			return;
		}

		ChatClient.order = null;
		statusLabel.setText("");
		Main.clientConsole.accept("OrderID " + id);
		Thread.sleep(1000);
		statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
		if (ChatClient.order == null) {
			statusLabel.setText("Order ID Not Found");
		} else {
			statusLabel.setText("Order ID Found");

			// Switch to UpdateOrder.fxml using existing stage
			FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateOrder.fxml"));
			Parent root = loader.load();
			UpdateOrderController controller = loader.getController();
			controller.loadOrder(ChatClient.order);

			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("updateorder.css").toExternalForm());

			// Save current scene for go-back
			Main.previousScene = ((Node) event.getSource()).getScene();

			Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			currentStage.setScene(scene);
			currentStage.setTitle("Update Order");
			currentStage.show();
		}
	}




}
