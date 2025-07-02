import java.io.IOException;

import data.SubscriberSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controller for the Manager's interface, extending common functionalities from {@code workerController}.
 * Provides navigation to manager-specific views such as reports and parking activity.
 */
public class managerController extends workerController {

    /**
     * Navigates to the Parking Spot Report page.
     * @param event the action event triggered by clicking the report button
     */
    @FXML
    void reportsBtn(ActionEvent event) {
        try {
            Main.switchScene("parkingSpotReport.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the Monthly Report View page.
     * @param event the action event triggered by clicking the monthly report button
     */
    @FXML
    void MonthlyreportsBtn(ActionEvent event) {
        try {
            Main.switchScene("MonthlyReportView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the active parking details view for managers.
     * @param event the action event triggered by clicking the active details button
     */
    @FXML
    void parkingActiveDetailsBtn(ActionEvent event) {
        try {
            Main.switchScene("parkingActiveDetailsOnManager.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns to the main page (MainPage.fxml).
     * @param event the action event triggered by clicking the back button
     */
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent previousRoot = FXMLLoader.load(getClass().getResource("MainPage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(previousRoot));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
