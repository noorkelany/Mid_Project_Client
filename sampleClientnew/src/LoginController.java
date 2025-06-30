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

public class LoginController {
	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField visiblePasswordField;

	@FXML
	private ImageView eyeIcon;

	@FXML
	private Label errorLabel;
	private static Label errorLabelStatic;

	private Image eyeOpen;
	private Image eyeClosed;
	private boolean showingPassword = false;

	/**
	 * 
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

	@FXML
	private void handleQRLoginLink() {
		try {
			Main.switchScene("QRCode.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
