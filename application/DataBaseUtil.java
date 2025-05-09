package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseUtil {

	private final static String DATABASE_URL = "jdbc:mysql://localhost:3306/pharmacy_database";
	private final static String USERNAME = "root";
	private final static String PASSWORD = "Halaibre264**";

	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Connection error.");
		}
		return null;
	}
}