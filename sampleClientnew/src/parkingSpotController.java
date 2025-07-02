import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * controller for parking spot
 */
public class parkingSpotController {
    /**
     * method for switching to cardelivery page
     * @param event button clicked
     */
    @FXML
    void confirmDeliveryButton(ActionEvent event) {
		try {
			Main.switchScene("CarDelivery.fxml");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
