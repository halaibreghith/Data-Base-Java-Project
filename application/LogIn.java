package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;

public class LogIn extends Application {

	PasswordField passwordField;
	TextField nameTextField;
	TextField licenseTextField, genderTextField, phoneTextField, shiftTextField, pharmacyIdTextField, salaryTextField,
			employeeIdTextField;
	private final String DATABASE_URL = "jdbc:mysql://localhost:3306/pharmacy_database";
	private final String USERNAME = "root";
	private final String PASSWORD = "Halaibre264**";
	static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		mainScreen(primaryStage);

	}

	private void handleLogin() {
		String usernameText = employeeIdTextField.getText();
		String password = passwordField.getText();

		if (usernameText == null || usernameText.isEmpty() || password == null || password.isEmpty()) {
			showAlert("Login", "Username and password cannot be empty");
			return;
		}

		try {
			int username = Integer.parseInt(usernameText);

			try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {
				String query = "SELECT * FROM pharmacists WHERE employee_id = ? AND password = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					preparedStatement.setInt(1, username);
					preparedStatement.setString(2, password);

					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						if (resultSet.next()) {
							// Successful login
							String name = getName(username);
							Platform.runLater(() -> Main.openNewWindow(name));
							primaryStage.close();
						} else {
							// Invalid credentials
							showAlert("Login", "Invalid username or password");
						}
					}
				}
			} catch (SQLException e) {
				showAlert("Login", "Database connection error: " + e.getMessage());
			} catch (NumberFormatException ex) {
				showAlert("Login", "Invalid number format. Please enter valid numbers.");
			}
		} catch (NumberFormatException ex) {
			showAlert("Login", "Invalid number format. Please enter valid numbers.");
		}
	}

	private String getName(int username) {
		// SQL query to select the name column based on the primary key
		String sqlQuery = "SELECT first_name FROM pharmacists WHERE employee_id = ?";

		try {
			// Load the JDBC driver
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Establish a connection to the database
			Connection connection = DataBaseUtil.getConnection();

			// Create a prepared statement with the SQL query
			PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

			// Set the primary key value as a parameter in the prepared statement
			preparedStatement.setInt(1, username);

			// Execute the query and get the result set
			ResultSet resultSet = preparedStatement.executeQuery();

			// Check if a result was returned
			if (resultSet.next()) {
				String name = resultSet.getString("first_name");
				System.out.println("Name for PK " + username + ": " + name);
				return name;
			} else {
				System.out.println("No record found for PK " + username);
			}

			// Close resources
			resultSet.close();
			preparedStatement.close();
			connection.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	private boolean handleConfirm() {
		String usernameText = employeeIdTextField.getText();
		String password = passwordField.getText();

		if (usernameText == null || usernameText.isEmpty() || password == null || password.isEmpty()) {
			showAlert("Sign in Error", "Username and password cannot be empty");
			return false;
		}

		try {
			int username = Integer.parseInt(usernameText);

			try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD)) {
				String query = "SELECT * FROM pharmacists WHERE employee_id = ? AND password = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					preparedStatement.setInt(1, username);
					preparedStatement.setString(2, password);

					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						if (resultSet.next()) {
							// Successful login
							return true;
						}
					}
				}
			} catch (SQLException e) {
				showAlert("Sign in Error", "Database connection error: " + e.getMessage());
			} catch (NumberFormatException ex) {
				showAlert("Sign in Error", "Invalid number format. Please enter valid numbers.");
			}

		} catch (NumberFormatException ex) {
			showAlert("Sign in Error", "Invalid number format. Please enter valid numbers.");
		}
		showAlert("Sign in Error", "Wrong ID or Password");
		return false;
	}

	private void confirmScreen(Stage primaryStage) {
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 674, 540);
		primaryStage.setTitle("Sign Up Form");
		primaryStage.setScene(scene);

		ImageView logoImageView = new ImageView(
				new Image("file:///C:/Users/DELL/Desktop/workspace/pharmacy-logo-data-base.png"));
		logoImageView.setFitWidth(325);
		logoImageView.setFitHeight(555);
		logoImageView.setLayoutX(-5);
		logoImageView.setLayoutY(-7);
		root.getChildren().add(logoImageView);

		Label requiredLabel = new Label("* Required");
		requiredLabel.setFont(new Font("System Bold Italic", 11));
		requiredLabel.setTextFill(javafx.scene.paint.Color.web("#e41321"));
		AnchorPane.setRightAnchor(requiredLabel, 22.0);
		AnchorPane.setTopAnchor(requiredLabel, 22.0);
		root.getChildren().add(requiredLabel);

		Label signUpLabel = new Label("SIGN UP");
		signUpLabel.setFont(new Font("System Bold Italic", 18));
		signUpLabel.setTextFill(javafx.scene.paint.Color.web("#9e095b"));
		AnchorPane.setLeftAnchor(signUpLabel, 325.0);
		AnchorPane.setTopAnchor(signUpLabel, 16.0);
		root.getChildren().add(signUpLabel);

		Separator separator = new Separator();
		separator.setPrefWidth(600.0);
		separator.setPrefHeight(2.0);
		separator.setStyle("-fx-background-color: #9e095d;");
		AnchorPane.setLeftAnchor(separator, 320.0);
		AnchorPane.setTopAnchor(separator, 54.0);
		root.getChildren().add(separator);

		Label nameLabel = new Label("Name :");
		nameLabel.setFont(new Font("System Bold Italic", 14));
		nameLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(nameLabel, 324.0);
		AnchorPane.setTopAnchor(nameLabel, 113.0);
		root.getChildren().add(nameLabel);

		Label employeeIdLabel = new Label("Employee ID :");
		employeeIdLabel.setFont(new Font("System Bold Italic", 14));
		employeeIdLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(employeeIdLabel, 324.0);
		AnchorPane.setTopAnchor(employeeIdLabel, 74.0);
		root.getChildren().add(employeeIdLabel);

		Label licenseLabel = new Label("License # :");
		licenseLabel.setFont(new Font("System Bold Italic", 14));
		licenseLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(licenseLabel, 324.0);
		AnchorPane.setTopAnchor(licenseLabel, 150.0);
		root.getChildren().add(licenseLabel);

		Label genderLabel = new Label("Gender :");
		genderLabel.setFont(new Font("System Bold Italic", 14));
		genderLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(genderLabel, 324.0);
		AnchorPane.setTopAnchor(genderLabel, 195.0);
		root.getChildren().add(genderLabel);

		Label phoneLabel = new Label("Phone # :");
		phoneLabel.setFont(new Font("System Bold Italic", 14));
		phoneLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(phoneLabel, 324.0);
		AnchorPane.setTopAnchor(phoneLabel, 236.0);
		root.getChildren().add(phoneLabel);

		Label passwordLabel = new Label("Password :");
		passwordLabel.setFont(new Font("System Bold Italic", 14));
		passwordLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(passwordLabel, 324.0);
		AnchorPane.setTopAnchor(passwordLabel, 284.0);
		root.getChildren().add(passwordLabel);

		Label shiftLabel = new Label("Shift :");
		shiftLabel.setFont(new Font("System Bold Italic", 14));
		shiftLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(shiftLabel, 324.0);
		AnchorPane.setTopAnchor(shiftLabel, 328.0);
		root.getChildren().add(shiftLabel);

		Label pharmacyIdLabel = new Label("Pharmacy ID :");
		pharmacyIdLabel.setFont(new Font("System Bold Italic", 14));
		pharmacyIdLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(pharmacyIdLabel, 324.0);
		AnchorPane.setTopAnchor(pharmacyIdLabel, 368.0);
		root.getChildren().add(pharmacyIdLabel);

		Label salaryLabel = new Label("Salary :");
		salaryLabel.setFont(new Font("System Bold Italic", 14));
		salaryLabel.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(salaryLabel, 324.0);
		AnchorPane.setTopAnchor(salaryLabel, 414.0);
		root.getChildren().add(salaryLabel);

		nameTextField = new TextField();
		AnchorPane.setLeftAnchor(nameTextField, 488.0);
		AnchorPane.setTopAnchor(nameTextField, 111.0);
		root.getChildren().add(nameTextField);

		employeeIdTextField = new TextField();
		AnchorPane.setLeftAnchor(employeeIdTextField, 488.0);
		AnchorPane.setTopAnchor(employeeIdTextField, 74.0);
		root.getChildren().add(employeeIdTextField);

		licenseTextField = new TextField();
		AnchorPane.setLeftAnchor(licenseTextField, 488.0);
		AnchorPane.setTopAnchor(licenseTextField, 148.0);
		root.getChildren().add(licenseTextField);

		genderTextField = new TextField();
		AnchorPane.setLeftAnchor(genderTextField, 488.0);
		AnchorPane.setTopAnchor(genderTextField, 193.0);
		root.getChildren().add(genderTextField);

		phoneTextField = new TextField();
		AnchorPane.setLeftAnchor(phoneTextField, 488.0);
		AnchorPane.setTopAnchor(phoneTextField, 233.0);
		root.getChildren().add(phoneTextField);

		passwordField = new PasswordField();
		AnchorPane.setLeftAnchor(passwordField, 488.0);
		AnchorPane.setTopAnchor(passwordField, 281.0);
		root.getChildren().add(passwordField);

		shiftTextField = new TextField();
		AnchorPane.setLeftAnchor(shiftTextField, 488.0);
		AnchorPane.setTopAnchor(shiftTextField, 322.0);
		root.getChildren().add(shiftTextField);

		pharmacyIdTextField = new TextField();
		AnchorPane.setLeftAnchor(pharmacyIdTextField, 488.0);
		AnchorPane.setTopAnchor(pharmacyIdTextField, 366.0);
		root.getChildren().add(pharmacyIdTextField);

		salaryTextField = new TextField();
		AnchorPane.setLeftAnchor(salaryTextField, 488.0);
		AnchorPane.setTopAnchor(salaryTextField, 412.0);
		root.getChildren().add(salaryTextField);

		Button addNewUserButton = new Button("Add New User");
		addNewUserButton.setFont(new Font("System Bold Italic", 14));
		addNewUserButton.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(addNewUserButton, 492.0);
		AnchorPane.setTopAnchor(addNewUserButton, 466.0);
		root.getChildren().add(addNewUserButton);

		Button cancelButton = new Button("Cancel");
		cancelButton.setFont(new Font("System Bold Italic", 14));
		cancelButton.setTextFill(javafx.scene.paint.Color.web("#9e095d"));
		AnchorPane.setLeftAnchor(cancelButton, 356.0);
		AnchorPane.setTopAnchor(cancelButton, 466.0);
		root.getChildren().add(cancelButton);
		cancelButton.setOnAction(e -> mainScreen(primaryStage));

		Label asteriskLabel1 = createAsteriskLabel(636.0, 64.0);
		Label asteriskLabel2 = createAsteriskLabel(636.0, 103.0);
		Label asteriskLabel3 = createAsteriskLabel(636.0, 150.0);
		Label asteriskLabel4 = createAsteriskLabel(636.0, 185.0);
		Label asteriskLabel5 = createAsteriskLabel(636.0, 223.0);
		Label asteriskLabel6 = createAsteriskLabel(636.0, 273.0);
		Label asteriskLabel7 = createAsteriskLabel(636.0, 314.0);
		Label asteriskLabel8 = createAsteriskLabel(636.0, 356.0);
		Label asteriskLabel9 = createAsteriskLabel(636.0, 404.0);

		root.getChildren().addAll(asteriskLabel1, asteriskLabel2, asteriskLabel3, asteriskLabel4, asteriskLabel5,
				asteriskLabel6, asteriskLabel7, asteriskLabel8, asteriskLabel9);

		addNewUserButton.setOnAction(e -> {
			if (isInputValid()) {
				String sql = "INSERT INTO pharmacists (phone_number, license_number, gender, shift, first_name, last_name, employee_id, pharmacy_id, salary, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

				try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
						PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

					preparedStatement.setInt(1, Integer.parseInt(phoneTextField.getText()));
					preparedStatement.setString(2, licenseTextField.getText());
					preparedStatement.setString(3, genderTextField.getText());
					preparedStatement.setString(4, shiftTextField.getText());

					String Name = nameTextField.getText();
					String[] name = Name.split(" ");

					preparedStatement.setString(5, name[0]);
					preparedStatement.setString(6, name[1]);
					preparedStatement.setInt(7, Integer.parseInt(employeeIdTextField.getText()));
					preparedStatement.setInt(8, Integer.parseInt(pharmacyIdTextField.getText()));
					preparedStatement.setDouble(9, Double.parseDouble(salaryTextField.getText()));
					preparedStatement.setString(10, passwordField.getText());

					preparedStatement.executeUpdate();
					showAlert("Sign Up ", "User added successfully.");

				} catch (SQLException ex) {
					ex.printStackTrace();
					// Handle the exception (e.g., show an error message)
					showAlert("Sign Up Error", "user already Exist");
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					// Handle the exception (e.g., show an error message)
					showAlert("Sign Up Error", "Invalid number format. Please enter valid numbers.");
				}
			}
		});

		primaryStage.show();
	}

	// Method to validate input fields
	private boolean isInputValid() {
		if (phoneTextField.getText().isEmpty() || licenseTextField.getText().isEmpty()
				|| genderTextField.getText().isEmpty() || shiftTextField.getText().isEmpty()
				|| nameTextField.getText().isEmpty() || employeeIdTextField.getText().isEmpty()
				|| pharmacyIdTextField.getText().isEmpty() || salaryTextField.getText().isEmpty()
				|| passwordField.getText().isEmpty()) {
			showAlert("Sign Up Error", "All fields are required. Please enter values in all fields.");
			return false;
		}
		return true;
	}

	private Label createAsteriskLabel(double layoutX, double layoutY) {
		Label asteriskLabel = new Label("*");
		asteriskLabel.setFont(new Font("System Bold Italic", 14));
		asteriskLabel.setTextFill(javafx.scene.paint.Color.web("#e41321"));
		AnchorPane.setLeftAnchor(asteriskLabel, layoutX);
		AnchorPane.setTopAnchor(asteriskLabel, layoutY);
		return asteriskLabel;
	}

