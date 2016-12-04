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

package org.didelphis.toolbox.frontend.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;
import org.didelphis.io.DiskFileHandler;
import org.didelphis.io.FileHandler;
import org.didelphis.soundchange.ErrorLogger;
import org.didelphis.soundchange.SoundChangeScript;
import org.didelphis.soundchange.StandardScript;
import org.didelphis.soundchange.command.LexiconIOCommand;
import org.didelphis.toolbox.frontend.ThemeManager;
import org.didelphis.toolbox.frontend.data.LexiconData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 10/29/2016
 */
public final class PanelController extends StackPane {
	private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");

	private static final Pattern TRIM_PATH = Pattern.compile(".*[/\\\\]");
	private static final Pattern NEWLINE = Pattern.compile("\\r|\\r?\\n");
	private static final double MILLI = 1.0E-6;
	private static final String DEFAULT = "main";

	private final WebEngine engine;
	
	// Map from Path to 
	private final Map<String, CodeEditor>    codeEditors;
	private final Map<String, LexiconViewer> lexiconViewers;
	
	private final LogViewer logViewer;
	private final FileHandler fileHandler;

	public PanelController() {
		super();
		codeEditors = new HashMap<>();
		lexiconViewers = new HashMap<>();

		WebView webview = new WebView();
		engine = webview.getEngine();
		engine.load(getResourceURL());
		getChildren().add(webview);

		// Controllers for pane contents
		logViewer = new LogViewer("logViewer", engine);

		// Populate initial view
		addCodeEditor(DEFAULT);

		engine.setOnAlert(event -> System.out.println(event.toString()));
		engine.setOnError(event -> System.err.println(event.toString()));
		fileHandler = new DiskFileHandler("UTF-8");
	}

	public void saveProjectAs(File file) {
			CodeEditor editor = codeEditors.get(DEFAULT);
			// TODO: add hooks for saving other files
			// Paths generated programmatically based on references in the script?
			editor.saveEditor(file);
	}

	public void newProject(File file) {
		// TODO: clear state
		CodeEditor editor = codeEditors.get(DEFAULT);
		editor.saveEditor(file);
	}

	public void openProject(File file) {
		try {
			String data = FileUtils.readFileToString(file);
			codeEditors.get(DEFAULT).setCode(data);
			compileScript();
			// TODO: add hooks for opening other files
		} catch (IOException e) {
			logViewer.error(file.toString(), -1, "",e.toString());
		}
	}

	public void addThemeListenerTo(ReadOnlyIntegerProperty property) {
		property.addListener((observable, oldValue, newValue) -> {
			String name = ThemeManager.get((int) newValue);
			String type = ThemeManager.getType(name);
			String normalized = ThemeManager.getNormalized((int) newValue);
			setTheme(type, normalized);
		});
	}

	public void addFontSizeListenerTo(ReadOnlyProperty<Integer> property) {
		property.addListener((observable, oldValue, newValue) ->  {
			for (CodeEditor editor : codeEditors.values()) {
						editor.setFontSize(newValue);
					}
				}
		);
	}

	public void addHiddenCharListenerTo(BooleanProperty property) {
		property.addListener((observable, oldValue, newValue) -> {
			for (CodeEditor editor : codeEditors.values()) {
				editor.setShowHiddenCharacters(newValue);
			}
		});
	}

	public void saveProject() {
		for (Map.Entry<String, CodeEditor> entry : codeEditors.entrySet()) {
			String pathname = entry.getKey();
			try {
				File file = new File(pathname);
				CodeEditor editor = entry.getValue();
				String code = editor.getCodeAndSnapshot();
				FileUtils.write(file, code);
			} catch (IOException e) {
				logViewer.error(pathname, -1, "", e.toString());
			}
		}
	}

