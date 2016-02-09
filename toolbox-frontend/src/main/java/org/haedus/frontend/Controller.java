package org.haedus.frontend;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/3/2016
 */
public class Controller {

	public void openFile(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Project");
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			//TODO: do things here
		}
	}

	public void newFile(ActionEvent actionEvent) {
		//TODO: how do i make a new a project idk
	}

	public void saveFile(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Project");
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			//TODO: do things here
		}
	}

	public void closeFile(ActionEvent actionEvent) {
		//TODO: how do i close a project idk
	}
}
