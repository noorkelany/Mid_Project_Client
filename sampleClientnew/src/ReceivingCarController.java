import data.EmailSender;
import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

/**
 * Controller for the Receiving Car page.
 * Allows sending confirmation codes and retrieving forgotten codes via Email or SMS.
 */
public class ReceivingCarController {

    @FXML private Button btnExit;
    @FXML private Button btnSend;
    @FXML private Button btnForgotCode;
    @FXML private TextField parkingCodetxt;
    @FXML private Label forgotCodeLabel;
    @FXML private Button btnSMS;
    @FXML private Button btnEmail;
    @FXML private Label forgotCodeTitle;

    // NEW: UI elements for styled message box
    @FXML private VBox messageBox;
    @FXML private Label messageLabel;

    private Main mainApp;
    private int parkingCode = -1;
    private String currentMode = "";

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes the client and handles server responses.
     */
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
                                                showStyledMessage("Navigation failed.", "#B00020");
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
                                    showStyledMessage("Failed to retrieve code.", "#B00020");
                                } else {
                                    String message = "Your parking code: " + parkingCode;
                                    if (currentMode.equals("email")) {
                                        EmailSender.sendEmail(SubscriberSession.getSubscriber().getEmail(), message);
                                        showStyledMessage("Code sent successfully via Email!", "#007700");
                                        forgotCodeTitle.setText("");
                                    } else if (currentMode.equals("sms")) {
                                        showSmsPopup(message);
                                        showStyledMessage("Code sent successfully via SMS!", "#007700");
                                    }
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showStyledMessage("Error occurred.", "#B00020");
                    }
                });
            }
        };
    }

    private String getParkingCode() {
        return parkingCodetxt.getText();
    }

    /**
     * Sends the entered parking code to the server to retrieve the car.
     */
    public void Send(ActionEvent event) {
        String parkingCode = getParkingCode().trim();
        if (parkingCode.isEmpty()) {
            showStyledMessage("You must enter a confirmation code first", "#B00020");
            return;
        }

        ResponseWrapper parkingCodeRsp = new ResponseWrapper("PARKINGCODE", parkingCode,
                SubscriberSession.getSubscriber().getCode());
        Main.clientConsole.accept(parkingCodeRsp);
    }

    public void ForgotCode(ActionEvent event) throws Exception {
        Main.switchScene("forgotCodePage.fxml");
    }

    /**
     * Triggered when user clicks "By Email" button.
     * Sends request to server to retrieve the code and send by email.
     */
    public void SendByEmail(ActionEvent event) {
        currentMode = "email";
        forgotCodeTitle.setText("Sending...");
        forgotCodeTitle.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");

        int code = SubscriberSession.getSubscriber().getCode();
        ResponseWrapper rsp = new ResponseWrapper("getParkingCodeForSubscriber", code);
        Main.clientConsole.accept(rsp);
    }

    /**
     * Triggered when user clicks "By SMS" button.
     * Sends request to server to retrieve the code and simulate SMS popup.
     */
    public void SendBySMS(ActionEvent event) {
        currentMode = "sms";
        int code = SubscriberSession.getSubscriber().getCode();
        ResponseWrapper rsp = new ResponseWrapper("getParkingCodeForSubscriber", code);
        Main.clientConsole.accept(rsp);
    }

    /**
     * Displays a modal popup to simulate SMS message.
     */
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

    /**
     * Styled message box for displaying success/error messages.
     * @param message  The message to display.
     * @param colorHex The text color (e.g. "#007700" for green).
     */
    private void showStyledMessage(String message, String colorHex) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold; -fx-font-size: 14px;");
        messageBox.setVisible(true);
        messageBox.setManaged(true);
    }

    /**
     * Navigates back to the main subscriber screen.
     */
    public void handleBackToMainPage(ActionEvent event) {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
