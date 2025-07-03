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

import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller class for displaying a subscriber's parking order status history.
 * It initializes the TableView to show past and active parking orders,
 * communicates with the server to fetch this data, and handles user navigation.
 */
public class OrderStatusController implements Initializable {

    /** TableView displaying the order history */
    @FXML
    private TableView<Order> historyTable;

    /** Column showing the parking space number */
    @FXML
    private TableColumn<Order, Integer> parkingspaceCol;

    /** Column showing the order date */
    @FXML
    private TableColumn<Order, Date> orderDateCol;

    /** Column showing the order delivery time */
    @FXML
    private TableColumn<Order, Time> ordertimeCol;

    /** Column showing the order status */
    @FXML
    private TableColumn<Order, String> orderstatusCol;

    /** Column showing number of extensions */
    @FXML
    private TableColumn<Order, Integer> numberofextendsCol;

    /** Column showing the receiving car time */
    @FXML
    private TableColumn<Order, Time> recivingcartimeCol1;

    /**
     * Initializes the controller by:
     * <ul>
     *     <li>Binding columns to {@link Order} properties</li>
     *     <li>Setting up the client console listener</li>
     *     <li>Sending a request to retrieve the subscriber's parking history</li>
     * </ul>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parkingspaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        ordertimeCol.setCellValueFactory(new PropertyValueFactory<>("delTime"));
        orderstatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        numberofextendsCol.setCellValueFactory(new PropertyValueFactory<>("numberofextend"));
        recivingcartimeCol1.setCellValueFactory(new PropertyValueFactory<>("recTime"));

        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setEditable(false);

        // Override console behavior to listen for order history response
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                if (message instanceof ResponseWrapper wrapper
                        && "SubscriberParkingStatusResult".equals(wrapper.getType())) {

                    Object data = wrapper.getData();
                    if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
                        ObservableList<Order> observableOrders = FXCollections.observableArrayList((List<Order>) list);
                        Platform.runLater(() -> historyTable.setItems(observableOrders));
                    }
                }
            }
        };

        // Request order history for logged-in subscriber
        if (SubscriberSession.getSubscriber() != null) {
            int subscriberId = SubscriberSession.getSubscriber().getCode();
            ResponseWrapper request = new ResponseWrapper("SubscriberParkingStatus", subscriberId);
            Main.clientConsole.accept(request);
        }
    }

    /**
     * Logs the subscriber out and navigates to the main page.
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
     * Navigates back to the subscriber's main page.
     */
    @FXML
    private void handleSubscriberOrders() {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the page for updating subscriber profile information.
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
     * Opens a new window for car delivery operations.
     * Loads a new scene with custom styling.
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
     * Navigates to the car receiving registration page.
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
     * Navigates to the page for extending a parking session.
     */
    @FXML
    public void handleExtendsParkingTime() {
        try {
            Main.switchScene("ExtendParkingTime.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
