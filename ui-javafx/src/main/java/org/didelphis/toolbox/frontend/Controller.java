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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.FileChooser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.didelphis.io.DiskFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.soundchange.ErrorLogger;
import org.didelphis.soundchange.StandardScript;
import org.didelphis.soundchange.command.LexiconIOCommand;
import org.didelphis.toolbox.frontend.components.Annotation;
import org.didelphis.toolbox.frontend.components.CodeEditor;
import org.didelphis.toolbox.frontend.components.LexiconViewer;
import org.didelphis.toolbox.frontend.components.LogViewer;
import org.didelphis.toolbox.frontend.components.PanelController;
import org.didelphis.toolbox.frontend.data.LexiconData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 2/3/2016
 */
public class Controller implements Initializable {
	
	private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");
	private static final FileChooser.ExtensionFilter SCRIPT_EXTENSION_FILTER =
			new FileChooser.ExtensionFilter("Script Files", "*.rule", "*.*");
	
		private static final String MAIN = "main";

	private final static Map<String, String> THEMES = new LinkedHashMap<>();
	private final static List<String> THEME_LIST;
	private static final Pattern NEWLINE = Pattern.compile("\\r|\\r?\\n");

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
		THEMES.put("Ambiance",                "dark");
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

	private final ErrorLogger errorLogger;
	
	private File scriptFile;
	
