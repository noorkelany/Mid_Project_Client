
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

		// Override ClientConsole to capture server response
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

	@FXML
	public void goBack() {
		Stage currentStage = (Stage) orderNumberField.getScene().getWindow();
		currentStage.setScene(Main.previousScene);
		currentStage.setTitle("Order Lookup");
		currentStage.show();
	}


}
