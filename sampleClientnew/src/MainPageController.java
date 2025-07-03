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
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controller class for the main page of the BPARK system.
 * Handles navigation and setup of initial UI components like logo and login.
 */
public class MainPageController {

    /**
     * Text area to display information or server messages.
     */
    @FXML
    private TextArea infoArea;

    /**
     * Image view for displaying the BPARK logo.
     */
    @FXML
    private ImageView logoImage;

    /**
     * Button to trigger login navigation.
     */
    @FXML
    private Button loginBtn;

    /**
     * Initializes the main page controller by loading the logo
     * and setting up the client console for incoming messages.
     */
    @FXML
    public void initialize() {
        try {
            InputStream is = getClass().getResourceAsStream("logo_bpark.jpg");
            if (is == null) {
                System.out.println("⚠️ Failed to load image: Input stream is null");
            } else {
                logoImage.setImage(new Image(is));
            }

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

            // If user already logged in, this info can be used to hide login button, etc.
            if (SubscriberSession.getSubscriber() != null || StaffSession.getInstance().getUsername() != null) {
                System.out.println(SubscriberSession.getSubscriber() + " " + StaffSession.getInstance().getUsername());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the subscriber main page.
     */
    public void handleSubscriberHomePage() {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a request to the server to retrieve client connection details.
     */
    public void handleConnectionInfo() {
        Main.clientConsole.accept("clientDetails");
    }

    /**
     * Navigates to the monthly report view.
     */
    public void handleGeneratingReport1() {
        try {
            Main.switchScene("MonthlyReportView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the parking spot report view.
     */
    public void handleGeneratingReport2() {
        try {
            Main.switchScene("parkingSpotReport.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the parking spot delivery page.
     * @param event The action event triggered by the button.
     */
    @FXML
    void DeliveryButton(ActionEvent event) {
        try {
            Main.switchScene("parkingSpotPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the page for ordering a parking spot.
     */
    @FXML
    void handleOrderParkingSpot() {
        try {
            Main.switchScene("MakingOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the login page for subscribers.
     */
    public void handleLogin() {
        try {
            Main.switchScene("SubscriberLogin.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the worker home page.
     * @param event The action event triggered by the button.
     */
    @FXML
    void workerBtn(ActionEvent event) {
        try {
            Main.switchScene("workerHomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the manager home page.
     * @param event The action event triggered by the button.
     */
    @FXML
    void handleManagerBtn(ActionEvent event) {
        try {
            Main.switchScene("managerHomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a modal window for entering an order ID for update purposes.
     */
    public void handleUpdate() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderIDPage.fxml"));
            Parent root = loader.load();

            Stage newWindow = new Stage();
            newWindow.setTitle("Enter Order ID");
            newWindow.setScene(new Scene(root));
            newWindow.initOwner(((Stage) infoArea.getScene().getWindow()));
            newWindow.initModality(Modality.APPLICATION_MODAL); // Block main window
            newWindow.showAndWait(); // Wait for this window to close

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
