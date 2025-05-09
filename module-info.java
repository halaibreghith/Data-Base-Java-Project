module baseProject {
	requires javafx.controls;
	requires java.sql;
	requires java.desktop;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
}
