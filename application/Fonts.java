package application;

import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Fonts {

	public static void setRegularFont(Text text, Color color, double size) {
		File fontFile = new File(
				"C:\\Users\\DELL\\Desktop\\workspace\\baseProject\\src\\Font\\Poppins-Regular.ttf");
		try {
			Font customFont = Font.loadFont(fontFile.toURI().toURL().openStream(), size);
			text.setFont(customFont);
			text.setFill(color);
		} catch (Exception e) {
			System.out.println("Font error.");
			e.printStackTrace();
		}
	}

	public static void setRegularFont(Label text, Color color, double size) {
		File fontFile = new File(
				"C:\\Users\\DELL\\Desktop\\workspace\\baseProject\\src\\Font\\Poppins-Regular.ttf");
		try {
			Font customFont = Font.loadFont(fontFile.toURI().toURL().openStream(), size);
			text.setFont(customFont);
			text.setTextFill(color);
		} catch (Exception e) {
			System.out.println("Font error.");
			e.printStackTrace();
		}
	}
}