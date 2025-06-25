
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

	/** Table displaying the full parking report */
	@FXML
	private TableView<MonthlyParkingEntry> reportTable;

	@FXML
	private Button showTableButton;

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

	/**
	 * Initializes the controller. Sets up table column bindings and registers a
	 * client listener for receiving the report data from the server.
	 */

	@FXML
	private BarChart<String, Number> warningChart;
	
	@FXML private VBox tableContainer;
	
	@FXML private Button toggleTableButton;
	private boolean isTableVisible = false;


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
	 * Sets the data for the report table and updates the duration chart.
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
	 * Updates the bar chart by calculating total duration (in blocks of 30 minutes)
	 * per car number from the current report data.
	 */
	/**
	 * Updates the bar chart showing total parking duration per car in hours. Bars
	 * are colored orange if the car has any session with extensions, green
	 * otherwise. Tooltips show total hours and number of extensions per car.
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

			// Track if this car had any session with extensions
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
				// Color bar based on extension status
				String color = hasExtensions.getOrDefault(car, false) ? "#FF9800" : "#4CAF50";
				dataPoint.getNode().setStyle("-fx-bar-fill: " + color + ";");

				// Count total extensions for this car
				int totalExtends = reportData.stream().filter(e -> e.getCarNumber().equals(car))
						.mapToInt(MonthlyParkingEntry::getNumberOfExtends).sum();

				Tooltip tooltip = new Tooltip("Car: " + car + "\nDuration: " + String.format("%.1f", duration)
						+ " hours" + "\nExtensions: " + totalExtends + " times");
				Tooltip.install(dataPoint.getNode(), tooltip);
			});
		}

		durationChart.getData().add(series);
	}

	/**
	 * Updates the warningChart (a BarChart) to show total delay warnings per
	 * subscriber.
	 * 
	 * <p>
	 * This method processes the {@code reportData} list, aggregates delay warnings
	 * for each unique subscriber code, and plots the result in a bar chart. The
	 * X-axis represents subscriber codes, and the Y-axis shows the total number of
	 * delay warnings recorded for that subscriber during the report period.
	 * </p>
	 *
	 * <p>
	 * This visualization helps identify which subscribers frequently returned their
	 * cars late or received system warnings.
	 * </p>
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
	
	private String generateColorForKey(int key) {
        String[] colors = {"#4CAF50", "#03A9F4", "#FF9800", "#E91E63", "#9C27B0", "#00BCD4", "#FF5722", "#8BC34A"};
        return colors[key % colors.length];
    }

	public void handleBack() {
		try {

			Main.switchScene("managerHomePage.fxml");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	public void handleExportPDF() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save PDF Report");
		fileChooser.setInitialFileName("MonthlyParkingReport.pdf");

		// Add PDF filter
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

		File file = fileChooser.showSaveDialog(reportTable.getScene().getWindow());

		if (file != null) {
			// Force .pdf extension if not present
			if (!file.getName().toLowerCase().endsWith(".pdf")) {
				file = new File(file.getAbsolutePath() + ".pdf");
			}

			PdfExporter.exportReportToPdf(file, reportTable, durationChart, warningChart);
		}
	}

	@FXML
	private void handleShowTable() {
		tableContainer.setVisible(true);
		tableContainer.setManaged(true);
		showTableButton.setDisable(true); // למנוע לחיצה חוזרת
	}

	/*
	 * Optional export method: Uncomment to allow saving the report to a PDF file
	 *
	 * @FXML public void handleExportPdf() { FileChooser fileChooser = new
	 * FileChooser(); fileChooser.setTitle("Save PDF Report");
	 * fileChooser.setInitialFileName("MonthlyParkingReport.pdf"); File file =
	 * fileChooser.showSaveDialog(reportTable.getScene().getWindow());
	 *
	 * if (file != null) { PdfExporter.exportReportToPdf(file, reportData); } }
	 */
	
	
	@FXML
	private void handleToggleTable() {
	    isTableVisible = !isTableVisible;
	    tableContainer.setVisible(isTableVisible);
	    tableContainer.setManaged(isTableVisible);
	    toggleTableButton.setText(isTableVisible ? "Hide Table" : "Show Table");
	}

}
