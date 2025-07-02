import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controller for the IP input page.
 * Handles connection setup to the server using the IP address entered by the user.
 */
public class IpPageController {

    @FXML
    private TextField ipTextField;

    @FXML
    private TextArea errorTextArea;

    /**
     * Triggered when the "Connect" button is clicked.
     * Validates the IP address, attempts to establish a client-server connection,
     * and navigates to the main page on success.
     *
     * @param event the action event from the connect button
     */
    @FXML
    private void onConnectClicked(ActionEvent event) {
        // Get the IP address entered in the field
        String ip = ipTextField.getText();

        if (isValidIP(ip)) {
            Main.serverIP = ip;
            try {
                // Start the client console and load the main page
                Main.clientConsole = new ClientConsole(ip, 5555);
                Main.switchScene("MainPage.fxml");
            } catch (Exception e) {
                // Connection failed
                errorTextArea.setText("Failed to connect to server.");
                e.printStackTrace();
            }
        } else {
            // IP address is invalid
            errorTextArea.setText("Invalid IP address.");
            errorTextArea.setVisible(true);
        }
    }

    /**
     * Validates an IP address string using a regular expression.
     *
     * @param ip the IP address to validate
     * @return true if the IP address is valid, false otherwise
     */
    private boolean isValidIP(String ip) {
        return ip.matches(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
          + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }
}
