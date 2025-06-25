import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import data.ParkingSpotsSession;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ParkingMapController implements Initializable {

	@FXML
	private GridPane parkingGrid;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Map<Integer, Boolean> spotMap = ParkingSpotsSession.getMap();
		System.out.println("spot map " + spotMap);
		int col = 0;
		int row = 0;
		int maxCols = 5;
		for (Map.Entry<Integer, Boolean> entry : spotMap.entrySet()) {
			int spotId = entry.getKey();
			boolean isFree = entry.getValue();

			Circle spotCircle = new Circle(20);
			spotCircle.setFill(isFree ? Color.GREEN : Color.RED);

			if (isFree) {
				spotCircle.setOnMouseClicked(event -> {
					System.out.println("Clicked Spot ID: " + spotId);
					// Optional: store or send this ID
				});
				spotCircle.setCursor(javafx.scene.Cursor.HAND); // Set hand cursor
			}

			Label label = new Label(String.valueOf(spotId));
			label.setTextFill(Color.BLACK);

			GridPane cell = new GridPane();
			cell.setHgap(5);
			cell.add(spotCircle, 0, 0);
			cell.add(label, 1, 0);

			parkingGrid.add(cell, col, row);

			col++;
			if (col >= maxCols) {
				col = 0;
				row++;
			}
		}

	}

	public void handleBack() {

	}
}