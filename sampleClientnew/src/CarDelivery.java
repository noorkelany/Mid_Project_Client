import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import data.Car;
import data.EmailSender;
import data.Order;
import data.ResponseWrapper;
import data.Subscriber;
import data.SystemStatus;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CarDelivery implements Initializable {

	@FXML
	private TextField modelField, yearField, carNumberField;

	@FXML
	private Label emptyLabels, successLabel;

	@FXML
	private Spinner<Integer> hourSpinner;

	@FXML
	private Label carNumberLabel;
	@FXML
	private Label yearLabel;
	@FXML
	private Label modelLabel;

	Subscriber subscriber;
	int parkingCode,code,confirmationCode;

	private Car car;
	private int extraHours;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Order order1 = null; 
	/**
	 * this function set the current order
	 * @param order
	 */
	public void setOrder(Order order) {
		//this.parkingCode = order.getParking_space();
		this.code = order.getSubscriber_id();
	}
	/**
	 * this function for the subscribers want to delivery his car
	 * @param order
	 * @param sub
	 */
	public void setOrderWithCode(Order order,Subscriber sub) {
		order1 = order;
		subscriber = sub;
	}
	/**
	 * this function for generate the confirmation code
	 * @param parkingCode
	 * @return
	 */
	public static int generateConfirmationCode(int parkingCode) {
		return parkingCode * 73 + 1234;
	}
	/**
	 * this function to show alert message
	 * @param msg
	 */
	public void alert(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Message");
		alert.setHeaderText(null);
		alert.setContentText(msg);
		alert.showAndWait();
	}
	/**
	 * 
	 * @param event this function when the subscriber press to delivery button it
	 *              will send request to server with order details
	 */
	@FXML
	void DeliveryButton(ActionEvent event) {
	    String carNumber = carNumberField.getText().trim();
	    String model = modelField.getText().trim();
	    String yearText = yearField.getText().trim();

	    boolean allFieldsFilled = !carNumber.isEmpty() && !model.isEmpty() && !yearText.isEmpty();
	    boolean isCarNumberValid = Car.formatIsraeliCarNumber(carNumber) != null;
	    boolean isModelValid = Car.isValidCarModel(model);
	    boolean isYearValid = false;
	    int year = -1;

	    try {
	        year = Integer.parseInt(yearText);
	        isYearValid = Car.isValidCarYear(year);
	    } catch (NumberFormatException e) {
	        isYearValid = false;
	    }

	    if (allFieldsFilled && isCarNumberValid && isModelValid && isYearValid) {
	        //all fields is valid
	        emptyLabels.setVisible(false);

	        carNumberField.getStyleClass().remove("error-border");
	        modelField.getStyleClass().remove("error-border");
	        yearField.getStyleClass().remove("error-border");

	        carNumberLabel.setVisible(false);
	        modelLabel.setVisible(false);
	        yearLabel.setVisible(false);

	        this.car = new Car(carNumber, model, year);
	        this.extraHours = hourSpinner.getValue();
	        this.startTime = LocalDateTime.now();
	        this.endTime = startTime.plusHours(extraHours);

	        if (order1 == null) {
	            Order fullOrder = new Order(code, 0, startTime, extraHours, endTime, car, 0);
	            Main.clientConsole.accept(new ResponseWrapper("DELIVERYCAR", fullOrder));
	        } else {
	            order1.setNumberofextend(extraHours);
	            order1.setDelivery_time(endTime);
	            order1.setRecivingcartime(endTime);
	            order1.setCar(car);
	            Main.clientConsole.accept(new ResponseWrapper("DELIVERYCAR", order1));
	        }

	    } else {
	        emptyLabels.setText("You have empty fields.");
	        emptyLabels.setVisible(true);

	        // Car number
	        if (!isCarNumberValid) {
	            if (!carNumberField.getStyleClass().contains("error-border")) {
	                carNumberField.getStyleClass().add("error-border");
	            }
	            carNumberLabel.setVisible(true);
	        } else {
	            carNumberField.getStyleClass().remove("error-border");
	            carNumberLabel.setVisible(false);
	        }

	        // Model
	        if (!isModelValid) {
	            if (!modelField.getStyleClass().contains("error-border")) {
	                modelField.getStyleClass().add("error-border");
	            }
	            modelLabel.setVisible(true);
	        } else {
	            modelField.getStyleClass().remove("error-border");
	            modelLabel.setVisible(false);
	        }

	        // Year
	        if (!isYearValid) {
	            if (!yearField.getStyleClass().contains("error-border")) {
	                yearField.getStyleClass().add("error-border");
	            }
	            yearLabel.setVisible(true);
	        } else {
	            yearField.getStyleClass().remove("error-border");
	            yearLabel.setVisible(false);
	        }
	    }
	}

	/**
	 * this function -> back to main page
	 * 
	 * @param event
	 * @throws Exception
	 */
	@FXML
	public void handleBackToMainPage(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
			Parent root = loader.load();
			Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
			stage.setScene(new Scene(root));
			stage.setTitle("BPARK Dashboard");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * this function for initialize when the window will work the function will active
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// min = 4, max = 12, initial (default) = 4
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 12, 4);
		hourSpinner.setValueFactory(valueFactory);

		carNumberLabel.setVisible(false);
		modelLabel.setVisible(false);
		yearLabel.setVisible(false);

		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				Platform.runLater(() -> {

					if (message instanceof ResponseWrapper response) {
						if (response.getType().equals("SUCCESS_DELIVERY")) {
							Order order = (Order) response.getData();
							parkingCode = order.getParking_space();
							confirmationCode = order.getConfirmation_code();

							Stage stage = (Stage) successLabel.getScene().getWindow();
							stage.close();

							Platform.runLater(() -> {
								Alert alert = new Alert(Alert.AlertType.INFORMATION);
								alert.setTitle("Success");
								alert.setHeaderText(null);
								alert.setContentText(String.format(
										"✅ The car was delivered successfully.\n📍 Parking spot: %d\n🔐 Confirmation code: %d",
										parkingCode,
										confirmationCode
										));
								alert.showAndWait();
							});
						}

					}

					if (message instanceof SystemStatus status) {
						if (status == SystemStatus.NO_PARKING_SPOT) {
							emptyLabels.setText("No available parking spot.");
						} else if (status == SystemStatus.ALREADY_DELIVERED) {
							emptyLabels.setText("This subscriber already has an active delivery.");
						}

						emptyLabels.setVisible(true);
					}
				});
			}

		};
	}
}

