import data.Subscriber;
import data.SubscriberSession;
import data.ResponseWrapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateSubscriberController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private Label statusMessage;

    private boolean showingPassword = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Subscriber sub = SubscriberSession.getSubscriber();
        if (sub != null) {
            usernameField.setText(sub.getUsername());
            passwordField.setText(sub.getPassword());
            visiblePasswordField.setText(sub.getPassword());
            phoneField.setText(sub.getPhoneNumber());
            emailField.setText(sub.getEmail());
        }

        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);

        // האזנה לתגובה מהשרת
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                if (message instanceof ResponseWrapper response && "UpdateSubscriberResult".equals(response.getType())) {
                    boolean success = (boolean) response.getData();
                    Platform.runLater(() -> {
                        if (success) {
                            showMessage("✅ Details updated successfully.", "success");
                            Subscriber updated = new Subscriber(
                                    usernameField.getText(),
                                    showingPassword ? visiblePasswordField.getText() : passwordField.getText(),
                                    phoneField.getText(),
                                    emailField.getText(),
                                    SubscriberSession.getSubscriber().getCode()
                            );
                            SubscriberSession.setSubscriber(updated);
                        } else {
                            showMessage("❌ Failed to update details.", "error");
                        }
                    });
                }
            }
        };
    }

    @FXML
    public void togglePasswordVisibility() {
        showingPassword = !showingPassword;

        if (showingPassword) {
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText();
        String password = showingPassword ? visiblePasswordField.getText() : passwordField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showMessage("❌ Please fill in all fields.", "error");
            return;
        }

        Subscriber updated = new Subscriber(username, password, phone, email, SubscriberSession.getSubscriber().getCode());
        Main.clientConsole.accept(new ResponseWrapper("UpdateSubscriber", updated));
    }
    
    @FXML
    private void showMessage(String text, String type) {
        statusMessage.setText(text);
        statusMessage.setVisible(true);
        statusMessage.setStyle("");

        switch (type) {
            case "success" -> statusMessage.setStyle(
                    "-fx-background-color: #d4edda; " +
                    "-fx-text-fill: green; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 8; " +
                    "-fx-background-radius: 6;"
            );
            case "error" -> statusMessage.setStyle(
                    "-fx-background-color: #f8d7da; " +
                    "-fx-text-fill: darkred; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 8; " +
                    "-fx-background-radius: 6;"
            );
            default -> statusMessage.setStyle(
                    "-fx-background-color: #e2e3e5; " +
                    "-fx-text-fill: black; " +
                    "-fx-font-weight: normal; " +
                    "-fx-padding: 8; " +
                    "-fx-background-radius: 6;"
            );
        }
    }


    public void handleBackToMainPage(ActionEvent event) {
		try {
		/*// Load the previous FXML
		Parent previousRoot = FXMLLoader.load(getClass().getResource("SubscriberMain.fxml"));

		// Get current stage from any control (e.g. the button)
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		// Replace the scene in the same window
		stage.setScene(new Scene(previousRoot));*/
			Main.switchScene("SubscriberMain.fxml");
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
    
    @FXML
    private void handleSubscriberOrders() {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


