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
import org.apache.commons.io.FileUtils;
import org.didelphis.toolbox.frontend.components.CodeEditor;
import org.didelphis.toolbox.frontend.components.LogViewer;
import org.didelphis.toolbox.frontend.components.PanelController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/3/2016
 */
public class Controller implements Initializable {
	
	private static final FileChooser.ExtensionFilter SCRIPT_EXTENSION_FILTER =
			new FileChooser.ExtensionFilter("Script Files", "*.rule", "*.*");
	
		private static final String MAIN = "main";

	private final static Map<String, String> THEMES = new LinkedHashMap<>();
	private final static List<String> THEME_LIST;

	static {
		THEMES.put("Chrome",                  "light");
		THEMES.put("Clouds",                  "light");
		THEMES.put("Crimson Editor",          "light");
		THEMES.put("Dawn",                    "light");
		THEMES.put("Dreamweaver",             "light");
		THEMES.put("Eclipse",                 "light");
		THEMES.put("GitHub",                  "light");
		THEMES.put("IPlastic",                "light");
		THEMES.put("Katzenmilch",             "light");
		THEMES.put("Kuroir",                  "light");
		THEMES.put("Solarized Light",         "light");
		THEMES.put("SQLServer",               "light");
		THEMES.put("Textmate",                "light");
		THEMES.put("Tomorrow",                "light");
		THEMES.put("XCode",                   "light");
		THEMES.put("Chaos",                   "dark");
		THEMES.put("Clouds Midnight",         "dark");
		THEMES.put("Cobalt",                  "dark");// blue
		THEMES.put("Idle Fingers",            "dark");
		THEMES.put("KR Theme",                "dark");
		THEMES.put("Merbivore",               "dark");
		THEMES.put("Merbivore Soft",          "dark");
		THEMES.put("Mono Industrial",         "dark");
		THEMES.put("Monokai",                 "dark");
		THEMES.put("Pastel On Dark",          "dark");
		THEMES.put("Solarized Dark",          "dark");// blue
		THEMES.put("Terminal",                "dark");
		THEMES.put("Tomorrow Night",          "dark");
		THEMES.put("Tomorrow Night Blue",     "dark");// blue
		THEMES.put("Tomorrow Night Bright",   "dark");
		THEMES.put("Tomorrow Night Eighties", "dark");
		THEMES.put("Twilight",                "dark");
		THEMES.put("Vibrant Ink",             "dark");
		THEME_LIST = new ArrayList<>(THEMES.keySet());
	}

	@FXML private ChoiceBox<String> themePicker;
	@FXML private Spinner<Integer> fontSizeSpinner;
	@FXML private PanelController panelController;
	@FXML private CheckBox hiddenCharBox;
	
	public Controller() {}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		themePicker.setValue("Chrome");
		themePicker.setItems(FXCollections.observableArrayList(THEMES.keySet()));
		themePicker.getSelectionModel().selectedIndexProperty().addListener(
				//TODO: panelController could return listener?
				(observable, oldValue, newValue) -> {
					String name = THEME_LIST.get((int) newValue);
					String theme = name.toLowerCase().replaceAll("\\s", "_");
					panelController.setTheme(THEMES.get(name), theme);
				});
		fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4,24,14));
		fontSizeSpinner.setEditable(true);
		fontSizeSpinner.valueProperty().addListener(
				//TODO: panelController could return listener?
				(observable, oldValue, newValue) ->  {
					for (CodeEditor editor : panelController.getCodeEditors().values()) {
						editor.setFontSize(newValue);
					}
				});
		hiddenCharBox.selectedProperty().addListener(
				//TODO: panelController could return listener?
				(observable, oldValue, newValue) -> {
					for (CodeEditor editor : panelController.getCodeEditors().values()) {
						editor.setShowHiddenCharacters(newValue);
					}
				});
	}

	public void openProject() {
		FileChooser chooser = chooser("Open Script");
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			panelController.openProject(file);
		}
	}
	
	public void saveProjectAs() {
		FileChooser chooser = chooser("Save Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				CodeEditor editor = panelController.getCodeEditors().get(MAIN);
				// TODO: add hooks for saving other files
				String data = editor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(file.toString(), -1, "", e.toString());
			}
		}
	}

	public void newProject() {
		// TODO: clear current state
		FileChooser chooser = chooser("New Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				CodeEditor editor = panelController.getCodeEditors().get(MAIN);
				String data = editor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(file.toString(), -1, "", e.toString());
			}
		}
	}

	public void saveProjectFile() {
		if (scriptFile != null) {
			try {
				CodeEditor editor = panelController.getCodeEditors().get(MAIN);
				// TODO: add hooks for saving other files
				String data = editor.getCodeAndSnapshot();
				FileUtils.write(scriptFile, data);
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(getFileName(), -1, "", e.toString());
			}
		} else {
			saveProjectAs();
		}
	}
	
	private FileChooser chooser(String title) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		chooser.getExtensionFilters().addAll(SCRIPT_EXTENSION_FILTER);
		chooser.setInitialDirectory(getParentFile());
		return chooser;
	}

	private File getParentFile() {
		return scriptFile != null ? scriptFile.getParentFile() : new File("./");
	}
}
