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

package org.didelphis.toolbox.components;

import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URL;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 10/31/2016
 */
public class LogView  extends Pane {

	private final WebView webview;
	private final WebEngine engine;
	
	public LogView() {
		super();
		webview = new WebView();
		engine = webview.getEngine();
		engine.load(getResourceURL("logView.html"));
		getChildren().add(webview);
	}

	public void append(String... data) {
		StringBuilder sb = new StringBuilder();
		for (String d : data) {
			sb.append(d);
		}

		String escaped = StringEscapeUtils.escapeEcmaScript(sb.toString());
		engine.executeScript("append(\"" + escaped + "\")");
	}
	
	public void clear() {
		engine.executeScript("clear()");
	}

	private static String getResourceURL(String path) {
		URL resource = CodeEditor.class.getClassLoader().getResource(path);
		if (resource != null) {
			return resource.toExternalForm();
		} else {
			return null;
		}
	}
}
