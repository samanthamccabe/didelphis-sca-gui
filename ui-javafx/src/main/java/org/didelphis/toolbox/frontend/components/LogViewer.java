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

import javafx.scene.web.WebEngine;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 11/15/2016
 */
public class LogViewer extends Component {
	
	public LogViewer(String id, WebEngine engine) {
		super(id, engine);
	}

	@Override
	public void generate() {
		// TODO:
	}
	
	public void setTheme(String theme) {
		execute("errorLogger.setTheme(\"ace/theme/" + theme + "\")");
	}

	public void error(String script, int line, String data) {
		log("ERROR", script, line, data);
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
		execute("errorLogger.append(\"" + escaped + "\");");
	}

	private void log(String code, String script, int line, String data) {
		String n = String.valueOf(line);
		String escaped = StringEscapeUtils.escapeEcmaScript(
				build("[", code, "] line: ", n, " ", script, " | ", data, "\n")
		);
		execute("errorLogger.append(\"" + escaped + "\");");
	}

	public void clearLog() {
		execute("errorLogger.clear();");
	}

	private static String build(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String string : strings) {
			sb.append(string);
		}
		return sb.toString();
	}
}