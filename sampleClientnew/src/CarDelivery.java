import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import data.Car;
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

	Subscriber subscriber;
	int parkingCode,code,confirmationCode;
	
	private Car car;
	private int extraHours;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	
	public void setOrder(Order order) {
		//this.parkingCode = order.getParking_space();
		this.code = order.getSubscriber_id();
	}

	public static int generateConfirmationCode(int parkingCode) {
	    return parkingCode * 73 + 1234;
	}

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
	        emptyLabels.setVisible(false);
	        carNumberField.getStyleClass().remove("error-border");
	        modelField.getStyleClass().remove("error-border");
	        yearField.getStyleClass().remove("error-border");

	        // שמור את פרטי הרכב כ-state עד שנקבל parkingCode ו-confirmationCode
	        this.car = new Car(carNumber, model, year);
	        this.extraHours = hourSpinner.getValue();
	        this.startTime = LocalDateTime.now();
	        this.endTime = startTime.plusHours(extraHours);
	        Order fullOrder = new Order(code, 0, startTime, extraHours, endTime, car, 0);
	        Main.clientConsole.accept(new ResponseWrapper("DELIVERYCAR", fullOrder));


	    } else {
	        emptyLabels.setText("You have empty fields.");
	        emptyLabels.setVisible(!allFieldsFilled);

	        if (!isCarNumberValid && !carNumberField.getStyleClass().contains("error-border"))
	            carNumberField.getStyleClass().add("error-border");

	        if (!isModelValid && !modelField.getStyleClass().contains("error-border"))
	            modelField.getStyleClass().add("error-border");

	        if (!isYearValid && !yearField.getStyleClass().contains("error-border"))
	            yearField.getStyleClass().add("error-border");
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// min = 4, max = 12, initial (default) = 4
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(4, 12, 4);
		hourSpinner.setValueFactory(valueFactory);
		
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
			    Platform.runLater(() -> {

			        if (message instanceof ResponseWrapper response) {
			        	if (response.getType().equals("SUCCESS_DELIVERY")) {
			        	    Order order = (Order) response.getData();
			        	    parkingCode = order.getParking_space();
			        	    confirmationCode = order.getConfirmation_code();

			        	    System.out.printf("parkingCode=%d\n", parkingCode);
			        	    System.out.printf("confirmationCode=%d\n", confirmationCode);

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

			            //if (status == SystemStatus.SUCCESS_DELIVERY) {
			                // כבר טופל דרך ה-ResponseWrapper, לא נדרש כאן כלום
			                // (אפשר למחוק את כל הבלוק הזה או להשאיר הערה)
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

