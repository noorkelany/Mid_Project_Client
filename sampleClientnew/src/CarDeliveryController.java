import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Date;

import data.Order;
import data.ResponseWrapper;

/**
 * Controller class for the Car Delivery GUI. Handles subscription code input,
 * validation with the server, displays feedback messages to the user, and opens
 * the delivery window.
 */
public class CarDeliveryController {

	/** Holds the parking spot number received from the server */
	private String msg;

	/** Holds the subscription code entered by the user */
	private String code;

	/** Text field where user inputs subscription code */
	@FXML
	private TextField subscriptionCodeField;

	/** Label used to show success or error messages to the user */
	@FXML
	private Label outputLabel;

	/** Button used to proceed with delivering the car */
	@FXML
	private Button deliverCarClicked;

	/**
	 * Called automatically after FXML components are loaded. Initializes the client
	 * console and sets up message handling from the server related to subscription
	 * code checking.
	 */
	@FXML
	public void initialize() {
		outputLabel.setVisible(false);

		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			/**
			 * Handles incoming messages from the server and updates the GUI accordingly.
			 *
			 * @param message the object received from the server
			 */
			@Override
			public void display(Object message) {
				Platform.runLater(() -> {
					if (message instanceof ResponseWrapper rsp) {
						if ("CHECK_USER_CODE_RESULT".equals(rsp.getType())) {
							Object data = rsp.getData();
							if (data instanceof String receivedMsg) {
								msg = receivedMsg;

								if ("Invalid Code".equals(msg)) {
									outputLabel.setText("❌ Invalid subscription code. Please try again.");
									outputLabel.setStyle(
											"-fx-font-size: 14px; -fx-text-fill: red; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10px; -fx-background-color: #ffe5e5;");
									deliverCarClicked.setVisible(false);
								} else if ("FULL".equals(msg)) {
									outputLabel.setText("❌ The Parking is Full");
									outputLabel.setStyle(
											"-fx-font-size: 14px; -fx-text-fill: red; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10px; -fx-background-color: #ffe5e5;");
									deliverCarClicked.setVisible(false);
								} else {
									outputLabel.setText("✅press delivery to deliver the car");
									outputLabel.setStyle(
											"-fx-font-size: 14px; -fx-text-fill: green; -fx-border-color: green; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 10px; -fx-background-color: #e6ffe6;");
									deliverCarClicked.setVisible(true);
								}
								outputLabel.setVisible(true);

							}
						}
					}
				});
			}
		};
	}

	/**
	 * Called when the "Check Code" button is clicked. Sends the entered
	 * subscription code to the server for validation. Displays warning if the field
	 * is empty.
	 */
	@FXML
	private void checkCodeClicked() {
		String inputCode = subscriptionCodeField.getText();
		outputLabel.setVisible(false);
		deliverCarClicked.setVisible(false);
		if (inputCode == null || inputCode.trim().isEmpty()) {
			outputLabel.setText("⚠️Please enter a subscription code.");
			outputLabel.setTextFill(Color.ORANGE);
			outputLabel.setVisible(true);
		} else {
			ResponseWrapper request = new ResponseWrapper("CHECK_USER_CODE", inputCode);
			Main.clientConsole.accept(request);
		}
	}

	/**
	 * Called when the "Deliver The Car" button is clicked. Parses the code and
	 * parking number, creates an Order object, and opens a new delivery
	 * confirmation window. Displays error if values are invalid or window fails to
	 * load.
	 */
	@FXML
	private void DeliverCarClicked() {
		try {
			code = subscriptionCodeField.getText();

			if (code == null || code.trim().isEmpty()) {
				outputLabel.setText("⚠️ Enter subscription code before delivery.");
				outputLabel.setTextFill(Color.ORANGE);
				outputLabel.setVisible(true);
				return;
			}

			int subscriberId = Integer.parseInt(code);
			// int parkingNumber = Integer.parseInt(msg);

			Order order1 = new Order(
					// parkingNumber,
					subscriberId);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("CarDelivery.fxml"));
			Parent root = loader.load();

			CarDelivery controller = loader.getController();
			controller.setOrder(order1);

			Stage stage = new Stage();
			stage.setTitle("Delivery Confirmation");
			stage.setScene(new Scene(root));
			stage.show();

			Stage currentStage = (Stage) deliverCarClicked.getScene().getWindow();
			currentStage.close();

		} catch (IOException | NumberFormatException e) {
			outputLabel.setText("❌ Error: " + e.getMessage());
			outputLabel.setTextFill(Color.RED);
			outputLabel.setVisible(true);
		}
	}

	/**method to switch to CarDeliveryWithCode page 
	 * @param event click on link to insert confirmation code
	 */
	@FXML
	void confCode(MouseEvent event) {
		try {
			// Load the previous FXML
			Parent previousRoot = FXMLLoader.load(getClass().getResource("CarDeliveryWithCode.fxml"));
			// Get current stage from any control (e.g. the button)
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			// Replace the scene in the same window
			stage.setScene(new Scene(previousRoot));
			// Main.switchScene("CarDeliveryWithCode.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to return to the main page
	 * @param event button clicked event
	 */
	@FXML
	public void handleBackToMainPage(ActionEvent event) {
		try {
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
