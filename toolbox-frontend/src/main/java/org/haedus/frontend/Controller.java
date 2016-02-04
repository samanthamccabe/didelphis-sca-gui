package org.haedus.frontend;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/3/2016
 */
public class Controller {
	public Label helloWorld;



	public void sayHelloWorld(ActionEvent actionEvent) {
		helloWorld.setText("Hello World!");
	}
}
