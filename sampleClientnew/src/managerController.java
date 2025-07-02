import java.io.IOException;

import data.SubscriberSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class managerController extends workerController {
	
    @FXML
    void reportsBtn(ActionEvent event) {
		try {
			Main.switchScene("parkingSpotReport.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FXML
    void MonthlyreportsBtn(ActionEvent event) {
		try {
			Main.switchScene("MonthlyReportView.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @FXML
    void parkingActiveDetailsBtn(ActionEvent event) {
        try {
            Main.switchScene("parkingActiveDetailsOnManager.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  
    @FXML
	private void handleBack(ActionEvent event) {
	    try {
	    	 // Load the previous FXML
	        Parent previousRoot = FXMLLoader.load(getClass().getResource("MainPage.fxml"));

	        // Get current stage from any control (e.g. the button)
	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

	        // Replace the scene in the same window
	        stage.setScene(new Scene(previousRoot));
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
}
