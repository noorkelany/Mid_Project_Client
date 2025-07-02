import java.io.IOException;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.ResourceBundle;

import data.Order;
import data.ResponseWrapper;
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
 * Controller for displaying currently active parking orders. It fetches data
 * from the server and populates a table view with ongoing sessions, including
 * subscriber details and time information.
 */
public class parkingActiveDetailsController implements Initializable {

	/** Table column for the car number */
	@FXML
	private TableColumn<Order, String> carNumberCol;

	/** Table column for the confirmation code */
	@FXML
	private TableColumn<Order, Integer> confCodeCol;

	/** Table column for the car delivery time */
	@FXML
	private TableColumn<Order, Time> delTimeCol;

	/** Table column for the car receiving time */
	@FXML
	private TableColumn<Order, Time> recTimeCol;

	/** Table column for the subscriber's code */
	@FXML
	private TableColumn<Order, Integer> subscriberCodeCol;

	/** Table column for the parking space number */
	@FXML
	private TableColumn<Order, Integer> parkingSpaceCol;

	/** Table view displaying the list of active parking orders */
	@FXML
	private TableView<Order> parkingTable;

	/**
	 * Initializes the controller by binding the table columns to the Order model,
	 * setting table behavior, and requesting active parking data from the server.
	 *
	 * @param location  URL location used to resolve relative paths for the root
	 *                  object
	 * @param resources ResourceBundle for localized resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		carNumberCol.setCellValueFactory(new PropertyValueFactory<>("carNumber"));
		confCodeCol.setCellValueFactory(new PropertyValueFactory<>("confirmation_code"));
		delTimeCol.setCellValueFactory(new PropertyValueFactory<>("delTime"));
		recTimeCol.setCellValueFactory(new PropertyValueFactory<>("recTime"));
		subscriberCodeCol.setCellValueFactory(new PropertyValueFactory<>("subscriber_id"));
		parkingSpaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));

		parkingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		parkingTable.setEditable(false);
		parkingTable.setFocusTraversable(false);

		// Set up server communication
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				try {
					if (message instanceof ArrayList<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
						ObservableList<Order> data = FXCollections.observableArrayList();
						@SuppressWarnings("unchecked")
						ArrayList<Order> orderList = (ArrayList<Order>) list;
						for (Object o : orderList) {
							data.add((Order) o);
						}
						parkingTable.setItems(data);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		// Send request to server for active parking orders
		ResponseWrapper parkingSubscriberResponse = new ResponseWrapper("parkingActiveList", null);
		Main.clientConsole.accept(parkingSubscriberResponse);
	}

	/**
	 * Navigates to the worker home page.
	 *
	 * @param event The action event triggered by the button.
	 */
	@FXML
	void subscriberDetailsBtn(ActionEvent event) {
		try {
			Main.switchScene("workerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates to the view showing all orders.
	 */
	@FXML
	private void showOrdersBtn() {
		try {
			Main.switchScene("ShowOrder.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Navigates to the registration page for adding a new subscriber.
	 */
	@FXML
	public void RegisterNewSubscriber() {
		try {
			Main.switchScene("Register.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns to the worker home page.
	 */
	@FXML
	private void handleBack() {
		try {
			Main.switchScene("workerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
