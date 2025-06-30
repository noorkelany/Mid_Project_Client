import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import java.util.ResourceBundle;

import data.EmailSender;
import data.ParkingSpotsSession;
import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

/**
 * Controller class for handling the "Make Order" screen in the parking system.
 * <p>
 * This class manages date/time selection, displays available parking spots,
 * handles user orders, and communicates with the server.
 * </p>
 */
public class MakingOrderController implements Initializable {

	/** Picker for selecting the desired parking date */
	@FXML
	private DatePicker datePicker;

	/** Dropdown for selecting the start hour */
	@FXML
	private ComboBox<String> startHourComboBox;

	/** Dropdown for selecting the end hour */
	@FXML
	private ComboBox<String> endHourComboBox;

	/** Label for displaying status messages (e.g., errors, confirmations) */
	@FXML
	private Label statusLabel;

	/** Scroll pane containing the grid of parking spots */
	@FXML
	private ScrollPane parkingScroll;

	/** Grid to display available parking spots */
	@FXML
	private GridPane grid;

	/** Button to finalize placing an order */
	@FXML
	private Button placeOrderButton;

	/** Button to request available parking spots */
	@FXML
	private Button orderButton;

	/** ID of the parking spot selected by the user */
	private Integer selectedSpotId = null;

	/**
	 * Initializes the UI components and sets up the server listener for responses.
	 *
	 * @param location  the location used to resolve relative paths for the root
	 *                  object
	 * @param resources the resources used to localize the root object
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (SubscriberSession.getSubscriber() == null) {
			placeOrderButton.setVisible(false);
		}

		// Set allowed date range for orders (1–7 days ahead)
		LocalDate now = LocalDate.now();
		LocalDate minDate = now.plusDays(1);
		LocalDate maxDate = now.plusDays(7);
		datePicker.setDayCellFactory(getDateCellFactory(minDate, maxDate));
		datePicker.setValue(minDate);

		// Populate time dropdowns with 24-hour format options
		for (int i = 0; i < 24; i++) {
			String hour = String.format("%02d:00", i);
			startHourComboBox.getItems().add(hour);
			endHourComboBox.getItems().add(hour);
		}

		// Default selections
		startHourComboBox.getSelectionModel().selectFirst();
		endHourComboBox.getSelectionModel().selectLast();

		placeOrderButton.setDisable(true);

		// Setup server listener for messages
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
							placeOrderButton.setDisable(true);

							String content = "Order created successfully, your code: " + code;
							EmailSender.sendEmail(SubscriberSession.getSubscriber().getEmail(), content);

							// Pause before redirecting to main screen
							PauseTransition delay = new PauseTransition(Duration.seconds(3));
							delay.setOnFinished(event -> {
								try {
									Main.switchScene("SubscriberMain.fxml");
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
							delay.play();
						}
					}
				});
			}
		};
	}

	/**
	 * Returns a DateCell factory to disable unavailable date selections.
	 *
	 * @param minDate the earliest selectable date
	 * @param maxDate the latest selectable date
	 * @return callback for customizing DatePicker cells
	 */
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

	/**
	 * Handles the action of the "Order" button.
	 * <p>
	 * Validates time input and sends a request to fetch available parking spots.
	 * </p>
	 */
	public void handleOrder() {
		LocalDate date = datePicker.getValue();
		LocalTime start = LocalTime.parse(startHourComboBox.getValue());
		LocalTime end = LocalTime.parse(endHourComboBox.getValue());

		if (!start.isBefore(end)) {
			statusLabel.setText("Start time must be before end time.");
			statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			return;
		}

		ResponseWrapper timeRequest = new ResponseWrapper("TIME", start, end);
		ResponseWrapper request = new ResponseWrapper("REQUEST_ORDER", date, timeRequest);
		Main.clientConsole.accept(request);
	}

	/**
	 * Renders the parking spot availability grid based on the server response.
	 *
	 * @param spotMap a map of parking spot IDs and their availability status
	 */
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

	/**
	 * Handles the action of the "Place Order" button.
	 * <p>
	 * Constructs and sends the final order request to the server.
	 * </p>
	 */
	public void handlePlaceOrder() {
		if (selectedSpotId != null) {
			System.out.println("Confirmed order for spot ID: " + selectedSpotId);
			LocalDate date = datePicker.getValue();
			LocalTime start = LocalTime.parse(startHourComboBox.getValue());
			LocalTime end = LocalTime.parse(endHourComboBox.getValue());

			ResponseWrapper timeRequest = new ResponseWrapper("TIME", start, end);
			ResponseWrapper details = new ResponseWrapper("REQUEST_ORDER", date, timeRequest);
			ResponseWrapper rsp = new ResponseWrapper("ORDER_DETAILS", selectedSpotId, details);
			ResponseWrapper response = new ResponseWrapper("ORDER_DETAILS", rsp, SubscriberSession.getSubscriber());
			Main.clientConsole.accept(response);
		}
	}

	/**
	 * Handles the back navigation when the user clicks the "Back" button.
	 *
	 * @param event the action event triggered by the button
	 */
	public void handleBack(ActionEvent event) {
		try {
			String previousScene = "MainPage.fxml";
			if (SubscriberSession.getSubscriber() != null)
				previousScene = "SubscriberMain.fxml";
			Parent previousRoot = FXMLLoader.load(getClass().getResource(previousScene));
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(previousRoot));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
