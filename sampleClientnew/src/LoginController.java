import java.io.IOException;
import data.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * handles the login logic and scene switching based on user role.
 * we also added support for password visibility toggle and user feedback.
 */
public class LoginController {

    // input fields
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;

    // password visibility toggle
    @FXML private ImageView eyeIcon;
    private Image eyeOpen;
    private Image eyeClosed;
    private boolean showingPassword = false;

    // message box (for success and error)
    @FXML private VBox messageBox;
    @FXML private Label messageLabel;

    // extra error label used inside async callbacks
    @FXML private Label errorLabel;
    private static Label errorLabelStatic;

    @FXML
    public void initialize() {
        eyeOpen = new Image(getClass().getResourceAsStream("/images/eye_open.png"));
        eyeClosed = new Image(getClass().getResourceAsStream("/images/eye_closed.png"));
        eyeIcon.setImage(eyeClosed);

        // hide message box at start
        messageBox.setVisible(false);
        messageBox.setManaged(false);

        // static reference for internal methods
        errorLabelStatic = errorLabel;

        // init connection to server
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object response) {
                Platform.runLater(() -> handleServerResponse(response));
            }
        };
    }

    private void handleServerResponse(Object response) {
        try {
            ResponseWrapper rsp = (ResponseWrapper) response;
            if (!rsp.getType().equals("LOGIN_RESPONSE")) {
                showError("invalid response from server.");
                return;
            }

            switch (rsp.getData().toString()) {
                case "subscriber":
                    Subscriber sub = (Subscriber) rsp.getExtra();
                    SubscriberSession.setSubscriber(sub);
                    showSuccessMessage("welcome, " + sub.getUsername() + "!", "SubscriberMain.fxml");
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
                    showError("invalid username or password.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("something went wrong.");
        }
    }

    // toggle password visibility when eye icon is clicked
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

    // login button clicked
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText().trim() : visiblePasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("username and password are required.");
            return;
        }

        Login login = new Login(username, password);
        ResponseWrapper wrapper = new ResponseWrapper("LOGIN", login);
        Main.clientConsole.accept(wrapper);
    }

    // redirect to QR login scene
    @FXML
    private void handleQRLoginLink() throws Exception {
        try {
            Main.switchScene("QRCode.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // go back to main page
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

    // display error message in red box
    private void showError(String message) {
        messageBox.setVisible(true);
        messageBox.setManaged(true);
        messageBox.getStyleClass().removeAll("success-box");
        if (!messageBox.getStyleClass().contains("error-box")) {
            messageBox.getStyleClass().add("error-box");
        }
        messageLabel.setText(message);
    }

    // display success message and switch scene after 2 seconds
    private void showSuccessMessage(String message, String nextScene) {
        messageBox.setVisible(true);
        messageBox.setManaged(true);
        messageBox.getStyleClass().removeAll("error-box");
        if (!messageBox.getStyleClass().contains("success-box")) {
            messageBox.getStyleClass().add("success-box");
        }
        messageLabel.setText(message);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> {
                    try {
                        Main.switchScene(nextScene);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // fallback method for unexpected errors
    private void showLoginError() {
        if (errorLabelStatic != null) {
            errorLabelStatic.setText("invalid username or password.");
            errorLabelStatic.setStyle("-fx-text-fill: red;");
        }
    }
}
