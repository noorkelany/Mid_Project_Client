import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.MonthlyParkingEntry;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Controller class for the Monthly Parking Report view. This class handles:
 * <ul>
 * <li>Displaying report data in a TableView</li>
 * <li>Rendering a summary chart of total parking duration per car</li>
 * <li>Receiving data from the server</li>
 * </ul>
 */
public class MonthlyReportController {

    /** Bar chart displaying total parking duration per car number */
    @FXML
    private BarChart<String, Number> durationChart;

    /** Bar chart displaying total delay warnings per subscriber */
    @FXML
    private BarChart<String, Number> warningChart;

    /** Table displaying the full parking report */
    @FXML
    private TableView<MonthlyParkingEntry> reportTable;

    @FXML
    private Button showTableButton;

    @FXML
    private VBox tableContainer;

    @FXML
    private Button toggleTableButton;

    /** Table columns mapped to MonthlyParkingEntry fields */
    @FXML
    private TableColumn<MonthlyParkingEntry, Integer> subscriberCodeCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, String> carNumberCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, String> parkingDateCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, String> startTimeCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, String> endTimeCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, Integer> durationCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, Integer> extendsCol;
    @FXML
    private TableColumn<MonthlyParkingEntry, Integer> warningsCol;

    /** Singleton-like static instance for UI updates */
    private static MonthlyReportController instance;

    /** Observable list for populating the TableView */
    private ObservableList<MonthlyParkingEntry> reportData = FXCollections.observableArrayList();

    private boolean isTableVisible = false;

    /**
     * Initializes the controller. Sets up table column bindings and registers a
     * client listener for receiving the report data from the server.
     */
    @FXML
    public void initialize() {
        instance = this;

        // Bind table columns to data fields
        subscriberCodeCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getSubscriberCode()));
        carNumberCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCarNumber()));
        parkingDateCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getParkingDate()));
        startTimeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStartTime()));
        endTimeCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getEndTime()));
        durationCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getDurationMinutes()));
        extendsCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getNumberOfExtends()));
        warningsCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getDelayWarnings()));

        // Override the client console display method to handle incoming report data
        Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
            @Override
            public void display(Object msg) {
                System.out.println("Received object: " + msg);

                if (msg instanceof ArrayList<?>) {
                    ArrayList<?> list = (ArrayList<?>) msg;
                    if (!list.isEmpty() && list.get(0) instanceof MonthlyParkingEntry) {
                        ArrayList<MonthlyParkingEntry> entries = (ArrayList<MonthlyParkingEntry>) list;
                        Platform.runLater(() -> {
                            instance.setReportData(entries);
                        });
                    }
                }
            }
        };

        // Start by requesting data from the server
        loadDataFromServer();
    }

    /**
     * Sends a message to the server requesting the current month's parking report.
     */
    private void loadDataFromServer() {
        Main.clientConsole.accept("GET_MONTHLY_REPORT");
    }

    /**
     * Sets the data for the report table and updates both charts.
     *
     * @param report The list of report entries received from the server.
     */
    public void setReportData(ArrayList<MonthlyParkingEntry> report) {
        reportData.setAll(report);
        reportTable.setItems(reportData);
        updateDurationChart();
        updateWarningChart();
    }

    /**
     * Updates the bar chart showing total parking duration per car in hours.
     * Bars are colored orange if the car has any session with extensions,
     * green otherwise. Tooltips show total hours and number of extensions per car.
     */
    private void updateDurationChart() {
        durationChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Double> durationMap = new HashMap<>();
        Map<String, Boolean> hasExtensions = new HashMap<>();

        for (MonthlyParkingEntry entry : reportData) {
            String car = entry.getCarNumber();
            double durationHours = entry.getDurationMinutes() / 60.0;

            durationMap.put(car, durationMap.getOrDefault(car, 0.0) + durationHours);
            if (entry.getNumberOfExtends() > 0) {
                hasExtensions.put(car, true);
            } else {
                hasExtensions.putIfAbsent(car, false);
            }
        }

        for (Map.Entry<String, Double> entry : durationMap.entrySet()) {
            String car = entry.getKey();
            double duration = entry.getValue();

            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(car, duration);
            series.getData().add(dataPoint);

            Platform.runLater(() -> {
                String color = hasExtensions.getOrDefault(car, false) ? "#FF9800" : "#4CAF50";
                dataPoint.getNode().setStyle("-fx-bar-fill: " + color + ";");

                int totalExtends = reportData.stream()
                        .filter(e -> e.getCarNumber().equals(car))
                        .mapToInt(MonthlyParkingEntry::getNumberOfExtends)
                        .sum();

                Tooltip tooltip = new Tooltip("Car: " + car + "\nDuration: " + String.format("%.1f", duration)
                        + " hours" + "\nExtensions: " + totalExtends + " times");
                Tooltip.install(dataPoint.getNode(), tooltip);
            });
        }

        durationChart.getData().add(series);
    }

    /**
     * Updates the warningChart to show total delay warnings per subscriber.
     * Aggregates delay warnings by subscriber code.
     */
    private void updateWarningChart() {
        warningChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<Integer, Integer> warningMap = new HashMap<>();

        for (MonthlyParkingEntry entry : reportData) {
            int subscriberCode = entry.getSubscriberCode();
            int warnings = entry.getDelayWarnings();
            warningMap.put(subscriberCode, warningMap.getOrDefault(subscriberCode, 0) + warnings);
        }

        for (Map.Entry<Integer, Integer> entry : warningMap.entrySet()) {
            int code = entry.getKey();
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(String.valueOf(code), entry.getValue());
            series.getData().add(dataPoint);

            Platform.runLater(() -> {
                String color = generateColorForKey(code);
                dataPoint.getNode().setStyle("-fx-bar-fill: " + color + ";");
            });
        }

        warningChart.getData().add(series);
    }

    /**
     * Generates a color from a fixed palette based on an integer key.
     *
     * @param key The identifier (e.g. subscriber code)
     * @return A hex color string
     */
    private String generateColorForKey(int key) {
        String[] colors = {
            "#4CAF50", "#03A9F4", "#FF9800", "#E91E63",
            "#9C27B0", "#00BCD4", "#FF5722", "#8BC34A"
        };
        return colors[key % colors.length];
    }

    /**
     * Handles navigation back to the manager home page.
     */
    public void handleBack() {
        try {
            Main.switchScene("managerHomePage.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles exporting the report table and charts into a PDF file.
     * Opens a FileChooser for saving the report.
     */
    @FXML
    public void handleExportPDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.setInitialFileName("MonthlyParkingReport.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

        if (file != null) {
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            PdfExporter.exportReportToPdf(file, reportTable, durationChart, warningChart);
        }
    }

    /**
     * Shows the report table if hidden and disables the button to avoid multiple clicks.
     */
    @FXML
    private void handleShowTable() {
        tableContainer.setVisible(true);
        tableContainer.setManaged(true);
        showTableButton.setDisable(true);
    }

    /**
     * Toggles visibility of the table and updates button text accordingly.
     */
    @FXML
    private void handleToggleTable() {
        isTableVisible = !isTableVisible;
        tableContainer.setVisible(isTableVisible);
        tableContainer.setManaged(isTableVisible);
        toggleTableButton.setText(isTableVisible ? "Hide Table" : "Show Table");
    }
}
