package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Orders extends HBox {

	private TextField searchField = new TextField();
	private ComboBox<String> searchOptionComboBox = new ComboBox<>();
	private TableView<ObservableList<String>> orderTable = new TableView<>();
	private TableView<ObservableList<String>> arrivedOrderTable = new TableView<>();
	private Connection connection = DataBaseUtil.getConnection();

	public Orders(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		initializeSearchComboBox();
		initializeOrderTable();

		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Orders");
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

		Text inventoryTitle = new Text("Pending Orders");
		inventoryTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		inventoryTitle.setFill(Color.web("#293442"));

		Text searchText = new Text("Search by");
		searchText.setFont(Font.font("Verdana", 16));
		searchText.setFill(Color.web("#293442"));

		HBox searchHBox = new HBox(20);
		searchHBox.getChildren().addAll(inventoryTitle, searchText, searchOptionComboBox, searchField);
		searchHBox.setAlignment(Pos.CENTER);

		searchField.setPromptText("Search");

		Button addButton = new Button("Add Order");
		addButton.setOnAction(event -> openAddOrderPopup());
		addButton.setScaleX(2);
		addButton.setScaleY(2);

		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> openEditOrderPopup());
		editButton.setScaleX(2);
		editButton.setScaleY(2);

		Button arrivedButton = new Button("Arrived");
		arrivedButton.setOnAction(e -> deleteSelectedOrder());
		arrivedButton.setScaleX(2);
		arrivedButton.setScaleY(2);

		HBox buttonsHBox = new HBox(100);
		buttonsHBox.getChildren().addAll(addButton, editButton, arrivedButton);
		buttonsHBox.setAlignment(Pos.CENTER);

		// Arrived Orders Table
		Text arrivedOrdersTitle = new Text("Arrived Orders");
		arrivedOrdersTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		arrivedOrdersTitle.setFill(Color.web("#293442"));

		
		initializeArrivedOrderTable(arrivedOrderTable);

		VBox arrivedOrdersVBox = new VBox(10);
		arrivedOrdersVBox.getChildren().addAll(arrivedOrdersTitle, arrivedOrderTable);

		searchOptionComboBox.setOnAction(event -> fetchOrderData());
		searchField.textProperty().addListener((observable, oldValue, newValue) -> fetchOrderData());
		searchField.setMaxWidth(Region.USE_PREF_SIZE);

		mainVBox.getChildren().addAll(headerHBox, searchHBox, orderTable, buttonsHBox, arrivedOrdersVBox);
		mainVBox.setAlignment(Pos.CENTER);

		getChildren().addAll(sidebar, mainVBox);

		fetchOrderData();
		fetchArrivedOrderData();
	}

	private void initializeSearchComboBox() {
		// Modify this based on your "Orders" table columns
		searchOptionComboBox.getItems().addAll("order_id", "quantity", "manufacturer_id", "medicine_id");
		searchOptionComboBox.setValue("order_id"); // Default selection
	}

	private void initializeOrderTable() {
		// Clear existing columns
		orderTable.getColumns().clear();

		// Assuming column names match with the database columns
		for (String columnName : searchOptionComboBox.getItems()) {
			// Modify this based on your "Orders" table columns
			TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
			column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
					param.getValue().get(orderTable.getColumns().indexOf(column))));
			orderTable.getColumns().add(column);
		}

		orderTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void fetchOrderData() {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			// Update the SQL query to select only rows where arrived is false
			String selectedOption = searchOptionComboBox.getValue();
			String query = "SELECT * FROM orders WHERE " + selectedOption + " LIKE ? AND arrived = false";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "%" + searchField.getText() + "%");

			resultSet = preparedStatement.executeQuery();

			orderTable.getItems().clear();

			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				for (String columnName : searchOptionComboBox.getItems()) {
					rowData.add(resultSet.getString(columnName));
				}
				orderTable.getItems().add(rowData);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void deleteSelectedOrder() {
		ObservableList<String> selectedRow = orderTable.getSelectionModel().getSelectedItem();

		if (selectedRow != null) {
			String orderId = selectedRow.get(0);

			updateOrderArrived(orderId);
		} else {
			System.out.println("Please select a row to update.");
		}
		fetchArrivedOrderData();
	}

	private void updateOrderArrived(String orderId) {
		try {
			int order_id = Integer.parseInt(orderId);

			// Get the current date
			java.sql.Date arrivalDate = new java.sql.Date(System.currentTimeMillis());

			String query = "UPDATE orders SET arrived = true, arrival_date = ? WHERE order_id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				preparedStatement.setDate(1, arrivalDate);
				preparedStatement.setInt(2, order_id);
				preparedStatement.executeUpdate();
			}

			// Refresh the displayed data after updating the arrived status
			fetchOrderData();
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void openAddOrderPopup() {
		Stage popupStage = new Stage();
		popupStage.setTitle("Add Order");

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		TextField quantityField = new TextField();
		TextField manufacturerIdField = new TextField();
		TextField medicineIdField = new TextField();

		Label quantityLabel = new Label("Quantity:");
		Label manufacturerIdLabel = new Label("Manufacturer ID:");
		Label medicineIdLabel = new Label("Medicine ID:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		quantityLabel.setPadding(labelMargin);
		manufacturerIdLabel.setPadding(labelMargin);
		medicineIdLabel.setPadding(labelMargin);

		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Retrieve the field values and call the addOrder method
			String[] fieldValues = { quantityField.getText(), manufacturerIdField.getText(),
					medicineIdField.getText() };
			addOrder(fieldValues);

			popupStage.close(); // Close the popup after adding the order
		});

		popupContent.getChildren().addAll(new HBox(quantityLabel, quantityField),
				new HBox(manufacturerIdLabel, manufacturerIdField), new HBox(medicineIdLabel, medicineIdField),
				addButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(300);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void addOrder(String[] fieldValues) {
		try {
			// Construct the SQL query dynamically for the "Orders" table
			StringBuilder queryBuilder = new StringBuilder(
					"INSERT INTO orders (quantity, manufacturer_id, medicine_id) VALUES (?, ?, ?)");

			String query = queryBuilder.toString();

			// Prepare the statement
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				// Set parameter values
				for (int i = 0; i < fieldValues.length; i++) {
					preparedStatement.setString(i + 1, fieldValues[i]);
				}

				// Execute the update
				preparedStatement.executeUpdate();
			}

			// Refresh the displayed data after adding an order
			fetchOrderData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void openEditOrderPopup() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = orderTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Open the popup with the selected data for editing
			openAddEditOrderPopup("Edit Order", "Edit", selectedRow);
		} else {
			System.out.println("Please select a row to edit.");
		}
	}

	private void openAddEditOrderPopup(String title, String buttonText, ObservableList<String> rowData) {
		Stage popupStage = new Stage();
		popupStage.setTitle(title);

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		TextField quantityField = new TextField();
		TextField manufacturerIdField = new TextField();
		TextField medicineIdField = new TextField();

		Label quantityLabel = new Label("Quantity:");
		Label manufacturerIdLabel = new Label("Manufacturer ID:");
		Label medicineIdLabel = new Label("Medicine ID:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		quantityLabel.setPadding(labelMargin);
		manufacturerIdLabel.setPadding(labelMargin);
		medicineIdLabel.setPadding(labelMargin);

		// Set the fields based on the rowData for editing
		quantityField.setText(rowData.get(0));
		manufacturerIdField.setText(rowData.get(2));
		medicineIdField.setText(rowData.get(1));

		Button editButton = new Button(buttonText);
		editButton.setOnAction(e -> {
			// Retrieve the field values and call the editOrder method
			String[] fieldValues = { quantityField.getText(), manufacturerIdField.getText(),
					medicineIdField.getText() };
			editOrder(rowData, fieldValues);

			popupStage.close(); // Close the popup after editing the order
		});

		popupContent.getChildren().addAll(new HBox(quantityLabel, quantityField),
				new HBox(manufacturerIdLabel, manufacturerIdField), new HBox(medicineIdLabel, medicineIdField),
				editButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(300);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void editOrder(ObservableList<String> rowData, String[] fieldValues) {
		try {
			// Retrieve the order_id or a unique identifier from the selected row
			String orderId = rowData.get(0);

			// Parse the order_id to an integer
			int orderIdInt = Integer.parseInt(orderId);

			// Construct the SQL query dynamically for updating an order by ID
			StringBuilder queryBuilder = new StringBuilder("UPDATE orders SET ");
			queryBuilder.append("quantity = ?, manufacturer_id = ?, medicine_id = ? ");
			queryBuilder.append("WHERE order_id = ?");

			String query = queryBuilder.toString();

			// Prepare the statement
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				// Set parameter values
				for (int i = 0; i < fieldValues.length; i++) {
					preparedStatement.setString(i + 1, fieldValues[i]);
				}
				// Set the last parameter to the ID
				preparedStatement.setInt(fieldValues.length + 1, orderIdInt);

				// Execute the update
				preparedStatement.executeUpdate();
			}

			// Refresh the displayed data after editing an order
			fetchOrderData();
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void initializeArrivedOrderTable(TableView<ObservableList<String>> table) {
		// Clear existing columns
		table.getColumns().clear();

		for (String columnName : searchOptionComboBox.getItems()) {
			TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
			column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
					param.getValue().get(table.getColumns().indexOf(column))));
			table.getColumns().add(column);
		}

		// Add column for arrival date
		TableColumn<ObservableList<String>, String> arrivalDateColumn = new TableColumn<>("Arrival Date");
		arrivalDateColumn.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
				param.getValue().get(table.getColumns().indexOf(arrivalDateColumn))));
		table.getColumns().add(arrivalDateColumn);

		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void fetchArrivedOrderData() {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			// Update the SQL query to select only rows where arrived is true
			String selectedOption = searchOptionComboBox.getValue();
			String query = "SELECT * FROM orders WHERE " + selectedOption + " LIKE ? AND arrived = true";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "%" + searchField.getText() + "%");

			resultSet = preparedStatement.executeQuery();

			arrivedOrderTable.getItems().clear();

			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				for (String columnName : searchOptionComboBox.getItems()) {
					rowData.add(resultSet.getString(columnName));
				}
				// Add the arrival date to the row data
				rowData.add(resultSet.getString("arrival_date"));
				arrivedOrderTable.getItems().add(rowData);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

}
