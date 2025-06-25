
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import data.ParkingSpotReportEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;

/**
 * Controller class for displaying the monthly report of parking spots. It
 * shows: - Total parking duration (regular + extended) per spot. - Total number
 * of late returns per spot.
 */
public class ParkingSpotReportController {

	@FXML
	private BarChart<String, Number> durationChart;

	@FXML
	private BarChart<String, Number> lateReturnChart;

	/** Static instance for safe access from lambda */
	private static ParkingSpotReportController instance;

	/**
	 * Called by JavaFX after loading the FXML layout. Sets up the client response
	 * handler and requests data from the server.
	 */
	@FXML
	public void initialize() {
		instance = this;

		// Set up the client listener
		Main.clientConsole = new ClientConsole(Main.serverIP, 5555) {
			@Override
			public void display(Object msg) {
				if (msg instanceof ArrayList<?>) {
					ArrayList<?> list = (ArrayList<?>) msg;
					if (!list.isEmpty() && list.get(0) instanceof ParkingSpotReportEntry) {
						ArrayList<ParkingSpotReportEntry> entries = (ArrayList<ParkingSpotReportEntry>) list;
						Platform.runLater(() -> instance.setReportData(entries));
					}
				}
			}
		};

		// Load the report on startup
		loadDataFromServer();
	}

	/**
	 * Sends a request to the server to retrieve the parking spot report.
	 */
	private void loadDataFromServer() {
		Main.clientConsole.accept("GET_PARKING_SPOT_REPORT");
	}

	/**
	 * Populates the charts using the received report data.
	 * 
	 * @param report List of ParkingSpotReportEntry representing the report.
	 */
	public void setReportData(ArrayList<ParkingSpotReportEntry> report) {
		updateDurationChart(report);
		updateLateReturnsChart(report);
	}

	/**
	 * Populates the stacked bar chart with regular and extended durations per
	 * parking spot.
	 *
	 * @param report the list of report entries.
	 */

	private void updateDurationChart(ArrayList<ParkingSpotReportEntry> report) {
		durationChart.getData().clear();

		// Map entries by parkingCode
		Map<Integer, ParkingSpotReportEntry> reportMap = new HashMap<>();
		for (ParkingSpotReportEntry entry : report) {
			reportMap.put(entry.getParkingCode(), entry);
		}

		XYChart.Series<String, Number> regularSeries = new XYChart.Series<>();
		regularSeries.setName("Regular");

		XYChart.Series<String, Number> extendedSeries = new XYChart.Series<>();
		extendedSeries.setName("Extended");

		int totalSpots = 20; // dynamic value from DB

		for (int i = 1; i <= totalSpots; i++) {
			String spotCode = String.valueOf(i);
			ParkingSpotReportEntry entry = reportMap.getOrDefault(i, new ParkingSpotReportEntry(i, 0, 0, 0));

			double regularHours = entry.getRegularDuration() / 60.0;
			double extendedHours = entry.getExtendedDuration() / 60.0;

			XYChart.Data<String, Number> regularBar = new XYChart.Data<>(spotCode, regularHours);
			XYChart.Data<String, Number> extendedBar = new XYChart.Data<>(spotCode, extendedHours);

			regularSeries.getData().add(regularBar);
			extendedSeries.getData().add(extendedBar);

			// Tooltip installation (after render)
			Platform.runLater(() -> {
				Tooltip.install(regularBar.getNode(), new Tooltip(
						"Spot: " + spotCode + "\nRegular: " + String.format("%.2f", regularHours) + " hours"));
				Tooltip.install(extendedBar.getNode(), new Tooltip(
						"Spot: " + spotCode + "\nExtended: " + String.format("%.2f", extendedHours) + " hours"));
			});
		}

		durationChart.getData().addAll(regularSeries, extendedSeries);

	}

	/**
	 * Populates the bar chart showing number of late returns per parking spot.
	 *
	 * @param report the list of report entries.
	 */
	private void updateLateReturnsChart(ArrayList<ParkingSpotReportEntry> report) {
		lateReturnChart.getData().clear();

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		Map<Integer, Integer> map = new HashMap<>();
		for (ParkingSpotReportEntry entry : report) {
			map.put(entry.getParkingCode(), entry.getLateReturns());
		}

		int maxLate = 0;
		int totalSpots = 20;

		for (int i = 1; i <= totalSpots; i++) {
			int value = map.getOrDefault(i, 0);
			maxLate = Math.max(maxLate, value);
			String code = String.valueOf(i);

			XYChart.Data<String, Number> bar = new XYChart.Data<>(code, value);
			series.getData().add(bar);

			Platform.runLater(() -> {
				Tooltip.install(bar.getNode(), new Tooltip("Spot: " + code + "\nLate Returns: " + value));
			});
		}

		lateReturnChart.getData().add(series);

		NumberAxis yAxis = (NumberAxis) lateReturnChart.getYAxis();
		yAxis.setTickUnit(1);
		yAxis.setMinorTickCount(0);
		yAxis.setAutoRanging(false);
		yAxis.setUpperBound(Math.max(1, maxLate + 1));
		yAxis.setLowerBound(0);
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
		fileChooser.setInitialFileName("ParkingSpotReport.pdf");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
		File file = fileChooser.showSaveDialog(durationChart.getScene().getWindow());

		if (file != null) {
			// Ensure the file has a .pdf extension
			if (!file.getName().toLowerCase().endsWith(".pdf")) {
				file = new File(file.getAbsolutePath() + ".pdf");
			}

			PdfExporter.exportReportToPdf(file, null, durationChart, lateReturnChart);
		}
	}

}
