import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.BarChart;
import javafx.scene.control.TableView;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Utility class to export parking reports to a PDF document. Supports exporting
 * a table and two bar charts into a single PDF file.
 */
public class PdfExporter {

	/**
	 * Exports the given TableView and BarCharts into a single PDF file.
	 *
	 * @param file   the destination file to save the PDF.
	 * @param table  the TableView containing tabular parking data (can be null).
	 * @param chart1 the first bar chart (e.g., duration summary).
	 * @param chart2 the second bar chart (e.g., late returns).
	 * @param <T>    the data type of the TableView.
	 */
	public static <T> void exportReportToPdf(File file, TableView<T> table, BarChart<?, ?> chart1,
			BarChart<?, ?> chart2) {
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();

			// Title
			Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
			Paragraph title = new Paragraph("Parking Spot Monthly Report", titleFont);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(new Paragraph(" ")); // Spacer

			// Table export (if present)
			if (table != null) {
				PdfPTable pdfTable = new PdfPTable(table.getColumns().size());
				pdfTable.setWidthPercentage(100);
				pdfTable.setSpacingBefore(10f);
				pdfTable.setSpacingAfter(10f);

				// Header row
				for (var col : table.getColumns()) {
					PdfPCell headerCell = new PdfPCell(
							new Phrase(col.getText(), new Font(Font.HELVETICA, 12, Font.BOLD)));
					headerCell.setBackgroundColor(Color.LIGHT_GRAY);
					headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
					headerCell.setPadding(5f);
					pdfTable.addCell(headerCell);
				}

				// Data rows
				for (T item : table.getItems()) {
					for (int i = 0; i < table.getColumns().size(); i++) {
						Object cellData = table.getColumns().get(i).getCellData(item);
						PdfPCell dataCell = new PdfPCell(new Phrase(cellData == null ? "" : cellData.toString()));
						dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
						dataCell.setPadding(4f);
						pdfTable.addCell(dataCell);
					}
				}

				document.add(pdfTable);
			}

			// Charts
			addChartImageToPdf(chart1, document, "Duration (Regular vs Extended)");
			addChartImageToPdf(chart2, document, "Number of Late Returns");

			document.close();
			System.out.println("✅ PDF exported successfully to " + file.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Renders a JavaFX BarChart as an image and adds it to the PDF.
	 *
	 * @param chart   the JavaFX BarChart to export.
	 * @param doc     the open PDF Document to write to.
	 * @param heading the title label for the chart section.
	 */
	private static void addChartImageToPdf(BarChart<?, ?> chart, Document doc, String heading) {
		try {
			WritableImage image = chart.snapshot(new SnapshotParameters(), null);
			BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
			File tempFile = File.createTempFile("chart", ".png");
			ImageIO.write(bufferedImage, "png", tempFile);

			doc.add(new Paragraph(" "));
			Paragraph chartTitle = new Paragraph(heading, new Font(Font.HELVETICA, 14, Font.BOLD));
			chartTitle.setSpacingAfter(10f);
			doc.add(chartTitle);

			Image chartImg = Image.getInstance(tempFile.getAbsolutePath());
			chartImg.scaleToFit(500, 300);
			chartImg.setAlignment(Image.MIDDLE);
			doc.add(chartImg);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
