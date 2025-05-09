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

public class Customers extends HBox {

	private TextField searchField = new TextField();
	private ComboBox<String> searchOptionComboBox = new ComboBox<>();
	private TableView<ObservableList<String>> customersTable = new TableView<>();
	private Connection connection = DataBaseUtil.getConnection();
	private ObservableList<String> selectedRow;
	private final String[] historyColumnNames = {"consumption_date", "customer_id", "barcode"};


	public Customers(String userName) {
		Sidebar sidebar = new Sidebar(userName);
		ScrollPane scrollPane = new ScrollPane(sidebar);
		scrollPane.getStyleClass().add("scroll-pane");
		String cssPath = getClass().getResource("styles.css").toExternalForm();
		scrollPane.getStylesheets().add(cssPath);

		initializeSearchComboBox();
		initializeCustomerTable();

		VBox mainVBox = new VBox(30);

		HBox headerHBox = new HBox(900);
		Text title = new Text("Customers");
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

		Text customerTitle = new Text("Customers");
		customerTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
		customerTitle.setFill(Color.web("#293442"));

		Text searchText = new Text("Search by");
		searchText.setFont(Font.font("Verdana", 16));
		searchText.setFill(Color.web("#293442"));

		searchField.setPromptText("Search");

		// Create "Add" button for customer
		Button addButton = new Button("Add Customer");
		addButton.setOnAction(event -> openAddCustomerPopup());
		addButton.setScaleX(2);
		addButton.setScaleY(2);

		Button editButton = new Button("Edit");
		editButton.setOnAction(e -> openAddEditCustomerPopup("Edit Customer", "Save Changes", selectedRow));

		editButton.setScaleX(2);
		editButton.setScaleY(2);

		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> deleteSelectedCustomer());
		deleteButton.setScaleX(2);
		deleteButton.setScaleY(2);
		
		Button historyButton = new Button("History");
		historyButton.setOnAction(e -> openHistoryPopup(selectedRow));
		historyButton.setScaleX(2);
		historyButton.setScaleY(2);

		HBox buttonsHBox = new HBox(100);
		buttonsHBox.getChildren().addAll(addButton, editButton, deleteButton, historyButton);
		buttonsHBox.setAlignment(Pos.CENTER);

		searchOptionComboBox.setOnAction(event -> fetchCustomerData());
		searchField.textProperty().addListener((observable, oldValue, newValue) -> fetchCustomerData());
		searchField.setMaxWidth(Region.USE_PREF_SIZE);

		mainVBox.getChildren().addAll(headerHBox, customerTitle, searchText, searchOptionComboBox, searchField,
				customersTable, buttonsHBox);
		mainVBox.setAlignment(Pos.CENTER);

		getChildren().addAll(sidebar, mainVBox);

		fetchCustomerData();

