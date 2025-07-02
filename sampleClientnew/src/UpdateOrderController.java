import java.sql.Date;
import java.time.LocalDate;

import data.Order;
import data.UpdateOrderDetails;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for updating an existing order.
 * <p>Allows the user to modify the parking space and order placement date.
 * Validates inputs and sends updates to the server.</p>
 */
public class UpdateOrderController {

    /** Text field for displaying or editing the order number */
    @FXML
    private TextField orderNumberField;

    /** Text field for editing the assigned parking space */
    @FXML
    private TextField parkingSpaceField;

    /** DatePicker to choose a new order date */
    @FXML
    private DatePicker datePicker;

    /** Label used to show status messages (success or error) */
    @FXML
    private Label statusLabel;

    /** Reference to the main application (optional) */
    private Main mainApp;

    /** The order being modified */
    private Order currentOrder;

    /**
     * Sets the main application reference (used for navigation or future expansion).
     * @param mainApp the main application instance
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes the controller:
     * <ul>
     *     <li>Disables past dates in the date picker</li>
     *     <li>Sets the default selected date</li>
     *     <li>Overrides the client console to receive server messages</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        // Disable past dates
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                }
            }
        });

        datePicker.setValue(LocalDate.now());

        // Listen for response from server
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                Platform.runLater(() -> {
                    if (message instanceof String str) {
                        statusLabel.setText(str);
                        if (str.toLowerCase().contains("invalid")) {
                            statusLabel.setStyle("-fx-text-fill: red;");
                        } else if (str.toLowerCase().contains("success")) {
                            statusLabel.setStyle("-fx-text-fill: green;");
                        } else {
                            statusLabel.setStyle("-fx-text-fill: black;");
                        }
                    }
                });
            }
        };
    }

    /**
     * Loads order details into the UI fields for editing.
     * @param order the order to update
     */
    public void loadOrder(Order order) {
        try {
            this.currentOrder = order;
            this.orderNumberField.setText(Integer.toString(order.getOrder_number()));
            this.parkingSpaceField.setText(Integer.toString(order.getParking_space()));
            this.datePicker.setPromptText(order.getDate_of_placing_an_order().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Validates and sends updated order details to the server.
     * Displays success or error messages based on user input and server response.
     */
    public void handleUpdate() {
        try {
            int orderNumber = Integer.parseInt(orderNumberField.getText().trim());
            int parkingSpace = Integer.parseInt(parkingSpaceField.getText().trim());
            LocalDate localDate = datePicker.getValue();

            if (localDate == null) {
                statusLabel.setText("Please select a date.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            Date sqlDate = Date.valueOf(localDate);
            UpdateOrderDetails update = new UpdateOrderDetails(orderNumber, parkingSpace, sqlDate);
            Main.clientConsole.accept(update);
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid number format.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }

    /**
     * Handles the back button action, returning to the previous scene.
     */
    @FXML
    public void goBack() {
        Stage currentStage = (Stage) orderNumberField.getScene().getWindow();
        currentStage.setScene(Main.previousScene);
        currentStage.setTitle("Order Lookup");
        currentStage.show();
    }
}
