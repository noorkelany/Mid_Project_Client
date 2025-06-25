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
    @Override
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
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberLogin.fxml"));
	        Parent root = loader.load();
	        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	        stage.setScene(new Scene(root));
	        stage.setTitle("BPARK - Subscriber Login");
	        stage.show();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
}
