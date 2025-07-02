import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import data.Order;
import data.ResponseWrapper;
import data.SubscriberSession;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * Controller for the subscriber's main dashboard.
 * Displays recent order history and allows navigation to features like:
 * <ul>
 *     <li>Profile update</li>
 *     <li>Car delivery</li>
 *     <li>Code retrieval</li>
 *     <li>Extending parking time</li>
 *     <li>Order status</li>
 * </ul>
 */
public class SubscriberMainController implements Initializable {

    /** Table to display recent parking orders of the subscriber */
    @FXML
    private TableView<Order> historyTable;

    /** Column for order number */
    @FXML
    private TableColumn<Order, Integer> orderNumberCol;

    /** Column for order date */
    @FXML
    private TableColumn<Order, String> orderDateCol;

    /** Column for date the order was placed */
    @FXML
    private TableColumn<Order, String> datePlacedCol;

    /** Column for parking space associated with the order */
    @FXML
    private TableColumn<Order, String> parkingSpaceCol;

    /** Column for the car number */
    @FXML
    private TableColumn<Order, String> carNumberCol;

    /**
     * Initializes the table view and fetches the subscriber’s order history from the server.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        orderNumberCol.setCellValueFactory(new PropertyValueFactory<>("order_number"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        datePlacedCol.setCellValueFactory(new PropertyValueFactory<>("date_of_placing_an_order"));
        parkingSpaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));
        carNumberCol.setCellValueFactory(new PropertyValueFactory<>("carNumber"));

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
     * Refreshes the order history for the current subscriber.
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
     * Navigates to the subscriber profile update page.
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
     * Opens a separate window for car delivery processing.
     */
    @FXML
    public void carDeliveryBtnClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CarDelivery2.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("CarDelivery.css").toExternalForm());

            Stage stage = new Stage();
            stage.setTitle("Car Delivery");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the page where the subscriber registers car pickup.
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
     * Navigates to the view for extending the subscriber's parking time.
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
     * Navigates to the order status view for the subscriber.
     */
    @FXML
    public void handleOrdersstatus() {
        try {
            Main.switchScene("OrderStatus.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
