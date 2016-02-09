package org.haedus.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 8/17/2015
 */
public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		URL resource = getClass().getClassLoader().getResource("main.fxml");
		if (resource != null) {
			Parent root = FXMLLoader.load(resource);
			primaryStage.setTitle("Haedus SCA Workbench");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		}
	}
	public static void main(String[] args) {
		launch(args);
	}
}
