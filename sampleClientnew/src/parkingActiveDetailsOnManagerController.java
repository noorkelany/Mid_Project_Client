import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.ResourceBundle;

import data.Order;
import data.ResponseWrapper;
import data.Subscriber;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class parkingActiveDetailsOnManagerController extends parkingActiveDetailsController {
	
    @FXML
    void reportsBtn(ActionEvent event) throws Exception {
		Main.switchScene("parkingSpotReport.fxml");
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
    void subscriberDetailsBtn(ActionEvent event) {
    	try {
			Main.switchScene("managerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SubscriberLogin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Subscriber Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
