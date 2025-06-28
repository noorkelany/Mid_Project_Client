import java.io.InputStream;

import data.StaffSession;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainPageController {

	@FXML
	private TextArea infoArea;

	@FXML
	private ImageView logoImage;
	@FXML
	private Button loginBtn;
	

	@FXML
	public void initialize() {
		try {
			InputStream is = getClass().getResourceAsStream("logo_bpark.jpg");
			if (is == null) {
				System.out.println("⚠️ Failed to load image: Input stream is null");
			} else {
				logoImage.setImage(new Image(is));
			}

			// המשך האתחול
			Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
				@Override
				public void display(Object message) {
					Platform.runLater(() -> {
						if (message instanceof String str) {
							infoArea.setText(str);
						}
					});
				}
			};
			if (SubscriberSession.getSubscriber() != null || StaffSession.getInstance().getUsername() != null) {
				System.out.println(SubscriberSession.getSubscriber() + " " + StaffSession.getInstance().getUsername());
				//loginBox.setVisible(false);

			}
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleSubscriberHomePage() {
		try {
			Main.switchScene("SubscriberMain.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleConnectionInfo() {
		Main.clientConsole.accept("clientDetails");
	}

	public void handleGeneratingReport1() {
		try {
			Main.switchScene("MonthlyReportView.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//handleGeneratingReport1
	public void handleGeneratingReport2() {
		try {
			Main.switchScene("parkingSpotReport.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void DeliveryButton(ActionEvent event) {
		try {
			Main.switchScene("parkingSpotPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void handleOrderParkingSpot() {
		try {
			Main.switchScene("MakingOrder.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleLogin() {
		try {
			Main.switchScene("SubscriberLogin.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void workerBtn(ActionEvent event) {
		try {
			Main.switchScene("workerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void handleManagerBtn(ActionEvent event) {
		try {
			Main.switchScene("managerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void handleUpdate() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderIDPage.fxml"));
			Parent root = loader.load();

			Stage newWindow = new Stage();
			newWindow.setTitle("Enter Order ID");
			newWindow.setScene(new Scene(root));
			newWindow.initOwner(((Stage) infoArea.getScene().getWindow()));
			newWindow.initModality(Modality.APPLICATION_MODAL); // blocks main window
			newWindow.showAndWait(); // waits until this window closes

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
