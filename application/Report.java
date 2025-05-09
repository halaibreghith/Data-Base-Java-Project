package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Report extends HBox {

	private TextField searchField = new TextField();
	private ComboBox<String> optionsComboBox = new ComboBox<>();
	private Connection connection = DataBaseUtil.getConnection();
	private TableView<ObservableList<String>> tableView = new TableView<>();
	private ObservableList<ObservableList<String>> originalData;
	private ComboBox<String> searchColumnComboBox;

	public Report(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		getChildren().addAll(sidebar, getMainHBox());

		// Set the event handler for ComboBox selection
		optionsComboBox.setOnAction(e -> {
			String selectedOption = optionsComboBox.getValue();
			if (selectedOption != null) {
				switch (selectedOption) {
				case "Close Expired Items":
					setupTableAndFetchExpiredItems();
					updateSearchOptionsForExpiredItems();
					break;
				case "Generate Balance Report":
					generateBalanceReport();
					updateSearchOptionsForBalanceReport();
					break;
				case "Low Stock Report":
					setupTableAndFetchLowStockItems();
					updateSearchOptionsForLowStockItems();
					break;
				default:
					System.out.println("Invalid option selected");
				}
			}
		});

		searchField.setPromptText("Search");
		searchField.setMaxWidth(Region.USE_PREF_SIZE);
		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterMedicine(newValue, searchColumnComboBox.getValue());
		});
	}

	private VBox getMainHBox() {
		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Report");
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

		optionsComboBox.getItems().addAll("Close Expired Items", "Generate Balance Report", "Low Stock Report");
		optionsComboBox.setPromptText("Select an option");

		searchColumnComboBox = new ComboBox<>();
		searchColumnComboBox.getItems().addAll("medicine_name", "generic_name", "manufacturer", "production_date",
				"expired_date", "stock", "price", "prescription", "route_usage", "storaging", "strength", "dosage_form",
				"barcode", "manufacturer_id");
		searchColumnComboBox.setPromptText("Select Search Column");

		headerHBox.getChildren().addAll(title, label);

		mainVBox.setAlignment(Pos.CENTER);
		mainVBox.getChildren().addAll(headerHBox, optionsComboBox, tableView, searchField, searchColumnComboBox);
		return mainVBox;
	}

	private void setupTableAndFetchExpiredItems() {
		System.out.println("Fetching expired items...");

		// Get the current date
		LocalDate currentDate = LocalDate.now();

		// Calculate the date 3 months from now
		LocalDate expirationDate = currentDate.plusMonths(3);

		// SQL query to retrieve expired items with stock greater than 0
		String sql = "SELECT medicine_name, generic_name, manufacturer, production_date, expired_date, stock, price, prescription, route_usage, storaging, strength, dosage_form, barcode, manufacturer_id FROM medicine WHERE expired_date <= ? AND stock > 0 ORDER BY expired_date, stock";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setDate(1, java.sql.Date.valueOf(expirationDate));

			// Execute the query and process the result set
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

				// Create table columns dynamically based on the ResultSet metadata
				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					final int colIndex = i;
					TableColumn<ObservableList<String>, String> column = new TableColumn<>(
							resultSet.getMetaData().getColumnName(i));
					column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex - 1)));
					tableView.getColumns().add(column);
				}

				// Add a new column for total cost
				TableColumn<ObservableList<String>, String> totalCostColumn = new TableColumn<>("Total Cost");
				totalCostColumn.setCellValueFactory(param -> {
					String price = param.getValue().get(6);
					String stock = param.getValue().get(5);
					double totalPrice = Double.parseDouble(price) * Integer.parseInt(stock);
					return new SimpleStringProperty(String.valueOf(totalPrice));
				});
				tableView.getColumns().add(totalCostColumn);

				while (resultSet.next()) {
					// Process each row of the result set
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
						row.add(resultSet.getString(i));
					}
					data.add(row);
				}

				// Set the items in the TableView
				tableView.setItems(data);
				originalData = FXCollections.observableArrayList(data);
				tableView.setItems(originalData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void filterMedicine(String keyword, String selectedColumn) {
		if (selectedColumn == null || keyword == null || keyword.trim().isEmpty()) {
			// Reset the filter
			tableView.setItems(originalData);
			return;
		}

		// Create a filtered list based on the selected column
		FilteredList<ObservableList<String>> filteredData = new FilteredList<>(originalData,
				p -> p.get(getColumnIndex(selectedColumn)).toLowerCase().contains(keyword.toLowerCase()));

		// Bind the filtered list to the TableView
		SortedList<ObservableList<String>> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
	}

	// Helper method to get the index of the selected column
	private int getColumnIndex(String selectedColumn) {
		for (int i = 0; i < tableView.getColumns().size(); i++) {
			if (tableView.getColumns().get(i).getText().equals(selectedColumn)) {
				return i;
			}
		}
		return -1; // Column not found
	}

	private void generateBalanceReport() {
	    System.out.println("Generating balance report...");

	    try {
	        // Clear existing columns and items
	        tableView.getColumns().clear();
	        tableView.getItems().clear();

	        // Set up new columns
	        setupTableColumnsForBalanceReport();

	        // Fetch individual pharmacist names and their salaries
	        String salarySql = "SELECT first_name, salary FROM pharmacists";
	        Map<String, Double> salaries = fetchNameAmountMap(salarySql);

	        // Fetch individual medicine names, their prices, and stocks for expired medicines
	        String priceStockSql = "SELECT medicine_name, price, stock FROM medicine WHERE expired_date < CURDATE()";
	        Map<String, Map<String, Double>> expiredMedicineData = fetchNamePriceStockMap(priceStockSql);

	        // Create a unified list of rows for the balance report
	        ObservableList<ObservableList<String>> balanceReportData = FXCollections.observableArrayList();

	        // Add rows for each pharmacist and their salary
	        double totalSalary = 0;
	        for (Map.Entry<String, Double> entry : salaries.entrySet()) {
	            String pharmacistName = entry.getKey();
	            double pharmacistSalary = entry.getValue();
	            balanceReportData.add(createBalanceReportRow(pharmacistName + " Salary", String.valueOf(pharmacistSalary)));
	            totalSalary += pharmacistSalary;
	        }

	        // Add a row for total salary
	        balanceReportData.add(createBalanceReportRow("Total Salary", String.valueOf(totalSalary)));

	        // Add rows for each expired medicine and its cost (considering stock)
	        for (Map.Entry<String, Map<String, Double>> entry : expiredMedicineData.entrySet()) {
	            String medicineName = entry.getKey();
	            double medicinePrice = entry.getValue().get("price");
	            double medicineStock = entry.getValue().get("stock");

	            double totalCost = medicinePrice * medicineStock;
	            balanceReportData.add(createBalanceReportRow(medicineName + " Cost", String.valueOf(totalCost)));
	        }

	        // Calculate total medicine cost separately
	        double totalMedicineCost = expiredMedicineData.values().stream()
	                .mapToDouble(entry -> entry.get("price") * entry.get("stock")).sum();

	        // Add a single row for total medicine cost
	        balanceReportData.add(createBalanceReportRow("Total Medicine Cost", String.valueOf(totalMedicineCost)));

	        // Calculate total sells from the "sale" table
	        String totalSellsSql = "SELECT SUM(totalAmount) AS totalSells FROM sale";
	        double totalSells = fetchTotalSells(totalSellsSql);

	        // Add a single row for total sells
	        balanceReportData.add(createBalanceReportRow("Total Sells", String.valueOf(totalSells)));

	        // Calculate total balance
	        double totalBalance = totalSells - totalSalary - totalMedicineCost;
	        balanceReportData.add(createBalanceReportRow("Total Balance", String.valueOf(totalBalance)));

	        // Set the items in the TableView
	        tableView.setItems(balanceReportData);
	        originalData = FXCollections.observableArrayList(balanceReportData);
	        tableView.setItems(originalData);

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	private double fetchTotalSells(String sql) throws SQLException {
	    try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
	        if (resultSet.next()) {
	            return resultSet.getDouble("totalSells");
	        }
	    }
	    return 0.0;
	}

	private Map<String, Map<String, Double>> fetchNamePriceStockMap(String sql) throws SQLException {
		Map<String, Map<String, Double>> result = new HashMap<>();

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				String name = resultSet.getString("medicine_name");
				double price = resultSet.getDouble("price");
				double stock = resultSet.getDouble("stock");

				Map<String, Double> data = new HashMap<>();
				data.put("price", price);
				data.put("stock", stock);

				result.put(name, data);
			}
		}

		return result;
	}

	// Helper method to create a row for the balance report
	private ObservableList<String> createBalanceReportRow(String description, String amount) {
		ObservableList<String> row = FXCollections.observableArrayList();
		row.add(description);
		row.add(amount);
		return row;
	}

	private Map<String, Double> fetchNameAmountMap(String sql) throws SQLException {
		Map<String, Double> result = new HashMap<>();

		try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				String name = resultSet.getString(1);
				double amount = resultSet.getDouble(2);
				result.put(name, amount);
			}
		}

		return result;
	}

	private void setupTableColumnsForBalanceReport() {
		// Add columns for balance report
		TableColumn<ObservableList<String>, String> column1 = new TableColumn<>("Description");
		column1.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(0)));

		TableColumn<ObservableList<String>, String> column2 = new TableColumn<>("Amount");
		column2.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(1)));

		tableView.getColumns().addAll(column1, column2);
	}

	private void setupTableAndFetchLowStockItems() {
		System.out.println("Fetching low stock items...");

		// SQL query to retrieve low stock items
		String sql = "SELECT medicine_name, generic_name, manufacturer, production_date, expired_date, stock, price, prescription, route_usage, storaging, strength, dosage_form, barcode, manufacturer_id FROM medicine WHERE stock < 20";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			// Execute the query and process the result set
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

				// Create table columns dynamically based on the ResultSet metadata
				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					TableColumn<ObservableList<String>, String> column = new TableColumn<>(
							resultSet.getMetaData().getColumnName(i));
					final int colIndex = i;
					column.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(colIndex - 1)));
					tableView.getColumns().add(column);
				}

				while (resultSet.next()) {
					// Process each row of the result set
					ObservableList<String> row = FXCollections.observableArrayList();
					for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
						row.add(resultSet.getString(i));
					}
					data.add(row);
				}

				// Set the items in the TableView
				tableView.setItems(data);
				originalData = FXCollections.observableArrayList(data);
				tableView.setItems(originalData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void updateSearchOptionsForExpiredItems() {
		// Update searchColumnComboBox options based on the requirements for expired
		// items
		searchColumnComboBox.getItems().clear();
		searchColumnComboBox.getItems().addAll("medicine_name", "generic_name", "expired_date", "price");
		searchColumnComboBox.setPromptText("Select Search Column");
	}

	private void updateSearchOptionsForBalanceReport() {
		// Update searchColumnComboBox options based on the requirements for balance
		// report
		searchColumnComboBox.getItems().clear();
		searchColumnComboBox.getItems().addAll("Description", "Amount"); // Add relevant columns for balance report
		searchColumnComboBox.setPromptText("Select Search Column");
	}

	private void updateSearchOptionsForLowStockItems() {
		// Update searchColumnComboBox options based on the requirements for low stock
		// items
		searchColumnComboBox.getItems().clear();
		searchColumnComboBox.getItems().addAll("medicine_name", "generic_name", "stock", "price");
		searchColumnComboBox.setPromptText("Select Search Column");
	}

}
