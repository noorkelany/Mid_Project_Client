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

public class UpdateOrderController {

	@FXML
	private TextField orderNumberField;
	@FXML
	private TextField parkingSpaceField;
	@FXML
	private DatePicker datePicker;
	@FXML
	private Label statusLabel;

	private Main mainApp;
	private Order currentOrder;

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

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

		// Set default date
		datePicker.setValue(LocalDate.now());

		// ClientConsole override to show status messages
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

	public void handleUpdate() {
		try {
			String orderStr = orderNumberField.getText().trim();
			String spaceStr = parkingSpaceField.getText().trim();
			LocalDate localDate = datePicker.getValue();

			if (orderStr.isEmpty() || spaceStr.isEmpty() || localDate == null) {
				showStatusMessage("All fields must be filled.");
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


	@FXML
	public void goBack() {
		Stage currentStage = (Stage) orderNumberField.getScene().getWindow();
		currentStage.setScene(Main.previousScene);
		currentStage.setTitle("Order Lookup");
		currentStage.show();
	}
}
