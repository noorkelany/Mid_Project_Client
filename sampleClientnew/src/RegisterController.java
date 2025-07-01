import java.util.regex.Pattern;

import data.Subscriber;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

/**
 * Controller for handling subscriber registration form.
 */
public class RegisterController {

    @FXML
    private TextField emailField, phoneField, usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private HBox messageContainer;

    private static RegisterController instance;

    public RegisterController() {
        instance = this;
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @FXML
    private void handleRegister() {
        clearMessage(); // נקה הודעות קודמות

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showMessage("All fields must be filled.", "error");
            return;
        }

        if (!isValidEmail(email)) {
            showMessage("Invalid email format.", "error");
            return;
        }

        if (!isValidPhone(phone)) {
            showMessage("Invalid phone number. Must be 10 digits. Example: 0501234567", "error");
            return;
        }

        Subscriber sub = new Subscriber(username, password, phone, email, 0);
        Main.clientConsole.accept(sub);
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    private void showMessage(String text, String type) {
        messageContainer.getChildren().clear();

        Label msg = new Label(text);
        msg.setStyle(getStyleForType(type));
        messageContainer.getChildren().add(msg);
    }

    private void clearMessage() {
        messageContainer.getChildren().clear();
    }

    @FXML
    private void handleBack() {
        Main.goBackToPreviousScene();
    }

    /**
     * Called when server sends back registration result.
     */
    public void handleServerResponse(String message) {
        clearMessage();
        if (message.toLowerCase().contains("successful")) {
            showMessage(message, "success");
        } else {
            showMessage(message, "error");
        }
    }

    private String getStyleForType(String type) {
        if ("success".equals(type)) {
            return "-fx-background-color: rgba(144, 238, 144, 0.4);" +  // light green transparent
                   "-fx-text-fill: #006400;" +                         // dark green text
                   "-fx-font-weight: bold;" +
                   "-fx-padding: 12;" +
                   "-fx-border-color: #006400;" +
                   "-fx-border-width: 1;" +
                   "-fx-background-radius: 6;" +
                   "-fx-border-radius: 6;";
        } else { // error
            return "-fx-background-color: rgba(255, 99, 71, 0.3);" +  // light red transparent
                   "-fx-text-fill: #990000;" +                        // dark red text
                   "-fx-font-weight: bold;" +
                   "-fx-padding: 12;" +
                   "-fx-border-color: #990000;" +
                   "-fx-border-width: 1;" +
                   "-fx-background-radius: 6;" +
                   "-fx-border-radius: 6;";
        }
    }
}
