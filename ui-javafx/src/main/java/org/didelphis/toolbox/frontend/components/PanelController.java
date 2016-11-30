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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 10/29/2016
 */
public class PanelController extends StackPane {

	private final WebEngine engine;
	
	private final Map<String, CodeEditor>    codeEditors;
	private final Map<String, LexiconViewer> lexiconViewers;
	
	private final LogViewer logViewer;

	public PanelController() {
		super();
		codeEditors = new HashMap<>();
		lexiconViewers = new HashMap<>();

		WebView webview = new WebView();
		engine = webview.getEngine();

		engine.load(getResourceURL("panelview.html"));
		getChildren().add(webview);

		// Controllers for pane contents
		logViewer = new LogViewer("logViewer", engine);

		// Populate initial view
		addCodeEditor("main");

		engine.setOnAlert(event -> System.out.println(event.toString()));
		engine.setOnError(event -> System.err.println(event.toString()));
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

	private static String getResourceURL(String path) {
		URL resource = PanelController.class.getClassLoader().getResource(path);
		return resource != null ? resource.toExternalForm() : null;
	}

	public void setTheme(String type, String name) {
		engine.executeScript("setTheme(\'"+type+"\',\'"+name+"\')");
	}
}