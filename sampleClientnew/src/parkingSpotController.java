import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class parkingSpotController {
    @FXML
    void confirmDeliveryButton(ActionEvent event) {
		try {
			Main.switchScene("CarDelivery.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
