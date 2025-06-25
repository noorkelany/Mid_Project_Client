
import java.io.IOException;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;

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
                            case "EMPTYPARKINGSPACE" -> {
                                showMessage("✅ Car on the way...", "success");
                                new Thread(() -> {
                                    try {
                                        Thread.sleep(4000);
                                        Platform.runLater(() -> {
                                            try {
                                                Main.switchScene("MainPage.fxml");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                showMessage("❌ Navigation failed.", "error");
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            }
                            case "RetreivedParkingCode" -> {
                                parkingCode = Integer.parseInt(rsp.getData().toString());
                                if (parkingCode == -1) {
                                    showMessage("❌ Failed to retrieve code.", "error");
                                } else {
                                    String message = "Your parking code: " + parkingCode;
                                    if (currentMode.equals("email")) {
                                        EmailSender.sendEmail(SubscriberSession.getSubscriber().getEmail(), message);
                                        showMessage("✅ Code sent successfully.", "success");
                                        forgotCodeTitle.setText("");
                                    } else if (currentMode.equals("sms")) {
                                        showSmsPopup(message);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage("❌ Error occurred.", "error");
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
            showMessage("⚠️ You must enter a confirmation code first", "error");
            return;
        }
        ResponseWrapper parkingCodeRsp = new ResponseWrapper("PARKINGCODE", parkingCode,
                SubscriberSession.getSubscriber().getCode());
        Main.clientConsole.accept(parkingCodeRsp);
    }

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
    public void ForgotCode(ActionEvent event) throws Exception {
        Main.switchScene("forgotCodePage.fxml");
    }

    public void SendByEmail(ActionEvent event) {
        currentMode = "email";
        forgotCodeTitle.setText("Sending...");
        forgotCodeTitle.setStyle(
            "-fx-text-fill: white;" +               
            "-fx-background-color: #4CAF50;" +      
            "-fx-padding: 8 16;" +                  
            "-fx-background-radius: 6;" +           
            "-fx-font-weight: bold;" +              
            "-fx-font-size: 13px;" +                
            "-fx-alignment: center;"                
        );



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
        messageLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 15;" +
            "-fx-background-color: #d4edda;" +        // Light green background
            "-fx-text-fill: #155724;" +               // Dark green text
            "-fx-border-color: #c3e6cb;" +            // Matching green border
            "-fx-border-width: 1;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-alignment: center;" +
            "-fx-font-weight: bold;"
        );

        StackPane root = new StackPane(messageLabel);
        root.setStyle("-fx-background-color: transparent;");
        Scene popupScene = new Scene(root, 300, 100);
        popupStage.setScene(popupScene);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("SMS Simulation");
        popupStage.show();

        // Automatically close the popup after 4 seconds and return to ReceivingCarPage
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

    @FXML
    private void showMessage(String text, String type) {
        forgotCodeLabel.setText(text);
        forgotCodeLabel.setVisible(true);
        forgotCodeLabel.getStyleClass().removeAll("message-success", "message-error", "message-info");
        switch (type) {
            case "success" -> forgotCodeLabel.getStyleClass().add("message-success");
            case "error" -> forgotCodeLabel.getStyleClass().add("message-error");
            default -> forgotCodeLabel.getStyleClass().add("message-info");
        }
    }
}