// for confirm method , if statement to required text fields
	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void mainScreen(Stage primaryStage) {
		AnchorPane root = new AnchorPane();
		root.setPrefSize(600, 421);

		// Image
		Image image = new Image(
				"file:///C:/Users/DELL/Desktop/workspace/pharmacy-logo-data-base.png");
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(429);
		imageView.setFitWidth(248);
		root.getChildren().add(imageView);

		// Labels
		Label passwordLabel = new Label("Password:");
		passwordLabel.setFont(Font.font("System Bold Italic", 14));
		AnchorPane.setLeftAnchor(passwordLabel, 273.0);
		AnchorPane.setTopAnchor(passwordLabel, 205.0);
		root.getChildren().add(passwordLabel);

		Label userIdLabel = new Label("User ID:");
		userIdLabel.setFont(Font.font("System Bold", 14));
		AnchorPane.setLeftAnchor(userIdLabel, 272.0);
		AnchorPane.setTopAnchor(userIdLabel, 172.0);
		root.getChildren().add(userIdLabel);

		Label titleLabel = new Label("LOG IN");
		titleLabel.setFont(new Font(64));
		titleLabel.setTextFill(Color.valueOf("#9a045c"));
		titleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
		titleLabel.setPrefSize(323, 133);
		AnchorPane.setLeftAnchor(titleLabel, 262.0);
		AnchorPane.setTopAnchor(titleLabel, 14.0);
		root.getChildren().add(titleLabel);

		// TextFields
		passwordField = new PasswordField();
		AnchorPane.setLeftAnchor(passwordField, 364.0);
		AnchorPane.setTopAnchor(passwordField, 202.0);
		root.getChildren().add(passwordField);

		employeeIdTextField = new TextField();
		AnchorPane.setLeftAnchor(employeeIdTextField, 364.0);
		AnchorPane.setTopAnchor(employeeIdTextField, 169.0);
		root.getChildren().add(employeeIdTextField);

		// Buttons
		Button loginButton = createButton("Log in", "#900853");
		AnchorPane.setLeftAnchor(loginButton, 396.0);
		AnchorPane.setTopAnchor(loginButton, 247.0);
		root.getChildren().add(loginButton);
		loginButton.setOnAction(e -> handleLogin());

		Button signUpButton = createButton("Sign up", "#8a085a");
		AnchorPane.setLeftAnchor(signUpButton, 396.0);
		AnchorPane.setTopAnchor(signUpButton, 305.0);
		root.getChildren().add(signUpButton);
		signUpButton.setOnAction(e -> signUp(primaryStage));

		Button cancelButton = createButton("Cancel", "#940770");
		cancelButton.setOnAction(e -> primaryStage.close());
		AnchorPane.setLeftAnchor(cancelButton, 396.0);
		AnchorPane.setTopAnchor(cancelButton, 360.0);
		root.getChildren().add(cancelButton);

		Scene scene = new Scene(root);
		primaryStage.setTitle("Login Application");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Button createButton(String text, String textColor) {
		Button button = new Button(text);
		button.setFont(Font.font("System Bold Italic", 14));
		button.setTextFill(Color.valueOf(textColor));
		button.setPrefSize(85, 30);
		button.setAlignment(javafx.geometry.Pos.CENTER);
		return button;
	}

	private void signUp(Stage primaryStage) {
		AnchorPane root = new AnchorPane();
		root.setPrefSize(600, 400);
		root.setStyle("-fx-background-color: #66023c;");

		// Cancel Button
		Button cancelButton = createStyledButton("Cancel", "#8a085f", 169, 258);
		root.getChildren().add(cancelButton);
		cancelButton.setOnAction(e -> mainScreen(primaryStage));

		// Confirm Button
		Button confirmButton = createStyledButton("Confirm", "#a40c74", 375, 258);
		root.getChildren().add(confirmButton);
		confirmButton.setOnAction(e -> {
			if (handleConfirm()) {
				confirmScreen(primaryStage);
			}

		});

		// Label for manager information
		Label infoLabel = new Label("Confirm Employee informations for adding new employee :");
		infoLabel.setFont(Font.font("System Bold Italic", 14));
		infoLabel.setTextFill(createRadialGradient(Color.web("#ffffff"), Color.WHITE, 1.0, 0.5));
		AnchorPane.setLeftAnchor(infoLabel, 83.0);
		AnchorPane.setTopAnchor(infoLabel, 62.0);
		root.getChildren().add(infoLabel);

		// Employee ID Label
		Label managerIdLabel = createStyledLabel("Employee ID :", "#f2eeee", 121, 146);
		root.getChildren().add(managerIdLabel);

		// Password Label
		Label passwordLabel = createStyledLabel("Password :", "#fffdfd", 121, 184);
		root.getChildren().add(passwordLabel);

		// Employee ID TextField
		employeeIdTextField = new TextField();
		AnchorPane.setLeftAnchor(employeeIdTextField, 226.0);
		AnchorPane.setTopAnchor(employeeIdTextField, 144.0);
		root.getChildren().add(employeeIdTextField);

		// Password PasswordField
		passwordField = new PasswordField();
		AnchorPane.setLeftAnchor(passwordField, 226.0);
		AnchorPane.setTopAnchor(passwordField, 188.0);
		root.getChildren().add(passwordField);

		Scene scene = new Scene(root);
		primaryStage.setTitle("Confirm Manager Information");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private RadialGradient createRadialGradient(Color color1, Color color2, double centerX, double radius) {
		return new RadialGradient(centerX, 0.5, centerX, radius, 1, true, null, new Stop(0, color1),
				new Stop(1, color2));
	}

	private Button createStyledButton(String text, String textColor, double layoutX, double layoutY) {
		Button button = new Button(text);
		button.setFont(Font.font("System Bold Italic", 14));
		button.setTextFill(Color.web(textColor));
		AnchorPane.setLeftAnchor(button, layoutX);
		AnchorPane.setTopAnchor(button, layoutY);
		return button;
	}

	private Label createStyledLabel(String labelText, String textFill, double layoutX, double layoutY) {
		Label label = new Label(labelText);
		label.setFont(Font.font("System Bold Italic", 14));
		label.setTextFill(Color.web(textFill));
		AnchorPane.setLeftAnchor(label, layoutX);
		AnchorPane.setTopAnchor(label, layoutY);
		return label;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
