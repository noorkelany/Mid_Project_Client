import java.io.IOException;

import data.Login;
import data.Manager;
import data.ResponseWrapper;
import data.Role;
import data.StaffSession;
import data.Subscriber;
import data.SubscriberSession;
import data.Worker;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Class to control the login process, redirect to the right page
 */
public class LoginController {
	/**
	 * The text field for entering the username.
	 */
	@FXML
	private TextField usernameField;

	/**
	 * The password field for secure password input (hidden characters).
	 */
	@FXML
	private PasswordField passwordField;

	/**
	 * The visible password field for showing the password in plain text.
	 */
	@FXML
	private TextField visiblePasswordField;

	/**
	 * The eye icon used to toggle password visibility.
	 */
	@FXML
	private ImageView eyeIcon;

	/**
	 * Label used to display error messages to the user.
	 */
	@FXML
	private Label errorLabel;

	/**
	 * Static reference to the error label used in inner classes or callbacks.
	 */
	private static Label errorLabelStatic;

	/**
	 * The image shown when the password is visible (eye open).
	 */
	private Image eyeOpen;

	/**
	 * The image shown when the password is hidden (eye closed).
	 */
	private Image eyeClosed;

	/**
	 * Boolean flag indicating whether the password is currently visible.
	 */
	private boolean showingPassword = false;


	/**
	 * Initializes the controller, sets up the eye icon and handles the response from the server
	 */
	@FXML
	public void initialize() {
		errorLabelStatic = errorLabel;
		eyeOpen = new Image(getClass().getResourceAsStream("/images/eye_open.png"));
		eyeClosed = new Image(getClass().getResourceAsStream("/images/eye_closed.png"));
		eyeIcon.setImage(eyeClosed);

		// Override ClientConsole to capture server response
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object role) {
				Platform.runLater(() -> {
					try {
						ResponseWrapper rsp = (ResponseWrapper) role;
						System.out.println("Received response: " + rsp.getType());
						System.out.println("Role: " + rsp.getData());
						System.out.println("Extra: " + rsp.getExtra());
						String type = rsp.getType(); // תיקון קריטי

						switch (type) {
						case "LOGIN_RESPONSE":
							switch (rsp.getData().toString()) {
							case "subscriber":
								Subscriber sub = (Subscriber) rsp.getExtra();
								SubscriberSession.setSubscriber(sub);
								Main.switchScene("SubscriberMain.fxml");

								break;
							case "worker":
								Worker worker = (Worker) rsp.getExtra();
								StaffSession.getInstance().login(worker.getUsername(), Role.WORKER);
								Main.switchScene("workerHomePage.fxml");
								break;
							case "manager":
								Manager manager = (Manager) rsp.getExtra();
								StaffSession.getInstance().login(manager.getUsername(), Role.ADMIN);
								Main.switchScene("managerHomePage.fxml");
								break;

							default:
								showLoginError();
							}
							break;

						default:
							showLoginError();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			/**
			 * Method to display error message
			 */
			private void showLoginError() {
				System.out.println("❌ Invalid username or password.");
				if (errorLabelStatic != null) {
					errorLabelStatic.setText("Invalid username or password.");
					errorLabelStatic.setStyle("-fx-text-fill: red;");
				}
			}
		};
	}

	/**
	 * method to toggle between the show password icon states
	 */
	@FXML
	private void togglePasswordVisibility() {
		showingPassword = !showingPassword;

		if (showingPassword) {
			visiblePasswordField.setText(passwordField.getText());
			visiblePasswordField.setVisible(true);
			visiblePasswordField.setManaged(true);
			passwordField.setVisible(false);
			passwordField.setManaged(false);
			eyeIcon.setImage(eyeOpen);
		} else {
			passwordField.setText(visiblePasswordField.getText());
			passwordField.setVisible(true);
			passwordField.setManaged(true);
			visiblePasswordField.setVisible(false);
			visiblePasswordField.setManaged(false);
			eyeIcon.setImage(eyeClosed);
		}
	}

	/**
	 * Method to activate the login button(check the username & password if they
	 * exists)
	 */
	@FXML
	private void handleLogin() {
		try {
			String username = usernameField.getText().trim();
			String password = passwordField.isVisible() ? passwordField.getText().trim()
					: visiblePasswordField.getText().trim();
			System.out.println(username + "->" + password);
			if (username.isEmpty() || password.isEmpty()) {
				errorLabel.setText("Username and password are required.");
				return;
			}
			if (username.equals("admin") && password.equals("admin")) {

			}
			Login login = new Login(username, password);
			ResponseWrapper rsp = new ResponseWrapper("LOGIN", login);
			Main.clientConsole.accept(rsp);
		} catch (Exception e) {
			errorLabel.setText("Login failed due to error.");
			e.printStackTrace();
		}
	}

	/**
	 * method to switch to QR code page
	 */
	@FXML
	private void handleQRLoginLink() {
		try {
			Main.switchScene("QRCode.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to switch to main page
	 * @param event login button clicked
	 */
	@FXML
	private void handleBack(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle("BPARK - Main Page");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
