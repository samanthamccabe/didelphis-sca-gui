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

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URL;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 10/29/2016
 */
public class CodeEditor extends StackPane {
		
	private final WebView webview;
	private final WebEngine engine;
	
	/** a snapshot of the code to be edited kept for easy initialization and reversion of editable code. */
	private String editingCode;
	
	public CodeEditor() {
		super();
		webview = new WebView();
		engine = webview.getEngine();
		
		engine.setOnError(event -> System.err.println(event.toString()));
		
		this.editingCode = "";
		engine.load(getResourceURL("codeEditor.html"));
		getChildren().add(webview);
	}
	
	/** sets the current code in the editor and creates an editing snapshot of the code which can be reverted to. */
	public void setCode(String newCode) {
		editingCode = newCode;
		String escaped = StringEscapeUtils.escapeEcmaScript(newCode);
		engine.executeScript("editor.setValue(\""+escaped+"\");");
	}

	/** returns the current code in the editor and updates an editing snapshot of the code which can be reverted to. */
	public String getCodeAndSnapshot() {
		editingCode = (String) engine.executeScript("editor.getValue();");
		return editingCode;
	}
	
	public void setShowHiddenCharacters(boolean b) {
		engine.executeScript("editor.setShowInvisibles(" + b + ")");
	}
	
	public void setUseLineWrap(boolean b) {
		engine.executeScript("log.session.setUseWrapMode(" + b + ");");
	}
	public void error(String script, int line, String... strings) {
		log("ERROR", script, line, strings);
	}
	
	public void error(String script, int line, String data, Exception e) {
		// Range(Number startRow, Number startColumn, Number endRow, Number endColumn)
		int numLines = data.split("(\r\n?|\n)").length;
		int from = line - 1;
		int to = numLines + from - 1;
		engine.executeScript("addMarker("+from + ", 0, " + to + ", 1);");
		log("ERROR", script, line, data, " ", e == null ? "" : e.toString());
	}
	
	public void clearErrorMarkers() {
		engine.executeScript("clearMarkers();");
	}

	public void warn(String script, int line, String... strings) {
		log("WARN", script, line, strings);
	}
	
	public void info(String script, String... strings) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder
				.append("INFO")
				.append(" [")
				.append(script)
				.append("] ");
		for (String string : strings) {
			stringbuilder.append(string);
		}
		stringbuilder.append("\n");
		String input = stringbuilder.toString();
		String escaped = StringEscapeUtils.escapeEcmaScript(input);
		engine.executeScript("log.insert(\"" + escaped + "\");");
		showLog();
	}

	public void setTheme(String name) {
		engine.executeScript("editor.setTheme(\"ace/theme/"+name+"\");");
		engine.executeScript("log.setTheme(\"ace/theme/"+name+"\");");
	}

	public void setFontSize(Number fontSize) {
		engine.executeScript("editor.setFontSize(" + String.valueOf(fontSize) + ")");
	}

	private void log(String code, String script, int line, String... strings) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder
				.append(code)
				.append(" [")
				.append(script)
				.append("] line: ")
				.append(line)
				.append(" - ");
		for (String string : strings) {
			stringbuilder.append(string);
		}
		stringbuilder.append("\n");
		String input = stringbuilder.toString();
		String escaped = StringEscapeUtils.escapeEcmaScript(input);
		engine.executeScript("log.insert(\"" + escaped + "\");");
		showLog();
	}

	public void showLog() {
		engine.executeScript("log.container.parentNode.style.display='';");
	}

	public void hideLog() {
		engine.executeScript("log.container.parentNode.style.display='none';");
	}

	public void clearLog() {
		engine.executeScript("log.setValue(\"\",0);");
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