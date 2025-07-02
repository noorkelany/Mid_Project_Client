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

/**
 * Controller class for the worker's main dashboard.
 * Displays a table of all subscribers and provides navigation to additional actions.
 */
public class workerController implements Initializable {

    /** Table column showing subscriber code (ID) */
    @FXML private TableColumn<Subscriber, Integer> codeCol;

    /** Table column showing subscriber email */
    @FXML private TableColumn<Subscriber, String> emailCol;

    /** Table column showing subscriber password */
    @FXML private TableColumn<Subscriber, String> passwordCol;

    /** Table column showing subscriber phone number */
    @FXML private TableColumn<Subscriber, String> phoneNumberCol;

    /** Table column showing subscriber username */
    @FXML private TableColumn<Subscriber, String> usernameCol;

    /** TableView to display list of all subscribers */
    @FXML private TableView<Subscriber> subscriberTable;

    /**
     * Initializes the subscriber table and sets up the client console
     * to listen for and populate data from the server.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up table column mappings
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        passwordCol.setCellValueFactory(new PropertyValueFactory<>("password"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        subscriberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        subscriberTable.setEditable(false);
        subscriberTable.setFocusTraversable(false);

        // Define server response handling
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

        // Request subscriber list from server
        ResponseWrapper subscriberResponse = new ResponseWrapper("subscriberList", null);
        Main.clientConsole.accept(subscriberResponse);
    }

    /**
     * Navigates to the parking active details page.
     * @param event the action event
     */
    @FXML
    void parkingActiveDetailsBtn(ActionEvent event) {
        try {
            Main.switchScene("parkingActiveDetails.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles back button click to return to the main page.
     */
    @FXML
    private void handleBack() {
        try {
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the orders list view.
     */
    @FXML
    private void showOrdersBtn() {
        try {
            Main.switchScene("ShowOrder.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the page for registering a new subscriber.
     */
    @FXML
    public void RegisterNewSubscriber() {
        try {
            Main.switchScene("Register.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out the current worker and returns to the main login page.
     */
    @FXML
    public void handleLogout() {
        try {
            Main.switchScene("MainPage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