		customersTable.setOnMouseClicked(event -> {
			selectedRow = customersTable.getSelectionModel().getSelectedItem();
		});
	}

	private void initializeSearchComboBox() {
		searchOptionComboBox.getItems().addAll("insurance", "allergies", "phone_number", "gender", "birth_date",
				"address", "customer_id", "first_name", "last_name");
		searchOptionComboBox.setValue("first_name"); // Default selection
	}

	@SuppressWarnings("deprecation")
	private void initializeCustomerTable() {
		// Clear existing columns
		customersTable.getColumns().clear();

		// Assuming column names match with the database columns
		for (String columnName : searchOptionComboBox.getItems()) {
			TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
			column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
					param.getValue().get(customersTable.getColumns().indexOf(column))));
			customersTable.getColumns().add(column);
		}

		customersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
	}

	private void fetchCustomerData() {
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			// Prepare the query based on the selected search option
			String selectedOption = searchOptionComboBox.getValue();
			String query = "SELECT * FROM customer WHERE " + selectedOption + " LIKE ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, "%" + searchField.getText() + "%");

			// Execute the query and get the result set
			resultSet = preparedStatement.executeQuery();

			// Clear existing data in the table
			customersTable.getItems().clear();

			// Populate the TableView with the data
			while (resultSet.next()) {
				ObservableList<String> rowData = FXCollections.observableArrayList();
				for (String columnName : searchOptionComboBox.getItems()) {
					rowData.add(resultSet.getString(columnName));
				}
				customersTable.getItems().add(rowData);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQLException appropriately
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

	private void openAddCustomerPopup() {
		Stage popupStage = new Stage();
		popupStage.setTitle("Add Customer");

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields for customers
		TextField insuranceField = new TextField();
		TextField allergiesField = new TextField();
		TextField phoneNumberField = new TextField();
		TextField genderField = new TextField();
		TextField birthDateField = new TextField();
		TextField addressField = new TextField();
		TextField customerIdField = new TextField();
		TextField firstNameField = new TextField();
		TextField lastNameField = new TextField();

		// Labels for customers
		Label insuranceLabel = new Label("Insurance:");
		Label allergiesLabel = new Label("Allergies:");
		Label phoneNumberLabel = new Label("Phone Number:");
		Label genderLabel = new Label("Gender:");
		Label birthDateLabel = new Label("Birth Date:");
		Label addressLabel = new Label("Address:");
		Label customerIdLabel = new Label("Customer ID:");
		Label firstNameLabel = new Label("First Name:");
		Label lastNameLabel = new Label("Last Name:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		insuranceLabel.setPadding(labelMargin);
		allergiesLabel.setPadding(labelMargin);
		phoneNumberLabel.setPadding(labelMargin);
		genderLabel.setPadding(labelMargin);
		birthDateLabel.setPadding(labelMargin);
		addressLabel.setPadding(labelMargin);
		customerIdLabel.setPadding(labelMargin);
		firstNameLabel.setPadding(labelMargin);
		lastNameLabel.setPadding(labelMargin);

		// Button
		Button addButton = new Button("Add");
		addButton.setOnAction(e -> {
			// Retrieve the field values and call the addCustomer method
			String[] fieldValues = { insuranceField.getText(), allergiesField.getText(), phoneNumberField.getText(),
					genderField.getText(), birthDateField.getText(), addressField.getText(), customerIdField.getText(),
					firstNameField.getText(), lastNameField.getText() };
			addCustomer(fieldValues);

			popupStage.close(); // Close the popup after adding the customer
		});

		popupContent.getChildren().addAll(new HBox(insuranceLabel, insuranceField),
				new HBox(allergiesLabel, allergiesField), new HBox(phoneNumberLabel, phoneNumberField),
				new HBox(genderLabel, genderField), new HBox(birthDateLabel, birthDateField),
				new HBox(addressLabel, addressField), new HBox(customerIdLabel, customerIdField),
				new HBox(firstNameLabel, firstNameField), new HBox(lastNameLabel, lastNameField), addButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void addCustomer(String[] fieldValues) {
		try {
			// Construct the SQL query dynamically for the customer table
			StringBuilder queryBuilder = new StringBuilder("INSERT INTO customer (");
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

			// Refresh the displayed data after adding a customer
			fetchCustomerData();
		} catch (SQLException e) {
			e.printStackTrace();
			// Handle SQLException appropriately
		}
	}

	private void openEditCustomerPopup(String title, String buttonText, ObservableList<String> rowData) {
		Stage popupStage = new Stage();
		popupStage.setTitle(title);

		VBox popupContent = new VBox(10);
		popupContent.setPadding(new Insets(10));

		// Fields for customers
		TextField firstNameField = new TextField();
		TextField lastNameField = new TextField();
		TextField genderField = new TextField();
		TextField birthDateField = new TextField();
		TextField addressField = new TextField();
		TextField phoneNumberField = new TextField();
		TextField allergiesField = new TextField();
		TextField insuranceField = new TextField();

		// Labels for customers
		Label firstNameLabel = new Label("First Name:");
		Label lastNameLabel = new Label("Last Name:");
		Label genderLabel = new Label("Gender:");
		Label birthDateLabel = new Label("Birth Date:");
		Label addressLabel = new Label("Address:");
		Label phoneNumberLabel = new Label("Phone Number:");
		Label allergiesLabel = new Label("Allergies:");
		Label insuranceLabel = new Label("Insurance:");

		Insets labelMargin = new Insets(0, 10, 0, 0);
		// Apply padding to labels
		firstNameLabel.setPadding(labelMargin);
		lastNameLabel.setPadding(labelMargin);
		genderLabel.setPadding(labelMargin);
		birthDateLabel.setPadding(labelMargin);
		addressLabel.setPadding(labelMargin);
		phoneNumberLabel.setPadding(labelMargin);
		allergiesLabel.setPadding(labelMargin);
		insuranceLabel.setPadding(labelMargin);

		// Set the fields based on the rowData for editing
		firstNameField.setText(rowData.get(7));
		lastNameField.setText(rowData.get(8));
		genderField.setText(rowData.get(3));
		birthDateField.setText(rowData.get(4));
		addressField.setText(rowData.get(5));
		phoneNumberField.setText(rowData.get(2));
		allergiesField.setText(rowData.get(1));
		insuranceField.setText(rowData.get(0));

		// Button
		Button editButton = new Button(buttonText);
		editButton.setOnAction(e -> {
			// Retrieve the field values and call the editCustomer method
			String[] fieldValues = { insuranceField.getText(), allergiesField.getText(), phoneNumberField.getText(),
					genderField.getText(), birthDateField.getText(), addressField.getText(), rowData.get(6),
					firstNameField.getText(), lastNameField.getText() };
			editCustomer(rowData, fieldValues);

			popupStage.close(); // Close the popup after editing the customer
		});

		popupContent.getChildren().addAll(new HBox(insuranceLabel, insuranceField),
				new HBox(allergiesLabel, allergiesField), new HBox(phoneNumberLabel, phoneNumberField),
				new HBox(genderLabel, genderField), new HBox(birthDateLabel, birthDateField),
				new HBox(addressLabel, addressField), new HBox(firstNameLabel, firstNameField),
				new HBox(lastNameLabel, lastNameField), editButton);

		Scene popupScene = new Scene(popupContent);
		popupStage.setScene(popupScene);

		// Set a minimum height for the Stage
		popupStage.setMinHeight(500);

		// Show the popup
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.showAndWait();
	}

	private void editCustomer(ObservableList<String> rowData, String[] fieldValues) {
		try {
			// Retrieve the customer_id or a unique identifier from the selected row
			String customerId = rowData.get(6); // Assuming the customer_id is in the seventh column

			// Parse the customer_id to an integer
			int customerIdInt = Integer.parseInt(customerId);

			// Construct the SQL query dynamically for updating a customer by ID
			StringBuilder queryBuilder = new StringBuilder("UPDATE customer SET ");
			for (String columnName : searchOptionComboBox.getItems()) {
				queryBuilder.append(columnName.toLowerCase()).append(" = ?,");
			}
			queryBuilder.deleteCharAt(queryBuilder.length() - 1); // Remove the last comma
			queryBuilder.append(" WHERE customer_id = ?");

			String query = queryBuilder.toString();

			// Prepare the statement
			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				// Set parameter values
				for (int i = 0; i < fieldValues.length; i++) {
					preparedStatement.setString(i + 1, fieldValues[i]);
				}
				// Set the last parameter to the ID
				preparedStatement.setInt(fieldValues.length + 1, customerIdInt); // Updated index

				// Execute the update
				preparedStatement.executeUpdate();
			}

			// Refresh the displayed data after editing a customer
			fetchCustomerData();
		} catch (SQLException | NumberFormatException e) {
			e.printStackTrace();
			// Handle exceptions appropriately
		}
	}

	private void deleteSelectedCustomer() {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = customersTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			// Call the deleteCustomer method
			deleteCustomer(selectedRow);
		} else {
			// Display an error message or handle the case where no row is selected
			System.out.println("Please select a row to delete.");
		}
	}

	private void deleteCustomer(ObservableList<String> rowData) {
		// Retrieve the selected row from the table
		ObservableList<String> selectedRow = customersTable.getSelectionModel().getSelectedItem();

		// Check if a row is selected
		if (selectedRow != null) {
			try {
				// Assuming the unique identifier (ID) is in the seventh column
				String customerId = selectedRow.get(6);

				// Parse the customer_id to an integer
				int customerIdInt = Integer.parseInt(customerId);

				// Construct the SQL query dynamically for deleting a customer by ID
				String query = "DELETE FROM customer WHERE customer_id = ?";

				// Prepare the statement
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					// Set parameter value
					preparedStatement.setInt(1, customerIdInt);

					// Execute the deletion
					preparedStatement.executeUpdate();
				}

				// Refresh the displayed data after deleting a customer
				fetchCustomerData();
			} catch (SQLException | NumberFormatException e) {
				e.printStackTrace();
				// Handle exceptions appropriately
			}
		} else {
			// Display an error message or handle the case where no row is selected
			System.out.println("Please select a row to delete.");
		}
	}
	
	private void openAddEditCustomerPopup(String title, String buttonText, ObservableList<String> rowData) {
	    Stage popupStage = new Stage();
	    popupStage.setTitle(title);

	    VBox popupContent = new VBox(10);
	    popupContent.setPadding(new Insets(10));

	    // Fields for customers
	    TextField firstNameField = new TextField();
	    TextField lastNameField = new TextField();
	    TextField genderField = new TextField();
	    TextField birthDateField = new TextField();
	    TextField addressField = new TextField();
	    TextField phoneNumberField = new TextField();
	    TextField allergiesField = new TextField();
	    TextField insuranceField = new TextField();

	    // Labels for customers
	    Label firstNameLabel = new Label("First Name:");
	    Label lastNameLabel = new Label("Last Name:");
	    Label genderLabel = new Label("Gender:");
	    Label birthDateLabel = new Label("Birth Date:");
	    Label addressLabel = new Label("Address:");
	    Label phoneNumberLabel = new Label("Phone Number:");
	    Label allergiesLabel = new Label("Allergies:");
	    Label insuranceLabel = new Label("Insurance:");

	    Insets labelMargin = new Insets(0, 10, 0, 0);
	    // Apply padding to labels
	    firstNameLabel.setPadding(labelMargin);
	    lastNameLabel.setPadding(labelMargin);
	    genderLabel.setPadding(labelMargin);
	    birthDateLabel.setPadding(labelMargin);
	    addressLabel.setPadding(labelMargin);
	    phoneNumberLabel.setPadding(labelMargin);
	    allergiesLabel.setPadding(labelMargin);
	    insuranceLabel.setPadding(labelMargin);

	    // Set the fields based on the rowData for editing
	    if (rowData != null) {
	        insuranceField.setText(rowData.get(0));
	        allergiesField.setText(rowData.get(1));
	        phoneNumberField.setText(rowData.get(2));
	        genderField.setText(rowData.get(3));
	        birthDateField.setText(rowData.get(4));
	        addressField.setText(rowData.get(5));
	        firstNameField.setText(rowData.get(7));
	        lastNameField.setText(rowData.get(8));
	    }

	    // Button
	    Button editButton = new Button(buttonText);
	    editButton.setOnAction(e -> {
	        // Retrieve the field values and call the editCustomer method
	        String[] fieldValues = {insuranceField.getText(), allergiesField.getText(),
	                phoneNumberField.getText(), genderField.getText(), birthDateField.getText(),
	                addressField.getText(), rowData == null ? null : rowData.get(6), // customer_id
	                firstNameField.getText(), lastNameField.getText()};
	        if (rowData == null) {
	            addCustomer(fieldValues);
	        } else {
	            editCustomer(rowData, fieldValues);
	        }

	        popupStage.close(); // Close the popup after editing the customer
	    });

	    popupContent.getChildren().addAll(
	            new HBox(insuranceLabel, insuranceField),
	            new HBox(allergiesLabel, allergiesField),
	            new HBox(phoneNumberLabel, phoneNumberField),
	            new HBox(genderLabel, genderField),
	            new HBox(birthDateLabel, birthDateField),
	            new HBox(addressLabel, addressField),
	            new HBox(firstNameLabel, firstNameField),
	            new HBox(lastNameLabel, lastNameField),
	            editButton
	    );

	    Scene popupScene = new Scene(popupContent);
	    popupStage.setScene(popupScene);

	    // Set a minimum height for the Stage
	    popupStage.setMinHeight(500);

	    // Show the popup
	    popupStage.initModality(Modality.APPLICATION_MODAL);
	    popupStage.showAndWait();
	}
	
	private void openHistoryPopup(ObservableList<String> selectedRow) {
	    if (selectedRow == null) {
	        // Display an error message or handle the case where no row is selected
	        System.out.println("Please select a row to view history.");
	        return;
	    }

	    Stage historyPopupStage = new Stage();
	    historyPopupStage.setTitle("Customer History");

	    TableView<ObservableList<String>> historyTable = new TableView<>();

	    // Assuming column names match with the "consumes" table columns
	    String[] historyColumnNames = {"consumption_date", "customer_id", "barcode"};

	    for (String columnName : historyColumnNames) {
	        TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
	        column.setCellValueFactory(param -> new javafx.beans.property.SimpleStringProperty(
	                param.getValue().get(historyTable.getColumns().indexOf(column))));
	        historyTable.getColumns().add(column);
	    }

	    historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

	    VBox historyPopupContent = new VBox(10);
	    historyPopupContent.setPadding(new Insets(10));
	    historyPopupContent.getChildren().add(historyTable);

	    Scene historyPopupScene = new Scene(historyPopupContent);
	    historyPopupStage.setScene(historyPopupScene);

	    // Set a minimum height for the Stage
	    historyPopupStage.setMinHeight(500);

	    // Fetch history data for the selected customer and populate the table
	    fetchHistoryData(selectedRow, historyTable);

	    // Show the history popup
	    historyPopupStage.initModality(Modality.APPLICATION_MODAL);
	    historyPopupStage.showAndWait();
	}

	private void fetchHistoryData(ObservableList<String> selectedRow, TableView<ObservableList<String>> historyTable) {
	    ResultSet resultSet = null;
	    PreparedStatement preparedStatement = null;

	    try {
	        // Retrieve the customer_id from the selected row
	        String customerId = selectedRow.get(6); // Assuming customer_id is in the seventh column

	        // Prepare the query to get history data for the selected customer
	        String query = "SELECT * FROM consumes WHERE customer_id = ?";
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setString(1, customerId);

	        // Execute the query and get the result set
	        resultSet = preparedStatement.executeQuery();

	        // Clear existing data in the table
	        historyTable.getItems().clear();

	        // Populate the history TableView with the data
	        while (resultSet.next()) {
	            ObservableList<String> rowData = FXCollections.observableArrayList();
	            for (String columnName : historyColumnNames) {
	                rowData.add(resultSet.getString(columnName));
	            }
	            historyTable.getItems().add(rowData);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        // Handle SQLException appropriately
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
}