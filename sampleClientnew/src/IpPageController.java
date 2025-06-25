
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class IpPageController {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextArea errorTextArea;
    @FXML
    private void onConnectClicked(ActionEvent event) {
    	//get the the ip from the field
        String ip = ipTextField.getText();
        if (isValidIP(ip)) {
            Main.serverIP = ip;
            try {
            	//start the main page 
                Main.clientConsole = new ClientConsole(ip, 5555);
                Main.switchScene("MainPage.fxml");
            } catch (Exception e) {
                errorTextArea.setText("Failed to connect to server.");
                e.printStackTrace();
            }
        } else {
        	//if the ip not valid
            errorTextArea.setText("Invalid IP address.");
            //show the error message.
            errorTextArea.setVisible(true);
        }
    }

    private boolean isValidIP(String ip) {
        return ip.matches(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
          + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }
}