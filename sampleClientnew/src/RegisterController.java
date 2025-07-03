import java.util.regex.Pattern;

import data.ResponseWrapper;
import data.Subscriber;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

/**
 * controller for handling registration form
 * we validate inputs and send data to server
 */
public class RegisterController {

    @FXML
    private TextField emailField, phoneField, usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private HBox messageContainer;

    private static RegisterController instance;

    // just storing reference to use it later if needed
    public RegisterController() {
        instance = this;
    }

    public static RegisterController getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        // client listener - waiting for response after sending registration
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object msg) {
                Platform.runLater(() -> {
                    try {
                        ResponseWrapper rsp = (ResponseWrapper) msg;
                        if ("CODE".equals(rsp.getType())) {
                            handleServerResponse((String) rsp.getData());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
    }

    /**
     * when user clicks register
     */
    @FXML
    private void handleRegister() {
        clearMessage();

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showMessage("all fields must be filled.", "error");
            return;
        }

        if (!isValidEmail(email)) {
            showMessage("invalid email format.", "error");
            return;
        }

        if (!isValidPhone(phone)) {
            showMessage("invalid phone number. use 10 digits like: 0501234567", "error");
            return;
        }

        Subscriber sub = new Subscriber(username, password, phone, email, 0);
        ResponseWrapper rsp = new ResponseWrapper("REGISTER_SUBSCRIBER", sub);
        Main.clientConsole.accept(rsp);
    }

    /**
     * handles response from server and shows message
     */
    public void handleServerResponse(String message) {
        clearMessage();
        if (message.toLowerCase().contains("successful")) {
            showMessage(message, "success");
            clearForm();
        } else {
            showMessage(message, "error");
        }
    }

    /**
     * clear all input fields
     */
    public void clearForm() {
        emailField.clear();
        phoneField.clear();
        usernameField.clear();
        passwordField.clear();
    }

    /**
     * check if email is valid
     */
    private boolean isValidEmail(String email) {
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
    }

    /**
     * check if phone is valid (10 digits only)
     */
    private boolean isValidPhone(String phone) {
        return phone.matches("\\d{10}");
    }

    /**
     * show styled message inside message container
     */
    private void showMessage(String text, String type) {
        messageContainer.getChildren().clear();
        Label msg = new Label(text);
        msg.setStyle(getStyleForType(type));
        messageContainer.getChildren().add(msg);
    }

    /**
     * clear message container
     */
    private void clearMessage() {
        messageContainer.getChildren().clear();
    }

    /**
     * css for success or error msg
     */
    private String getStyleForType(String type) {
        if ("success".equals(type)) {
            return "-fx-background-color: rgba(144, 238, 144, 0.4);" +
                   "-fx-text-fill: #006400;" +
                   "-fx-font-weight: bold;" +
                   "-fx-padding: 12;" +
                   "-fx-border-color: #006400;" +
                   "-fx-border-width: 1;" +
                   "-fx-background-radius: 6;" +
                   "-fx-border-radius: 6;";
        } else {
            return "-fx-background-color: rgba(255, 99, 71, 0.3);" +
                   "-fx-text-fill: #990000;" +
                   "-fx-font-weight: bold;" +
                   "-fx-padding: 12;" +
                   "-fx-border-color: #990000;" +
                   "-fx-border-width: 1;" +
                   "-fx-background-radius: 6;" +
                   "-fx-border-radius: 6;";
        }
    }

    /**
     * go back to previous page (worker page)
     */
    @FXML
    private void handleBack() {
        Main.goBackToPreviousScene();
    }
}
