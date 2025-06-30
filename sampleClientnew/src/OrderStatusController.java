
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

public class OrderStatusController implements Initializable {

    @FXML private TableView<Order> historyTable;
    @FXML private TableColumn<Order, Integer> parkingspaceCol;
    @FXML private TableColumn<Order, Date> orderDateCol;
    @FXML private TableColumn<Order, Time> ordertimeCol;
    @FXML private TableColumn<Order, String> orderstatusCol;
    @FXML private TableColumn<Order, Integer> numberofextendsCol;
    @FXML private TableColumn<Order, Time> recivingcartimeCol1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // קישור בין עמודות לשדות במחלקת Order
        parkingspaceCol.setCellValueFactory(new PropertyValueFactory<>("parking_space"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("order_date"));
        ordertimeCol.setCellValueFactory(new PropertyValueFactory<>("delTime"));
        orderstatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        numberofextendsCol.setCellValueFactory(new PropertyValueFactory<>("numberofextend"));
        recivingcartimeCol1.setCellValueFactory(new PropertyValueFactory<>("recTime"));

        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        historyTable.setEditable(false);

        // הגדרת קונסולה ללקוח לקליטת תגובה מהשרת
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object message) {
                if (message instanceof ResponseWrapper wrapper &&
                        "SubscriberParkingStatusResult".equals(wrapper.getType())) {

                    Object data = wrapper.getData();
                    if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
                        ObservableList<Order> observableOrders =
                                FXCollections.observableArrayList((List<Order>) list);
                        Platform.runLater(() -> historyTable.setItems(observableOrders));
                    }
                }
            }
        };

        // שליחת בקשת היסטוריית סטטוס חנייה
        if (SubscriberSession.getSubscriber() != null) {
            int subscriberId = SubscriberSession.getSubscriber().getCode();
            ResponseWrapper request = new ResponseWrapper("SubscriberParkingStatus", subscriberId);
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
    private void handleSubscriberOrders() {
        try {
            Main.switchScene("SubscriberMain.fxml");
        } catch (Exception e) {
            e.printStackTrace();
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
}
