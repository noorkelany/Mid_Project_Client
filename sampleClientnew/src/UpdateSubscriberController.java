import data.Subscriber;
import data.SubscriberSession;
import data.ResponseWrapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateSubscriberController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    private boolean showingPassword = false;

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

        // האזנה לתגובה מהשרת
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                if (message instanceof ResponseWrapper response && "UpdateSubscriberResult".equals(response.getType())) {
                    boolean success = (boolean) response.getData();
                    Platform.runLater(() -> {
                        Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
                        alert.setTitle("Update Status");
                        alert.setHeaderText(null);
                        alert.setContentText(success ? "Details updated successfully." : "Failed to update details.");
                        alert.showAndWait();

                        if (success) {
                            Subscriber updated = new Subscriber(
                                    usernameField.getText(),
                                    showingPassword ? visiblePasswordField.getText() : passwordField.getText(),
                                    phoneField.getText(),
                                    emailField.getText(),
                                    SubscriberSession.getSubscriber().getCode()
                            );
                            SubscriberSession.setSubscriber(updated);
                        }
                    });
                }
            }
        };
    }

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

    @FXML
    private void handleSave() {
        String username = usernameField.getText();
        String password = showingPassword ? visiblePasswordField.getText() : passwordField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();

        Subscriber updated = new Subscriber(username, password, phone, email, SubscriberSession.getSubscriber().getCode());
        Main.clientConsole.accept(new ResponseWrapper("UpdateSubscriber", updated));
    }

    @FXML
    private void handleLogout() {
        try {
        	SubscriberSession.setSubscriber(null);
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSubscriberOrders() {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @FXML
    private void handleUpdateSubscriber() {
        try {
            Main.switchScene("UpdateSubscriber.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	@FXML
	public void carDeliveryBtnClicked() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CarDelivery2.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root); // ← קודם יוצרים את הסצנה

			// ורק לאחר מכן מוסיפים CSS
			scene.getStylesheets().add(getClass().getResource("CarDelivery.css").toExternalForm());

			Stage stage = new Stage();
			stage.setTitle("Car Delivery");
			stage.setScene(scene);
			stage.show();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void handleRegister() {
		try {
			Main.switchScene("ReceivingCarPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	@FXML
	void handleExtendsParkingTime() {
		try {
			Main.switchScene("ExtendParkingTime.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@FXML
	void handleOrderParkingSpot()
	{
		try {
			Main.switchScene("MakingOrder.fxml");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}


