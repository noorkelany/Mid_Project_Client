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

public class SubscriberStatusController implements Initializable {

    @FXML private TableView<Order> historyTable;

    @FXML private TableColumn<Order, Integer> orderNumberCol;
    @FXML private TableColumn<Order, Date> orderDateCol;
    @FXML private TableColumn<Order, Date> datePlacedCol;
    @FXML private TableColumn<Order, Integer> parkingSpaceCol;
    @FXML private TableColumn<Order, String> carNumberCol;
    @FXML private TableColumn<Order, String> statusCol;
    @FXML private TableColumn<Order, Integer> numberOfExtendsCol;
    @FXML private TableColumn<Order, Time> receivingCarTimeCol;

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

    @FXML
    private void handleLogout() {
        try {
            SubscriberSession.setSubscriber(null);
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSubscriberOrders() {
        if (SubscriberSession.getSubscriber() != null) {
            int subscriberId = SubscriberSession.getSubscriber().getCode();
            ResponseWrapper request = new ResponseWrapper("SubscriberHistory", subscriberId);
            Main.clientConsole.accept(request);
        }
    }

    @FXML
    private void handleUpdateSubscriber() {
        try {
            Main.switchScene("UpdateSubscriber.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void carDeliveryBtnClicked() {
        try {
            Main.switchScene("CarDelivery2.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRegister() {
        try {
            Main.switchScene("ReceivingCarPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleExtendsParkingTime() {
        try {
            Main.switchScene("ExtendParkingTime.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleOrderParkingSpot() {
        try {
            Main.switchScene("MakingOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
