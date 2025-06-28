import data.EmailSender;
import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReceivingCarController {

	@FXML
	private Button btnExit;
	@FXML
	private Button btnSend;
	@FXML
	private Button btnForgotCode;
	@FXML
	private TextField parkingCodetxt;
	@FXML
	private Label forgotCodeLabel;
	@FXML
	private Button btnSMS;
	@FXML
	private Button btnEmail;
	@FXML
	private Label forgotCodeTitle;

	private Main mainApp;
	private int parkingCode = -1;
	private String currentMode = "";

	public void setMainApp(Main mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	public void initialize() {
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object role) {
				Platform.runLater(() -> {
					try {
						ResponseWrapper rsp = (ResponseWrapper) role;
						String responseType = rsp.getType();

						switch (responseType) {
						case "EMPTYPARKINGSPACE":
							forgotCodeLabel.setVisible(true);
							forgotCodeLabel.setText("Car on the way...");
							forgotCodeLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

							new Thread(() -> {
								try {
									Thread.sleep(4000);
									Platform.runLater(() -> {
										try {
											Main.switchScene("MainPage.fxml");
										} catch (Exception e) {
											e.printStackTrace();
											forgotCodeLabel.setText("Navigation failed.");
											forgotCodeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
										}
									});
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}).start();
							break;

						case "RetreivedParkingCode":
							parkingCode = Integer.parseInt(rsp.getData().toString());
							if (parkingCode == -1) {
								forgotCodeLabel.setText("Failed to retrieve code.");
								forgotCodeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
							} else {
								String message = "Your parking code: " + parkingCode;
								if (currentMode.equals("email")) {
									EmailSender.sendEmail(SubscriberSession.getSubscriber().getEmail(), message);
									forgotCodeLabel.setText("Code sent successfully.");
									forgotCodeLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
									forgotCodeTitle.setText("");
								} else if (currentMode.equals("sms")) {
									showSmsPopup(message);
								}
							}
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
						forgotCodeLabel.setText("Error occurred.");
						forgotCodeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
					}
				});
			}
		};
	}

	private String getParkingCode() {
		return parkingCodetxt.getText();
	}

	public void Send(ActionEvent event) {
		String parkingCode = getParkingCode().trim();
		if (parkingCode.isEmpty()) {
			forgotCodeLabel.setText("You must enter a confirmation code first");
			forgotCodeLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
			return;
		}
		ResponseWrapper parkingCodeRsp = new ResponseWrapper("PARKINGCODE", parkingCode,
				SubscriberSession.getSubscriber().getCode());
		Main.clientConsole.accept(parkingCodeRsp);
	}

	public void ForgotCode(ActionEvent event) throws Exception {
		Main.switchScene("forgotCodePage.fxml");
	}

	public void SendByEmail(ActionEvent event) {
		currentMode = "email";
		forgotCodeTitle.setText("Sending...");
		forgotCodeTitle.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

		int code = SubscriberSession.getSubscriber().getCode();
		ResponseWrapper rsp = new ResponseWrapper("getParkingCodeForSubscriber", code);
		Main.clientConsole.accept(rsp);
	}

	public void SendBySMS(ActionEvent event) {
		currentMode = "sms";

		int code = SubscriberSession.getSubscriber().getCode();
		ResponseWrapper rsp = new ResponseWrapper("getParkingCodeForSubscriber", code);
		Main.clientConsole.accept(rsp);
	}

	private void showSmsPopup(String message) {
		Stage popupStage = new Stage();
		Label messageLabel = new Label(message);
		messageLabel.setStyle("-fx-font-size: 14px; -fx-padding: 20; -fx-text-fill: black;");
		Scene popupScene = new Scene(new StackPane(messageLabel), 300, 100);
		popupStage.setScene(popupScene);
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("SMS Simulation");
		popupStage.show();

		new Thread(() -> {
			try {
				Thread.sleep(4000);
				Platform.runLater(() -> {
					popupStage.close();
					try {
						Main.switchScene("ReceivingCarPage.fxml");
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void handleBackToMainPage(ActionEvent event) {
		try {
		/*// Load the previous FXML
		Parent previousRoot = FXMLLoader.load(getClass().getResource("SubscriberMain.fxml"));

		// Get current stage from any control (e.g. the button)
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		// Replace the scene in the same window
		stage.setScene(new Scene(previousRoot));*/
			Main.switchScene("SubscriberMain.fxml");
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}
}
