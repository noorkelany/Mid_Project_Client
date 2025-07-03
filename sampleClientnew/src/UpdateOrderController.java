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
 * Controller class for the Update Order screen.
 * Allows the user to edit the order number, parking space, and order date.
 * Validates input and sends update request to the server.
 */
public class UpdateOrderController {

    /** Text field for displaying/editing the order number */
    @FXML
    private TextField orderNumberField;

    /** Text field for editing the assigned parking space */
    @FXML
    private TextField parkingSpaceField;

    /** DatePicker to choose the updated order date */
    @FXML
    private DatePicker datePicker;

    /** Label for displaying success or error messages */
    @FXML
    private Label statusLabel;

    /** Stores the current order being edited */
    private Order currentOrder;

    /**
     * Initializes the controller.
     * Disables past dates in the date picker and sets up the client console listener
     * to receive server response messages.
     */
    @FXML
    public void initialize() {
        /* Disable past dates in the date picker */
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

        /* Set today's date as the default */
        datePicker.setValue(LocalDate.now());

        /* Set up listener for server messages */
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                Platform.runLater(() -> {
                    if (message instanceof String str) {
                        showStatusMessage(str);
                    }
                });
            }
        };
    }

    /**
     * Loads an existing order's data into the form for editing.
     * 
     * @param order The order to load into the update form
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
     * Handles the update button action.
     * Validates the user input and sends the updated order details to the server.
     */
    public void handleUpdate() {
        try {
            String orderStr = orderNumberField.getText().trim();
            String spaceStr = parkingSpaceField.getText().trim();
            LocalDate localDate = datePicker.getValue();

            if (orderStr.isEmpty() || spaceStr.isEmpty() || localDate == null) {
                showStatusMessage("All fields must be filled.");
                return;
            }

            /* Explicit null check for safety */
            if (localDate == null) {
                statusLabel.setText("Please select a date.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            int orderNumber = Integer.parseInt(orderStr);
            int parkingSpace = Integer.parseInt(spaceStr);
            Date sqlDate = Date.valueOf(localDate);

            UpdateOrderDetails update = new UpdateOrderDetails(orderNumber, parkingSpace, sqlDate);
            Main.clientConsole.accept(update);

        } catch (NumberFormatException e) {
            showStatusMessage("Invalid number format.");
        } catch (Exception e) {
            showStatusMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Displays a styled status message in the statusLabel.
     * Green background for success, red for error.
     * 
     * @param message The message to display
     */
    private void showStatusMessage(String message) {
        statusLabel.setVisible(true);
        statusLabel.setText(message);

        if (message.toLowerCase().contains("success")) {
            statusLabel.setStyle(
                "-fx-text-fill: green;" +
                "-fx-background-color: #d4edda;" +
                "-fx-padding: 10;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: green;"
            );
        } else {
            statusLabel.setStyle(
                "-fx-text-fill: red;" +
                "-fx-background-color: #f8d7da;" +
                "-fx-padding: 10;" +
                "-fx-border-radius: 6;" +
                "-fx-background-radius: 6;" +
                "-fx-border-color: red;"
            );
        }
    }

    /**
     * Handles the "Back" button action.
     * Returns the user to the previous screen.
     */
    @FXML
    public void goBack() {
        Stage currentStage = (Stage) orderNumberField.getScene().getWindow();
        currentStage.setScene(Main.previousScene);
        currentStage.setTitle("Order Lookup");
        currentStage.show();
    }
}
