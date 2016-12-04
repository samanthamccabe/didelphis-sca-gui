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

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;
import org.didelphis.toolbox.frontend.components.PanelController;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Primary controller for the Didelphis UI
 */
public final class Controller implements Initializable {
	
	private static final FileChooser.ExtensionFilter SCRIPT_EXTENSION_FILTER =
			new FileChooser.ExtensionFilter("Script Files", "*.rule", "*.*");

	@FXML private ChoiceBox<String> themePicker;
	@FXML private Spinner<Integer> fontSizeSpinner;
	@FXML private PanelController panelController;
	@FXML private CheckBox hiddenCharBox;
	private File currentFolder;

	public Controller(){}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		currentFolder = new File("./");

		themePicker.setValue("Chrome");
		themePicker.setItems(FXCollections.observableArrayList(ThemeManager.listThemes()));

		fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4,24,14));
		fontSizeSpinner.setEditable(true);

		panelController.addThemeListenerTo(themePicker.getSelectionModel().selectedIndexProperty());
		panelController.addFontSizeListenerTo(fontSizeSpinner.valueProperty());
		panelController.addHiddenCharListenerTo(hiddenCharBox.selectedProperty());
	}

	public void runScript() {
		panelController.runScript();
	}

	public void compileScript() {
		panelController.compileScript();
	}

	public void openProject() {
		FileChooser chooser = newChooser("Open Script");
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			currentFolder = file.getParentFile();
			panelController.openProject(file);
		}
	}
	
	public void saveProjectAs() {
		FileChooser chooser = newChooser("Save Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			currentFolder = file.getParentFile();
			panelController.saveProjectAs(file);
		}
	}

	public void newProject() {
		FileChooser chooser = newChooser("New Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			currentFolder = file.getParentFile();
			panelController.newProject(file);
		}
	}

	public void saveProjectFile() {
		panelController.saveProject();
	}
	
	private FileChooser newChooser(String title) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		chooser.getExtensionFilters().addAll(SCRIPT_EXTENSION_FILTER);
		chooser.setInitialDirectory(currentFolder);
		return chooser;
	}

	@Override
	public String toString() {
		return "Controller{" +
				"themePicker=" + themePicker +
				", fontSizeSpinner=" + fontSizeSpinner +
				", panelController=" + panelController +
				", hiddenCharBox=" + hiddenCharBox +
				", currentFolder=" + currentFolder +
				'}';
	}
}
