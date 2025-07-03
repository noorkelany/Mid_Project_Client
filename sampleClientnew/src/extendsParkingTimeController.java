import java.io.IOException;

import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

/**
 * Controller for handling the extension of parking time. Allows the user to
 * select additional hours and sends the request to the server. Updates the UI
 * based on the server's response.
 */
public class extendsParkingTimeController {

	/**
	 * spinner for picking an hour
	 */
	@FXML
	private Spinner<Integer> hourSpinner;

	/**
	 * label for displaying messages for the user
	 */
	@FXML
	private Label statusLabel;

	/**
	 * label for displaying messages for the user
	 */
	private static Label statusLabelStatic;

	/**
	 * Initializes the controller, sets up the hour spinner, and configures the
	 * client-side response handler for server messages.
	 */
	@FXML
	public void initialize() {
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4, 1);
		hourSpinner.setValueFactory(valueFactory);
		statusLabelStatic = statusLabel;

		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object msg) {
				Platform.runLater(() -> {
					try {
						if (msg instanceof ResponseWrapper rsp) {
							if ("TIME_REQUEST_ANSWER".equals(rsp.getType())) {
								String message = rsp.getData().toString();
								extendsParkingTimeController.setStatus(message);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		};
	}

	/**
	 * Sends the selected hour value to the server to request a parking time
	 * extension.
	 */
	public void sendSelectedHour() {
		int selectedHour = hourSpinner.getValue();
		ResponseWrapper rsp = new ResponseWrapper("EXTEND_PARKING_TIME", SubscriberSession.getSubscriber().getCode(),
				selectedHour);
		System.out.println("controller");
		Main.clientConsole.accept(rsp);
	}

	/**
	 * Updates the status label with a message from the server and navigates back to
	 * the main page after a short delay.
	 *
	 * @param message The message to display (e.g. approved or rejected)
	 */
	public static void setStatus(String message) {
		if (statusLabelStatic != null) {
			statusLabelStatic.setText(message);
			if (message.toLowerCase().contains("approved")) {
				statusLabelStatic.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
			} else {
				statusLabelStatic.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			}

			// Delay for 3 seconds, then switch scene
			new Thread(() -> {
				try {
					Thread.sleep(3000);
					Platform.runLater(() -> {
						try {
							Main.switchScene("MainPage.fxml");
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();
		}
	}

	/**
	 * Handles navigation back to the subscriber's main page.
	 *
	 * @param event the button click event triggering the navigation
	 */
	@FXML
	public void handleBackToMainPage(ActionEvent event) {
		try {
			Parent previousRoot = FXMLLoader.load(getClass().getResource("SubscriberMain.fxml"));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(previousRoot));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
