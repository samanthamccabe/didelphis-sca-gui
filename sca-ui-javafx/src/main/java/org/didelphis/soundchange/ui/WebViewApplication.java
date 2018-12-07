package org.didelphis.soundchange.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;

public class WebViewApplication extends Application {

	private Scene scene;

	@Override public void start(Stage stage) {
		// create the scene
		stage.setTitle("Web View");
		WebView root = new WebView();

		WebEngine engine = root.getEngine();

		engine.load(generateResourceURL());

		scene = new Scene(root, 750, 500);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args){
		launch(args);
	}

	private static String generateResourceURL() {
		URL resource = WebViewApplication.class.getClassLoader()
				.getResource("index.html");
		return (resource != null) ? resource.toExternalForm() : null;
	}
}