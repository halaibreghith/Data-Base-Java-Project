package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;

public class Sidebar extends VBox {

	private final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();

	public Sidebar(String name) {
		// Creating all the options
		HBox dashboard = getButton("Dashboard",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/dashboard.png");
		HBox inventory = getButton("Inventory",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/inventory.png");
		HBox sell = getButton("Sell", "file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/sell.png");
		HBox report = getButton("Report",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/report.png");
		HBox orders = getButton("Orders",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/orders.png");
		HBox employees = getButton("Employees",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/employees.png");
		HBox customers = getButton("Customers",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/customers.png");
		HBox suppliers = getButton("Suppliers",
				"file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/suppliers.png");

		// Configurations
		setPrefWidth(SCREEN_WIDTH * 0.175);
		setAlignment(Pos.TOP_CENTER);
		getChildren().addAll(getTitle(), getLogin(name), dashboard, inventory, sell, report, orders, employees,
				customers, suppliers);
		setStyle("-fx-background-color: #293442");

		// Events for Buttons
		dashboard.setOnMouseClicked(event -> {
			Main.setSceneDashboard();
		});
		inventory.setOnMouseClicked(event -> {
			Main.setSceneInventory();
		});
		sell.setOnMouseClicked(event -> {
			Main.setSceneSellProduct();
		});
		report.setOnMouseClicked(event -> {
			Main.setSceneReport();
		});
		orders.setOnMouseClicked(event -> {
			Main.setSceneOrders();
		});
		employees.setOnMouseClicked(event -> {
			Main.setScenePharmacists();
		});
		customers.setOnMouseClicked(event -> {
			Main.setSceneCustomers();
		});
		suppliers.setOnMouseClicked(event -> {
			Main.setSceneSuppliers();
		});
	}

	// Creating the title
	private HBox getTitle() {
		HBox hbox = new HBox(20);

		Image img = new Image("file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/pharmacy-logo.png");
		ImageView view = new ImageView(img);

		Label label = new Label("Grand Pharm");
		Fonts.setRegularFont(label, Color.WHITE, 20);
		label.setGraphic(view);
		label.setTextFill(Color.WHITE);

		hbox.getChildren().addAll(label);
		hbox.setAlignment(Pos.CENTER);
		hbox.setStyle("-fx-background-color: #1d242e");
		hbox.setPadding(new Insets(10, 0, 10, 0));

		return hbox;
	}

	// Creating the log in information
	private HBox getLogin(String name) {
		HBox hbox = new HBox(20);

		Image img = new Image("file:///C:/Users/DELL/Desktop/workspace/baseProject/src/icons/user-logo.png");
		ImageView view = new ImageView(img);

		VBox textVBox = new VBox(10);
		Text nameText = new Text(name);
		Fonts.setRegularFont(nameText, Color.WHITE, 18);
		textVBox.getChildren().addAll(nameText);

		BorderStroke borderStroke = new BorderStroke(javafx.scene.paint.Color.web("#394C56"), // Border color
				BorderStrokeStyle.SOLID, // Border style
				null, // Corner radii (use null for default)
				new BorderWidths(0, 0, 2, 0) // Border widths (top: 2 pixels, others: 0 pixels)
		);

		// Apply the border to the HBox
		Border border = new Border(borderStroke);
		hbox.setBorder(border);

		hbox.getChildren().addAll(view, textVBox);
		hbox.setAlignment(Pos.CENTER);
		hbox.setPadding(new Insets(20, 0, 20, 0));
		return hbox;
	}

	// Creating the options
	private HBox getButton(String text, String imgPath) {
		HBox hbox = new HBox();

		Image img = new Image(imgPath);
		ImageView view = new ImageView(img);

		Label label = new Label(text);
		Fonts.setRegularFont(label, Color.WHITE, 18);
		label.setGraphic(view);
		label.setGraphicTextGap(20);

		// Border
		BorderStroke borderStroke = new BorderStroke(javafx.scene.paint.Color.web("#394C56"), // Border color
				BorderStrokeStyle.SOLID, // Border style
				null, // Corner radii (use null for default)
				new BorderWidths(0, 0, 2, 0) // Border widths (top: 2 pixels, others: 0 pixels)
		);

		// Apply the border to the HBox
		Border border = new Border(borderStroke);
		hbox.setBorder(border);

		hbox.getChildren().add(label);
		hbox.setAlignment(Pos.TOP_CENTER);
		hbox.setCursor(Cursor.HAND);
		hbox.setPadding(new Insets(10, 0, 10, 0));

		return hbox;
	}
}
