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

public class Inventory extends HBox {

	private TextField searchField = new TextField();
	private ComboBox<String> searchOptionComboBox = new ComboBox<>();
	private TableView<ObservableList<String>> medicineTable = new TableView<>();
	private Connection connection = DataBaseUtil.getConnection();

	public Inventory(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		initializeSearchComboBox();
		initializeMedicineTable();

		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Inventory");
		title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		title.setFill(Color.web("#293442"));

		Label label = new Label("Log Out");
		label.setStyle("-fx-text-fill: #293442;");
		label.setGraphic(
				new ImageView(new Image("file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/inventory.png")));
		label.setGraphicTextGap(10);
		label.setCursor(Cursor.HAND);

		label.setOnMouseClicked(event -> {
			Main.closeStage();
			Stage primaryStage = new Stage();
			LogIn log = new LogIn();
			log.mainScreen(primaryStage);

		});
		
		headerHBox.getChildren().addAll(title, label);

		Text inventoryTitle = new Text("Inventory");
		inventoryTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		inventoryTitle.setFill(Color.web("#293442"));

		Text searchText = new Text("Search by");
		searchText.setFont(Font.font("Verdana", 16));
		searchText.setFill(Color.web("#293442"));

		searchField.setPromptText("Search");

		// Create "Add" button
		Button addButton = new Button("Add Medicine");
		addButton.setOnAction(event -> openAddMedicinePopup());
		addButton.setScaleX(2);
		addButton.setScaleY(2);

		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> openEditMedicinePopup());
		editButton.setScaleX(2);
		editButton.setScaleY(2);

		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteSelectedMedicine());
		deleteButton.setScaleX(2);
		deleteButton.setScaleY(2);
		
		HBox buttonsHBox = new HBox(100);
		buttonsHBox.getChildren().addAll(addButton, editButton, deleteButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		
		searchOptionComboBox.setOnAction(event -> fetchMedicineData());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> fetchMedicineData());
		searchField.setMaxWidth(Region.USE_PREF_SIZE);


		mainVBox.setAlignment(Pos.TOP_CENTER);
		mainVBox.getChildren().addAll(headerHBox, inventoryTitle, searchText, searchOptionComboBox, searchField,
				medicineTable, buttonsHBox);

		getChildren().addAll(sidebar, mainVBox);

		fetchMedicineData();
	}

	private void initializeSearchComboBox() {
		searchOptionComboBox.getItems().addAll("medicine_name", "generic_name", "manufacturer", "production_date",
				"expired_date", "stock", "price", "prescription", "route_usage", "storaging", "strength", "dosage_form",
				"barcode");
		searchOptionComboBox.setValue("medicine_name"); // Default selection
	}

	private void initializeMedicineTable() {
		// Clear existing columns
		medicineTable.getColumns().clear();

		// Assuming column names match with the database columns
		for (String columnName : searchOptionComboBox.getItems()) {
			TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
			column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
					param.getValue().get(medicineTable.getColumns().indexOf(column))));
			medicineTable.getColumns().add(column);
		}

		medicineTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void fetchMedicineData() {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			// Prepare the query based on the selected search option
			String selectedOption = searchOptionComboBox.getValue();
			String query = "SELECT * FROM medicine WHERE " + selectedOption + " LIKE ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "%" + searchField.getText() + "%");

			// Execute the query and get the result set
			resultSet = preparedStatement.executeQuery();

			// Clear existing data in the table
			medicineTable.getItems().clear();

			// Populate the TableView with the data
			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				for (String columnName : searchOptionComboBox.getItems()) {
					rowData.add(resultSet.getString(columnName));
				}
				medicineTable.getItems().add(rowData);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				// Close resources in the finally block
				if (resultSet != null) {
					resultSet.close();
				}
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				// Handle SQLException appropriately
			}
		}
	}

	private String[] labels = { "Medicine Name:", "Generic Name:", "Manufacturer:", "Production Date:", "Expired Date:",
			"Stock:", "Price:", "Prescription:", "Route Usage:", "Storaging:", "Strength:", "Dosage Form:",
			"Barcode:" };

	private void openAddMedicinePopup() {
		Stage popupStage = new Stage();
		popupStage.setTitle("Add Medicine");

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields
		TextField medicineNameField = new TextField();
		TextField genericNameField = new TextField();
		TextField manufacturerField = new TextField();
		TextField productionDateField = new TextField();
		TextField expiredDateField = new TextField();
		TextField stockField = new TextField();
		TextField priceField = new TextField();
		TextField prescriptionField = new TextField();
		TextField routeUsageField = new TextField();
		TextField storagingField = new TextField();
		TextField strengthField = new TextField();
		TextField dosageFormField = new TextField();
		TextField barcodeField = new TextField();

		// Labels
		Label medicineNameLabel = new Label("Medicine Name:");
		Label genericNameLabel = new Label("Generic Name:");
		Label manufacturerLabel = new Label("Manufacturer:");
		Label productionDateLabel = new Label("Production Date:");
		Label expiredDateLabel = new Label("Expired Date:");
		Label stockLabel = new Label("Stock:");
		Label priceLabel = new Label("Price:");
		Label prescriptionLabel = new Label("Prescription:");
		Label routeUsageLabel = new Label("Route Usage:");
		Label storagingLabel = new Label("Storaging:");
		Label strengthLabel = new Label("Strength:");
		Label dosageFormLabel = new Label("Dosage Form:");
		Label barcodeLabel = new Label("Barcode:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		medicineNameLabel.setPadding(new Insets(0, 10, 0, 0));
		genericNameLabel.setPadding(labelMargin);
		manufacturerLabel.setPadding(labelMargin);
		productionDateLabel.setPadding(labelMargin);
		expiredDateLabel.setPadding(labelMargin);
		stockLabel.setPadding(labelMargin);
		priceLabel.setPadding(labelMargin);
		prescriptionLabel.setPadding(labelMargin);
		routeUsageLabel.setPadding(labelMargin);
		storagingLabel.setPadding(labelMargin);
		strengthLabel.setPadding(labelMargin);
		dosageFormLabel.setPadding(labelMargin);
		barcodeLabel.setPadding(labelMargin);

		// Button
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Retrieve the field values and call the addMedicine method
			String[] fieldValues = { medicineNameField.getText(), genericNameField.getText(),
					manufacturerField.getText(), productionDateField.getText(), expiredDateField.getText(),
					stockField.getText(), priceField.getText(), prescriptionField.getText(), routeUsageField.getText(),
					storagingField.getText(), strengthField.getText(), dosageFormField.getText(),
					barcodeField.getText() };
			addMedicine(fieldValues);

			popupStage.close(); // Close the popup after adding the medicine
		});

		popupContent.getChildren().addAll(new HBox(medicineNameLabel, medicineNameField),
				new HBox(genericNameLabel, genericNameField), new HBox(manufacturerLabel, manufacturerField),
				new HBox(productionDateLabel, productionDateField), new HBox(expiredDateLabel, expiredDateField),
				new HBox(stockLabel, stockField), new HBox(priceLabel, priceField),
				new HBox(prescriptionLabel, prescriptionField), new HBox(routeUsageLabel, routeUsageField),
				new HBox(storagingLabel, storagingField), new HBox(strengthLabel, strengthField),
				new HBox(dosageFormLabel, dosageFormField), new HBox(barcodeLabel, barcodeField), addButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void addMedicine(String[] fieldValues) {
		try {
			// Construct the SQL query dynamically
			StringBuilder queryBuilder = new StringBuilder("INSERT INTO medicine (");
			for (String label : labels) {
				queryBuilder.append(label.toLowerCase().replace(":", "").replace(" ", "_")).append(",");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(") VALUES (");
			for (String ignored : fieldValues) {
				queryBuilder.append("?,");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(")");

			String query = queryBuilder.toString();

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			for (int i = 0; i < fieldValues.length; i++) {
				preparedStatement.setString(i + 1, fieldValues[i]);
			}

			// Execute the INSERT query
			preparedStatement.executeUpdate();

			// Close resources
			preparedStatement.close();

			// Refresh the table to display the new data
			fetchMedicineData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void openEditMedicinePopup() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = medicineTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Open the popup with the selected data for editing
			openAddEditMedicinePopup("Edit Medicine", "Edit", selectedRow);
		} else {
			// Display an error message or handle the case where no row is selected
			System.out.println("Please select a row to edit.");
		}
	}

	private void openAddEditMedicinePopup(String title, String buttonText, ObservableList<String> rowData) {
		Stage popupStage = new Stage();
		popupStage.setTitle(title);

		VBox popupContent = new VBox(10); // Adjust the spacing as needed
		popupContent.setPadding(new Insets(10));

		// Fields
		TextField medicineNameField = new TextField();
		TextField genericNameField = new TextField();
		TextField manufacturerField = new TextField();
		TextField productionDateField = new TextField();
		TextField expiredDateField = new TextField();
		TextField stockField = new TextField();
		TextField priceField = new TextField();
		TextField prescriptionField = new TextField();
		TextField routeUsageField = new TextField();
		TextField storagingField = new TextField();
		TextField strengthField = new TextField();
		TextField dosageFormField = new TextField();
		TextField barcodeField = new TextField();

		// Labels
		Label medicineNameLabel = new Label("Medicine Name:");
		Label genericNameLabel = new Label("Generic Name:");
		Label manufacturerLabel = new Label("Manufacturer:");
		Label productionDateLabel = new Label("Production Date:");
		Label expiredDateLabel = new Label("Expired Date:");
		Label stockLabel = new Label("Stock:");
		Label priceLabel = new Label("Price:");
		Label prescriptionLabel = new Label("Prescription:");
		Label routeUsageLabel = new Label("Route Usage:");
		Label storagingLabel = new Label("Storaging:");
		Label strengthLabel = new Label("Strength:");
		Label dosageFormLabel = new Label("Dosage Form:");
		Label barcodeLabel = new Label("Barcode:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		medicineNameLabel.setPadding(labelMargin);
		genericNameLabel.setPadding(labelMargin);
		manufacturerLabel.setPadding(labelMargin);
		productionDateLabel.setPadding(labelMargin);
		expiredDateLabel.setPadding(labelMargin);
		stockLabel.setPadding(labelMargin);
		priceLabel.setPadding(labelMargin);
		prescriptionLabel.setPadding(labelMargin);
		routeUsageLabel.setPadding(labelMargin);
		storagingLabel.setPadding(labelMargin);
		strengthLabel.setPadding(labelMargin);
		dosageFormLabel.setPadding(labelMargin);
		barcodeLabel.setPadding(labelMargin);

		// Set the fields based on the rowData for editing
		medicineNameField.setText(rowData.get(0));
		genericNameField.setText(rowData.get(1));
		manufacturerField.setText(rowData.get(2));
		productionDateField.setText(rowData.get(3));
		expiredDateField.setText(rowData.get(4));
		stockField.setText(rowData.get(5));
		priceField.setText(rowData.get(6));
		prescriptionField.setText(rowData.get(7));
		routeUsageField.setText(rowData.get(8));
		storagingField.setText(rowData.get(9));
		strengthField.setText(rowData.get(10));
		dosageFormField.setText(rowData.get(11));
		barcodeField.setText(rowData.get(12));

		// Button
		Button editButton = new Button(buttonText);
		editButton.setOnAction(e -> {
			// Retrieve the field values and call the editMedicine method
			String[] fieldValues = { medicineNameField.getText(), genericNameField.getText(),
					manufacturerField.getText(), productionDateField.getText(), expiredDateField.getText(),
					stockField.getText(), priceField.getText(), prescriptionField.getText(), routeUsageField.getText(),
					storagingField.getText(), strengthField.getText(), dosageFormField.getText(),
					barcodeField.getText() };
			editMedicine(rowData, fieldValues);

			popupStage.close(); // Close the popup after editing the medicine
		});

		popupContent.getChildren().addAll(new HBox(medicineNameLabel, medicineNameField),
				new HBox(genericNameLabel, genericNameField), new HBox(manufacturerLabel, manufacturerField),
				new HBox(productionDateLabel, productionDateField), new HBox(expiredDateLabel, expiredDateField),
				new HBox(stockLabel, stockField), new HBox(priceLabel, priceField),
				new HBox(prescriptionLabel, prescriptionField), new HBox(routeUsageLabel, routeUsageField),
				new HBox(storagingLabel, storagingField), new HBox(strengthLabel, strengthField),
				new HBox(dosageFormLabel, dosageFormField), new HBox(barcodeLabel, barcodeField), editButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500); // Adjust the height as needed

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void editMedicine(ObservableList<String> originalData, String[] newFieldValues) {
		try {
			// Construct the SQL query dynamically
			StringBuilder queryBuilder = new StringBuilder("UPDATE medicine SET ");
			for (String label : labels) {
				queryBuilder.append(label.toLowerCase().replace(":", "").replace(" ", "_")).append("=?,");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(" WHERE ");
			
			queryBuilder.append(labels[0].toLowerCase().replace(":", "").replace(" ", "_")).append("=?");

			String query = queryBuilder.toString();

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			for (int i = 0; i < newFieldValues.length; i++) {
				preparedStatement.setString(i + 1, newFieldValues[i]);
			}
			// Set the primary key value for the WHERE clause
			preparedStatement.setString(newFieldValues.length + 1, originalData.get(0));

			// Execute the UPDATE query
			preparedStatement.executeUpdate();

			// Close resources
			preparedStatement.close();

			// Refresh the table to display the updated data
			fetchMedicineData();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQLException appropriately
		}
	}

	private void deleteSelectedMedicine() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = medicineTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Get the primary key value (assuming it's the first column)
			String primaryKeyValue = selectedRow.get(0);

			// Call the method to delete the selected row from the database
			deleteMedicine(primaryKeyValue);
		} else {
			// No Row Selected
			System.out.println("Please select a row to delete.");
		}
	}

	private void deleteMedicine(String primaryKeyValue) {
		try {
			// Construct the SQL query dynamically
			String query = "DELETE FROM medicine WHERE " + labels[0].toLowerCase().replace(":", "").replace(" ", "_")
					+ "=?";

			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, primaryKeyValue);

			// Execute the DELETE query
			int rowsAffected = preparedStatement.executeUpdate();

			// Close resources
			preparedStatement.close();

			// Check if the deletion was successful
			if (rowsAffected > 0) {
				System.out.println("Medicine deleted successfully!");
				// Refresh the table to display the updated data
				fetchMedicineData();
			} else {
				System.out.println("Failed to delete medicine. No matching record found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
