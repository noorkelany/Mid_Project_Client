import data.Subscriber;
import data.SubscriberSession;
import data.ResponseWrapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for updating subscriber details.
 * Allows users to edit username, password, phone number, and email.
 * Handles server communication and navigation.
 */
public class UpdateSubscriberController implements Initializable {

    /** Username field */
    @FXML private TextField usernameField;
    /** Hidden password field */
    @FXML private PasswordField passwordField;
    /** Visible password field (toggled view) */
    @FXML private TextField visiblePasswordField;
    /** Phone number field */
    @FXML private TextField phoneField;
    /** Email field */
    @FXML private TextField emailField;
    /** Label for status messages */
    @FXML private Label statusMessage;

    /** Flag indicating if password is currently shown */
    private boolean showingPassword = false;

    /**
     * Initializes form with current subscriber data and sets up server listener.
     */
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

        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                if (message instanceof ResponseWrapper response && "UpdateSubscriberResult".equals(response.getType())) {
                    boolean success = (boolean) response.getData();
                    System.out.println("Update success? " + success); // בדיקת הצלחה
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

    /**
     * Toggles between visible and hidden password fields.
     */
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

    /**
     * Sends updated subscriber details to the server.
     */
    @FXML
    private void handleSave() {
    	System.out.println("Save button clicked!");

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

    /**
     * Shows a styled status message.
     * @param text the message to display
     * @param type message type ("success", "error", etc.)
     */
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
                    "-fx-background-radius: 6;");
            case "error" -> statusMessage.setStyle(
                    "-fx-background-color: #f8d7da; " +
                    "-fx-text-fill: darkred; " +
                    "-fx-font-weight: bold; " +
                    "-fx-padding: 8; " +
                    "-fx-background-radius: 6;");
            default -> statusMessage.setStyle(
                    "-fx-background-color: #e2e3e5; " +
                    "-fx-text-fill: black; " +
                    "-fx-font-weight: normal; " +
                    "-fx-padding: 8; " +
                    "-fx-background-radius: 6;");
        }
    }
    
    /**
     * Logs the user out and navigates to login page.
     */
    @FXML
    private void handleLogout() {
        try {
            SubscriberSession.setSubscriber(null);
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the main subscriber page.
     */
    public void handleBackToMainPage(ActionEvent event) {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to subscriber home.
     */
    @FXML
    private void handleSubscriberOrders() {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reloads update subscriber screen.
     */
    @FXML
    private void handleUpdateSubscriber() {
        try {
            Main.switchScene("UpdateSubscriber.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the car delivery page.
     */
    @FXML
    public void carDeliveryBtnClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CarDelivery2.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("CarDelivery.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Car Delivery");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the receiving car screen.
     */
    public void handleRegister() {
        try {
            Main.switchScene("ReceivingCarPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the screen for extending parking time.
     */
    @FXML
    void handleExtendsParkingTime() {
        try {
            Main.switchScene("ExtendParkingTime.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the screen for placing a new parking order.
     */
    @FXML
    void handleOrderParkingSpot() {
        try {
            Main.switchScene("MakingOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
