package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DashBoard extends HBox {

	private LineChart<String, Number> lineChart = createLineChart();
	private ComboBox<String> monthComboBox = new ComboBox<>();
	private ComboBox<String> yearComboBox = new ComboBox<>();

	private double getTotalAmountSold(String selectedMonth, String selectedYear) {
		double totalAmount = 0.0;

		// Convert month name to its numeric representation
		int monthNumber = getMonthNumber(selectedMonth);

		String query = "SELECT SUM(totalAmount) FROM sale WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ?";

		try (Connection connection = DataBaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, monthNumber);
			preparedStatement.setString(2, selectedYear);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					totalAmount = resultSet.getDouble(1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace(); // Handle exceptions properly in your application
		}

		return totalAmount;
	}

	// Method to convert month name to its numeric representation
	private int getMonthNumber(String monthName) {
		String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		for (int i = 0; i < months.length; i++) {
			if (months[i].equalsIgnoreCase(monthName)) {
				return i + 1; // Months are 1-indexed in SQL
			}
		}
		return 0; // Invalid month name
	}

	public DashBoard(String name) {
		Sidebar sidebar = new Sidebar(name);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		getChildren().addAll(sidebar, getMainHBox());
		// Initialize data when the dashboard is created
		updateData();

		monthComboBox.setOnAction(e -> updateData());
		yearComboBox.setOnAction(e -> updateData());
	}

	private ScrollPane getMainHBox() {
		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Dashboard");
		title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		title.setFill(Color.web("#293442"));

		Label label = new Label("Log Out");
		label.setStyle("-fx-text-fill: #293442;");
		label.setGraphic(
				new ImageView(new Image("file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/exit.png")));
		label.setGraphicTextGap(10);
		label.setCursor(Cursor.HAND);

		label.setOnMouseClicked(event -> {
			Main.closeStage();
			Stage primaryStage = new Stage();
			LogIn log = new LogIn();
			log.mainScreen(primaryStage);

		});

		// Create the first VBox for total amount sold
		VBox totalAmountVBox = new VBox(10);
		totalAmountVBox.setPadding(new Insets(10));
		totalAmountVBox.setStyle("-fx-border-color: lightblue; -fx-border-width: 4; -fx-border-radius: 5;");

		HBox totalAmountVBox1 = new HBox(10);

		Text totalAmountTitle = new Text("Total Amount Sold");
		totalAmountTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		Label descriptionLabel = new Label("Select Month and Year:");
		descriptionLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		descriptionLabel.setTextFill(Color.web("#293442"));

		// Populate the ComboBox with months
		monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June", "July", "August",
				"September", "October", "November", "December");

		// Set default value to the actual month
		String actualMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM"));
		monthComboBox.setValue(actualMonth);

		// Populate the ComboBox with years
		yearComboBox.getItems().addAll("2022", "2023", "2024");

		// Set default value to the actual year
		String actualYear = String.valueOf(LocalDate.now().getYear());
		yearComboBox.setValue(actualYear);

		totalAmountVBox1.getChildren().addAll(totalAmountTitle, descriptionLabel, monthComboBox, yearComboBox);
		// Add components to the first VBox
		totalAmountVBox.getChildren().addAll(totalAmountVBox1, lineChart);

		// Create the second VBox for Medicine Shortage
		VBox medicineShortageVBox = new VBox(10);
		medicineShortageVBox.setPadding(new Insets(10));
		medicineShortageVBox.setStyle("-fx-border-color: lightcoral; -fx-border-width: 4; -fx-border-radius: 5;");

		// Components for the Medicine Shortage VBox
		Text medicineShortageTitle = new Text("Medicine Shortage");
		medicineShortageTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		// Create a TableView for Medicine Shortage
		TableView<ObservableList<String>> medicineTableView = new TableView<>();

		medicineTableView.setFixedCellSize(25); // Adjust the height as needed

		// Set the maximum height for TableView to show only 10 rows
		medicineTableView.setMaxHeight(6 * medicineTableView.getFixedCellSize());

		// Set the preferred height to allow the TableView to expand if needed
		medicineTableView.setPrefHeight(TableView.USE_COMPUTED_SIZE);

		// Define columns
		TableColumn<ObservableList<String>, String> nameColumn = new TableColumn<>("Medicine Name");
		TableColumn<ObservableList<String>, String> genericNameColumn = new TableColumn<>("Generic Name");
		TableColumn<ObservableList<String>, String> manufacturerColumn = new TableColumn<>("Manufacturer");
		TableColumn<ObservableList<String>, String> productionDateColumn = new TableColumn<>("Production Date");
		TableColumn<ObservableList<String>, String> expiredDateColumn = new TableColumn<>("Expired Date");
		TableColumn<ObservableList<String>, String> priceColumn = new TableColumn<>("Price");
		TableColumn<ObservableList<String>, String> prescriptionColumn = new TableColumn<>("Prescription");
		TableColumn<ObservableList<String>, String> routeUsageColumn = new TableColumn<>("Route Usage");
		TableColumn<ObservableList<String>, String> storagingColumn = new TableColumn<>("Storaging");
		TableColumn<ObservableList<String>, String> strengthColumn = new TableColumn<>("Strength");
		TableColumn<ObservableList<String>, String> dosageFormColumn = new TableColumn<>("Dosage Form");
		TableColumn<ObservableList<String>, String> barcodeColumn = new TableColumn<>("Barcode");
		TableColumn<ObservableList<String>, String> manufacturerIdColumn = new TableColumn<>("Manufacturer ID");

		nameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(0)));
		genericNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(1)));
		manufacturerColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(2)));
		productionDateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(3)));
		expiredDateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(4)));
		priceColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(5)));
		prescriptionColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(6)));
		routeUsageColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(7)));
		storagingColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(8)));
		strengthColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(9)));
		dosageFormColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(10)));
		barcodeColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(11)));
		manufacturerIdColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().get(12)));

		medicineTableView.getColumns().addAll(nameColumn, genericNameColumn, manufacturerColumn, productionDateColumn,
				expiredDateColumn, priceColumn, prescriptionColumn, routeUsageColumn, storagingColumn, strengthColumn,
				dosageFormColumn, barcodeColumn, manufacturerIdColumn);

		// Fetch data from the "medicine" table where stock is 0 and add to TableView
		List<ObservableList<String>> medicineShortageData = getMedicineShortageData();
		medicineTableView.getItems().addAll(medicineShortageData);

		// Add components to the Medicine Shortage VBox
		medicineShortageVBox.getChildren().addAll(medicineShortageTitle, medicineTableView);

		// Create the third VBox for My Pharmacy
		VBox myPharmacyVBox = new VBox(10);
		myPharmacyVBox.setPadding(new Insets(10));
		myPharmacyVBox.setStyle("-fx-border-color: lightgreen; -fx-border-width: 4; -fx-border-radius: 5;");

		// Components for the My Pharmacy VBox
		Text myPharmacyTitle = new Text("My Pharmacy");
		myPharmacyTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

		// Display Total Number of Medicine
		int totalMedicine = getTotalRowCount("medicine");
		Text totalMedicineSubtitle = new Text("Total Number of Medicine: " + totalMedicine);
		totalMedicineSubtitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		totalMedicineSubtitle.setFill(Color.web("#293442"));

		// Display Total Number of Customers
		int totalCustomers = getTotalRowCount("customer");
		Text totalCustomersSubtitle = new Text("Total Number of Customers: " + totalCustomers);
		totalCustomersSubtitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		totalCustomersSubtitle.setFill(Color.web("#293442"));

		// Display Total Number of Suppliers
		int totalSuppliers = getTotalRowCount("medicinecompany");
		Text totalSuppliersSubtitle = new Text("Total Number of Suppliers: " + totalSuppliers);
		totalSuppliersSubtitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		totalSuppliersSubtitle.setFill(Color.web("#293442"));

		// Display Total Number of Medicine Sold
		double totalMedicineSold = getTotalMedicineSold();
		Text totalMedicineSoldSubtitle = new Text("Total Number of Medicine Sold: " + totalMedicineSold);
		totalMedicineSoldSubtitle.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
		totalMedicineSoldSubtitle.setFill(Color.web("#293442"));

		// Add components to the My Pharmacy VBox
		myPharmacyVBox.getChildren().addAll(myPharmacyTitle, totalMedicineSubtitle, totalCustomersSubtitle,
				totalSuppliersSubtitle, totalMedicineSoldSubtitle);

		headerHBox.getChildren().addAll(title, label);
		mainVBox.getChildren().addAll(headerHBox, totalAmountVBox, medicineShortageVBox, myPharmacyVBox);

		ScrollPane scrollPane = new ScrollPane(mainVBox);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		return scrollPane;
	}

	private LineChart<String, Number> createLineChart() {

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();
		LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setLegendVisible(false);
		lineChart.setPrefHeight(200);

		// Set chart title
		lineChart.setTitle("Monthly Sales");

		// Set axis labels
		xAxis.setLabel("Day");
		yAxis.setLabel("Amount");

		// Create series for the chart
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Sales");

		// Add sample data (you will replace this with your actual data)
		series.getData().add(new XYChart.Data<>("1", 1000));
		series.getData().add(new XYChart.Data<>("2", 1200));
		series.getData().add(new XYChart.Data<>("3", 800));
		series.getData().add(new XYChart.Data<>("4", 1500));

		// Add the series to the chart
		lineChart.getData().add(series);

		return lineChart;
	}

	private double getTotalAmountSold(String selectedMonth, String selectedYear, String selectedDay) {
		double totalAmount = 0.0;

		// Convert month name to its numeric representation
		int monthNumber = getMonthNumber(selectedMonth);

		String query = "SELECT SUM(totalAmount) FROM sale WHERE MONTH(sale_date) = ? AND YEAR(sale_date) = ? AND DAY(sale_date) = ?";

		try (Connection connection = DataBaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query)) {

			preparedStatement.setInt(1, monthNumber);
			preparedStatement.setString(2, selectedYear);
			preparedStatement.setString(3, selectedDay);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					totalAmount = resultSet.getDouble(1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return totalAmount;
	}

	private void updateData() {
		String selectedMonth = monthComboBox.getValue();
		String selectedYear = yearComboBox.getValue();
		double totalAmount = getTotalAmountSold(selectedMonth, selectedYear);

		// Clear existing data
		lineChart.getData().clear();

		// Create a new series for the updated data
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Sales");

		// Populate the series with actual data for each day
		for (int i = 1; i <= 31; i++) {
			String day = String.valueOf(i);
			totalAmount = getTotalAmountSold(selectedMonth, selectedYear, day);
			series.getData().add(new XYChart.Data<>(day, totalAmount));
		}

		// Add the new series to the chart
		lineChart.getData().add(series);
	}

	private List<ObservableList<String>> getMedicineShortageData() {
		List<ObservableList<String>> medicineShortageData = new ArrayList<>();

		String query = "SELECT medicine_name, generic_name, manufacturer, production_date, expired_date, "
				+ "price, prescription, route_usage, storaging, strength, dosage_form, barcode, manufacturer_id "
				+ "FROM medicine WHERE stock = 0";

		try (Connection connection = DataBaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				rowData.add(resultSet.getString("medicine_name"));
				rowData.add(resultSet.getString("generic_name"));
				rowData.add(resultSet.getString("manufacturer"));
				rowData.add(resultSet.getString("production_date"));
				rowData.add(resultSet.getString("expired_date"));
				rowData.add(resultSet.getString("price"));
				rowData.add(resultSet.getString("prescription"));
				rowData.add(resultSet.getString("route_usage"));
				rowData.add(resultSet.getString("storaging"));
				rowData.add(resultSet.getString("strength"));
				rowData.add(resultSet.getString("dosage_form"));
				rowData.add(resultSet.getString("barcode"));
				rowData.add(resultSet.getString("manufacturer_id"));

				medicineShortageData.add(rowData);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return medicineShortageData;
	}

	// Method to get total row count for a given table
	private int getTotalRowCount(String tableName) {
		int rowCount = 0;

		String query = "SELECT COUNT(*) FROM " + tableName;

		try (Connection connection = DataBaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				rowCount = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rowCount;
	}

	// Method to get total number of medicine sold
	private double getTotalMedicineSold() {
		double totalMedicineSold = 0.0;

		String query = "SELECT COUNT(*) FROM sale";

		try (Connection connection = DataBaseUtil.getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(query);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			if (resultSet.next()) {
				totalMedicineSold = resultSet.getDouble(1);
			}

		} catch (SQLException e) {
			e.printStackTrace(); // Handle exceptions properly in your application
		}

		return totalMedicineSold;
	}

}
