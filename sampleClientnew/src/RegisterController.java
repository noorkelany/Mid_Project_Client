import java.util.regex.Pattern;

import data.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for handling subscriber registration form. Validates input fields
 * and sends registration data to the server. Implemented by the student as part
 * of the BPARK system.
 */

public class RegisterController {
	@FXML
	private TextField emailField, phoneField, usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label statusLabel;
	private static RegisterController instance;

	public RegisterController() {
		instance = this;
	}

	public static RegisterController getInstance() {
		return instance;
	}

	/**
	 * Triggered when the user clicks the register button. Validates fields and
	 * sends a Subscriber object to the server if valid.
	 */
	@FXML
	private void handleRegister() {

		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String phone = phoneField.getText().trim();
		String email = emailField.getText().trim();

		if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
			showAlert(AlertType.WARNING, "All fields must be filled.");
			return;
		}

		if (!isValidEmail(email)) {
			showAlert(AlertType.ERROR, "Invalid email format.");
			return;
		}

		if (!isValidPhone(phone)) {
			showAlert(AlertType.ERROR, "Invalid phone number. Use only digits (10 characters).\nExample: 0501234567");
			return;
		}

		Subscriber sub = new Subscriber(username, password, phone, email, 0); // id, code מהשרת
		System.out.println(sub.toString() + " " + (sub instanceof Subscriber));
		Main.clientConsole.accept(sub);
		statusLabel.setText("Registration request sent!");

	}

	private boolean isValidEmail(String email) {
		return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
	}

	private boolean isValidPhone(String phone) {
		return phone.matches("\\d{10}"); // exactly 10 digits
	}

	private void showAlert(AlertType type, String message) {
		Alert alert = new Alert(type);
		alert.setContentText(message);
		alert.showAndWait();
	}

	@FXML
	private void handleBack() {
		Main.goBackToPreviousScene();
	}

	public void handleServerResponse(String message) {
		statusLabel.setText(message);
		Alert alert = new Alert(
				message.startsWith("Registration successful") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
				message, ButtonType.OK);
		alert.showAndWait();
	}

}
