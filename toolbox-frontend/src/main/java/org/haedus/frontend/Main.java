package org.haedus.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 8/17/2015
 */
public class Main extends Application {

	final Menu menu1 = new Menu("File");
	final Menu menu2 = new Menu("Options");
	final Menu menu3 = new Menu("Help");

	@Override
	public void start(Stage primaryStage) throws IOException {
		URL resource = getClass().getClassLoader().getResource("sample.fxml");
		Parent root = FXMLLoader.load(resource);
		primaryStage.setTitle("Hello World");
		primaryStage.setScene(new Scene(root, 300, 275));
		primaryStage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
