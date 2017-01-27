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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 11/15/2016
 *
 * Component for managing Ace editors
 */
public class CodeEditor extends AbstractComponent {

	private static final String ACCESS_PATH = PanelController.ACCESS_PATH + ".codeEditors";

	public CodeEditor(String id, WebEngine engine) {
		super(id, engine);
	}

	public void setCode(String newCode) {
		String escaped = StringEscapeUtils.escapeEcmaScript(newCode);
		execute(ACCESS_PATH + ".get(\"" + getId() + "\").setValue(\"" + escaped + "\");");
	}

	public void saveEditor(File file) {
		try (Writer writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(getCode());
		} catch (IOException e) {
			// TODO:
		}
	}

	public String getCode() {
		return (String) execute(ACCESS_PATH + ".get(\"" + getId() + "\").getValue();");
	}
	
	public void setShowHiddenCharacters(boolean b) {
		execute(ACCESS_PATH + ".get(\"" + getId() + "\").setShowInvisibles(" + b + ')');
	}

	public void clearErrorMarkers() {
		execute(ACCESS_PATH + ".get(\"" + getId() + "\").clearMarkers();");
		execute(ACCESS_PATH + ".get(\"" + getId() + "\").clearAnnotations();");
	}

	public void addAnnotation(Annotation annotation) {
		try {
			String val = new ObjectMapper().writeValueAsString(annotation);
			execute(ACCESS_PATH + ".get(\"" + getId() + "\").addAnnotation(" + val + ')');
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public void setAnnotations(List<Annotation> annotations) {
		try {
			String val = new ObjectMapper().writeValueAsString(annotations);
			execute(ACCESS_PATH + ".get(\"" + getId() + "\").setAnnotations(" + val + ')');
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void setTheme(String name) {
		execute(ACCESS_PATH + ".get(\"" + getId() + "\").setTheme(\"ace/theme/" + name + "\");");
	}

	public void setFontSize(Number fontSize) {
		execute(ACCESS_PATH + ".get(\"" + getId() + "\").setFontSize(" + fontSize + ')');
	}
}
