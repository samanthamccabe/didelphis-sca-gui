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

package org.didelphis.toolbox.frontend;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Samantha Fiona Morrigan McCabe
 * Created: 4/22/2016
 */
public class Workspace {
	
	private final Map<String, ProjectFile> projects;
	
	public Workspace() {
		projects = new LinkedHashMap<>();
	}
	
	public ProjectFile getProject(String id) {
		return projects.get(id);
	}
	
	public void addProject(String id, ProjectFile project) {
		projects.put(id, project);
	}
	
	public ProjectFile deleteProject(String id) {
		return projects.remove(id);
	}
}
