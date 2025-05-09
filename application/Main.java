package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {

	private static String userName;
	private static Stage newStage;
	public static Scene scene;
	private static Customers customers;
	private static DashBoard dashboard;
	private static Inventory inventory;
	private static Pharmacists pharmacists;
	private static Report report;
	private static SellProduct sellProduct;
	private static Suppliers suppliers;
	private static Orders orders;

	@Override
	public void start(Stage primaryStage) {
		try {
			scene = new Scene(dashboard);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.getStylesheets().add(getClass().getResource("tableview.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setMaximized(true);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openNewWindow(String name) {
		userName = name;
		customers = new Customers(userName);
		dashboard = new DashBoard(userName);
		inventory = new Inventory(userName);
		pharmacists = new Pharmacists(userName);
		report = new Report(userName);
		sellProduct = new SellProduct(userName);
		suppliers = new Suppliers(userName);
		orders = new Orders(userName);
		
		newStage = new Stage();
		scene = new Scene(new DashBoard(name));
		newStage.setScene(scene);
		newStage.setMaximized(true);
		newStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static void closeStage() {
		newStage.close();
	}

	// Setting the scene to the stage depending on the user option

	public static void setSceneDashboard() {
		scene.setRoot(dashboard);
	}

	public static void setSceneCustomers() {
		scene.setRoot(customers);
	}

	public static void setSceneInventory() {
		scene.setRoot(inventory);
	}

	public static void setScenePharmacists() {
		scene.setRoot(pharmacists);
	}

	public static void setSceneReport() {
		scene.setRoot(report);
	}

	public static void setSceneSellProduct() {
		scene.setRoot(sellProduct);
	}

	public static void setSceneSuppliers() {
		scene.setRoot(suppliers);
	}

	public static void setSceneOrders() {
		scene.setRoot(orders);
	}

}
