/******************************************************************************
 * Copyright (c) 2016 Samantha Fiona McCabe                                   *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package org.didelphis.toolbox.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/3/2016
 */
public class Controller implements Initializable {

	private static final int PORT = 8080;
	private static final String LANDING_PAGE = "localhost:" + PORT + "/index.html";
	
	private final Workspace workspace = new Workspace();
	
	@FXML
	WebView webView;

	public Controller() {}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		WebEngine engine = webView.getEngine();
		engine.load(LANDING_PAGE);
		engine.reload();
	}
	
	public void openFile(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Project");
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			//TODO: do things here
			try {
				Project project = new Project(file);
				workspace.addProject(file.getName(), project);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
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
