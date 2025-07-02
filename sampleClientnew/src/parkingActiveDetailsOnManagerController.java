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

/**
 * Controller to handle manager dash board
 */
public class parkingActiveDetailsOnManagerController extends parkingActiveDetailsController {

	/**
	 * Method to switch to parkingSpotReport page
	 * @param event button clicked
	 * @throws Exception exception that might be thrown 
	 */
	@FXML
	void reportsBtn(ActionEvent event) throws Exception {
		Main.switchScene("parkingSpotReport.fxml");
	}

	/**
	 * Method to switch to MonthlyReportView page
	 * @param event button clicked
	 * @throws Exception exception that might be thrown 
	 */
	@FXML
	void MonthlyreportsBtn(ActionEvent event) {
		try {
			Main.switchScene("MonthlyReportView.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * method to handle clicking on logout button
	 */
	@FXML
	public void handleLogout() {
		try {
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *method to handle clicking on subscriber details button
	 */
	@Override
	@FXML
	void subscriberDetailsBtn(ActionEvent event) {
		try {
			Main.switchScene("managerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * method to handle clicking on back button
	 * @param event back button clicked
	 */
	@FXML
	private void handleBack(ActionEvent event) {

		try {
			Main.switchScene("MainPage.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