	public Controller() {
		errorLogger = new ErrorLogger();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		themePicker.setValue("Chrome");
		themePicker.setItems(FXCollections.observableArrayList(THEMES.keySet()));
		themePicker.getSelectionModel().selectedIndexProperty().addListener(
				(observable, oldValue, newValue) -> {
					String name = THEME_LIST.get((int) newValue);
					String theme = name.toLowerCase().replaceAll("\\s", "_");
					for (CodeEditor editor : panelController.getCodeEditors().values()) {
						editor.setTheme(theme);
					}
					panelController.getLogViewer().setTheme(theme);
					panelController.setCSS(THEMES.get(name)); // light, dark, etc
				});
		fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(4,24,14));
		fontSizeSpinner.setEditable(true);
		fontSizeSpinner.valueProperty().addListener(
				(observable, oldValue, newValue) ->  {
					for (CodeEditor editor : panelController.getCodeEditors().values()) {
						editor.setFontSize(newValue);
					}
				});
		hiddenCharBox.selectedProperty().addListener(
				(observable, oldValue, newValue) -> {
					for (CodeEditor editor : panelController.getCodeEditors().values()) {
						editor.setShowHiddenCharacters(newValue);
					}
				});
	}

	public void openFile(ActionEvent actionEvent) {
		FileChooser chooser = chooser("Open Script");
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			try {
				scriptFile = file;
				String data = FileUtils.readFileToString(scriptFile);
				panelController.getCodeEditors().get(MAIN).setCode(data);
				compileScript();
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(file.toString(), -1, e.toString());
			}
		}
	}
	
	public void saveAsFile(ActionEvent actionEvent) {
		FileChooser chooser = chooser("Save Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				CodeEditor editor = panelController.getCodeEditors().get(MAIN);
				String data = editor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(file.toString(), -1, e.toString());
			}
		}
	}

	public void newFile(ActionEvent actionEvent) {
		FileChooser chooser = chooser("New Script");
		File file = chooser.showSaveDialog(null);
		if (file != null) {
			try {
				CodeEditor editor = panelController.getCodeEditors().get(MAIN);
				String data = editor.getCodeAndSnapshot();
				FileUtils.write(file, data);
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(file.toString(), -1, e.toString());
			}
		}
	}

	public void saveFile(ActionEvent actionEvent) {
		if (scriptFile != null) {
			try {
				CodeEditor editor = panelController.getCodeEditors().get(MAIN);
				String data = editor.getCodeAndSnapshot();
				FileUtils.write(scriptFile, data);
			} catch (IOException e) {
				LogViewer logViewer = panelController.getLogViewer();
				logViewer.error(getFileName(), -1, e.toString());
			}
		} else {
			saveAsFile(actionEvent);
		}
	}
	
	public void runScript() {
		CodeEditor editor = panelController.getCodeEditors().get(MAIN);
		String code = editor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		LogViewer logViewer = panelController.getLogViewer();
		
		errorLogger.clear();
		logViewer.clearLog();
		panelController.clearErrorMarkers();
		try {
			String fileName = getFileName();
			logViewer.info(fileName, "processing");

			long start = System.nanoTime();
			StandardScript script = new StandardScript(fileName, code, handler, errorLogger);
			script.process();
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			if (errorLogger.isEmpty()) {
				logViewer.info(fileName, "ran successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				generateErrorLog();
			}
			LexiconData lexiconData = buildLexicons(script);
			for (String key : lexiconData.keySet()) {
				List<String> subKeys = lexiconData.getSubKeys(key);
				List<List<String>> table = lexiconData.getAsTable(key);

				String newKey = editor.getId() + "-" + key; 
				LexiconViewer viewer = panelController.addLexiconView(newKey);
				viewer.generate();
				viewer.setContent(subKeys, table);
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement element : e.getStackTrace()) {
				sb.append("\n");
				sb.append(element);
			}
			logViewer.error(getFileName(), -1,
					"Unhandled error while running script! " + sb.toString());
		}
	}
	
	public void compileScript() {
		CodeEditor editor = panelController.getCodeEditors().get(MAIN);
		String code = editor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		LogViewer logViewer = panelController.getLogViewer();
		
		errorLogger.clear();
		logViewer.clearLog();
		panelController.clearErrorMarkers();
		try {
			String fileName = getFileName();
			logViewer.info(fileName, "processing");

			long start = System.nanoTime();
			StandardScript script = new StandardScript(fileName, code, handler, errorLogger);
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			if (errorLogger.isEmpty()) {
				logViewer.info(fileName, "compiled successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				generateErrorLog();
			}
			
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement element : e.getStackTrace()) {
				sb.append("\n");
				sb.append(element);
			}
			logViewer.error(getFileName(), -1,
					"Unhandled error while compiling script! " + sb.toString());
		}
	}

	@NotNull
	private LexiconData buildLexicons(StandardScript script) {
		LexiconData lexiconGroups = new LexiconData();
		for (Runnable runnable :  script.getCommands()) {
			if (runnable instanceof LexiconIOCommand) {
				LexiconIOCommand ioCommand = (LexiconIOCommand) runnable;
				String filePath = ioCommand.getFilePath();
				String path = filePath.replaceAll(".*[/\\\\]","");
				String handle = ioCommand.getFileHandle();
				FileHandler fileHandler = ioCommand.getFileHandler();
				List<String> list = fileHandler.readLines(filePath);
				if (lexiconGroups.containsKey(handle)) {
					lexiconGroups.get(handle).put(path, list);
				} else {
					LinkedHashMap<String, List<String>> map = new LinkedHashMap<>();
					map.put(path, list);
					lexiconGroups.put(handle, map);
				}
			}
		}
		return lexiconGroups;
	}

	private void generateErrorLog() {
		LogViewer logViewer = panelController.getLogViewer();
		logViewer.clearLog();
		
		List<Annotation> annotations = new ArrayList<>();
		for (ErrorLogger.Error err : errorLogger) {
			logViewer.error(err.getScript(), err.getLine(), err.getData());
			String message = err.getMessage();
			String html4 = StringEscapeUtils.escapeHtml4(message);
			int start = err.getLine() - 1;
			int end = start + NEWLINE.split(err.getData()).length - 1;
			annotations.add(Annotation.errorHTML(
					start, end, "<pre>"+ html4 +"</pre>")
			);
		}
		// TODO:
//		panelController.addAnnotations(annotations);
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
		
	private String getFileName() {
		return scriptFile == null ? "null" : scriptFile.toString();
	}
}
