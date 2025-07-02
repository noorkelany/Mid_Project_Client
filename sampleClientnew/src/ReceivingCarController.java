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

/**
 * Controller for the Receiving Car page. Allows subscribers to:
 * <ul>
 *     <li>Send confirmation codes for retrieving parked cars</li>
 *     <li>Retrieve forgotten codes via email or SMS</li>
 *     <li>Handle UI feedback and navigation</li>
 * </ul>
 */
public class ReceivingCarController {

    /** Button to exit the view */
    @FXML private Button btnExit;

    /** Button to send the confirmation code */
    @FXML private Button btnSend;

    /** Button to initiate code recovery */
    @FXML private Button btnForgotCode;

    /** Text field to enter the parking confirmation code */
    @FXML private TextField parkingCodetxt;

    /** Label to show messages such as status or errors */
    @FXML private Label forgotCodeLabel;

    /** Button to send code via SMS */
    @FXML private Button btnSMS;

    /** Button to send code via Email */
    @FXML private Button btnEmail;

    /** Label indicating the current operation like "Sending..." */
    @FXML private Label forgotCodeTitle;

    /** Reference to the main application instance */
    private Main mainApp;

    /** Stores the retrieved parking code */
    private int parkingCode = -1;

    /** Current sending mode ("email" or "sms") */
    private String currentMode = "";

    /**
     * Sets the main application reference (not used here, but allows future extension).
     * @param mainApp The main application instance.
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes the controller by setting up the client console
     * to handle responses such as retrieving parking code or freeing parking space.
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

    /**
     * Retrieves the text entered in the parking code field.
     * @return the entered parking code string
     */
    private String getParkingCode() {
        return parkingCodetxt.getText();
    }

    /**
     * Sends the entered parking code to the server for car retrieval.
     * Displays error message if input is empty.
     * @param event the triggered ActionEvent
     */
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

    /**
     * Navigates to the page for code recovery (e.g., forgotCodePage).
     * @param event the triggered ActionEvent
     * @throws Exception if scene switch fails
     */
    public void ForgotCode(ActionEvent event) throws Exception {
        Main.switchScene("forgotCodePage.fxml");
    }

    /**
     * Sends the parking code to the subscriber's email.
     * Triggers a server request to retrieve the code.
     * @param event the triggered ActionEvent
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
     * Simulates sending the parking code via SMS using a popup.
     * Triggers a server request to retrieve the code.
     * @param event the triggered ActionEvent
     */
    public void SendBySMS(ActionEvent event) {
        currentMode = "sms";
        int code = SubscriberSession.getSubscriber().getCode();
        ResponseWrapper rsp = new ResponseWrapper("getParkingCodeForSubscriber", code);
        Main.clientConsole.accept(rsp);
    }

    /**
     * Displays a popup simulating SMS delivery, then returns to the receiving car page.
     * @param message the message to display
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
     * Navigates back to the main subscriber page.
     * @param event the triggered ActionEvent
     */
    public void handleBackToMainPage(ActionEvent event) {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
