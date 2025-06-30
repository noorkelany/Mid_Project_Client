import java.io.InputStream;

import data.ResponseWrapper;
import data.Subscriber;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

public class QRCodeLoginController {

	@FXML
	private TextField codeField;

	@FXML
	private Label statusLabel;

	/**
	 * Handles the login process based on the entered code. Simulates scanning a QR
	 * code that contains the subscriber code.
	 */
	// save the sent subscriber and move to the subscriberMain
	@FXML
	public void initialize() {
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				Platform.runLater(() -> {
					if (message instanceof Subscriber sub) {
						SubscriberSession.setSubscriber(sub);
						try {
							Main.switchScene("SubscriberMain.fxml");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		};
	}

	@FXML
	private void handleLogin() {
		String code = codeField.getText().trim();

		if (code.isEmpty()) {
			statusLabel.setText("Please enter a code.");
			return;
		}

		// Replace with your actual validation logic:
		try {
			// LoginController loginCtrl = new LoginController();
			// Subscriber subscriber = loginCtrl.getSubscriberByCode(code);
			ResponseWrapper rsp = new ResponseWrapper("VALIDATE_SUBSCRIBER_CODE", Integer.parseInt(code));
			Main.clientConsole.accept(rsp);
			/*
			 * if (subscriber != null) { SubscriberSession.setSubscriber(subscriber); //
			 * Save to session statusLabel.setText("Login successful!");
			 * 
			 * 
			 * } else { statusLabel.setText("Invalid code. Try again."); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
