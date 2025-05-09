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

public class Suppliers extends HBox {

	private TextField searchField = new TextField();
	private ComboBox<String> searchOptionComboBox = new ComboBox<>();
	private TableView<ObservableList<String>> suppliersTable = new TableView<>();
	private Connection connection = DataBaseUtil.getConnection();
	private ObservableList<String> selectedRow;

	public Suppliers(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		initializeSearchComboBox();
		initializeSupplierTable();

		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Suppliers");
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

		Text inventoryTitle = new Text("Suppliers");
		inventoryTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		inventoryTitle.setFill(Color.web("#293442"));

		Text searchText = new Text("Search by");
		searchText.setFont(Font.font("Verdana", 16));
		searchText.setFill(Color.web("#293442"));

		searchField.setPromptText("Search");

		// Create "Add" button for supplier
		Button addButton = new Button("Add Supplier");
		addButton.setOnAction(event -> openAddSupplierPopup());
		addButton.setScaleX(2);
		addButton.setScaleY(2);

		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> openAddEditSupplierPopup("Edit Supplier", "Save Changes", selectedRow));

		editButton.setScaleX(2);
		editButton.setScaleY(2);

		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteSelectedSupplier());
		deleteButton.setScaleX(2);
		deleteButton.setScaleY(2);

		HBox buttonsHBox = new HBox(100);
		buttonsHBox.getChildren().addAll(addButton, editButton, deleteButton);
		buttonsHBox.setAlignment(Pos.CENTER);

		searchOptionComboBox.setOnAction(event -> fetchSupplierData());
		searchField.textProperty().addListener((observable, oldValue, newValue) -> fetchSupplierData());
		searchField.setMaxWidth(Region.USE_PREF_SIZE);

		mainVBox.getChildren().addAll(headerHBox, inventoryTitle, searchText, searchOptionComboBox, searchField,
				suppliersTable, buttonsHBox);
		mainVBox.setAlignment(Pos.CENTER);

		getChildren().addAll(sidebar, mainVBox);

		fetchSupplierData();
		
		suppliersTable.setOnMouseClicked(event -> {
		    selectedRow = suppliersTable.getSelectionModel().getSelectedItem();
		});
	}

	private void initializeSearchComboBox() {
		searchOptionComboBox.getItems().addAll("address", "name_company", "phone_number", "email", "manufacturer_id");
		searchOptionComboBox.setValue("name_company"); // Default selection
	}

	private void initializeSupplierTable() {
		// Clear existing columns
		suppliersTable.getColumns().clear();

		for (String columnName : searchOptionComboBox.getItems()) {
			TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
			column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
					param.getValue().get(suppliersTable.getColumns().indexOf(column))));
			suppliersTable.getColumns().add(column);
		}

		suppliersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void fetchSupplierData() {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			// Prepare the query based on the selected search option
			String selectedOption = searchOptionComboBox.getValue();
			String query = "SELECT * FROM medicinecompany WHERE " + selectedOption + " LIKE ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "%" + searchField.getText() + "%");

			// Execute the query and get the result set
			resultSet = preparedStatement.executeQuery();

			// Clear existing data in the table
			suppliersTable.getItems().clear();

			// Populate the TableView with the data
			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				for (String columnName : searchOptionComboBox.getItems()) {
					rowData.add(resultSet.getString(columnName));
				}
				suppliersTable.getItems().add(rowData);
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

	private void openAddSupplierPopup() {
		Stage popupStage = new Stage();
		popupStage.setTitle("Add Supplier");

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields for suppliers
		TextField addressField = new TextField();
		TextField nameCompanyField = new TextField();
		TextField phoneNumberField = new TextField();
		TextField emailField = new TextField();
		TextField manufacturerIdField = new TextField();

		// Labels for suppliers
		Label addressLabel = new Label("Address:");
		Label nameCompanyLabel = new Label("Name Company:");
		Label phoneNumberLabel = new Label("Phone Number:");
		Label emailLabel = new Label("Email:");
		Label manufacturerIdLabel = new Label("Manufacturer ID:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		addressLabel.setPadding(labelMargin);
		nameCompanyLabel.setPadding(labelMargin);
		phoneNumberLabel.setPadding(labelMargin);
		emailLabel.setPadding(labelMargin);
		manufacturerIdLabel.setPadding(labelMargin);

		// Button
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Retrieve the field values and call the addSupplier method
			String[] fieldValues = { addressField.getText(), nameCompanyField.getText(), phoneNumberField.getText(),
					emailField.getText(), manufacturerIdField.getText() };
			addSupplier(fieldValues);

			popupStage.close(); // Close the popup after adding the supplier
		});

		popupContent.getChildren().addAll(new HBox(addressLabel, addressField),
				new HBox(nameCompanyLabel, nameCompanyField), new HBox(phoneNumberLabel, phoneNumberField),
				new HBox(emailLabel, emailField), new HBox(manufacturerIdLabel, manufacturerIdField), addButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void addSupplier(String[] fieldValues) {
		try {
			// Construct the SQL query dynamically for the medicinecompany table
			StringBuilder queryBuilder = new StringBuilder("INSERT INTO medicinecompany (");
			for (String columnName : searchOptionComboBox.getItems()) {
				queryBuilder.append(columnName.toLowerCase()).append(",");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(") VALUES (");
			for (String ignored : fieldValues) {
				queryBuilder.append("?,");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(")");

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

			// Refresh the displayed data after adding a supplier
			fetchSupplierData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void openAddEditSupplierPopup(String title, String buttonText, ObservableList<String> rowData) {
		Stage popupStage = new Stage();
		popupStage.setTitle(title);

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields for suppliers
		TextField addressField = new TextField();
		TextField nameCompanyField = new TextField();
		TextField phoneNumberField = new TextField();
		TextField emailField = new TextField();
		TextField manufacturerIdField = new TextField();

		// Labels for suppliers
		Label addressLabel = new Label("Address:");
		Label nameCompanyLabel = new Label("Name Company:");
		Label phoneNumberLabel = new Label("Phone Number:");
		Label emailLabel = new Label("Email:");
		Label manufacturerIdLabel = new Label("Manufacturer ID:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		addressLabel.setPadding(labelMargin);
		nameCompanyLabel.setPadding(labelMargin);
		phoneNumberLabel.setPadding(labelMargin);
		emailLabel.setPadding(labelMargin);
		manufacturerIdLabel.setPadding(labelMargin);

		// Set the fields based on the rowData for editing
		addressField.setText(rowData.get(0));
		nameCompanyField.setText(rowData.get(1));
		phoneNumberField.setText(rowData.get(2));
		emailField.setText(rowData.get(3));
		manufacturerIdField.setText(rowData.get(4));

		// Button
		Button editButton = new Button(buttonText);
		editButton.setOnAction(e -> {
			// Retrieve the field values and call the editSupplier method
			String[] fieldValues = { addressField.getText(), nameCompanyField.getText(), phoneNumberField.getText(),
					emailField.getText(), manufacturerIdField.getText() };
			editSupplier(rowData, fieldValues);

			popupStage.close(); // Close the popup after editing the supplier
		});

		popupContent.getChildren().addAll(new HBox(addressLabel, addressField),
				new HBox(nameCompanyLabel, nameCompanyField), new HBox(phoneNumberLabel, phoneNumberField),
				new HBox(emailLabel, emailField), new HBox(manufacturerIdLabel, manufacturerIdField), editButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void editSupplier(ObservableList<String> rowData, String[] fieldValues) {
	    try {
	        // Retrieve the manufacturer_id or a unique identifier from the selected row
	        String manufacturerId = rowData.get(4); 

	        // Parse the manufacturer_id to an integer
	        int supplierId = Integer.parseInt(manufacturerId);

	        // Construct the SQL query dynamically for updating a supplier by ID
	        StringBuilder queryBuilder = new StringBuilder("UPDATE medicinecompany SET ");
	        for (String columnName : searchOptionComboBox.getItems()) {
	            queryBuilder.append(columnName.toLowerCase()).append(" = ?,");
	        }
	        queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
	        queryBuilder.append(" WHERE manufacturer_id = ?");

	        String query = queryBuilder.toString();

	        // Prepare the statement
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            // Set parameter values
	            for (int i = 0; i < fieldValues.length; i++) {
	                preparedStatement.setString(i + 1, fieldValues[i]);
	            }
	            // Set the last parameter to the ID
	            preparedStatement.setInt(fieldValues.length + 1, supplierId);  // Updated index

	            // Execute the update
	            preparedStatement.executeUpdate();
	        }

	        // Refresh the displayed data after editing a supplier
	        fetchSupplierData();
	    } catch (SQLException | NumberFormatException e) {
	        e.printStackTrace();
	    }
	}
	

	private void deleteSelectedSupplier() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = suppliersTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Call the deleteSupplier method
			deleteSupplier(selectedRow);
		} else {
			// Display an error message or handle the case where no row is selected
			System.out.println("Please select a row to delete.");
		}
	}

	private void deleteSupplier(ObservableList<String> rowData) {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = suppliersTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			try {
				// Assuming the unique identifier (ID) is in the fifth column
				String manufacturerId = selectedRow.get(4);

				// Parse the manufacturer_id to an integer
				int supplierId = Integer.parseInt(manufacturerId);

				// Construct the SQL query dynamically for deleting a supplier by ID
				String query = "DELETE FROM medicinecompany WHERE manufacturer_id = ?";

				// Prepare the statement
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					// Set parameter value
					preparedStatement.setInt(1, supplierId);

					// Execute the deletion
					preparedStatement.executeUpdate();
				}

				// Refresh the displayed data after deleting a supplier
				fetchSupplierData();
			} catch (SQLException | NumberFormatException e) {
				e.printStackTrace();
			}
		} else {
			// Display an error message or handle the case where no row is selected
			System.out.println("Please select a row to delete.");
		}
	}

}
