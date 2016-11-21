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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.web.WebEngine;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 11/15/2016
 */
public class CodeEditor extends Component {

	private String editingCode;

	public CodeEditor(String id, WebEngine engine) {
		super(id, engine);
	}

	@Override
	public void generate() {
		// TODO:
	}

	public void setCode(String newCode) {
		editingCode = newCode;
		String escaped = StringEscapeUtils.escapeEcmaScript(newCode);
		execute("codeEditors[\"" + getId() + "\"].setValue(\"" + escaped + "\");");
	}

	public String getCodeAndSnapshot() {
		return (String) execute("codeEditors[\"" + getId() + "\"].getValue();");
	}


	public void setShowHiddenCharacters(boolean b) {
		execute("codeEditors[\"" + getId() + "\"].setShowInvisibles(" + b + ")");
	}

	public void clearErrorMarkers() {
		execute("codeEditors[\"" + getId() + "\"].clearMarkers();");
		execute("codeEditors[\"" + getId() + "\"].clearAnnotations();");
	}

	public void addAnnotations(List<Annotation> annotations) {
		try {
			String val = new ObjectMapper().writeValueAsString(annotations);
			execute("codeEditors[\"" + getId() + "\"].setAnnotations(" + val + ")");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void setTheme(String name) {
		execute("codeEditors[\"" + getId() + "\"].setTheme(\"ace/theme/" + name + "\");");
	}

	public void setFontSize(Number fontSize) {
		execute("codeEditors[\"" + getId() + "\"].setFontSize(" + fontSize + ")");
	}
}
