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

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 11/15/2016
 */
public abstract class AbstractComponent {
	
	private final String id;
	private final WebEngine webEngine;
	private final ObjectMapper mapper; 

	/**
	 * @param id the HTML id for this component; it is recommended that this
	 *      also be the key of the map which stores these components in the 
	 *      <code>PanelController</code>
	 * @param webEngine the WebView engine in which this component is rendered
	 */
	protected AbstractComponent(String id, WebEngine webEngine) {
		this.id = id;
		this.webEngine = webEngine;
		mapper = new ObjectMapper();
	}
	
	public String getId() {
		return id;
	}
	
	public WebEngine getWebEngine() {
		return webEngine;
	}

	/**
	 * Executes a script in the context of the <code>WebEngine</code>.
	 * @return execution result, converted to a Java object using the following
	 * rules:
	 * <ul>
	 * <li>JavaScript Int32 is converted to {@code java.lang.Integer}
	 * <li>Other JavaScript numbers to {@code java.lang.Double}
	 * <li>JavaScript string to {@code java.lang.String}
	 * <li>JavaScript boolean to {@code java.lang.Boolean}
	 * <li>JavaScript {@code null} to {@code null}
	 * <li>Most JavaScript objects get wrapped as
	 *     {@code netscape.javascript.JSObject}
	 * <li>JavaScript JSNode objects get mapped to instances of
	 *     {@code netscape.javascript.JSObject}, that also implement
	 *     {@code org.w3c.dom.Node}
	 * <li>A special case is the JavaScript class {@code JavaRuntimeObject}
	 *     which is used to wrap a Java object as a JavaScript value - in this
	 *     case we just extract the original Java value.
	 * </ul>
	 */
	public Object execute(String script) {
		return webEngine.executeScript(script);
	}
	
	public Object execute(String command, Object parameter) {
		try {
			String data = mapper.writeValueAsString(parameter);
			String script = command + "(" + data + ");";
			return execute(script);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}