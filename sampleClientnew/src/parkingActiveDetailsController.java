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

public class parkingActiveDetailsController implements Initializable {
	
    @FXML
    private TableColumn<Order, String> carNumberCol;

    @FXML
    private TableColumn<Order, Integer> confCodeCol;

    @FXML
    private TableColumn<Order, Time> delTimeCol;

    @FXML
    private TableColumn<Order, Time> recTimeCol;

    @FXML
    private TableColumn<Order, Integer> subscriberCodeCol;
    @FXML
    private TableColumn<Order, Integer> parkingSpaceCol;
    @FXML
    private TableView<Order> parkingTable;
    
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
		
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				try {
					if (message instanceof ArrayList<?> list && !list.isEmpty() && list.get(0) instanceof Order) {
						ObservableList<Order> data = FXCollections.observableArrayList();
						@SuppressWarnings("unchecked")
						ArrayList<Order> orderList = (ArrayList<Order>) list;
						System.out.print(orderList.get(0).getDelTime());
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
		
		ResponseWrapper parkingSubscriberResponse = new ResponseWrapper("parkingActiveList", null);
		Main.clientConsole.accept(parkingSubscriberResponse);
	}
    
    @FXML
    void subscriberDetailsBtn(ActionEvent event) {
    	try {
			Main.switchScene("workerHomePage.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @FXML
    private void handleBack() {
        try {
            Main.switchScene("workerHomePage.fxml"); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    



}