	public void compileScript() {
		String editorKey = DEFAULT; // TODO: clean up
		CodeEditor editor = codeEditors.get(editorKey);
		String code = editor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");

		ErrorLogger errorLogger = new ErrorLogger();
		errorLogger.clear();
		logViewer.clearLog();
		clearErrorMarkers();
		try {
			logViewer.info(editorKey, "processing");

			long start = System.nanoTime();
			StandardScript script = new StandardScript(editorKey, code, handler, errorLogger);
			long end = System.nanoTime();
			if (errorLogger.isEmpty()) {
				double elapsed = (end - start) * 1.0E-6;
				logViewer.info(editorKey, "compiled successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				logErrors(errorLogger);
			}

		} catch (Exception e) {
			String stackTrace = makeStackTrace(e);
			logViewer.error(editorKey, -1,
					"Unhandled error while compiling script! ", stackTrace);
		}
	}

	@NotNull
	private static String makeStackTrace(Exception e) {
		StringBuilder sb = new StringBuilder(e.toString());
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append('\n');
			sb.append(element);
		}
		return sb.toString();
	}

	public void runScript() {
		CodeEditor editor = codeEditors.get(DEFAULT);
		String code = editor.getCodeAndSnapshot();

		ErrorLogger errorLogger = new ErrorLogger();
		errorLogger.clear();
		logViewer.clearLog();
		clearErrorMarkers();

		String fileName = editor.getId(); // TODO:
		try {
			logViewer.info(fileName, "processing");

			long start = System.nanoTime();
			StandardScript script = new StandardScript(fileName, code,
					fileHandler, errorLogger);
			script.process();
			long end = System.nanoTime();
			double elapsed = (end - start) * MILLI;
			if (errorLogger.isEmpty()) {
				logViewer.info(fileName, "ran successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				logErrors(errorLogger);
			}
			LexiconData lexiconData = buildLexicons(script);
			for (String key : lexiconData.keySet()) {
				List<String> subKeys = lexiconData.getSubKeys(key);
				List<List<String>> table = lexiconData.getAsTable(key);

				String newKey = editor.getId() + "-" + key;
				LexiconViewer viewer = addLexiconView(newKey);
				viewer.generate();
				viewer.setContent(subKeys, table);
			}
		} catch (Exception e) {
			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement element : e.getStackTrace()) {
				sb.append("\n");
				sb.append(element);
			}
			logViewer.error(fileName, -1,
					"Unhandled error while running script! ", sb.toString());
		}
	}

	@NotNull
	private static LexiconData buildLexicons(SoundChangeScript script) {
		LexiconData lexiconGroups = new LexiconData();
		for (Runnable runnable :  script.getCommands()) {
			if (runnable instanceof LexiconIOCommand) {
				LexiconIOCommand ioCommand = (LexiconIOCommand) runnable;
				String filePath = ioCommand.getFilePath();
				String path = TRIM_PATH.matcher(filePath).replaceAll("");
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

	private CodeEditor addCodeEditor(String id) {
		if (!codeEditors.containsKey(id)) {
			CodeEditor value = new CodeEditor(id, engine);
			codeEditors.put(id, value);
			return value;
		} else {
			return codeEditors.get(id);
		}
	}

	private LexiconViewer addLexiconView(String id) {
		if (!lexiconViewers.containsKey(id)) {
			LexiconViewer value = new LexiconViewer(id, engine);
			lexiconViewers.put(id, value);
			return value;
		} else {
			return lexiconViewers.get(id);
		}
	}

	private void clearErrorMarkers() {
		for (CodeEditor editor : codeEditors.values()) {
			editor.clearErrorMarkers();
		}
	}

	private void logErrors(Iterable<ErrorLogger.Error> errors) {
		logViewer.clearLog();
		for (ErrorLogger.Error error : errors) {
			logViewer.error(error);
			Annotation.error(error);
		}
	}

	private static String getResourceURL() {
		URL resource = PanelController.class
				.getClassLoader()
				.getResource("panelview.html");
		return resource != null ? resource.toExternalForm() : null;
	}

	private void setTheme(String type, String name) {
		engine.executeScript("setTheme(\'"+type+"\',\'"+name+"\')");
	}

	@Override
	public String toString() {
		return "PanelController{" +
				", codeEditors=" + codeEditors +
				", lexiconViewers=" + lexiconViewers +
				'}';
	}
}