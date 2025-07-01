
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IpPageController {

    @FXML
    private TextField ipTextField;
    @FXML
    private TextArea errorTextArea;

    @FXML
    private void onConnectClicked(ActionEvent event) {
        String ip = ipTextField.getText().trim();

        if (isValidIP(ip)) {
            Main.serverIP = ip;
            try {
                Main.clientConsole = new ClientConsole(ip, 5555);

                // Load the FXML manually
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ipTextField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("AutoParking reservation");

                stage.centerOnScreen(); // ✅ Center the window on screen
                stage.show();

            } catch (Exception e) {
                errorTextArea.setText("Failed to connect to server.");
                errorTextArea.setVisible(true);
                e.printStackTrace();
            }
        } else {
            errorTextArea.setText("Invalid IP address.");
            errorTextArea.setVisible(true);
        }
    }


    private boolean isValidIP(String ip) {
        return ip.matches(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
          + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }
}