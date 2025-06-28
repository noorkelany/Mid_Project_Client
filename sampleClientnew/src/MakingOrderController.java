import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.ResourceBundle;

import data.ParkingSpotsSession;
import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MakingOrderController implements Initializable {

	@FXML
	private DatePicker datePicker;

	@FXML
	private ComboBox<String> startHourComboBox;

	@FXML
	private ComboBox<String> endHourComboBox;

	@FXML
	private Label statusLabel;

	@FXML
	private ScrollPane parkingScroll;

	@FXML
	private GridPane grid;

	@FXML
	private Button placeOrderButton;

	private Integer selectedSpotId = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (SubscriberSession.getSubscriber() == null) {
			placeOrderButton.setVisible(false);
		}
		LocalDate now = LocalDate.now();
		LocalDate minDate = now.plusDays(1);
		LocalDate maxDate = now.plusDays(7);

		datePicker.setDayCellFactory(getDateCellFactory(minDate, maxDate));
		datePicker.setValue(minDate);

		for (int i = 0; i < 24; i++) {
			String hour = String.format("%02d:00", i);
			startHourComboBox.getItems().add(hour);
			endHourComboBox.getItems().add(hour);
		}
		startHourComboBox.getSelectionModel().selectFirst();
		endHourComboBox.getSelectionModel().selectLast();

		placeOrderButton.setDisable(true);

		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object msg) {
				Platform.runLater(() -> {
					if (msg instanceof ResponseWrapper rsp && "PARKING_LIST".equals(rsp.getType())) {
						grid.getChildren().clear();
						selectedSpotId = null;
						placeOrderButton.setDisable(true);

						if (rsp.getData() == null || ((Map<?, ?>) rsp.getData()).isEmpty()) {
							statusLabel.setText("No available parking spots for this time.");
							statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
						} else {
							statusLabel.setText("");
							@SuppressWarnings("unchecked")
							Map<Integer, Boolean> map = (Map<Integer, Boolean>) rsp.getData();
							ParkingSpotsSession.setMap(map);
							renderMap(map);
						}
					} else if (msg instanceof ResponseWrapper rsp && "CONFIRMATION_CODE".equals(rsp.getType())) {
						if (rsp.getData() == null) {
							statusLabel.setText("Order cannot be created.");
							statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
						} else {
							int code = Integer.parseInt(rsp.getData().toString());
							System.out.println(code);
							statusLabel.setText(
									"Order created successfully, your code: " + code + " sent to your email too");
							statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
							// send email
							/*
							 * try { Thread.sleep(5000); try { Main.switchScene("MainPage.fxml"); } catch
							 * (Exception e) { // TODO Auto-generated catch block e.printStackTrace(); } }
							 * catch (InterruptedException e) { // TODO Auto-generated catch block
							 * e.printStackTrace(); }
							 */

						}
					}
				});
			}
		};
	}

	private Callback<DatePicker, DateCell> getDateCellFactory(LocalDate minDate, LocalDate maxDate) {
		return picker -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (item.isBefore(minDate) || item.isAfter(maxDate)) {
					setDisable(true);
					setStyle("-fx-background-color: #ffc0cb;");
				}
			}
		};
	}

	public void handleOrder() {
		LocalDate date = datePicker.getValue();
		LocalTime start = LocalTime.parse(startHourComboBox.getValue());
		LocalTime end = LocalTime.parse(endHourComboBox.getValue());

		ResponseWrapper timeRequest = new ResponseWrapper("TIME", start, end);
		ResponseWrapper request = new ResponseWrapper("REQUEST_ORDER", date, timeRequest);
		Main.clientConsole.accept(request);
	}

	private void renderMap(Map<Integer, Boolean> spotMap) {
		grid.getChildren().clear();
		selectedSpotId = null;
		placeOrderButton.setDisable(true);

		int col = 0, row = 0, maxCols = 5;

		for (Map.Entry<Integer, Boolean> entry : spotMap.entrySet()) {
			int spotId = entry.getKey();
			boolean isFree = entry.getValue();

			Circle spotCircle = new Circle(20);
			spotCircle.setFill(isFree ? Color.GREEN : Color.RED);

			Label label = new Label(String.valueOf(spotId));
			HBox cell = new HBox(5, spotCircle, label);
			grid.add(cell, col, row);

			if (isFree) {
				spotCircle.setCursor(Cursor.HAND);
				spotCircle.setOnMouseClicked(e -> {
					selectedSpotId = spotId;
					statusLabel.setText("Selected spot: " + spotId);
					statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
					placeOrderButton.setDisable(false);
				});
			}

			col++;
			if (col >= maxCols) {
				col = 0;
				row++;
			}
		}
	}

	public void handlePlaceOrder() {
		if (selectedSpotId != null) {
			System.out.println("Confirmed order for spot ID: " + selectedSpotId);
			LocalDate date = datePicker.getValue();
			LocalTime start = LocalTime.parse(startHourComboBox.getValue());
			LocalTime end = LocalTime.parse(endHourComboBox.getValue());

			ResponseWrapper timeRequest = new ResponseWrapper("TIME", start, end);
			ResponseWrapper details = new ResponseWrapper("REQUEST_ORDER", date, timeRequest);
			ResponseWrapper rsp = new ResponseWrapper("ORDER_DETAILS", selectedSpotId, details);
			Main.clientConsole.accept(rsp);

		}
	}

	public void handleBack(ActionEvent event) {
		try {
			// Load the previous FXML
			Parent previousRoot = FXMLLoader.load(getClass().getResource("SubscriberMain.fxml"));
			// Get current stage from any control (e.g. the button)
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			// Replace the scene in the same window
			stage.setScene(new Scene(previousRoot));
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * try { Main.switchScene("MainPage.fxml"); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
	}
}
