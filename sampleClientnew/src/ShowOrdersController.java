import java.util.ArrayList;

import data.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for displaying all existing orders in a table view.
 * It fetches the list of orders from the server and displays order-related details,
 * including order number, parking space, confirmation code, and subscriber ID.
 */
public class ShowOrdersController {

    /** Table view displaying all orders */
    @FXML
    private TableView<Order> ordersTable;

    /** Column for the order number */
    @FXML
    private TableColumn<Order, Integer> orderNumberCol;

    /** Column for the parking space number */
    @FXML
    private TableColumn<Order, Integer> parkingSpaceCol;

    /** Column for the date of the order */
    @FXML
    private TableColumn<Order, String> orderDateCol;

    /** Column for the confirmation code */
    @FXML
    private TableColumn<Order, Integer> confirmationCodeCol;

    /** Column for the date when the order was placed */
    @FXML
    private TableColumn<Order, String> dateOfPlacingAnOrderCol;

    /** Column for the subscriber ID associated with the order */
    @FXML
    private TableColumn<Order, Integer> subscriberIdCol;

    /** Reference to the main application (optional for future use) */
    private Main mainApp;

    /**
     * Sets the main application instance (if needed for future communication).
     * @param mainApp the main application reference
     */
    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Initializes the orders table:
     * <ul>
     *     <li>Binds table columns to Order fields</li>
     *     <li>Sets up table policies</li>
     *     <li>Defines client listener to populate table on server response</li>
     * </ul>
     */
    @FXML
    public void initialize() {
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("order_number"));
        parkingSpaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        confirmationCodeCol.setCellValueFactory(new PropertyValueFactory<>("confirmation_code"));
        dateOfPlacingAnOrderCol.setCellValueFactory(new PropertyValueFactory<>("date_of_placing_an_order"));
        subscriberIdCol.setCellValueFactory(new PropertyValueFactory<>("subscriber_id"));

        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setEditable(false);
        ordersTable.setFocusTraversable(false);

        // Set temporary console listener to receive order list from server
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                try {
                    if (message instanceof ArrayList<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
                        ObservableList<Order> data = FXCollections.observableArrayList();
                        for (Object o : list) {
                            data.add((Order) o);
                        }
                        ordersTable.setItems(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Send request to server to fetch all orders
        Main.clientConsole.accept("showAllOrders");
    }

    /**
     * Handles the "Back" button action to return to the worker's home page.
     */
    public void goBack() {
        try {
            Main.switchScene("workerHomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the "Update Order" button action to navigate to the update order view.
     */
    public void updateOrder() {
        try {
            Main.switchScene("UpdateOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
