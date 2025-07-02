import data.Order;
import data.SubscriberSession;
import data.ResponseWrapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for displaying a subscriber's full order history and status details.
 * <p>This view includes order number, status, car number, parking spot, delivery/return times, and extensions.</p>
 * It also allows the subscriber to:
 * <ul>
 *     <li>Logout</li>
 *     <li>Update their profile</li>
 *     <li>Receive or return cars</li>
 *     <li>Extend parking</li>
 *     <li>Place new parking orders</li>
 * </ul>
 */
public class SubscriberStatusController implements Initializable {

    /** Table showing full order history */
    @FXML private TableView<Order> historyTable;

    /** Column showing the order number */
    @FXML private TableColumn<Order, Integer> orderNumberCol;

    /** Column showing the order date */
    @FXML private TableColumn<Order, Date> orderDateCol;

    /** Column showing when the order was placed */
    @FXML private TableColumn<Order, Date> datePlacedCol;

    /** Column showing the assigned parking space */
    @FXML private TableColumn<Order, Integer> parkingSpaceCol;

    /** Column showing the car number */
    @FXML private TableColumn<Order, String> carNumberCol;

    /** Column showing the status (e.g., ACTIVE, COMPLETED) */
    @FXML private TableColumn<Order, String> statusCol;

    /** Column showing how many times the parking was extended */
    @FXML private TableColumn<Order, Integer> numberOfExtendsCol;

    /** Column showing the actual receiving (pickup) time */
    @FXML private TableColumn<Order, Time> receivingCarTimeCol;

    /**
     * Initializes the controller: sets up the table columns,
     * defines server response behavior, and fetches order history for the logged-in subscriber.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("order_number"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        datePlacedCol.setCellValueFactory(new PropertyValueFactory<>("date_of_placing_an_order"));
        parkingSpaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));
        carNumberCol.setCellValueFactory(new PropertyValueFactory<>("carNumber"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        numberOfExtendsCol.setCellValueFactory(new PropertyValueFactory<>("numberofextend"));
        receivingCarTimeCol.setCellValueFactory(new PropertyValueFactory<>("recTime"));

        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setEditable(false);

        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                if (message instanceof ResponseWrapper wrapper &&
                    wrapper.getType().equals("SubscriberHistoryResult")) {

                    Object data = wrapper.getData();
                    if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
                        ObservableList<Order> observableOrders =
                                FXCollections.observableArrayList((List<Order>) list);

                        Platform.runLater(() -> historyTable.setItems(observableOrders));
                    }
                }
            }
        };

        if (SubscriberSession.getSubscriber() != null) {
            int subscriberId = SubscriberSession.getSubscriber().getCode();
            ResponseWrapper request = new ResponseWrapper("SubscriberHistory", subscriberId);
            Main.clientConsole.accept(request);
        }
    }

    /**
     * Logs the subscriber out and navigates to the main welcome page.
     */
    @FXML
    private void handleLogout() {
        try {
            SubscriberSession.setSubscriber(null);
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the subscriber’s order history from the server.
     */
    @FXML
    public void handleSubscriberOrders() {
        if (SubscriberSession.getSubscriber() != null) {
            int subscriberId = SubscriberSession.getSubscriber().getCode();
            ResponseWrapper request = new ResponseWrapper("SubscriberHistory", subscriberId);
            Main.clientConsole.accept(request);
        }
    }

    /**
     * Navigates to the profile update view for the subscriber.
     */
    @FXML
    private void handleUpdateSubscriber() {
        try {
            Main.switchScene("UpdateSubscriber.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the car delivery view.
     */
    @FXML
    public void carDeliveryBtnClicked() {
        try {
            Main.switchScene("CarDelivery2.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the car receiving page.
     */
    @FXML
    public void handleRegister() {
        try {
            Main.switchScene("ReceivingCarPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the parking extension view.
     */
    @FXML
    public void handleExtendsParkingTime() {
        try {
            Main.switchScene("ExtendParkingTime.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the page for placing a new parking order.
     */
    @FXML
    public void handleOrderParkingSpot() {
        try {
            Main.switchScene("MakingOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
