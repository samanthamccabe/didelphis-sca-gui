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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by samantha on 11/13/16.
 */
public class LexiconViewer extends AbstractComponent {

	public LexiconViewer(String id, WebEngine engine) {
		super(id, engine);
	}

	@Override
	public void generate() {
		execute("addViewer", getId());
	}

	public void setContent(List<String> subKeys, List<List<String>> table) {
		execute("controller.lexiconViewers.get(\"" + getId() + "\").createTable", subKeys);
		int i = 1;
		List<Object> blob = new ArrayList<>();
		for (List<String> list : table) {
			if ((i % 50) == 0) {
				execute("controller.lexiconViewers.get(\"" + getId() + "\").addData", blob);
				blob.clear();
			}
			list.add(0, String.valueOf(i));
			blob.add(list);
			i++;
		}
		if (!blob.isEmpty()) {
			execute("controller.lexiconViewers.get(\"" + getId() + "\").addData", blob);
		}
		execute("controller.lexiconViewers(\"" + getId() + "\").initialize()");
	}

	public void clear() {
		execute("controller.lexiconViewers(\"" + getId() + "\").clear()");
	}
}
