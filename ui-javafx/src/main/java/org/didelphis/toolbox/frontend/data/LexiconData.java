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

package org.didelphis.toolbox.frontend.data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 11/18/2016
 */
public class LexiconData extends LinkedHashMap<String, LinkedHashMap<String, List<String>>> {
	
	public List<List<String>> getAsTable(String key) {
		List<List<String>> list = new ArrayList<>();

		LinkedHashMap<String, List<String>> map = get(key);

		for (List<String> columns : map.values()) {
			list.add(columns);
		}

		List<List<String>> transpose = new ArrayList<>();
		int columns = list.size();
		int rows = list.get(0).size();
		for (int i = 0; i < rows; i++) {
			List<String> row = new ArrayList<>();
			for (int j = 0; j < columns; j++) {
				row.add(list.get(j).get(i));
			}
			transpose.add(row);
		}

		return transpose;
	}
	
	public List<String> getSubKeys(String key) {
		List<String> list = new ArrayList<>();
		list.addAll(get(key).keySet());
		return list;
	}
}
