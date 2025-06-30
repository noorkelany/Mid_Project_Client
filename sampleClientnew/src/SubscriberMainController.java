import data.Order;
import data.SubscriberSession;
import data.ResponseWrapper;
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
import java.util.List;
import java.util.ResourceBundle;

public class SubscriberMainController implements Initializable {

    @FXML private TableView<Order> historyTable;
    @FXML private TableColumn<Order, Integer> orderNumberCol;
    @FXML private TableColumn<Order, String> orderDateCol;
    @FXML private TableColumn<Order, String> datePlacedCol;
    @FXML private TableColumn<Order, String> parkingSpaceCol;
    @FXML private TableColumn<Order, String> carNumberCol;

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
    public void handleOrdersstatus() {
        try {
            Main.switchScene("OrderStatus.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}



