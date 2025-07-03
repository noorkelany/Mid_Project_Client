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

/**
 * Controller class responsible for displaying a visual map of parking spots.
 * Each spot is represented by a colored circle (green for free, red for occupied).
 * Clicking a free spot logs the spot ID.
 */
public class ParkingMapController implements Initializable {

    /**
     * The GridPane where parking spots are dynamically added as visual cells.
     */
    @FXML
    private GridPane parkingGrid;

    /**
     * Initializes the parking map by:
     * <ul>
     *   <li>Loading the parking spot availability map from session</li>
     *   <li>Rendering each spot as a circle (green = free, red = occupied)</li>
     *   <li>Adding click handlers to available (green) spots</li>
     * </ul>
     *
     * @param location  URL location used to resolve relative paths
     * @param resources ResourceBundle for localization (unused here)
     */
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

            // Create the circle representing the spot
            Circle spotCircle = new Circle(20);
            spotCircle.setFill(isFree ? Color.GREEN : Color.RED);

            if (isFree) {
                // Add interaction for free spots
                spotCircle.setOnMouseClicked(event -> {
                    System.out.println("Clicked Spot ID: " + spotId);
                    // TODO: handle selection (e.g., update session, switch scene)
                });
                spotCircle.setCursor(javafx.scene.Cursor.HAND);
            }

            // Label for the spot ID
            Label label = new Label(String.valueOf(spotId));
            label.setTextFill(Color.BLACK);

            // Combine circle and label into a mini cell
            GridPane cell = new GridPane();
            cell.setHgap(5);
            cell.add(spotCircle, 0, 0);
            cell.add(label, 1, 0);

            // Add to grid
            parkingGrid.add(cell, col, row);

            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }
}
