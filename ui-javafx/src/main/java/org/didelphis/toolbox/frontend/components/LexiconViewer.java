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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samantha on 11/13/16.
 */
public class LexiconViewer extends Component {
	
	public LexiconViewer(String id, WebEngine engine) {
		super(id, engine);
		
	}

	@Override
	public void generate() {
		execute("addViewer", getId());
	}

	public void addContent(List<String> subKeys, List<List<String>> table) {
		Map<String, Object> temp = new HashMap<>();
		temp.put("keys", subKeys);
		temp.put("table", table);
		execute("lexiconViewers[\""+getId()+"\"].setValue", temp);
	}

	public void clear() {
		execute("lexiconViewers[\""+getId()+"\"].clear()");
	}
}
