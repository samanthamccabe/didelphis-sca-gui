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
public class PanelController extends StackPane {
	private static final DecimalFormat FORMAT = new DecimalFormat("#0.00");

	private static final Pattern NEWLINE = Pattern.compile("\\r|\\r?\\n");
	private static final double MILLI = 1.0E-6;

	private final WebEngine engine;
	
	// Map from Path to 
	private final Map<String, CodeEditor>    codeEditors;
	private final Map<String, LexiconViewer> lexiconViewers;
	
	private final LogViewer logViewer;

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
		addCodeEditor("");

		engine.setOnAlert(event -> System.out.println(event.toString()));
		engine.setOnError(event -> System.err.println(event.toString()));
	}
	
	public void openProject(File file) {
		try {
			String data = FileUtils.readFileToString(file);
			codeEditors.get(0).setCode(data);
			compileScript();
			// TODO: add hooks for opening other files
		} catch (IOException e) {
			LogViewer logViewer = getLogViewer();
			logViewer.error(file.toString(), -1, "",e.toString());
		}
	}

	public void compileScript() {
		CodeEditor editor = getCodeEditors().get("");
		String code = editor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");
		LogViewer logViewer = getLogViewer();

		ErrorLogger errorLogger = new ErrorLogger();
		errorLogger.clear();
		logViewer.clearLog();
		clearErrorMarkers();
		try {
//			String fileName = getFileName();
			logViewer.info(fileName, "processing");

			long start = System.nanoTime();
			StandardScript script = new StandardScript(fileName, code, handler, errorLogger);
			long end = System.nanoTime();
			double elapsed = (end-start) * 1.0E-6;
			if (errorLogger.isEmpty()) {
				logViewer.info(fileName, "compiled successfully in ",
						FORMAT.format(elapsed), " ms");
			} else {
				logErrors(errorLogger);
			}

		} catch (Exception e) {
			StringBuilder sb = new StringBuilder(e.toString());
			for (StackTraceElement element : e.getStackTrace()) {
				sb.append("\n");
				sb.append(element);
			}
			logViewer.error(getFileName(), -1,
					"Unhandled error while compiling script! ", sb.toString());
		}
	}

	public void runScript() {
		CodeEditor editor = codeEditors.get("");
		String code = editor.getCodeAndSnapshot();
		FileHandler handler = new DiskFileHandler("UTF-8");

		ErrorLogger errorLogger = new ErrorLogger();
		errorLogger.clear();
		logViewer.clearLog();
		clearErrorMarkers();
		try {
			String fileName = getFileName();
			logViewer.info(fileName, "processing");

			long start = System.nanoTime();
			StandardScript script = new StandardScript(fileName, code, handler, errorLogger);
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
			logViewer.error(getFileName(), -1,
					"Unhandled error while running script! ", sb.toString());
		}
	}

	@NotNull
	private LexiconData buildLexicons(SoundChangeScript script) {
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

	public CodeEditor addCodeEditor(String id) {
		if (!codeEditors.containsKey(id)) {
			CodeEditor value = new CodeEditor(id, engine);
			codeEditors.put(id, value);
			return value;
		} else {
			return codeEditors.get(id);
		}
	}

	public LexiconViewer addLexiconView(String id) {
		if (!lexiconViewers.containsKey(id)) {
			LexiconViewer value = new LexiconViewer(id, engine);
			lexiconViewers.put(id, value);
			return value;
		} else {
			return lexiconViewers.get(id);
		}
	}

	public Map<String, CodeEditor> getCodeEditors() {
		return codeEditors;
	}

	public Map<String, LexiconViewer> getLexiconViewers() {
		return lexiconViewers;
	}

	public CodeEditor getCodeEditor(String id) {
		return codeEditors.get(id);
	}
	
	public LexiconViewer getLexiconViewer(String id) {
		return lexiconViewers.get(id);
	}

	public WebEngine getWebEngine() {
		return engine;
	}

	public LogViewer getLogViewer() {
		return logViewer;
	}

	public void clearErrorMarkers() {
		for (CodeEditor editor : codeEditors.values()) {
			editor.clearErrorMarkers();
		}
	}

	public void logErrors(Iterable<ErrorLogger.Error> errors) {
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

	public void setTheme(String type, String name) {
		engine.executeScript("setTheme(\'"+type+"\',\'"+name+"\')");
	}

//	private String getFileName() {
//		return scriptFile == null ? "null" : scriptFile.toString();
//	}
}