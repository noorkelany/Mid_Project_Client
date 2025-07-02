
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import data.ResponseWrapper;
import data.Subscriber;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class workerController implements Initializable{
    @FXML
    private TableColumn<Subscriber, Integer> codeCol;

    @FXML
    private TableColumn<Subscriber, String> emailCol;

    @FXML
    private TableColumn<Subscriber, String> passwordCol;

    @FXML
    private TableColumn<Subscriber, String> phoneNumberCol;

    @FXML
    private TableColumn<Subscriber, String> usernameCol;
    
    @FXML
    private TableView<Subscriber> subscriberTable;
    
	
    
	
    @FXML
    void parkingActiveDetailsBtn(ActionEvent event) {
    	try {
			Main.switchScene("parkingActiveDetails.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }




	@Override
	public void initialize(URL location, ResourceBundle resources) {
		codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
		passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
		phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
		
		subscriberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		subscriberTable.setEditable(false);
		subscriberTable.setFocusTraversable(false);
		
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object message) {
				try {
					if (message instanceof ArrayList<?> list && !list.isEmpty() && list.get(0) instanceof Subscriber) {
						ObservableList<Subscriber> data = FXCollections.observableArrayList();
						for (Object o : list) {
							data.add((Subscriber) o);
						}
						subscriberTable.setItems(data);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		ResponseWrapper subscriberResponse = new ResponseWrapper("subscriberList", null);
		Main.clientConsole.accept(subscriberResponse);
	}
	
	@FXML
	private void handleBack() {
	    try {
	        Main.switchScene("MainPage.fxml"); // שנה ל-FXML שמתאים
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	private void showOrdersBtn() {
	    try {
	        Main.switchScene("ShowOrder.fxml"); 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	@FXML
	public void RegisterNewSubscriber()
	{
		  try {
		        Main.switchScene("Register.fxml"); 
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}	
	@FXML
	public void handleLogout()
	{
		 try {
		        Main.switchScene("MainPage.fxml"); 
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	}


}
