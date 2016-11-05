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
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URL;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 10/29/2016
 */
public class CodeEditor extends Pane {
		
	private final WebView webview;
	private final WebEngine engine;
	
	/** a snapshot of the code to be edited kept for easy initialization and reversion of editable code. */
	private String editingCode;
	
	public CodeEditor() {
		super();
		webview = new WebView();
		engine = webview.getEngine();
		
		this.editingCode = "";

		webview.setPrefSize(1080, 690);
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

	private static String getResourceURL(String path) {
		URL resource = CodeEditor.class.getClassLoader().getResource(path);
		if (resource != null) {
			return resource.toExternalForm();
		} else {
			return null;
		}
	}
}