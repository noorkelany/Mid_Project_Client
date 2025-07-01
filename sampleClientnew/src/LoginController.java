import java.io.IOException;
import data.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private ImageView eyeIcon;
    @FXML private VBox messageBox;
    @FXML private Label messageLabel;

    private Image eyeOpen;
    private Image eyeClosed;
    private boolean showingPassword = false;

    @FXML
    public void initialize() {
        eyeOpen = new Image(getClass().getResourceAsStream("/images/eye_open.png"));
        eyeClosed = new Image(getClass().getResourceAsStream("/images/eye_closed.png"));
        eyeIcon.setImage(eyeClosed);

        messageBox.setVisible(false);
        messageBox.setManaged(false);

        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object role) {
                Platform.runLater(() -> {
                    try {
                        ResponseWrapper rsp = (ResponseWrapper) role;
                        switch (rsp.getType()) {
                            case "LOGIN_RESPONSE":
                                switch (rsp.getData().toString()) {
                                    case "subscriber":
                                        Subscriber sub = (Subscriber) rsp.getExtra();
                                        SubscriberSession.setSubscriber(sub);
                                        showSuccessMessage("Welcome, " + sub.getUsername() + "!", "SubscriberMain.fxml");

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
                                        showError("Invalid username or password.");
                                }
                                break;
                            default:
                                showError("Invalid username or password.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        };
    }

    private void showError(String message) {
        messageBox.setVisible(true);
        messageBox.setManaged(true);
        messageBox.getStyleClass().removeAll("success-box");
        if (!messageBox.getStyleClass().contains("error-box")) {
            messageBox.getStyleClass().add("error-box");
        }
        messageLabel.setText(message);
    }

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

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText().trim() : visiblePasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        Login login = new Login(username, password);
        ResponseWrapper rsp = new ResponseWrapper("LOGIN", login);
        Main.clientConsole.accept(rsp);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
