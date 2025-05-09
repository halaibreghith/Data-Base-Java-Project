package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellProduct extends HBox {

	private TableView<Map<String, String>> tableView = new TableView<>();
	private TextField searchField = new TextField();
	private ChoiceBox<String> searchCharacteristic = new ChoiceBox<>();
	private TableView<Map<String, String>> selectedRowsTableView = new TableView<>();
	private ObservableList<Map<String, String>> data = FXCollections.observableArrayList();
	private ObservableList<Map<String, String>> selectedRowsWithRequiredParams = FXCollections.observableArrayList();
	private Text totalAmount = new Text("0");
	private int currentQuantity = 0;

	public SellProduct(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		getChildren().addAll(sidebar, getMainHBox());

		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				String medicineName = newSelection.get("medicine_name");
				String genericName = newSelection.get("generic_name");
			}
		});
	}

	private VBox getMainHBox() {

		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Sell Product");
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

		headerHBox.getChildren().addAll(title, label);

		Text productsTitle = new Text("Products");
		productsTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		productsTitle.setFill(Color.web("#293442"));

		Text cartTitle = new Text("Cart");
		cartTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		cartTitle.setFill(Color.web("#293442"));

		Text searchText = new Text("Search by");
		searchText.setFont(Font.font("Verdana", 16));
		searchText.setFill(Color.web("#293442"));

		Button addButton = new Button("Add to Cart");
		addButton.setScaleX(2);
		addButton.setScaleY(2);

		TextField quantityTextField = new TextField();
		quantityTextField.setPromptText("Enter Quantity");

		Button sellButton = new Button("Sell");
		sellButton.setScaleX(2);
		sellButton.setScaleY(2);

		try {
			tableView.getColumns().clear();
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DataBaseUtil.getConnection();
			Statement statement = con.createStatement();

			ResultSet resultSet;

			resultSet = statement.executeQuery("SELECT * FROM medicine");
			while (resultSet.next()) {
				Map<String, String> rowData = new HashMap<>();

				rowData.put("medicine_name", resultSet.getString("medicine_name"));
				rowData.put("generic_name", resultSet.getString("generic_name"));
				rowData.put("manufacturer", resultSet.getString("manufacturer"));
				rowData.put("production_date", resultSet.getString("production_date"));
				rowData.put("expired_date", resultSet.getString("expired_date"));
				rowData.put("stock", resultSet.getString("stock"));
				rowData.put("price", resultSet.getString("price"));
				rowData.put("prescription", resultSet.getString("prescription"));
				rowData.put("route_usage", resultSet.getString("route_usage"));
				rowData.put("storaging", resultSet.getString("storaging"));
				rowData.put("strength", resultSet.getString("strength"));
				rowData.put("dosage_form", resultSet.getString("dosage_form"));
				rowData.put("barcode", resultSet.getString("barcode"));
				rowData.put("manufacturer_id", resultSet.getString("manufacturer_id"));

				data.add(rowData);
			}

			if (!data.isEmpty() && data.get(0) != null) {
			    for (String columnName : data.get(0).keySet()) {
			        TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
			        column.setCellValueFactory(new MapValueFactory(columnName));
			        tableView.getColumns().add(column);
			    }
			} else {
			    // Handle the case when the data list is empty or the first element is null
			    // You may show a message or take appropriate action.
			    System.out.println("Data is empty or first element is null.");
			}

			tableView.setItems(data);

			// Set a fixed row height for the TableView
			tableView.setFixedCellSize(25);

			// Set the maximum height for TableView to show only 10 rows
			tableView.setMaxHeight(6 * tableView.getFixedCellSize());

			tableView.setPrefHeight(TableView.USE_COMPUTED_SIZE);

			VBox vBox = new VBox();
			vBox.getChildren().addAll(new HBox(new Label("Search: "), searchField), tableView);

			// Create a ScrollPane and add the VBox to it
			ScrollPane scrollPane = new ScrollPane(vBox);

			// Create a filtered list for searching
			FilteredList<Map<String, String>> filteredData = new FilteredList<>(data, p -> true);

			searchField.setPromptText("Search");
			searchField.setMaxWidth(Region.USE_PREF_SIZE);
			// Add a listener to the search field
			searchField.textProperty()
					.addListener((observable, oldValue, newValue) -> filteredData.setPredicate(item -> {
						if (newValue == null || newValue.isEmpty()) {
							return true; // Show all items when the search field is empty
						}

						// Convert to lowercase for case-insensitive search
						String lowerCaseFilter = newValue.toLowerCase();

						// Check if the medicine_name contains the search query
						return item.get("medicine_name").toLowerCase().contains(lowerCaseFilter);
					}));

			// Wrap the filtered list in a sorted list
			SortedList<Map<String, String>> sortedData = new SortedList<>(filteredData);

			// Bind the sorted list to the TableView
			sortedData.comparatorProperty().bind(tableView.comparatorProperty());
			tableView.setItems(sortedData);
			// Create a ChoiceBox to allow the user to select the search characteristic
			searchCharacteristic.getItems().addAll("medicine_name", "generic_name", "manufacturer", "production_date",
					"expired_date", "stock", "price", "prescription", "route_usage", "storaging", "strength",
					"dosage_form", "barcode", "manufacturer_id");
			searchCharacteristic.setValue("medicine_name"); // Set a default value

			// Add a listener to the choice box to update the search characteristic
			searchCharacteristic.valueProperty().addListener((observable, oldValue, newValue) -> {
				searchField.setPromptText("Search by " + newValue);
				// Update the listener to use the selected characteristic
				searchField.textProperty()
						.addListener((textObservable, textOldValue, textNewValue) -> filteredData.setPredicate(item -> {
							if (textNewValue == null || textNewValue.isEmpty()) {
								return true; // Show all items when the search field is empty
							}

							// Convert to lowercase for case-insensitive search
							String lowerCaseFilter = textNewValue.toLowerCase();

							// Check if the selected characteristic contains the search query
							return item.get(newValue).toLowerCase().contains(lowerCaseFilter);
						}));
			});
			TableColumn<Map<String, String>, String> quantityColumn = new TableColumn<>("quantity");
			quantityColumn.setCellValueFactory(new MapValueFactory("quantity"));
			// Create columns for selectedRowsTableView
			if (!data.isEmpty() && data.get(0) != null) {
			    for (String columnName : data.get(0).keySet()) {
			        TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
			        column.setCellValueFactory(new MapValueFactory(columnName));
			        tableView.getColumns().add(column);
			    }
			} else {
			    // Handle the case when the data list is empty or the first element is null
			    // You may show a message or take appropriate action.
			    System.out.println("Data is empty or first element is null.");

			    // Optionally, you can add a default column or perform other actions.
			    // For example, add a default column:
			    TableColumn<Map<String, String>, String> defaultColumn = new TableColumn<>("Default Column");
			    tableView.getColumns().add(defaultColumn);
			}
			selectedRowsTableView.getColumns().add(quantityColumn);

			// Set the columns for selectedRowsTableView
			selectedRowsTableView.setItems(selectedRowsWithRequiredParams);

			// Set action for the "Add to Cart" button
			addButton.setOnAction(event -> {
				if (Integer.parseInt(quantityTextField.getText()) < 1)
					return;

				String quantityText = quantityTextField.getText();
				if (!quantityText.isEmpty()) {
					currentQuantity = Integer.parseInt(quantityText);
				}

				Map<String, String> productData = tableView.getSelectionModel().getSelectedItem();
				if (productData != null) {
					Map<String, String> secondTableData = new HashMap<>(productData);
					secondTableData.put("quantity", String.valueOf(currentQuantity));
					selectedRowsTableView.getItems().add(secondTableData);
					totalAmount.setText(Double.parseDouble(totalAmount.getText())
							+ (Double.parseDouble(secondTableData.get("price"))
									* Double.parseDouble(secondTableData.get("quantity")))
							+ "");
				}

			});

			// Coloring the table views
			tableView.setStyle("-fx-color: #ADD8E6;");
			selectedRowsTableView.setStyle("-fx-color: #ADD8E6;");

			// Set a fixed row height for the TableView
			selectedRowsTableView.setFixedCellSize(25); // Adjust the height as needed

			// Set the maximum height for TableView to show only 10 rows
			selectedRowsTableView.setMaxHeight(6 * tableView.getFixedCellSize());

			// Set the preferred height to allow the TableView to expand if needed
			selectedRowsTableView.setPrefHeight(TableView.USE_COMPUTED_SIZE);

			// Checkout
			totalAmount.setFont(Font.font("Verdana", 16));
			totalAmount.setFill(Color.web("#293442"));

			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		HBox searchHBox = new HBox(50);
		HBox.setHgrow(searchField, Priority.ALWAYS);
		searchHBox.setAlignment(Pos.CENTER);
		searchHBox.getChildren().addAll(searchText, searchField, searchCharacteristic);

		HBox cartHBox = new HBox(50);
		cartHBox.setAlignment(Pos.CENTER);
		cartHBox.getChildren().addAll(addButton, quantityTextField);

		HBox checkoutHBox = new HBox(40);
		Text totalPriceText = new Text("Total Price: ");
		totalPriceText.setFont(Font.font("Verdana", 16));
		totalPriceText.setFill(Color.web("#293442"));

		sellButton.setOnAction(event -> {
			showCustomerPopup();

		});

		sellButton.getStyleClass().add("button-light-blue");

		checkoutHBox.getChildren().addAll(totalPriceText, totalAmount, sellButton);
		checkoutHBox.setAlignment(Pos.CENTER);

		mainVBox.getChildren().addAll(headerHBox, productsTitle, searchHBox, tableView, cartTitle, cartHBox,
				selectedRowsTableView, checkoutHBox);
		return mainVBox;
	}

	private void showCustomerPopup() {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Select Customer");

		TableView<Map<String, String>> customerTableView = new TableView<>();
		ObservableList<Map<String, String>> customerData = FXCollections.observableArrayList();

		// Retrieve customer data from the "customer" table
		try {
			Connection connection = DataBaseUtil.getConnection();
			Statement statement = connection.createStatement();

			ResultSet resultSet = statement.executeQuery("SELECT * FROM customer");
			while (resultSet.next()) {
				Map<String, String> rowData = new HashMap<>();

				// Add columns to the rowData map
				rowData.put("insurance", resultSet.getString("insurance"));
				rowData.put("allergies", resultSet.getString("allergies"));
				rowData.put("phone_number", resultSet.getString("phone_number"));
				rowData.put("gender", resultSet.getString("gender"));
				rowData.put("birth_date", resultSet.getString("birth_date"));
				rowData.put("customer_id", resultSet.getString("customer_id"));
				rowData.put("first_name", resultSet.getString("first_name"));
				rowData.put("last_name", resultSet.getString("last_name"));

				customerData.add(rowData);
			}

			// Create columns for customerTableView
			for (String columnName : customerData.get(0).keySet()) {
				TableColumn<Map<String, String>, String> column = new TableColumn<>(columnName);
				column.setCellValueFactory(new MapValueFactory(columnName));
				customerTableView.getColumns().add(column);
			}

			customerTableView.setItems(customerData);

			for (Map<String, String> row : selectedRowsTableView.getItems()) {
				// Insert into the "sale" table
				String saleSql = "INSERT INTO sale (sale_date, quantitySold, totalAmount, medicine_id) VALUES (?, ?, ?, ?)";

				try (Connection con = DataBaseUtil.getConnection();
						PreparedStatement preparedStatement = con.prepareStatement(saleSql,
								Statement.RETURN_GENERATED_KEYS)) {

					// Create a Date object with the current date and time
					long currentTimeMillis = System.currentTimeMillis();
					Date currentDate = new Date(currentTimeMillis);

					preparedStatement.setDate(1, currentDate);
					preparedStatement.setInt(2, Integer.parseInt(row.get("quantity")));
					preparedStatement.setDouble(3,
							Double.parseDouble(row.get("price")) * Integer.parseInt(row.get("quantity")));
					preparedStatement.setInt(4, Integer.parseInt(row.get("barcode")));

					int affectedRows = preparedStatement.executeUpdate();

					if (affectedRows > 0) {
						// Retrieve the auto-generated sale_id
						try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
							if (generatedKeys.next()) {
								int saleId = generatedKeys.getInt(1);
								System.out.println("Inserted sale with ID: " + saleId);

								// Sell products for the selected customer
								sellProductsForCustomer(customerTableView.getSelectionModel().getSelectedItem(), saleId, selectedRowsTableView.getItems());
							}
						}
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			selectedRowsTableView.getItems().clear();
			totalAmount.setText("0");

		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Create a "Sell" button within the popup
		Button sellFromCustomerButton = new Button("Sell");
		sellFromCustomerButton.setOnAction(event -> popupStage.close());

		VBox popupLayout = new VBox(10);
		popupLayout.getChildren().addAll(customerTableView, sellFromCustomerButton);
		Scene popupScene = new Scene(popupLayout, 500, 400);
		popupStage.setScene(popupScene);
		popupStage.showAndWait();
	}

	private void sellProductsForCustomer(Map<String, String> customerData, int saleId, List<Map<String, String>> selectedRows) {
	    // Check if a customer is selected
	    if (customerData != null) {
	    	 System.out.println("test");
	        for (Map<String, String> row : selectedRows) {
	           
	            // Insert into the "consumes" table
	            String consumesSql = "INSERT INTO consumes (consumption_date, customer_id, barcode, sale_id) VALUES (?, ?, ?, ?)";
	            System.out.println("consumesSql: " + consumesSql);

	            try (Connection con = DataBaseUtil.getConnection();
	                 PreparedStatement consumesStatement = con.prepareStatement(consumesSql)) {

	                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

	                consumesStatement.setDate(1, currentDate);
	                consumesStatement.setInt(2, Integer.parseInt(customerData.get("customer_id")));
	                consumesStatement.setInt(3, Integer.parseInt(row.get("barcode")));
	                consumesStatement.setInt(4, saleId);

	                consumesStatement.executeUpdate();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}


}
