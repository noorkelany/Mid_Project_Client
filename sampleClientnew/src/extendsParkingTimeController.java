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

public class extendsParkingTimeController {

	@FXML
	private Spinner<Integer> hourSpinner;

	@FXML
	private Label statusLabel;

	private static Label statusLabelStatic;

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

	public void sendSelectedHour() {
		int selectedHour = hourSpinner.getValue();
		ResponseWrapper rsp = new ResponseWrapper("EXTEND_PARKING_TIME", SubscriberSession.getSubscriber().getCode(),
				selectedHour);
		System.out.println("controller");
		Main.clientConsole.accept(rsp);
	}

	public static void setStatus(String message) {
		if (statusLabelStatic != null) {
			statusLabelStatic.setText(message);
			if (message.toLowerCase().contains("approved")) {
				statusLabelStatic.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
			} else {
				statusLabelStatic.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			}

			// Create a new background thread for the delay
			new Thread(() -> {
				try {
					Thread.sleep(3000); // wait 3 seconds
					Platform.runLater(() -> {
						try {
							// Main.switchScene("subscriberHomePage.fxml");
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
	
	@FXML
    public void handleBackToMainPage(ActionEvent event) {
		try {
			// Load the previous FXML
			Parent previousRoot = FXMLLoader.load(getClass().getResource("SubscriberMain.fxml"));
			// Get current stage from any control (e.g. the button)
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			// Replace the scene in the same window
			stage.setScene(new Scene(previousRoot));
		} catch (Exception e) {
			e.printStackTrace();
		}
       /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("BPARK Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
