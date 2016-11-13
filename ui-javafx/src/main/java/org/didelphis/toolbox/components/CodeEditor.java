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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URL;
import java.util.List;

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
	
	public void error(String script, int line, String data) {
		log("ERROR", script, line, data);
	}
	
	public void clearErrorMarkers() {
		engine.executeScript("clearMarkers();");
		engine.executeScript("editor.session.clearAnnotations();");
	}

	public void addAnnotations(List<Annotation> annotations) {
		try {
			String val = new ObjectMapper().writeValueAsString(annotations);
			execute("setAnnotations("+val+")");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
		
	public void info(String script, String... strings) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("[INFO] ").append(script).append(" ");
		for (String string : strings) {
			stringbuilder.append(string);
		}
		stringbuilder.append("\n");
		String input = stringbuilder.toString();
		String escaped = StringEscapeUtils.escapeEcmaScript(input);
		execute("log.insert(\"" + escaped + "\");");
		showLog();
	}

	public void setTheme(String name) {
		execute("editor.setTheme(\"ace/theme/"+name+"\");");
		execute("log.setTheme(\"ace/theme/"+name+"\");");
	}

	public void setFontSize(Number fontSize) {
		execute("editor.setFontSize(" + String.valueOf(fontSize) + ")");
	}

	private void log(String code, String script, int line, String data) {
		String n = String.valueOf(line);
		String escaped = StringEscapeUtils.escapeEcmaScript(
				build("[", code, "] line: ", n, " ", script, " - ", data, "\n")
		);
		execute("log.insert(\"" + escaped + "\");");
		showLog();
	}

	public void showLog() {
		execute("log.container.parentNode.style.display='';");
	}

	public void hideLog() {
		execute("log.container.parentNode.style.display='none';");
	}

	public void clearLog() {
		execute("log.setValue(\"\",0);");
	}

	private Object execute(String command) {
		return engine.executeScript(command);
	}
	
	private static String build(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			sb.append(string);
		}
		return sb.toString();
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