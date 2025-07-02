import java.util.regex.Pattern;

import data.EmailSender;
import data.ResponseWrapper;
import data.Subscriber;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for handling subscriber registration form.
 * Validates input fields and sends registration data to the server.
 * Part of the BPARK system.
 */
public class RegisterController {

    @FXML
    private TextField emailField, phoneField, usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private static RegisterController instance;

    /**
     * Constructor - sets this instance as the singleton reference.
     */
    public RegisterController() {
        instance = this;
    }

    /**
     * Gets the current instance of RegisterController.
     *
     * @return the current instance
     */
    public static RegisterController getInstance() {
        return instance;
    }

    /**
     * Clears all input fields and status label in the registration form.
     */
    public void clearForm() {
        emailField.clear();
        phoneField.clear();
        usernameField.clear();
        passwordField.clear();
        statusLabel.setText("");
    }

    /**
     * Initializes the controller and sets up the client-server listener
     * for receiving registration responses from the server.
     */
    @FXML
    public void initialize() {
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object role) {
                Platform.runLater(() -> {
                    try {
                        ResponseWrapper rsp = (ResponseWrapper) role;
                        String responseType = rsp.getType();
                        String message = (String) rsp.getData();
                        switch (responseType) {
                            case "CODE":
                                statusLabel.setText(message);
                                Alert alert = new Alert(
                                        message.startsWith("Registration successful") ?
                                                Alert.AlertType.INFORMATION :
                                                Alert.AlertType.ERROR,
                                        message, ButtonType.OK);
                                alert.showAndWait();
                                clearForm();
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
    }

    /**
     * Handles the register button action.
     * Validates user input and sends the Subscriber object to the server if valid.
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

        Subscriber sub = new Subscriber(username, password, phone, email, 0); // ID and code from server
        System.out.println(sub.toString() + " " + (sub instanceof Subscriber));
        ResponseWrapper rsp = new ResponseWrapper("REGISTER_SUBSCRIBER", sub);
        Main.clientConsole.accept(rsp);
        statusLabel.setText("Registration completed successfully!");
    }

    /**
     * Validates the email format using regex.
     *
     * @param email the email string to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }

    /**
     * Validates the phone number format.
     *
     * @param phone the phone number string to validate
     * @return true if exactly 10 digits, false otherwise
     */
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    /**
     * Displays an alert with the specified type and message.
     *
     * @param type    the type of alert (INFO, ERROR, etc.)
     * @param message the message to display
     */
    private void showAlert(AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handles the "Back" button action. Navigates to the previous scene.
     */
    @FXML
    private void handleBack() {
        Main.goBackToPreviousScene();
    }

    /**
     * Handles a direct server response for registration.
     *
     * @param message the response message to display
     */
    public void handleServerResponse(String message) {
        statusLabel.setText(message);
        Alert alert = new Alert(
                message.startsWith("Registration successful") ?
                        Alert.AlertType.INFORMATION :
                        Alert.AlertType.ERROR,
                message, ButtonType.OK);
        alert.showAndWait();
    }
}
