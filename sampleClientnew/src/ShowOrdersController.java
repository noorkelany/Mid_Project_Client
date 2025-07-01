import java.util.ArrayList;

import data.Order;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for showorders.fxml — Displays all subscriber orders in a styled table.
 */
public class ShowOrdersController {

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, Integer> orderNumberCol;
    @FXML private TableColumn<Order, Integer> parkingSpaceCol;
    @FXML private TableColumn<Order, String> orderDateCol;
    @FXML private TableColumn<Order, Integer> confirmationCodeCol;
    @FXML private TableColumn<Order, String> dateOfPlacingAnOrderCol;
    @FXML private TableColumn<Order, Integer> subscriberIdCol;

    private Main mainApp;

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

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

        // Load data using temporary ClientConsole
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

        Main.clientConsole.accept("showAllOrders");
    }

    /**
     * Navigates back to the main subscriber screen.
     */
    public void goBack() {
        try {
            mainApp.switchScene("workerHomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the update order screen.
     */
    public void updateOrder() {
        try {
            mainApp.switchScene("UpdateOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
