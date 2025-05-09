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
import javafx.scene.control.PasswordField;
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

public class Pharmacists extends HBox {

	private TextField searchField = new TextField();
	private ComboBox<String> searchOptionComboBox = new ComboBox<>();
	private TableView<ObservableList<String>> pharmacistTable = new TableView<>();
	private Connection connection = DataBaseUtil.getConnection();

	public Pharmacists(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		initializeSearchComboBox();
		initializePharmacistTable();

		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Pharmacists");
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

		Text inventoryTitle = new Text("Pharmacists");
		inventoryTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		inventoryTitle.setFill(Color.web("#293442"));

		Text searchText = new Text("Search by");
		searchText.setFont(Font.font("Verdana", 16));
		searchText.setFill(Color.web("#293442"));

		searchField.setPromptText("Search");

		// Create "Add" button for pharmacist
		Button addButton = new Button("Add Pharmacist");
		addButton.setOnAction(event -> openAddPharmacistPopup());
		addButton.setScaleX(2);
		addButton.setScaleY(2);

		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> openEditPharmacistPopup());
		editButton.setScaleX(2);
		editButton.setScaleY(2);

		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteSelectedPharmacist());
		deleteButton.setScaleX(2);
		deleteButton.setScaleY(2);

		HBox buttonsHBox = new HBox(100);
		buttonsHBox.getChildren().addAll(addButton, editButton, deleteButton);
		buttonsHBox.setAlignment(Pos.CENTER);

		searchOptionComboBox.setOnAction(event -> fetchPharmacistData());
		searchField.textProperty().addListener((observable, oldValue, newValue) -> fetchPharmacistData());
		searchField.setMaxWidth(Region.USE_PREF_SIZE);

		mainVBox.getChildren().addAll(headerHBox, inventoryTitle, searchText, searchOptionComboBox, searchField,
				pharmacistTable, buttonsHBox);
		mainVBox.setAlignment(Pos.CENTER);

		getChildren().addAll(sidebar, mainVBox);

		fetchPharmacistData();
	}

	private void initializeSearchComboBox() {
		searchOptionComboBox.getItems().addAll("first_name", "last_name", "phone_number", "license_number", "gender",
				"shift", "employee_id", "pharmacy_id", "salary");
		searchOptionComboBox.setValue("first_name"); // Default selection
	}

	private void initializePharmacistTable() {
		// Clear existing columns
		pharmacistTable.getColumns().clear();

		for (String columnName : searchOptionComboBox.getItems()) {
			TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
			column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
					param.getValue().get(pharmacistTable.getColumns().indexOf(column))));
			pharmacistTable.getColumns().add(column);
		}

		pharmacistTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void fetchPharmacistData() {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			// Prepare the query based on the selected search option
			String selectedOption = searchOptionComboBox.getValue();
			String query = "SELECT * FROM pharmacists WHERE " + selectedOption + " LIKE ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "%" + searchField.getText() + "%");

			// Execute the query and get the result set
			resultSet = preparedStatement.executeQuery();

			// Clear existing data in the table
			pharmacistTable.getItems().clear();

			// Populate the TableView with the data
			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				for (String columnName : searchOptionComboBox.getItems()) {
					rowData.add(resultSet.getString(columnName));
				}
				pharmacistTable.getItems().add(rowData);
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
			}
		}
	}

	private void openAddPharmacistPopup() {
		Stage popupStage = new Stage();
		popupStage.setTitle("Add Pharmacist");

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields for pharmacists
		TextField firstNameField = new TextField();
		TextField lastNameField = new TextField();
		TextField phoneNumberField = new TextField();
		TextField licenseNumberField = new TextField();
		TextField genderField = new TextField();
		TextField shiftField = new TextField();
		TextField employeeIdField = new TextField();
		TextField pharmacyIdField = new TextField();
		TextField salaryField = new TextField();
		PasswordField passwordField = new PasswordField();

		// Labels for pharmacists
		Label firstNameLabel = new Label("First Name:");
		Label lastNameLabel = new Label("Last Name:");
		Label phoneNumberLabel = new Label("Phone Number:");
		Label licenseNumberLabel = new Label("License Number:");
		Label genderLabel = new Label("Gender:");
		Label shiftLabel = new Label("Shift:");
		Label employeeIdLabel = new Label("Employee ID:");
		Label pharmacyIdLabel = new Label("Pharmacy ID:");
		Label salaryLabel = new Label("Salary:");
		Label passwordLabel = new Label("Password:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		firstNameLabel.setPadding(labelMargin);
		lastNameLabel.setPadding(labelMargin);
		phoneNumberLabel.setPadding(labelMargin);
		licenseNumberLabel.setPadding(labelMargin);
		genderLabel.setPadding(labelMargin);
		shiftLabel.setPadding(labelMargin);
		employeeIdLabel.setPadding(labelMargin);
		pharmacyIdLabel.setPadding(labelMargin);
		salaryLabel.setPadding(labelMargin);
		passwordLabel.setPadding(labelMargin);

		// Button
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Retrieve the field values and call the addPharmacist method
			String[] fieldValues = { firstNameField.getText(), lastNameField.getText(), phoneNumberField.getText(),
					licenseNumberField.getText(), genderField.getText(), shiftField.getText(),
					employeeIdField.getText(), pharmacyIdField.getText(), salaryField.getText(),
					passwordField.getText() };
			addPharmacist(fieldValues);

			popupStage.close(); // Close the popup after adding the pharmacist
		});

		popupContent.getChildren().addAll(new HBox(firstNameLabel, firstNameField),
				new HBox(lastNameLabel, lastNameField), new HBox(phoneNumberLabel, phoneNumberField),
				new HBox(licenseNumberLabel, licenseNumberField), new HBox(genderLabel, genderField),
				new HBox(shiftLabel, shiftField), new HBox(employeeIdLabel, employeeIdField),
				new HBox(pharmacyIdLabel, pharmacyIdField), new HBox(salaryLabel, salaryField),
				new HBox(passwordLabel, passwordField), // Include the new password field
				addButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void addPharmacist(String[] fieldValues) {
		try {
			// Construct the SQL query dynamically for pharmacists table
			StringBuilder queryBuilder = new StringBuilder("INSERT INTO pharmacists (");
			for (String columnName : searchOptionComboBox.getItems()) {
				queryBuilder.append(columnName.toLowerCase()).append(",");
			}
			queryBuilder.append("password) VALUES (");
			for (int i = 0; i < fieldValues.length - 1; i++) {
				queryBuilder.append("?,");
			}
			queryBuilder.append("?)"); // Add the new password field

			String query = queryBuilder.toString();

			// Prepare the statement
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				// Set parameter values
				for (int i = 0; i < fieldValues.length; i++) {
					preparedStatement.setString(i + 1, fieldValues[i]);
				}

				preparedStatement.setString(fieldValues.length, "123");

				// Execute the update
				preparedStatement.executeUpdate();
			}

			// Refresh the displayed data after adding a pharmacist
			fetchPharmacistData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String[] labels = { "Medicine Name:", "Generic Name:", "Manufacturer:", "Production Date:", "Expired Date:",
			"Stock:", "Price:", "Prescription:", "Route Usage:", "Storaging:", "Strength:", "Dosage Form:",
			"Barcode:" };

	private void openEditPharmacistPopup() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = pharmacistTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Open the popup with the selected data for editing
			openAddEditPharmacistPopup("Edit Pharmacist", "Edit", selectedRow);
		} else {
			System.out.println("Please select a row to edit.");
		}
	}

	private void openAddEditPharmacistPopup(String title, String buttonText, ObservableList<String> rowData) {
		Stage popupStage = new Stage();
		popupStage.setTitle(title);

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields for pharmacists
		TextField firstNameField = new TextField();
		TextField lastNameField = new TextField();
		TextField phoneNumberField = new TextField();
		TextField licenseNumberField = new TextField();
		TextField genderField = new TextField();
		TextField shiftField = new TextField();
		TextField employeeIdField = new TextField();
		TextField pharmacyIdField = new TextField();
		TextField salaryField = new TextField();
		PasswordField passwordField = new PasswordField();

		// Labels for pharmacists
		Label firstNameLabel = new Label("First Name:");
		Label lastNameLabel = new Label("Last Name:");
		Label phoneNumberLabel = new Label("Phone Number:");
		Label licenseNumberLabel = new Label("License Number:");
		Label genderLabel = new Label("Gender:");
		Label shiftLabel = new Label("Shift:");
		Label employeeIdLabel = new Label("Employee ID:");
		Label pharmacyIdLabel = new Label("Pharmacy ID:");
		Label salaryLabel = new Label("Salary:");
		Label passwordLabel = new Label("Password:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		firstNameLabel.setPadding(labelMargin);
		lastNameLabel.setPadding(labelMargin);
		phoneNumberLabel.setPadding(labelMargin);
		licenseNumberLabel.setPadding(labelMargin);
		genderLabel.setPadding(labelMargin);
		shiftLabel.setPadding(labelMargin);
		employeeIdLabel.setPadding(labelMargin);
		pharmacyIdLabel.setPadding(labelMargin);
		salaryLabel.setPadding(labelMargin);
		passwordLabel.setPadding(labelMargin);

		// Set the fields based on the rowData for editing
		firstNameField.setText(rowData.get(0));
		lastNameField.setText(rowData.get(1));
		phoneNumberField.setText(rowData.get(2));
		licenseNumberField.setText(rowData.get(3));
		genderField.setText(rowData.get(4));
		shiftField.setText(rowData.get(5));
		employeeIdField.setText(rowData.get(6));
		pharmacyIdField.setText(rowData.get(7));
		salaryField.setText(rowData.get(8));

		// Button
		Button editButton = new Button(buttonText);
		editButton.setOnAction(e -> {
			// Retrieve the field values and call the editPharmacist method
			String[] fieldValues = { firstNameField.getText(), lastNameField.getText(), phoneNumberField.getText(),
					licenseNumberField.getText(), genderField.getText(), shiftField.getText(),
					employeeIdField.getText(), pharmacyIdField.getText(), salaryField.getText(),
					passwordField.getText() };
			editPharmacist(rowData, fieldValues);

			popupStage.close(); // Close the popup after editing the pharmacist
		});

		popupContent.getChildren().addAll(new HBox(firstNameLabel, firstNameField),
				new HBox(lastNameLabel, lastNameField), new HBox(phoneNumberLabel, phoneNumberField),
				new HBox(licenseNumberLabel, licenseNumberField), new HBox(genderLabel, genderField),
				new HBox(shiftLabel, shiftField), new HBox(employeeIdLabel, employeeIdField),
				new HBox(pharmacyIdLabel, pharmacyIdField), new HBox(salaryLabel, salaryField),
				new HBox(passwordLabel, passwordField), editButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void deleteSelectedPharmacist() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = pharmacistTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Retrieve the ID or a unique identifier from the selected row
			String pharmacistId = selectedRow.get(1); // Assuming the ID is in the first column

			// Call the deletePharmacist method
			deletePharmacist();
		} else {
			// Display an error message or handle the case where no row is selected
			System.out.println("Please select a row to delete.");
		}
	}

	private void deletePharmacist() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = pharmacistTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			try {
				// Assuming the unique identifier (ID) is in the first column
				String pharmacistId = selectedRow.get(6);

				// Parse the employee_id to an integer
				int employeeId = Integer.parseInt(pharmacistId);

				// Construct the SQL query dynamically for deleting a pharmacist by ID
				String query = "DELETE FROM pharmacists WHERE employee_id = ?";

				// Prepare the statement
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					// Set parameter value
					preparedStatement.setInt(1, employeeId);

					// Execute the deletion
					preparedStatement.executeUpdate();
				}

				// Refresh the displayed data after deleting a pharmacist
				fetchPharmacistData();
			} catch (SQLException | NumberFormatException e) {
				e.printStackTrace();
				// Handle exceptions appropriately
			}
		} else {
			System.out.println("Please select a row to delete.");
		}
	}

	private void editPharmacist(ObservableList<String> rowData, String[] fieldValues) {
		try {
			// Retrieve the employee_id or a unique identifier from the selected row
			String pharmacistId = rowData.get(6);

			// Parse the employee_id to an integer
			int employeeId = Integer.parseInt(pharmacistId);

			// Construct the SQL query dynamically for updating a pharmacist by ID
			StringBuilder queryBuilder = new StringBuilder("UPDATE pharmacists SET ");
			for (String columnName : searchOptionComboBox.getItems()) {
				queryBuilder.append(columnName.toLowerCase()).append(" = ?,");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(", password = ? WHERE employee_id = ?");

			String query = queryBuilder.toString();

			// Prepare the statement
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				// Set parameter values
				for (int i = 0; i < fieldValues.length; i++) {
					preparedStatement.setString(i + 1, fieldValues[i]);
				}
				preparedStatement.setString(fieldValues.length + 1, "default_password");
				// Set the last parameter to the ID
				preparedStatement.setInt(fieldValues.length + 1, employeeId);

				// Execute the update
				preparedStatement.executeUpdate();
			}

			// Refresh the displayed data after editing a pharmacist
			fetchPharmacistData();
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
		}
	}

}
